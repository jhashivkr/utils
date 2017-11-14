package com.ibg.parsers.list;

import java.util.List;
import java.util.Set;

public class ListMasterData<T> {
	private List<T> listMaster;
	private Set<String> users;

	public Set<String> getUsers() {
		return users;
	}

	protected void setUsers(Set<String> users) {
		this.users = users;
	}

	public List<T> getListMaster() {
		return listMaster;
	}

	protected void setListMaster(List<T> listMaster) {
		this.listMaster = listMaster;
	}

}
