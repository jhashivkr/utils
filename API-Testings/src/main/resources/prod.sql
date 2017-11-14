select * from prodCREATE TABLE "EB"."PROD"
(
   TTL_ID decimal(22) PRIMARY KEY NOT NULL,
   PROD_ID varchar2(30),
   PROD_DSPL_TP_CD varchar2(4),
   PROD_TP_CD varchar2(4),
   ISBN_ID varchar2(10),
   SKU_VEND_ID varchar2(20),
   MDIA_TP_CD varchar2(4),
   FRMT_TP_CD varchar2(4),
   UPC_ID varchar2(20),
   CONT_TP_CD varchar2(2),
   PRES_TP_CD varchar2(2),
   ADNC_AGE_LVL_MN_CD varchar2(2),
   PROD_SRC_ORIG_CD varchar2(4),
   TTL_DRVD_NM varchar2(150),
   ADNC_AGE_LVL_MX_CD varchar2(2),
   ITEM_CST_RTL_AMT decimal(7),
   TTL_ARTC_LDNG_NM varchar2(5),
   ITEM_CST_DISC_CD varchar2(5),
   ITEM_LIB_RTL_AMT decimal(7),
   ITEM_LIB_DISC_CD varchar2(5),
   IMPT_ID decimal(22),
   SJCG_DEWY_DEC_NBR varchar2(12),
   TTL_GRP_MTCH_NBR decimal(22),
   TTL_GRP_SUB_NBR decimal(22),
   ITEM_RTL_STK_CD varchar2(1),
   PROD_BSAC_ID varchar2(9),
   PROD_BSAC_2_ID varchar2(9),
   PUB_ALPH_ID varchar2(4),
   PUB_NUM_ID varchar2(4),
   PROD_BSAC_3_ID varchar2(9),
   TTL_PUB_DT timestamp,
   CTBR_ID decimal(22),
   CTBR_NM varchar2(256),
   PROD_ABBV_TAG_NBR varchar2(9),
   FTUR_LRGE_PRNT_IND varchar2(1),
   FTUR_ILTD_IND varchar2(1),
   SERS_ID decimal(22),
   SERS_ISSU_NBR varchar2(10),
   LANG_CD varchar2(3),
   FBP_BSAC_CD varchar2(2),
   MDIA_BSAC_CD varchar2(1),
   BSAC_CHLD_TP_CD varchar2(2),
   LCCN_ID varchar2(15),
   TTL_WT_MEAS decimal(8),
   TTL_HGT_MEAS decimal(4),
   TTL_WDTH_MEAS decimal(4),
   TTL_THCK_MEAS decimal(4),
   PROD_PRPK_IND varchar2(1),
   TTL_RTL_AVAL_DT timestamp,
   TTL_UNIT_TOT_CNT decimal(22),
   EDTN_NBR varchar2(4),
   TTL_EDTN_DN varchar2(256),
   ITEM_STRP_IND varchar2(1),
   RTL_APLC_IND varchar2(1),
   SJCG_MINR_SPAR_CD varchar2(2),
   SJCG_MJR_SPAR_CD varchar2(2),
   SJCG_ECPA_CD varchar2(9),
   SJCG_MJR_ECPA_CD varchar2(2),
   IMPT_NM varchar2(100),
   ITEM_IMPT_IND varchar2(1),
   ITEM_INDX_IND varchar2(1),
   ITEM_DSHP_ELIG_IND varchar2(1),
   ITEM_DSHP_REQ_IND varchar2(1),
   BOOK_IND varchar2(1),
   MUSC_IND varchar2(1),
   GIFT_IND varchar2(1),
   VI_IND varchar2(1),
   TTL_DRVD_NM_UC varchar2(150),
   TTL_ARTC_LDNG_UC varchar2(5),
   PUB_AVAL_STAT_CD varchar2(3),
   CART_UNIT_OPTI_CNT decimal(22),
   IMPT_NM_UC varchar2(100),
   PROD_PRPK_CD varchar2(1),
   ITEM_ID decimal(22),
   CUST_RTN_CD varchar2(1),
   CUST_RTN_DT timestamp,
   CUST_DISC_CD varchar2(1),
   ILS_ONLY_IND varchar2(1) DEFAULT 'N',
   KYWD char(1),
   PRDL_IND char(1) DEFAULT 'N',
   PRRN_INIT_UNIT_CNT decimal(22),
   SCHL_LIB_DISC_CD varchar2(5),
   TTL_FWRD_ID decimal(22),
   AR_MLTP_QUIZ_IND char(1),
   SRC_MLTP_QUIZ_IND char(1),
   EAN_ID char(13),
   ITEM_INTL_DISC_CD varchar2(5),
   RSTC_TTL_IND char(1),
   DGTL_IND char(1),
   MARC_IND char(1),
   VG_IND char(1) DEFAULT 'N',
   IKID_IND char(1) DEFAULT 'Y'
)
;
CREATE UNIQUE INDEX PROD_PK2 ON "EB"."PROD"(TTL_ID)
;
CREATE INDEX PROD_EAN_IDX ON "EB"."PROD"(EAN_ID)
;
CREATE INDEX PROD_IMPT_IDX ON "EB"."PROD"(IMPT_ID)
;
CREATE INDEX IDX$$_66E40001 ON "EB"."PROD"
(
  EAN_ID,
  TTL_DRVD_NM,
  TTL_ID
)
;
CREATE INDEX QUEST_SX_IDXB4F0749B2927CF59BB ON "EB"."PROD"
(
  PROD_BSAC_ID,
  SERS_ISSU_NBR
)
;
CREATE INDEX PROD_LANG_CD_IDX ON "EB"."PROD"(LANG_CD)
;
CREATE INDEX PROD_SKU_VEND_IDX ON "EB"."PROD"(SKU_VEND_ID)
;
CREATE INDEX PROD_TTL_PUB_DT_IDX ON "EB"."PROD"(TTL_PUB_DT)
;
CREATE INDEX PROD_SJCG_DEWY_IDX ON "EB"."PROD"(SJCG_DEWY_DEC_NBR)
;
CREATE INDEX PROD_UPC_IDX ON "EB"."PROD"(UPC_ID)
;
CREATE INDEX PROD_ISBN_IDX ON "EB"."PROD"(ISBN_ID)
;
CREATE INDEX PROD_TTL_DRVD_ARTC_IDX ON "EB"."PROD"
(
  TTL_DRVD_NM_UC,
  TTL_ARTC_LDNG_UC
)
;
CREATE INDEX PROD_TTL_DRVD_NM_UC_IDX ON "EB"."PROD"(TTL_DRVD_NM_UC)
;
CREATE INDEX PROD_KYWD_IDX ON "EB"."PROD"(KYWD)
;
CREATE INDEX PROD_TTL_GRP_MTCH_IDX ON "EB"."PROD"(TTL_GRP_MTCH_NBR)
;
CREATE INDEX PROD_ITEM_ID_IDX ON "EB"."PROD"(ITEM_ID)
;
CREATE INDEX PROD_ABBV_TAG_NBR_IDX ON "EB"."PROD"(PROD_ABBV_TAG_NBR)
;
CREATE INDEX PROD_EXTD_TTL_ORCL_TXT ON "EB"."PROD"(TTL_DRVD_NM)
;
