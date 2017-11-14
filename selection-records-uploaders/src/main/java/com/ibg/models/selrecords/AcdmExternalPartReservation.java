package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_EXTERNAL_PART_RESERVATION")
@IdName("OPR_ID")
@IdGenerator("ACDM_EXTERNAL_PART_RESERV_SEQ.nextval")
public class AcdmExternalPartReservation  extends Model {

}


