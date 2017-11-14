package ibg.cc.mq.writers;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.ibm.mq.constants.MQConstants;

/**
 *<B>(c) 2009 IngramBook. All rights reserved </B>
 *<B>Licensed material property of IngramBook</B><BR><BR>
 * 
 * <p>iSpeed constant variable common to e used by all classes<p>
 * e.g. public static final String C_<Contant name>
 * 
 * @author  Paul Maeng
 * @version 1.0 03/05/2009
 */
public interface IngConstants 
{
   // Global constants
   // COMM TYPE(ISPEED PATTERNS)
   public static final String C_i5MQFIFO = "i5MQFIFO";
   public static final String C_STDMQ = "STDMQ";
   public static final String C_TCPL = "TCPL";
   public static final String C_TCPC = "TCPC ";
   public static final String C_HTTP = "HTTP";
   public static final String C_HTTPS = "HTTPS";
   public static final String C_FILESWEEPER = "FILESWEEPER";
   public static final String C_XMODEM = "XMODEM";
   public static final String C_XMODEML = "XMODEML";
   public static final String C_XMODEMLT = "XMODEMLT";
   public static final String C_XMODEMCT = "XMODEMCT";
   public static final String C_BTREEL = "BTREEL";
   public static final String C_BTREEC = "BTREEC";
   
   public static final String C_USEDBOOK = "290";
   public static final String C_NEWBOOK ="978";
   public static final String C_BE_COMMTYPE = "BE_Comm_Type"; 
   public static final String C_FE_COMMTYPE = "FE_Comm_Type"; 
   public static final String C_FE = "FE";
   public static final String C_BE = "BE";

   public static final int C_RETRY = 3;
   public static final String TEST = "015";
   public static final String PROD = "014";
   public static final String TEST15 = "015";
   public static final String TEST17 = "017";
   public static final String PROD14 = "014";
   public static final String PROD16 = "016";
   public static final String C_PROD18 = "018";
   
   // @deprecated variables
   public static final String I_DEV = "DEV";
   public static final String I_TEST = "TEST";
   public static final String I_PROD = "PROD";
   
   public static final String C_DEV = "DEV";
   public static final String C_TEST = "TEST";
   public static final String C_PROD = "PROD";
   public static final String IDAY = "DAY";
   public static final String IWEEK = "WEEK";
   public static final String IMONTH = "MONTH";
   public static final String IYEAR = "YEAR";
   public static final String C_YMD8 = "YMD8";
   public static final String C_YMD6 = "YMD6";
   public static final String C_MDY8 = "MDY8";
   public static final String C_MDY6 = "MDY6";
   public static final String C_HMS = "HHMMSS";
   public static final String C_HMD = "HHMMDD"; //deprecated
   public static final String C_DATEFMT1 = "MM/dd/yyyy";
   public static final String C_DATEFMT2 = "yyyy-MM-dd";
   // 24hours HH
   public static final String C_DATETIME3 = "yyyy-MM-dd HH:mm:ss";
   // @depreciate  12hours hh
   public static final String C_DATETIME24 = "yyyy-MM-dd hh:mm:ss";
   public static final String C_DATEFORMAT24 = "yyyy-MM-dd HH:mm:ss";
   // 12hours hh
   public static final String C_DATETIME12 = "yyyy-MM-dd hh:mm:ss";
   public static final NumberFormat I_twoDigitfmtr = new DecimalFormat("00");
   public static final NumberFormat I_threeDigitfmtr = new DecimalFormat("000");
   public static final NumberFormat I_fourDigitfmtr = new DecimalFormat("0000");
   public static final String I_IBCDIST ="B";
   public static final String I_IPSDIST ="P";
   public static final String I_IPS ="IPS";
   public static final String I_IBC ="IBC";
   public static final int I_RETRY = 3; // depreciated
   
   // Track Constants
	public static final String I_ReqReceived = "R";
	public static final String I_ReqInterpreted = "I";
	public static final String I_ReqProcessing = "P";
	public static final String I_ReqRespAwaiting = "A";
	public static final String I_ReqResponseSent = "S";
	public static final String I_ReqResponseCB = "C";
	public static final String I_BarnAndNobFakeConfirm = "F";
	public static final String I_ReqSentQueue = "Q";
	// Track STAT	
	public static final String I_ReqInProcess = "P";
	public static final String I_ReqIsSatisfied = "S";
	public static final String I_ReqError = "E";
	public static final String I_ReqInRecovery = "R";
	public static final String I_ReqMultiple = "M";
	public static final String I_ReqNoEnd = "Z";

   public static final String C_ORDER = "ORDER";
   public static final String C_STOCKSTAT = "STOCKSTAT";
   public static final String C_SINGLESTAT = "SINGLESTAT";

   public static final String I_ReqOrder = "ORDER";
	public static final String I_ReqConfmAll = "CONFIRM";
	public static final String I_ReqConfmSpec = "CONFIRMSP";
	public static final String I_ReqStckStat = "STOCKSTAT";
	public static final String I_ReqEPSA = "EPSA";
	public static final String I_ReqEPS1 = "EPS1";
	public static final String I_ReqFile = "FILE";
	public static final String I_ReqSingStatus = "SINGLESTAT";
   
	// Added for EPS BISAC transfer from the mainframe
	public static final String I_TRANSFER = "TRANSFER";
	public static final String C_TRANSFER = "TRANSFER";

	public static final String BIGBISAC = "BIGBISAC";
   public static final String I_FLASHBACK = "FLASHBACK";
	public static final String SABISAC = "SABISAC";
	public static final String I_EPS = "EPS";
   public static final String I_ICEBERG = "ICEBERG";
   public static final String I_ROWBOAT = "ROWBOAT";

	public static final String I_MAINDOWN = "MAINDOWN.ERR";
   public static final String I_SYSDOWN = "SYSDOWN.ERR";
   public static final String I_FNF = "FNF.ERR";
   public static final String I_BADDATA = "BADDATA.ERR";
   public static final String I_INVACC = "INVACC.ERR";
   public static final String I_INVVER = "INVVER.ERR";
   public static final String I_NOCONF = "NOCONF.ERR";
   public static final String I_NOSAASTK = "NOSAASTK.ERR";
   public static final String I_POSTBACK = "POSTBACK.ERR";
   public static final String I_TRACKERR = "TRACKERROR.ERR";
   public static final String I_UKRT = "UKRT.ERR";
	
	public static final String I_XLATEIN = "XlateIn";
	public static final String I_XLATEOUT = "XlateOut";
	public static final String I_START = "Start";
	public static final String I_STATUS = "Status";
	public static final String I_SHUTDOWN = "Shutdown";
	// XModemFn constants
	public final static int I_HEADERSIZE = 3;
	public final static int I_PAYLOADSIZE = 128;
	public final static int I_CHECKDIGITSIZE = 1;
	public final static int I_CHECKDIGITOFFSET = 131;
	public final static int I_XMODEMPACKETLEN = 132;
	
	public final static int I_NULL = 0x00;
	public final static int I_SOH = 0x01;
	public final static int I_EOT = 0x04;
	public final static int I_ACK = 0x06;
	public final static int I_NAK = 0x15;
	public final static int I_NCG = 0x15;
	public final static int I_CAN = 0x18;
	public final static int I_SUB = 0x1a;
	public final static int I_C_NCG = 0x43;
    
	// Global MQ Constants
	public static int MQIConnAttmpts = 5;
	public static int MQIGetWaitSecs = 60;
	public static int MQI_SeqNo = 1;
	public PrintStream pout = System.out;
	public static final int MQI_c_CharacterSet = 500;
	public static final int MQI_c_Encoding  = 500;//546;
	public static final int MQI_Def_Encoding = MQConstants.MQENC_INTEGER_NORMAL | MQConstants.MQENC_DECIMAL_NORMAL | MQConstants.MQENC_FLOAT_IEEE_NORMAL;
	public static final int MQI_HeaderSize  = 88;
	public static final int MQI_BisacLength = 8968;
   public static final int MQI_FLAHSBACK_SEND = 8218;
   public static final int MQI_FLAHSBACK_RECV = 8976;
	public static final int MQI_NOOFATTEMPTS   = 5;
	public static final int MQI_CommToMainFrame   = 2;
	public static final int MQI_CommFromMainFrame = 3;
	public static final String MQI_c_ServerNum = "000";
	public static final String MQI_PUT = "PUT";
	public static final String MQI_ReqInterpreted = "I";
	public static final String MQI_ReqProcessing  = "P";
	public static final String MQI_ReqResponseSent = "S";
	public static final String MQI_ReqRespAwaiting = "A";
	public static final String MQI_ReqInProcess = "P";
	public static final String MQI_ReqIsSatisfied = "S";
	public static final String MQI_ReqInRecovery = "R";
	public static final String MQI_TESTSRV = "015";
	public static final String MQI_PRODSRV = "014";
	public static final String MQI_IBM037 = "IBM037";
	public static final int MQI_DATALENGTH24 = 24;
   public static final int MQI_DATALENGTH32 = 32;
	
	// MQPUT
	public static final int MQIPutOptions = MQConstants.MQOO_OUTPUT + MQConstants.MQOO_SET_ALL_CONTEXT + MQConstants.MQOO_FAIL_IF_QUIESCING + MQConstants.MQOO_INQUIRE;
	
	public static final int MQIPutMsgOption= MQConstants.MQPMO_NONE + MQConstants.MQPMO_SYNCPOINT;
	public static final int MQIPutMsgOptio2= MQConstants.MQPMO_SYNCPOINT + MQConstants.MQPMO_SET_IDENTITY_CONTEXT;
	public static final int MQINEWMPMO= MQConstants.MQPMO_NEW_MSG_ID + MQConstants.MQPMO_NEW_CORREL_ID + MQConstants.MQPMO_SYNCPOINT; 
	public static final int MQIOpenPutStat = 1;
	
	// MQGET
	public static final String MQI_GET  = "READDESTRUCT";
	public static final String MQI_RPLY = "READREPLY";
	public static final String MQI_READNON = "READNONDESTRUCT";
	public static final int SERVERTYPE  = 3;
	public static final int m_constMatchMsgID = 1;
	public static final int m_constMatchCorID = 2;
	public static final int MQIGetOptions  = MQConstants.MQOO_INPUT_SHARED + MQConstants.MQOO_FAIL_IF_QUIESCING + MQConstants.MQOO_INQUIRE;
	//public static final int MQIGetOptionsNon=MQConstants.MQOO_INPUT_SHARED + MQConstants.MQOO_BROWSE + MQConstants.MQOO_FAIL_IF_QUIESCING + MQConstants.MQOO_INQUIRE;
	public static final int MQIGetOptionsNon = MQConstants.MQOO_BROWSE + MQConstants.MQOO_INQUIRE;
	public static final int MQIGetMsgOption= MQConstants.MQGMO_WAIT + MQConstants.MQGMO_FAIL_IF_QUIESCING + MQConstants.MQGMO_SYNCPOINT;
	public static final int MQIOpenGetStat = 2;
	
	public static final int MQIOpenGet2Stat = 3;
	
	// Reporting
	public static final String ISPEED = "ISpeed";
	public static final String EDI = "EDI";
   public static final String EDIFY = "EDIFY";
   public static final String CREDIT = "CREDIT";	
   public static final String IPWS = "IPWS";
   public static final String IDSWEB = "IDSWEB";
   public static final String STOCK = "STOCK";
   
   public static final String C_ASCII = "ASCII";
   public static final String C_EBCDIC = "EBCDIC";
   
   // HTTTP Communication contants
   public static final String C_PUT = "PUT";
   public static final String C_POST = "POST";
   public static final String C_GET = "GET";
   
   public static final String C_YES = "Y";
   public static final String C_NO = "N";
}
