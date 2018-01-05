package ibg.actions.academics.checkstatus;

import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.actions.browserow.BaseAction;
import ibg.lib.activity.GanimedeThreadExecutor;
import ibg.lib.activity.ReqCallback;
import ibg.lib.activity.ResponseMapPurger;
import ibg.lib.activity.browserowp.db.objects.ItemActionInfoObj;
import ibg.lib.activity.browserowp.db.objects.ItemBrowseRowTabDataObj;
import ibg.lib.activity.browserowp.db.objects.JsonCheckStatusObj;
import ibg.lib.activity.browserowp.db.response.ObjectMapperWraper;
import ibg.lib.activity.browserowp.db.response.ResponseMapService;
import ibg.lib.activity.browserowp.db.response.ResponsePurgeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.opensymphony.xwork2.Action;

@Namespace(value = "/academics/checkstatus")
@Results({ @Result(location = "/browseRow/academicList.jsp", name = Action.SUCCESS) })
public final class CheckStatusJsonAction extends BaseAction {

	private static final long serialVersionUID = 8566851167443719634L;

	@SuppressWarnings("unchecked")
	public String execute() {

		String jsonResp = "[{'priority':'','browseRowMessage':'','ean':'','communityGroup':'','consortiaShortName':'','userID':'','listName':'','listOwner':'','listType':'','oidSalesOrderDetail':'','actionRejectBoth':'','actionAllowed':'','actionAllowBlock':'','actionAllowClaim':'', 'lastReceived':'', 'netPrice':''}]";
		Long listId = 0l;

		List<String> reqEans = null;
		HttpServletResponse resp = getServletResponse();
		resp.setContentType("application/json");

		String custGroup = (String) getSession().get("cisCustGroup");
		String userOwnerId = (String) getSession().get("sessionUserID");

		try {
			listId = Long.parseLong((getServletRequest().getParameter("list_id")).toString());
			reqEans = new LinkedList<String>(Arrays.asList((getServletRequest().getParameter("eans")).split(",")));

			if (null == reqEans || reqEans.isEmpty()) {
				resp.getWriter().write(jsonResp);
				return null;
			}

		} catch (Exception e) {
			try {
				resp.getWriter().write(jsonResp);
			} catch (IOException e1) {

			}
		}

		if (!ResponseMapService.isEmpty() && (null != reqEans && !reqEans.isEmpty())) {

			List<ItemActionInfoObj> data = (List<ItemActionInfoObj>) ResponseMapService.getListData(listId);

			if (null != data && !data.isEmpty() && data.size() > 0) {
				List<JsonCheckStatusObj> uiJsonData = new ArrayList<JsonCheckStatusObj>();

				try {
					for (ItemActionInfoObj dataObj : data) {
						if (null != dataObj) {
							if (null != dataObj.getEAN()) {
								if (reqEans.contains(dataObj.getEAN())) {
									JsonCheckStatusObj jsonObj = new JsonCheckStatusObj();

									jsonObj.setPriority(Integer.parseInt(dataObj.getPriority()));
									jsonObj.setBrowseRowMessage(dataObj.getBrowseRowMessage());

									// Check if priority is 60 or 70 then append expiration date from data base.
									if (jsonObj.getPriority() == 60 || jsonObj.getPriority() == 70) {

										AcademicListService service = null;
										service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
										Set<ItemBrowseRowTabDataObj> itemBrowseRowTabDataObj = (TreeSet<ItemBrowseRowTabDataObj>) service.findEanBrowseRowStatus(custGroup, dataObj.getEAN(),
												userOwnerId);
										for (ItemBrowseRowTabDataObj obj : itemBrowseRowTabDataObj) {
											if (jsonObj.getPriority() == Integer.parseInt(obj.getPriority())) {

												jsonObj.setBrowseRowMessage(dataObj.getBrowseRowMessage() + " " + obj.getDateToExpire());
											}
										}

									} else {
										jsonObj.setBrowseRowMessage(dataObj.getBrowseRowMessage());
									}

									jsonObj.setCommunityGroup(dataObj.getCommunityGroup());
									jsonObj.setConsortiaShortName(dataObj.getConsortiaShortName());
									jsonObj.setEan(dataObj.getEAN());
									jsonObj.setListName(dataObj.getListName());
									jsonObj.setListOwner(dataObj.getListOwner());
									jsonObj.setListType(dataObj.getListType());
									jsonObj.setUserID(dataObj.getUserID());
									jsonObj.setOidSalesOrderDetail(dataObj.getOidSalesOrderDetail());

									jsonObj.setActionAllowBlock(dataObj.getActionAllowBlock());
									jsonObj.setActionAllowClaim(dataObj.getActionAllowClaim());
									jsonObj.setActionAllowed(dataObj.getActionAllowed());
									jsonObj.setActionRejectBoth(dataObj.getActionRejectBoth());
									jsonObj.setLastReceived(dataObj.getLastReceived());
									jsonObj.setNetPrice(dataObj.getNetPrice());

									uiJsonData.add(jsonObj);

									ResponsePurgeList.putObj(listId, Integer.valueOf(dataObj.hashCode()));

									ListIterator<String> reqEansItr = reqEans.listIterator();
									for (; reqEansItr.hasNext();) {
										if (reqEansItr.next().equalsIgnoreCase(dataObj.getEAN())) {
											reqEansItr.remove();
										}

									}
									new ReqCallback() {
										public void executeNewTask() {
											GanimedeThreadExecutor.getReqFutures().add((Future<Runnable>) GanimedeThreadExecutor.getPurgeExecutor().submit(new ResponseMapPurger()));
										}
									}.executeNewTask();
								}// if (reqEans.contains(dataObj.getEAN()))

							}
						}

						if (reqEans.size() <= 0) {
							break;
						}

					}// for
				} catch (Exception e) {

				}

				try {

					if (null == uiJsonData || uiJsonData.isEmpty() || uiJsonData.size() <= 0) {
						resp.getWriter().write(jsonResp);
						return null;
					}

					ObjectMapperWraper mapper = (ObjectMapperWraper) AcademicServiceLocator.getBean(BeanName.JSON_MAPPER);
					jsonResp = mapper.write(uiJsonData);

					resp.flushBuffer();
					resp.getWriter().write(jsonResp);
					resp.getWriter().flush();

					data = null;
					uiJsonData = null;
					return null;

				} catch (JsonGenerationException e) {
					data = null;
					uiJsonData = null;
				} catch (JsonMappingException e) {
					data = null;
					uiJsonData = null;
				} catch (IOException e) {
					data = null;
					uiJsonData = null;
				}

			} // if (null != data)
			else {
				try {
					resp.getWriter().write(jsonResp);
				} catch (IOException e) {

				}
			}

		}// if (!ResponseMapService.isEmpty())
		else {
			try {
				resp.getWriter().write(jsonResp);

			} catch (IOException e) {

			}
		}

		return null;

	}

}
