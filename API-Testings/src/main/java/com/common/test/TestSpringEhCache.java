package com.common.test;


//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicListItem;
import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.product.search.SelectionListCacheManager;
import ibg.product.search.SelectionListCacheService;
import ibg.product.search.acdm.SearchInfoObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpringEhCache {
	
	private static ClassPathXmlApplicationContext context = null;

	static {

		try {

			context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	final static Logger logger = LoggerFactory.getLogger(TestSpringEhCache.class);
	
	@Autowired
	private CacheManager cacheManager;
	
	public TestSpringEhCache() {
		//cacheManager = new CacheManager(this.getClass().getResourceAsStream("/ehcache.xml"));
		
		//cacheManager = (CacheManager) AcademicServiceLocator.getBean(BeanName.CACHE_MANAGER);
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//new TestSpringEhCache().run();
		//new TestSpringEhCache().testCacheInterface();
		
		//SearchInfoObject searchInfoObj = SelectionListCacheManager.initilizeCache("shiv");
		//System.out.println(searchInfoObj.toString());
		
		//searchInfoObj.setCustGrp("NOVA");
		
		//System.out.println("user :" + searchInfoObj.getUserId());
		

	}
	
	private void testCacheInterface(){
		
		SearchInfoObject searchObj = getUserData("NOVA","fNegron","IN");
		SearchInfoObject searchObj1 = getUserData("WAG320","rma8de","IN");
		
		String id = searchObj.getListId();
		String id1 = searchObj1.getListId();
		
		SelectionListCacheService cache = (SelectionListCacheService) AcademicServiceLocator.getBean(BeanName.SEL_CACHE_SERVICE);
		
		cache.saveObj(searchObj, id);
		cache.saveObj(searchObj1, id1);
		
		System.out.printf("from cache - %s : %s\n\n",id, ((SearchInfoObject) cache.getObj(id)).toString());
		System.out.printf("from cache - %s : %s\n\n",id1, ((SearchInfoObject) cache.getObj(id1)).toString());
		
		cache.clearObj(id);
		
		System.out.printf("after delete attempt - from cache - %s : %s\n\n",id, (null != cache.getObj(id)) ? (SearchInfoObject) cache.getObj(id) : " Not Found");
		System.out.printf("after delete attempt - from cache - %s : %s\n\n",id1, (null != cache.getObj(id1)) ? (SearchInfoObject) cache.getObj(id1) : " Not Found");
		
	}

	

	private void run() {
		for (String cacheName : cacheManager.getCacheNames()) {			
			Cache cache = cacheManager.getCache(cacheName);			
			//System.out.println("cacheName: " + cacheName + " with " + cache.getSize() + " elements");			
			System.out.println("cacheName: " + cacheName );
		}

		Cache cache = (Cache) cacheManager.getCache("selListSrch");
		SearchInfoObject searchObj = getUserData("NOVA","fNegron","IN");
		//cache.put(new Element(searchObj.getListId(), getUserData())); //ehcache approach
		//cache.put(searchObj.getListId(), getUserData()); //spring approach
		
		for (String cacheName : cacheManager.getCacheNames()) {
			cache = cacheManager.getCache(cacheName);
			//System.out.println("cacheName: " + cacheName + " with " + cache.getSize() + " elements");
			System.out.println("cacheName: " + cacheName );
		}
		
		cache = cacheManager.getCache("selListSrch");		
		
		if(null != cache.get(searchObj.getListId())){
			
			//SearchInfoObject obj1 = (SearchInfoObject) cache.get(searchObj.getListId()).getObjectValue();
			SearchInfoObject obj1 = (SearchInfoObject) cache.get(searchObj.getListId()).get();
			System.out.println(obj1.toString());
		}
			
	}

	private SearchInfoObject getUserData(String custGroup, String userId, String listTypeId) {
		/*** get user eans starts **/

		//String custGroup = "NOVA";
		//String userId = "fNegron";
		//String listTypeId = "IN";
		SearchInfoObject searchInfoObj = null;

		Long listId = null;

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		
		AcademicList academicList = null;
		Map<String, List<Long>> academicListItem;
		List<AcademicListItem> academicItemList = null;
		List<String> searchEans = new LinkedList<String>();

		academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);
		List<String> searchTermList = new ArrayList<String>();
		if (null != academicList) {
			academicItemList = service.getAllBylistId(academicList.getListId());
			academicListItem = new HashMap<String, List<Long>>(academicItemList.size());
			for (AcademicListItem item : academicItemList) {
				if (null != item.getEan()) {
					searchEans.add(item.getEan());
					searchTermList.add(item.getEan());

					if (academicListItem.containsKey(item.getEan())) {
						academicListItem.get(item.getEan()).add(item.getOprId());
					} else {
						List<Long> oprs = new LinkedList<Long>();
						oprs.add(item.getOprId());
						academicListItem.put(item.getEan(), oprs);
					}
				}

			}

			listId = academicList.getListId(); // put this in request scope
			searchInfoObj = new SearchInfoObject();
			searchInfoObj.setListId(listId.toString());
			searchInfoObj.setAcademicListItem(academicListItem);
			searchInfoObj.setSearchBy("ISBN");
			//searchInfoObj.setSearchTerm("NOEAN");

		}
		/*** get user eans ends **/
		
		return searchInfoObj;
	}

}
