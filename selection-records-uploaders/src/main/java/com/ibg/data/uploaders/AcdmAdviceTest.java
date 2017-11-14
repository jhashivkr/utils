package com.ibg.data.uploaders;

import java.util.Date;
import java.util.List;

import com.ibg.models.CisUser;

public class AcdmAdviceTest {

	public AcdmAdviceTest() {

	}

	public void startDataUpload() {

		listUsers();
	}

	public void testAfterLoad() {
		//test if the connection is closed
		listUsers();
	}

	private void listUsers() {
		System.out.println("Listing users start: " + new Date());
		// List<CisUser> cisUsers = CisUser.findAll();
		List<CisUser> cisUsers = CisUser.find("user_id='CROSSIN'");

		for (CisUser user : cisUsers) {
			System.out.println("" + user.getId() + ", " + user.getString("USER_ID"));
		}

		System.out.println("Listing users done: " + new Date());

	}

}
