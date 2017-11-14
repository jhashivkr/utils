package com.ibg.selectionrecords.pojo;
// Generated Nov 14, 2013 3:33:55 PM by Hibernate Tools 3.2.1.GA


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * AcdmMarkAll generated by hbm2java
 */
public class AcdmMarkAll  implements java.io.Serializable {


     private BigDecimal markId;
     private BigDecimal listId;
     private String action;
     private BigDecimal quantity;
     private String userName;
     private String userEmailId;
     private BigDecimal targetListId;
     private String ismarkall;
     private String browseId;
     private String internalNote;
     private Serializable dateCreated;

    public AcdmMarkAll() {
    }

	
    public AcdmMarkAll(BigDecimal markId) {
        this.markId = markId;
    }
    public AcdmMarkAll(BigDecimal markId, BigDecimal listId, String action, BigDecimal quantity, String userName, String userEmailId, BigDecimal targetListId, String ismarkall, String browseId, String internalNote, Serializable dateCreated) {
       this.markId = markId;
       this.listId = listId;
       this.action = action;
       this.quantity = quantity;
       this.userName = userName;
       this.userEmailId = userEmailId;
       this.targetListId = targetListId;
       this.ismarkall = ismarkall;
       this.browseId = browseId;
       this.internalNote = internalNote;
       this.dateCreated = dateCreated;
    }
   
    public BigDecimal getMarkId() {
        return this.markId;
    }
    
    public void setMarkId(BigDecimal markId) {
        this.markId = markId;
    }
    public BigDecimal getListId() {
        return this.listId;
    }
    
    public void setListId(BigDecimal listId) {
        this.listId = listId;
    }
    public String getAction() {
        return this.action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    public BigDecimal getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserEmailId() {
        return this.userEmailId;
    }
    
    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }
    public BigDecimal getTargetListId() {
        return this.targetListId;
    }
    
    public void setTargetListId(BigDecimal targetListId) {
        this.targetListId = targetListId;
    }
    public String getIsmarkall() {
        return this.ismarkall;
    }
    
    public void setIsmarkall(String ismarkall) {
        this.ismarkall = ismarkall;
    }
    public String getBrowseId() {
        return this.browseId;
    }
    
    public void setBrowseId(String browseId) {
        this.browseId = browseId;
    }
    public String getInternalNote() {
        return this.internalNote;
    }
    
    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }
    public Serializable getDateCreated() {
        return this.dateCreated;
    }
    
    public void setDateCreated(Serializable dateCreated) {
        this.dateCreated = dateCreated;
    }




}


