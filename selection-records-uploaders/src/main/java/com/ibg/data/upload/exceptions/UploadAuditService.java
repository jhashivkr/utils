package com.ibg.data.upload.exceptions;

import com.ibg.parsers.json.SelectionRecord;

public class UploadAuditService {

private static UploadAudit uploadAudit = new UploadAudit();
	
	
	public static UploadAudit auditObj(){
		return uploadAudit;
	}
	
}
