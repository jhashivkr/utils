package com.common.test;

import ibg.administration.CISUserLoginData;
import ibg.product.information.EBPDataObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleEbookInfoAnalyzerTest<T, E> {

	private static final String ebl = "EBL";
	private static final String ebsco = "EBSCO";
	private static final String mil = "MIL";
	private static final String ebrary = "EBRARY";
	private static final String yes = "yes";
	private static final String no = "no";
	private static final String solr_no = "n";

	private T userLoginData;
	private Map<String, List<EBPDataObject>> cisEBPMap;

	private String ebookTextDisplay;
	private String dispEbookCur;
	private String dispEbookPrice;
	private String dispEbookLicense;
	private boolean foundTheMatchingOne = false;

	private static Map<String, String> ebpDownloadDetails;

	static {
		ebpDownloadDetails = new HashMap<String, String>();
		ebpDownloadDetails.put("EBL_DO", "Online and Downloadable");

		ebpDownloadDetails.put("EBL_EB", "Downloadable");
		ebpDownloadDetails.put("EBL_EC", "Online Only");
		ebpDownloadDetails.put("EBL_ED", "Available for download only");

		ebpDownloadDetails.put("EBRARY_N", "Online Only");
		ebpDownloadDetails.put("EBRARY_Y", "Downloadable and Online");

		ebpDownloadDetails.put("EBSCO_N", "Online Only");
		ebpDownloadDetails.put("EBSCO_Y", "Downloadable");

		ebpDownloadDetails.put("MIL_EB", " Downloadable");
		ebpDownloadDetails.put("MIL_EC", " Online Only");
		ebpDownloadDetails.put("MIL_ED", " Available for download only");
	}

	public <E> TitleEbookInfoAnalyzerTest() {
	}

	public void analyzeEbpData() {
		cleanOff();
		boolean status = false;

		for (Object key : ((CISUserLoginData) userLoginData).getPlatformPref()) {
			if (status && !foundTheMatchingOne) {
				foundTheMatchingOne = true;
			}

			if (!((CISUserLoginData) userLoginData).getEbpDataObjectMap().containsKey(key)) {
				continue;
			}

			// loop through the users ebp data
			for (EBPDataObject userEbp : ((CISUserLoginData) userLoginData).getEbpDataObjectMap().get(key.toString())) {

				if (status && !foundTheMatchingOne) {
					foundTheMatchingOne = true;
				}

				if (null != cisEBPMap.get(key.toString())) {
					List<EBPDataObject> cisEBPList = cisEBPMap.get(key.toString());

					// loop through the titles ebp data
					for (EBPDataObject titleEbp : cisEBPList) {
						if (status && !foundTheMatchingOne) {
							foundTheMatchingOne = true;
						}
						if (userEbp.getCisEbpLicence().equalsIgnoreCase(titleEbp.getCisEbpLicence())) {

							if (userEbp.getCisEbpCatpartner().equalsIgnoreCase(ebl)) {
								status = finerAnalysisEBL((CISUserLoginData) userLoginData, titleEbp);

							} else if (userEbp.getCisEbpCatpartner().equalsIgnoreCase(ebsco)) {
								status = finerAnalysisEBSCO((CISUserLoginData) userLoginData, titleEbp);

							} else if (userEbp.getCisEbpCatpartner().equalsIgnoreCase(mil)) {
								status = finerAnalysisMIL((CISUserLoginData) userLoginData, titleEbp);

							} else if (userEbp.getCisEbpCatpartner().equalsIgnoreCase(ebrary)) {
								status = finerAnalysisEBRARY((CISUserLoginData) userLoginData, titleEbp);

							}
						}// for

					}// for
				}// if

			}// for

		}

	}

	private String[] ebookPrice(CISUserLoginData userLoginData, EBPDataObject title) {
		String[] ebookData = new String[2];
		// price

		if (userLoginData.getFirstCurrencyValue().equalsIgnoreCase(title.getCisEbpCurrency())) {
			ebookData[0] = title.getCisEbpCurrency(); // cur
			ebookData[1] = title.getCisEbpPrice(); // price
		} else {

			for (String cur : userLoginData.getCurrencyPreferences()) {

				if (cur.equalsIgnoreCase(title.getCisEbpCurrency())) {
					ebookData[0] = title.getCisEbpCurrency(); // cur
					ebookData[1] = title.getCisEbpPrice(); // price
					break;
				}
			}

		}
		return ebookData;
	}

	private boolean finerAnalysisEBL(CISUserLoginData userLoginData, EBPDataObject title) {

		String[] ebookData = ebookPrice(userLoginData, title);
		dispEbookCur = ebookData[0];
		dispEbookPrice = ebookData[1];

		if (foundTheMatchingOne) {
			if (userLoginData.getFirstCurrencyValue().equalsIgnoreCase(title.getCisEbpCurrency())) {
				dispEbookCur = ebookData[0];
				dispEbookPrice = ebookData[1];
			}
			return true;
		}
		
		if ("Y".equalsIgnoreCase(title.getCisEbpDownloadable())) {
			if (ebpDownloadDetails.containsKey(title.getCisEbpDownloaddetails().trim())) {
				ebookTextDisplay = ebpDownloadDetails.get(title.getCisEbpDownloaddetails().trim());
			}
		}
		dispEbookLicense = title.getCisEbpLicence();

		return true;
	}

	private boolean finerAnalysisEBSCO(CISUserLoginData userLoginData, EBPDataObject title) {

		boolean ebscoCategoryOK = false;
		boolean ebscoLevelOK = false;

		if (null == title.getCisEbpEbscocategory() || !title.getCisEbpEbscocategory().isEmpty()) {
			ebscoCategoryOK = true;
		}
		if (userLoginData.getEbscoCategory().equalsIgnoreCase(title.getCisEbpEbscocategory())) {
			ebscoCategoryOK = true;
		}

		if (null == title.getCisEbpEbscolevel() || !title.getCisEbpEbscolevel().isEmpty())
			ebscoLevelOK = true;
		if (null == userLoginData.getEbsoLevel() || !userLoginData.getEbsoLevel().isEmpty()) {
			ebscoLevelOK = true;
		}
		if (title.getCisEbpEbscolevel().equalsIgnoreCase(userLoginData.getEbsoLevel())) {
			ebscoLevelOK = true;
		}

		// since both the licenses are equal then only it comes here
		if (ebscoCategoryOK && ebscoLevelOK) {

			String[] ebookData = ebookPrice(userLoginData, title);
			dispEbookCur = ebookData[0];
			dispEbookPrice = ebookData[1];

			if (foundTheMatchingOne) {
				if (userLoginData.getFirstCurrencyValue().equalsIgnoreCase(title.getCisEbpCurrency())) {
					dispEbookCur = ebookData[0];
					dispEbookPrice = ebookData[1];
				}
				return true;
			}
			if ("Y".equalsIgnoreCase(title.getCisEbpDownloadable())) {
				if (ebpDownloadDetails.containsKey(title.getCisEbpDownloaddetails().trim())) {
					ebookTextDisplay = ebpDownloadDetails.get(title.getCisEbpDownloaddetails().trim());
				}
			}
			dispEbookLicense = title.getCisEbpLicence();

			return true;
		}

		return false;

	}

	private boolean finerAnalysisMIL(CISUserLoginData userLoginData, EBPDataObject title) {

		if (yes.equalsIgnoreCase(userLoginData.getMilDownloadAgreement())) {
			String[] ebookData = ebookPrice(userLoginData, title);
			dispEbookCur = ebookData[0];
			dispEbookPrice = ebookData[1];

			if (foundTheMatchingOne) {
				if (userLoginData.getFirstCurrencyValue().equalsIgnoreCase(title.getCisEbpCurrency())) {
					dispEbookCur = ebookData[0];
					dispEbookPrice = ebookData[1];
				}
				return true;
			}
			
			if ("Y".equalsIgnoreCase(title.getCisEbpDownloadable())) {
				if (ebpDownloadDetails.containsKey(title.getCisEbpDownloaddetails().trim())) {
					ebookTextDisplay = ebpDownloadDetails.get(title.getCisEbpDownloaddetails().trim());
				}
			}

			return true;
		} else if (no.equalsIgnoreCase(userLoginData.getMilDownloadAgreement())) {
			if (!"MIL_ED".equalsIgnoreCase(title.getCisEbpDownloaddetails())) {
				String[] ebookData = ebookPrice(userLoginData, title);
				dispEbookCur = ebookData[0];
				dispEbookPrice = ebookData[1];

				if (foundTheMatchingOne) {
					if (userLoginData.getFirstCurrencyValue().equalsIgnoreCase(title.getCisEbpCurrency())) {
						dispEbookCur = ebookData[0];
						dispEbookPrice = ebookData[1];
					}
					return true;
				}
				
				if ("Y".equalsIgnoreCase(title.getCisEbpDownloadable())) {
					if (ebpDownloadDetails.containsKey(title.getCisEbpDownloaddetails().trim())) {
						ebookTextDisplay = ebpDownloadDetails.get(title.getCisEbpDownloaddetails().trim());
					}
				}

				return true;
			}

		}

		return false;

	}

	private boolean finerAnalysisEBRARY(CISUserLoginData userLoginData, EBPDataObject title) {

		String[] ebookData = ebookPrice(userLoginData, title);
		dispEbookCur = ebookData[0];
		dispEbookPrice = ebookData[1];

		if (foundTheMatchingOne) {
			if (userLoginData.getFirstCurrencyValue().equalsIgnoreCase(title.getCisEbpCurrency())) {
				dispEbookCur = ebookData[0];
				dispEbookPrice = ebookData[1];
			}
			return true;
		}
		if ("Y".equalsIgnoreCase(title.getCisEbpDownloadable())) {
			if (ebpDownloadDetails.containsKey(title.getCisEbpDownloaddetails().trim())) {
				ebookTextDisplay = ebpDownloadDetails.get(title.getCisEbpDownloaddetails().trim());
			}
		}

		return true;
	}
	
	/**
	 * @return the ebookTextDisplay
	 */
	public String getEbookTextDisplay() {
		return ebookTextDisplay;
	}

	/**
	 * @return the dispEbookLicense
	 */
	public String getDispEbookLicense() {
		return dispEbookLicense;
	}

	/**
	 * @return the dispEbookCur
	 */
	public String getDispEbookCur() {
		return dispEbookCur;
	}

	/**
	 * @return the dispEbookPrice
	 */
	public String getDispEbookPrice() {
		return dispEbookPrice;
	}

	/**
	 * @return the no
	 */
	public static String getNo() {
		return no;
	}

	/**
	 * @return the solrNo
	 */
	public static String getSolrNo() {
		return solr_no;
	}

	/**
	 * @param userLoginData
	 *            the userLoginData to set
	 */
	public void setUserLoginData(T userLoginData) {
		this.userLoginData = userLoginData;
	}

	/**
	 * @param cisEBPList
	 *            the cisEBPList to set
	 */
	public void setCisEBPMap(Map<String, List<EBPDataObject>> cisEBPMap) {
		this.cisEBPMap = cisEBPMap;
	}

	private void cleanOff() {

	}

}
