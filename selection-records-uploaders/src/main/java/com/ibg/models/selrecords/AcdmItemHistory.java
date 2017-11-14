package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_ITEM_HISTORY")
@IdName("HIST_ID")
@IdGenerator("ACDM_ITEM_HISTORY_SEQ.nextval")
public class AcdmItemHistory extends Model {

}
