package com.arteriatech.ss.msecsales.rspl.mbo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * Created by ${e10604} on ${27/4/2016}.
 *
 */
public class OutstandingBean implements Serializable{

    String invoiceNo;
    String invoiceDate;
    String invoiceAmount;
    private String invoiceBalanceAmount;
    String invoiceStatus;

    public String getHigherLevelItm() {
        return HigherLevelItm;
    }

    public void setHigherLevelItm(String higherLevelItm) {
        HigherLevelItm = higherLevelItm;
    }

    String HigherLevelItm="";
    String ItemInvoiceStatus;
    String matDesc;

    public String getZZIsBomMaterial() {
        return ZZIsBomMaterial;
    }

    public void setZZIsBomMaterial(String ZZIsBomMaterial) {
        this.ZZIsBomMaterial = ZZIsBomMaterial;
    }

    String ZZIsBomMaterial="";
    String matCode;
    String collectionAmount;
    private String OrderMatGrpDesc;
    private String InvoiceTypeID="";

    public String getItemInvoiceStatus() {
        return ItemInvoiceStatus;
    }

    public void setItemInvoiceStatus(String itemInvoiceStatus) {
        ItemInvoiceStatus = itemInvoiceStatus;
    }

    public String getDueDays() {
        return dueDays;
    }

    public void setDueDays(String dueDays) {
        this.dueDays = dueDays;
    }

    private  String dueDays="";

    public String getInvoiceTypeID() {
        return InvoiceTypeID;
    }

    public void setInvoiceTypeID(String invoiceTypeID) {
        InvoiceTypeID = invoiceTypeID;
    }

    public String getInvoiceTypeDesc() {
        return InvoiceTypeDesc;
    }

    public void setInvoiceTypeDesc(String invoiceTypeDesc) {
        InvoiceTypeDesc = invoiceTypeDesc;
    }

    private String InvoiceTypeDesc="";

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    private String MaterialDesc="";

    public String getDevCollAmount() {
        return devCollAmount;
    }

    public void setDevCollAmount(String devCollAmount) {
        this.devCollAmount = devCollAmount;
    }
    String DueDateStatus = "";
    String devCollAmount = "";
    String UOM = "";
    String Quantity = "";

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDueDateStatus() {
        return DueDateStatus;
    }

    public void setDueDateStatus(String dueDateStatus) {
        DueDateStatus = dueDateStatus;
    }



    public Boolean getIsDetailEnabled() {
        return isDetailEnabled;
    }

    public void setIsDetailEnabled(Boolean isDetailEnabled) {
        this.isDetailEnabled = isDetailEnabled;
    }

    Boolean isDetailEnabled;
    public String getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(String collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public String getCpGuid() {
        return cpGuid;
    }

    public void setCpGuid(String cpGuid) {
        this.cpGuid = cpGuid;
    }

    String cpGuid ="";

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    String deviceNo = "";

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    String deviceStatus = "";

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    String itemNo = "";

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    String uom="";

    public String getInvoiceGuid() {
        return invoiceGuid;
    }

    public void setInvoiceGuid(String invoiceGuid) {
        this.invoiceGuid = invoiceGuid;
    }

    String invoiceGuid;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    String currency;


    public String getMatDesc() {
        return matDesc;
    }

    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
    }

    public String getInvQty() {
        return invQty;
    }

    public void setInvQty(String invQty) {
        this.invQty = invQty;
    }



    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    String invQty;

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }


    public String getOrderMatGrpDesc() {
        return OrderMatGrpDesc;
    }

    public void setOrderMatGrpDesc(String orderMatGrpDesc) {
        OrderMatGrpDesc = orderMatGrpDesc;
    }
    private ArrayList<OutstandingBean> alItemList = new ArrayList<>();

    public ArrayList<OutstandingBean> getAlItemList() {
        return alItemList;
    }

    public void setAlItemList(ArrayList<OutstandingBean> alItemList) {
        this.alItemList = alItemList;
    }

    public String getInvoiceBalanceAmount() {
        return invoiceBalanceAmount;
    }

    public void setInvoiceBalanceAmount(String invoiceBalanceAmount) {
        this.invoiceBalanceAmount = invoiceBalanceAmount;
    }
}
