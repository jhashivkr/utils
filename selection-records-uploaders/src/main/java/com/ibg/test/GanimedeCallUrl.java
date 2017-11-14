package com.ibg.test;

public final class GanimedeCallUrl {
	private static StringBuilder gnmdUrl = new StringBuilder();
	private GanimedeUrlComps urlComps = null;

	public GanimedeCallUrl(String... urls) {
		if (urls.length > 0) {
			gnmdUrl.delete(0, gnmdUrl.length());
			for (String url : urls)
				gnmdUrl.append(url);

			if (null != gnmdUrl && !gnmdUrl.toString().isEmpty()) {
				urlComps = new GanimedeUrlComps();
				urlComps.blowUpComps();
			}
		}
	}
	
	

	public boolean setString(int index, String value) {
		urlComps.setString(index - 1, value);
		return true;
	}

	public String getGnmdUrl() {
		return urlComps.getGnmdUrl();
	}

	public static final class GanimedeUrlComps {

		private GanimedeUrlObj[] gnmdUrlCompObjs;
		private static String[] propGnmdUrls;

		private GanimedeUrlComps() {
		}
		
		private void getPropertiesUrl(){
			//String logFileName = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getJsonLogsDir() + endTime + " - "					+ selRecRdr.getJsonFileName() + ".log";
			//writer = new FileWriter(new File(logFileName));
		}

		private void blowUpComps() {
			String[] gnmdUrlComps = gnmdUrl.toString().split("\\?");
			int compLen = 0;
			if (null != gnmdUrlComps && gnmdUrlComps.length > 0) {
				compLen = gnmdUrlComps.length;
				gnmdUrlCompObjs = new GanimedeUrlObj[compLen];
			}

			compLen = 0;
			for (String comp : gnmdUrlComps) {
				gnmdUrlCompObjs[compLen] = new GanimedeUrlObj();
				gnmdUrlCompObjs[compLen].setUrlComp(comp);
				gnmdUrlCompObjs[compLen].setUrlCompVal("?");
				compLen++;
			}
		}

		private boolean setString(int index, String value) {

			if (null != gnmdUrlCompObjs) {
				gnmdUrlCompObjs[index].setUrlCompVal(value);
				return true;
			}
			return false;
		}

		private String getGnmdUrl() {
			StringBuilder strBldr = new StringBuilder();
			for (GanimedeUrlObj comp : gnmdUrlCompObjs) {
				strBldr.append(comp.getUrlComp()).append(comp.getUrlCompVal());
			}

			return (null != strBldr) ? strBldr.toString() : "";
		}

	}

	final static class GanimedeUrlObj {
		private String urlComp;
		private String urlCompVal;

		public String getUrlComp() {
			return urlComp;
		}

		public void setUrlComp(String urlComp) {
			this.urlComp = urlComp;
		}

		public String getUrlCompVal() {
			return urlCompVal;
		}

		public void setUrlCompVal(String urlCompVal) {
			this.urlCompVal = urlCompVal;
		}
	}

}
