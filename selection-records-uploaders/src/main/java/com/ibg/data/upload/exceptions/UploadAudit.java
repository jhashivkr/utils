package com.ibg.data.upload.exceptions;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UploadAudit {

	private Map<String, AuditDataStats> statsMap;

	public UploadAudit() {
		//statsMap = new LinkedHashMap<>();
		statsMap = new LinkedHashMap<String, AuditDataStats>();
	}

	private class AuditDataStats {
		public String ipageUserId;
		public List<listAuditData> auditData;

		public AuditDataStats() {
			auditData = new LinkedList<listAuditData>();
		}

		public String getIpageUserId() {
			return ipageUserId;
		}

		public void setIpageUserId(String ipageUserId) {
			this.ipageUserId = ipageUserId;
		}
	}

	private class listAuditData {
		listAuditData() {

		}

		public String getListTpId() {
			return listTpId;
		}

		public void setListTpId(String listTpId) {
			this.listTpId = listTpId;
		}

		public String getListId() {
			return listId;
		}

		public void setListId(String listId) {
			this.listId = listId;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		private String listTpId;
		private String listId;
		private int count;

	}
}
