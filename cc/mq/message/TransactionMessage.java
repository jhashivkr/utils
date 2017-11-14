package ibg.cc.mq.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.cybersource.creditcard.response.CreditCardResponse;

public class TransactionMessage {
	private String DIV_NM;
	private String BLTO_CD;
	private String CUST_PO_ID;
	private String SUBS_ID;
	private String REQ_ID;
	private String RESP_CD;
	private String RESP_DN;
	private String TRAN_TP_DN;
	private Integer TRAN_DUAR_TM;
	private String TRAN_DT;
	private String BILL_CO_CD;
	private String TERM_CD;
	private String MSG_TIEBACK;
	private String RESP_WRITE_STAT;
	private Integer BLTO_SUBS_ID;

	private String TRAN_AMT;
	private String MERCH_REF_CODE;
	private String CORREL_ID;

	private String CARD_TYPE;
	private String SUBS_CARD_TP_DN;

	private CreditCardInfo reqMsg;
	private CreditCardResponse respMsg;
	
	private String tranId;

	public TransactionMessage() {

	}

	public TransactionMessage(CreditCardInfo reqMsg, CreditCardResponse respMsg) {
		this.reqMsg = reqMsg;
		this.respMsg = respMsg;
	}

	public TransactionMessage builder() {
		return this.builder(this.reqMsg, this.respMsg);
	}

	public TransactionMessage builder(CreditCardInfo reqMsg, CreditCardResponse respMsg) {

		try {
			if (null != reqMsg && null != respMsg) {
				// from request | from db
				this.setDIV_NM(reqMsg.getRequestField(MQReqField.Division) + '|' + respMsg.getMerchantId());

				if (null != reqMsg.getRequestField(MQReqField.Card_Bill_To)
						&& !reqMsg.getRequestField(MQReqField.Card_Bill_To).trim().isEmpty()) {
					this.setBLTO_CD(reqMsg.getRequestField(MQReqField.Card_Bill_To));
				} else {
					this.setBLTO_CD(reqMsg.getRequestField(MQReqField.Merch_Bill_To));
				}

				this.setCUST_PO_ID(reqMsg.getRequestField(MQReqField.Cust_PO));
				this.setSUBS_ID(reqMsg.getRequestField(MQReqField.Subscript_Id));
				this.setREQ_ID(respMsg.getRequestID());

				this.setRESP_CD(respMsg.getStatusCode() + '|' + respMsg.getStatus());
				if (null != respMsg.getStatusMessage() && !respMsg.getStatusMessage().isEmpty()) {
					if (respMsg.getStatusMessage().length() >= 100) {
						this.setRESP_DN(respMsg.getStatusMessage().substring(0, 99));
					} else {
						this.setRESP_DN(respMsg.getStatusMessage());
					}
				}

				this.setTRAN_TP_DN(reqMsg.getRequestField(MQReqField.TranType));

				this.setTRAN_DUAR_TM(0);

				Calendar calendar = Calendar.getInstance();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				this.setTRAN_DT(df.format(calendar.getTime()));

				// TRAN_AMT
				this.setTRAN_AMT(reqMsg.getRequestField(MQReqField.Card_Amount) + '|'
						+ reqMsg.getRequestField(MQReqField.Card_Ship_Amt) + '|'
						+ reqMsg.getRequestField(MQReqField.Card_Tax_Amount));

				//store the tieback in theMSG_TIEBACK
				this.setMSG_TIEBACK(reqMsg.getRequestField(MQReqField.TieBack));
				//this.setMERCH_REF_CODE(reqMsg.getRequestField(MQReqField.TieBack));

				if (null != respMsg.getCorrelId() && !respMsg.getCorrelId().isEmpty()) {
					if (respMsg.getCorrelId().length() >= 60) {
						this.setCORREL_ID(respMsg.getCorrelId().substring(0, 59));
					} else {
						this.setCORREL_ID(respMsg.getCorrelId());
					}
				}

				this.setCARD_TYPE(reqMsg.getRequestField(MQReqField.Card_Type));

				return this;
			}
			return null;
		} catch (Exception e) {

			e.printStackTrace();

			return null;
		}
	}

	/**
	 * @return the dIV_NM
	 */
	public String getDIV_NM() {
		return DIV_NM;
	}

	/**
	 * @param dIV_NM
	 *            the dIV_NM to set
	 */
	public void setDIV_NM(String dIV_NM) {
		DIV_NM = dIV_NM;
	}

	/**
	 * @return the bLTO_CD
	 */
	public String getBLTO_CD() {
		return BLTO_CD;
	}

	/**
	 * @param bLTO_CD
	 *            the bLTO_CD to set
	 */
	public void setBLTO_CD(String bLTO_CD) {
		BLTO_CD = bLTO_CD;
	}

	/**
	 * @return the cUST_PO_ID
	 */
	public String getCUST_PO_ID() {
		return CUST_PO_ID;
	}

	/**
	 * @param cUST_PO_ID
	 *            the cUST_PO_ID to set
	 */
	public void setCUST_PO_ID(String cUST_PO_ID) {
		CUST_PO_ID = cUST_PO_ID;
	}

	/**
	 * @return the sUBS_ID
	 */
	public String getSUBS_ID() {
		return SUBS_ID;
	}

	/**
	 * @param sUBS_ID
	 *            the sUBS_ID to set
	 */
	public void setSUBS_ID(String sUBS_ID) {
		SUBS_ID = sUBS_ID;
	}

	/**
	 * @return the rEQ_ID
	 */
	public String getREQ_ID() {
		return REQ_ID;
	}

	/**
	 * @param rEQ_ID
	 *            the rEQ_ID to set
	 */
	public void setREQ_ID(String rEQ_ID) {
		REQ_ID = rEQ_ID;
	}

	/**
	 * @return the rESP_CD
	 */
	public String getRESP_CD() {
		return RESP_CD;
	}

	/**
	 * @param rESP_CD
	 *            the rESP_CD to set
	 */
	public void setRESP_CD(String rESP_CD) {
		RESP_CD = rESP_CD;
	}

	/**
	 * @return the rESP_DN
	 */
	public String getRESP_DN() {
		return RESP_DN;
	}

	/**
	 * @param rESP_DN
	 *            the rESP_DN to set
	 */
	public void setRESP_DN(String rESP_DN) {
		RESP_DN = rESP_DN;
	}

	/**
	 * @return the tRAN_TP_DN
	 */
	public String getTRAN_TP_DN() {
		return TRAN_TP_DN;
	}

	/**
	 * @param tRAN_TP_DN
	 *            the tRAN_TP_DN to set
	 */
	public void setTRAN_TP_DN(String tRAN_TP_DN) {
		TRAN_TP_DN = tRAN_TP_DN;
	}

	/**
	 * @return the tRAN_DUAR_TM
	 */
	public Integer getTRAN_DUAR_TM() {
		return TRAN_DUAR_TM;
	}

	/**
	 * @param tRAN_DUAR_TM
	 *            the tRAN_DUAR_TM to set
	 */
	public void setTRAN_DUAR_TM(Integer tRAN_DUAR_TM) {
		TRAN_DUAR_TM = tRAN_DUAR_TM;
	}

	/**
	 * @return the tRAN_DT
	 */
	public String getTRAN_DT() {
		return TRAN_DT;
	}

	/**
	 * @param tRAN_DT
	 *            the tRAN_DT to set
	 */
	public void setTRAN_DT(String tRAN_DT) {
		TRAN_DT = tRAN_DT;
	}

	/**
	 * @return the bILL_CO_CD
	 */
	public String getBILL_CO_CD() {
		return BILL_CO_CD;
	}

	/**
	 * @param bILL_CO_CD
	 *            the bILL_CO_CD to set
	 */
	public void setBILL_CO_CD(String bILL_CO_CD) {
		BILL_CO_CD = bILL_CO_CD;
	}

	/**
	 * @return the tERM_CD
	 */
	public String getTERM_CD() {
		return TERM_CD;
	}

	/**
	 * @param tERM_CD
	 *            the tERM_CD to set
	 */
	public void setTERM_CD(String tERM_CD) {
		TERM_CD = tERM_CD;
	}

	/**
	 * @return the MSG_TIEBACK
	 */
	public String getMSG_TIEBACK() {
		return MSG_TIEBACK;
	}

	/**
	 * @param MSG_TIEBACK
	 *            the MSG_TIEBACK to set
	 */
	public void setMSG_TIEBACK(String mSG_TIEBACK) {
		MSG_TIEBACK = mSG_TIEBACK;
	}

	/**
	 * @return the RESP_WRITE_STAT
	 */
	public String getRESP_WRITE_STAT() {
		return RESP_WRITE_STAT;
	}

	/**
	 * @param RESP_WRITE_STAT
	 *            the RESP_WRITE_STAT to set
	 */
	public void setRESP_WRITE_STAT(String rESP_WRITE_STAT) {
		RESP_WRITE_STAT = rESP_WRITE_STAT;
	}

	/**
	 * @return the bLTO_SUBS_ID
	 */
	public Integer getBLTO_SUBS_ID() {
		return BLTO_SUBS_ID;
	}

	/**
	 * @param bLTO_SUBS_ID
	 *            the bLTO_SUBS_ID to set
	 */
	public void setBLTO_SUBS_ID(Integer bLTO_SUBS_ID) {
		BLTO_SUBS_ID = bLTO_SUBS_ID;
	}

	/**
	 * @return the cARD_TYPE
	 */
	public String getCARD_TYPE() {
		return CARD_TYPE;
	}

	/**
	 * @param cARD_TYPE
	 *            the cARD_TYPE to set
	 */
	public void setCARD_TYPE(String cARD_TYPE) {
		CARD_TYPE = cARD_TYPE;
	}

	/**
	 * @return the sUBS_CARD_TP_DN
	 */
	public String getSUBS_CARD_TP_DN() {
		return SUBS_CARD_TP_DN;
	}

	/**
	 * @param sUBS_CARD_TP_DN
	 *            the sUBS_CARD_TP_DN to set
	 */
	public void setSUBS_CARD_TP_DN(String sUBS_CARD_TP_DN) {
		SUBS_CARD_TP_DN = sUBS_CARD_TP_DN;
	}

	/**
	 * @return the tRAN_AMT
	 */
	public String getTRAN_AMT() {
		return TRAN_AMT;
	}

	/**
	 * @param tRAN_AMT
	 *            the tRAN_AMT to set
	 */
	public void setTRAN_AMT(String tRAN_AMT) {
		TRAN_AMT = tRAN_AMT;
	}

	/**
	 * @return the mERCH_REF_CODE
	 */
	public String getMERCH_REF_CODE() {
		return MERCH_REF_CODE;
	}

	/**
	 * @param mERCH_REF_CODE
	 *            the mERCH_REF_CODE to set
	 */
	public void setMERCH_REF_CODE(String mERCH_REF_CODE) {
		MERCH_REF_CODE = mERCH_REF_CODE;
	}

	/**
	 * @return the cORREL_ID
	 */
	public String getCORREL_ID() {
		return CORREL_ID;
	}

	/**
	 * @param cORREL_ID
	 *            the cORREL_ID to set
	 */
	public void setCORREL_ID(String cORREL_ID) {
		CORREL_ID = cORREL_ID;
	}
	
	

	/**
	 * @return the tranId
	 */
	public String getTranId() {
		return tranId;
	}

	/**
	 * @param tranId the tranId to set
	 */
	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((BLTO_CD == null) ? 0 : BLTO_CD.hashCode());
		result = prime * result + ((CORREL_ID == null) ? 0 : CORREL_ID.hashCode());
		result = prime * result + ((CUST_PO_ID == null) ? 0 : CUST_PO_ID.hashCode());
		result = prime * result + ((DIV_NM == null) ? 0 : DIV_NM.hashCode());
		result = prime * result + ((REQ_ID == null) ? 0 : REQ_ID.hashCode());
		result = prime * result + ((RESP_CD == null) ? 0 : RESP_CD.hashCode());
		result = prime * result + ((TRAN_AMT == null) ? 0 : TRAN_AMT.hashCode());
		result = prime * result + ((TRAN_DT == null) ? 0 : TRAN_DT.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionMessage other = (TransactionMessage) obj;
		if (BLTO_CD == null) {
			if (other.BLTO_CD != null)
				return false;
		} else if (!BLTO_CD.equals(other.BLTO_CD))
			return false;
		if (CORREL_ID == null) {
			if (other.CORREL_ID != null)
				return false;
		} else if (!CORREL_ID.equals(other.CORREL_ID))
			return false;
		if (CUST_PO_ID == null) {
			if (other.CUST_PO_ID != null)
				return false;
		} else if (!CUST_PO_ID.equals(other.CUST_PO_ID))
			return false;
		if (DIV_NM == null) {
			if (other.DIV_NM != null)
				return false;
		} else if (!DIV_NM.equals(other.DIV_NM))
			return false;
		if (REQ_ID == null) {
			if (other.REQ_ID != null)
				return false;
		} else if (!REQ_ID.equals(other.REQ_ID))
			return false;
		if (RESP_CD == null) {
			if (other.RESP_CD != null)
				return false;
		} else if (!RESP_CD.equals(other.RESP_CD))
			return false;
		if (TRAN_AMT == null) {
			if (other.TRAN_AMT != null)
				return false;
		} else if (!TRAN_AMT.equals(other.TRAN_AMT))
			return false;
		if (TRAN_DT == null) {
			if (other.TRAN_DT != null)
				return false;
		} else if (!TRAN_DT.equals(other.TRAN_DT))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionMessage [DIV_NM=" + DIV_NM + ", BLTO_CD=" + BLTO_CD + ", CUST_PO_ID=" + CUST_PO_ID
				+ ", SUBS_ID=" + SUBS_ID + ", REQ_ID=" + REQ_ID + ", RESP_CD=" + RESP_CD + ", RESP_DN=" + RESP_DN
				+ ", TRAN_TP_DN=" + TRAN_TP_DN + ", TRAN_DUAR_TM=" + TRAN_DUAR_TM + ", TRAN_DT=" + TRAN_DT
				+ ", BILL_CO_CD=" + BILL_CO_CD + ", TERM_CD=" + TERM_CD + ", MSG_TIEBACK=" + MSG_TIEBACK
				+ ", RESP_WRITE_STAT=" + RESP_WRITE_STAT + ", BLTO_SUBS_ID=" + BLTO_SUBS_ID + ", TRAN_AMT=" + TRAN_AMT
				+ ", MERCH_REF_CODE=" + MERCH_REF_CODE + ", CORREL_ID=" + CORREL_ID + ", CARD_TYPE=" + CARD_TYPE
				+ ", SUBS_CARD_TP_DN=" + SUBS_CARD_TP_DN + ", reqMsg=" + reqMsg + ", respMsg=" + respMsg + ", tranId="
				+ tranId + "]";
	}
	
}
