package com.common.test;

import ibg.academics.dto.AcademicList;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSelectionRecOps {

	private static SessionFactory sessionFactory;
	private static Session session;
	private static Transaction transaction;

	private static ClassPathXmlApplicationContext context = null;

	static {

		try {
			context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
			sessionFactory = (SessionFactory) context.getBean("sessionFactory");
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		test3();
	}

	private static void test1() {
		// String findHql = "from AcademicList list where list.listId= :listid";
		String findHql = "from AcademicList list where list.libGrp = :libGrp";
		Long listid = new Long(1446l);
		String libGrp = "WAG320";

		Query query = session.createQuery(findHql);
		// query.setParameter("listid", listid);
		query.setParameter("libGrp", libGrp);

		System.out.println("query: " + query.getQueryString());

		List<AcademicList> objects = query.list();

		try {
			if (null != objects) {
				for (AcademicList obj : objects)
					System.out.println(obj.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void test2() {
		String findHql = "select auser.userFstName,auser.userLstName,list.listType,list.userOwnerId,item.dateCreated,item.quantity,item.action,item.ean,list.description from AcademicListItem item, AcademicList list, AcademicUser auser "
				+ "where item.listId = list.listId and auser.userId = list.userOwnerId and list.libGrp =:libGrp";

		String lib_grp = "WAG320";

		Query query = session.createQuery(findHql);
		query.setParameter("libGrp", lib_grp);

		System.out.println("query: " + query.getQueryString());

		List<Object[]> objects = query.list();

		try {
			if (null != objects) {
				for (Object[] obj : objects) {
					if (null != obj)
						System.out.println(obj[0].toString() + ", " + obj[1].toString() + ", " + obj[2].toString() + ", " + obj[3].toString() + ", " + obj[4].toString() + ", " + obj[5].toString()
								+ ", " + obj[6].toString() + ", " + obj[7].toString() + ", " + obj[8].toString());

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void test3() {

		String findHql = "from AcademicList list where list.libGrp = :libGrp";
		String libGrp = "WAG320";

		Query query = session.createQuery(findHql);
		query.setParameter("libGrp", libGrp);

		System.out.println("query: " + query.getQueryString());

		List<AcademicList> objects = query.list();

		try {
			if (null != objects) {
				for (AcademicList obj : objects)
					System.out.println(obj.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
