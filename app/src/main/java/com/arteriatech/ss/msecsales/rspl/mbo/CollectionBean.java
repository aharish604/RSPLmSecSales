package com.arteriatech.ss.msecsales.rspl.mbo;

import java.io.Serializable;

/**
 * Created by e10526 on 26-04-2016.
 */
public class CollectionBean implements Serializable{
    String CPNo="";
    String BankID = "";
    String BankName = "";
    String InstrumentNo="";
    String Amount ="";
    String Remarks="";
    String FIPDocType="";
    String PaymentModeID="";
    String FIPDate="";
    String FIPGUID="";
    String BranchName = "";
    String URTNo = "";
    String RefTypeID = "";
    String CPGUID32 = "";
    String InvoiceAmount = "";
    String ParentTypeID = "";
    String ParentName = "";
    String comingFrom = "";
    String OutstandingAmount = "";
    String division = "";

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getOutstandingAmount() {
        return OutstandingAmount;
    }

    public void setOutstandingAmount(String outstandingAmount) {
        OutstandingAmount = outstandingAmount;
    }



    public String getComingFrom() {
        return comingFrom;
    }

    public void setComingFrom(String comingFrom) {
        this.comingFrom = comingFrom;
    }

    public String getCpUID() {
        return cpUID;
    }

    public void setCpUID(String cpUID) {
        this.cpUID = cpUID;
    }

    String cpUID = "";

    public String getDeviceNo() {
        return DeviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        DeviceNo = deviceNo;
    }

    String DeviceNo = "";
    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }
    public String getParentTypeID() {
        return ParentTypeID;
    }

    public void setParentTypeID(String parentTypeID) {
        ParentTypeID = parentTypeID;
    }

    public String getRouteSchGuid() {
        return RouteSchGuid;
    }

    public void setRouteSchGuid(String routeSchGuid) {
        RouteSchGuid = routeSchGuid;
    }

    String RouteSchGuid = "";

    public String getBundleTotalInvAmt() {
        return BundleTotalInvAmt;
    }

    public void setBundleTotalInvAmt(String bundleTotalInvAmt) {
        BundleTotalInvAmt = bundleTotalInvAmt;
    }

    String BundleTotalInvAmt = "";

    public double getAdvanceAmount() {
        return AdvanceAmount;
    }

    public void setAdvanceAmount(double advanceAmount) {
        AdvanceAmount = advanceAmount;
    }

    double AdvanceAmount = 0.0;
    public String getInvoiceAmount() {
        return InvoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        InvoiceAmount = invoiceAmount;
    }



    public String getCPGUID32() {
        return CPGUID32;
    }

    public void setCPGUID32(String CPGUID32) {
        this.CPGUID32 = CPGUID32;
    }



    public String getBeatGuid() {
        return BeatGuid;
    }

    public void setBeatGuid(String beatGuid) {
        BeatGuid = beatGuid;
    }

    String BeatGuid = "";

    public String getCPName() {
        return CPName;
    }

    public void setCPName(String CPName) {
        this.CPName = CPName;
    }

    String CPName = "";

    public String getUTRNo() {
        return UTRNo;
    }

    public void setUTRNo(String UTRNo) {
        this.UTRNo = UTRNo;
    }

    String UTRNo = "";

    public String getChequeDate() {
        return ChequeDate;
    }

    public void setChequeDate(String chequeDate) {
        ChequeDate = chequeDate;
    }

    String ChequeDate = "";

    public String getCollDate() {
        return CollDate;
    }

    public void setCollDate(String collDate) {
        CollDate = collDate;
    }

    String CollDate = "";

    public String getRefTypeID() {
        return RefTypeID;
    }

    public void setRefTypeID(String refTypeID) {
        RefTypeID = refTypeID;
    }

    public String getRefTypeDesc() {
        return RefTypeDesc;
    }

    public void setRefTypeDesc(String refTypeDesc) {
        RefTypeDesc = refTypeDesc;
    }

    String RefTypeDesc = "";

    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }

    String CPTypeID = "";

    public String getInstrumentDate() {
        return InstrumentDate;
    }

    public void setInstrumentDate(String instrumentDate) {
        InstrumentDate = instrumentDate;
    }

    String InstrumentDate = "";

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String Currency = "";
    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getURTNo() {
        return URTNo;
    }

    public void setURTNo(String URTNo) {
        this.URTNo = URTNo;
    }



    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getSpNo() {
        return SpNo;
    }

    public void setSpNo(String spNo) {
        SpNo = spNo;
    }

    public String getSpFirstName() {
        return SpFirstName;
    }

    public void setSpFirstName(String spFirstName) {
        SpFirstName = spFirstName;
    }

    String ParentID="";
    String SpNo="";
    String SpFirstName="";



    public String getPaymentModeDesc() {
        return PaymentModeDesc;
    }

    public void setPaymentModeDesc(String paymentModeDesc) {
        PaymentModeDesc = paymentModeDesc;
    }

    String PaymentModeDesc="";
    public String getSPGUID() {
        return SPGUID;
    }

    public void setSPGUID(String SPGUID) {
        this.SPGUID = SPGUID;
    }

    String SPGUID="";

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    String CPGUID="";


    public String getFIPGUID() {
        return FIPGUID;
    }

    public void setFIPGUID(String FIPGUID) {
        this.FIPGUID = FIPGUID;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getBankID() {
        return BankID;
    }

    public void setBankID(String bankID) {
        BankID = bankID;
    }

    public String getInstrumentNo() {
        return InstrumentNo;
    }

    public void setInstrumentNo(String instrumentNo) {
        InstrumentNo = instrumentNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getFIPDocType() {
        return FIPDocType;
    }

    public void setFIPDocType(String FIPDocType) {
        this.FIPDocType = FIPDocType;
    }

    public String getPaymentModeID() {
        return PaymentModeID;
    }

    public void setPaymentModeID(String paymentModeID) {
        PaymentModeID = paymentModeID;
    }

    public String getFIPDate() {
        return FIPDate;
    }

    public void setFIPDate(String FIPDate) {
        this.FIPDate = FIPDate;
    }


}
