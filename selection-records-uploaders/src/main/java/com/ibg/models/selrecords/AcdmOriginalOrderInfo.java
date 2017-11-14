package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_ORIGINAL_ORDER_INFO")
@IdName("OPR_ID")
@IdGenerator("ACDM_ORIGINAL_ORDER_INFO_SEQ .nextval")
public class AcdmOriginalOrderInfo extends Model {

}


