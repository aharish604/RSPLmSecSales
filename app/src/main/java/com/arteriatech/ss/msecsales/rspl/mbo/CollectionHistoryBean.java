package com.arteriatech.ss.msecsales.rspl.mbo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10526 on 28-04-2016.
 */
public class CollectionHistoryBean implements Serializable {
    String deviceNo = "";
    String deviceStatus = "";
    private String FIPDocNo = "";
    private String FIPDate = "";
    private String Amount = "";
    private String CPNo = "";
    private String PaymentModeDesc = "";
    private String PaymentModeID = "";
    private String FIPItemNo = "";
    private String InvoiceDate = "";
    private String InvoiceAmount = "";
    private String InvoiceClearedAmount = "";
    private String InvoiceBalanceAmount = "";
    private String dueDays = "";
    private String RetID = "";
    private String RetName = "";
    private String PaidAmt = "";
    private String ReferenceTypeDesc = "";
    private String ReferenceTypeID = "";
    private String TotalPaidAmount = "";
    private Boolean isDetailEnabled;
    private String CpMobileNo = "";
    private String CpName = "";
    private String CollTime = "";
    private String InvoiceNo = "";
    private String FIPGUID = "";
    private String InstrumentNo = "";
    private String InstrumentDate = "";
    private String Currency = "";
    private String BranchName = "";
    private String BankName = "";
    private String BankID = "";
    private String Remarks = "";
    private String payable = "";
    private String netPayable = "";
    private String CashDiscountPercentage = "";
    private String CashDiscount = "";
    private ArrayList<CollectionHistoryBean> alCollItemList = null;

    public String getRetID() {
        return RetID;
    }

    public void setRetID(String retID) {
        RetID = retID;
    }

    public String getRetName() {
        return RetName;
    }

    public void setRetName(String retName) {
        RetName = retName;
    }

    public String getPaidAmt() {
        return PaidAmt;
    }

    public void setPaidAmt(String paidAmt) {
        PaidAmt = paidAmt;
    }

    public String getReferenceTypeDesc() {
        return ReferenceTypeDesc;
    }

    public void setReferenceTypeDesc(String referenceTypeDesc) {
        ReferenceTypeDesc = referenceTypeDesc;
    }

    public String getTotalPaidAmount() {
        return TotalPaidAmount;
    }

    public void setTotalPaidAmount(String totalPaidAmount) {
        TotalPaidAmount = totalPaidAmount;
    }

    public Boolean getIsDetailEnabled() {
        return isDetailEnabled;
    }

    public void setIsDetailEnabled(Boolean isDetailEnabled) {
        this.isDetailEnabled = isDetailEnabled;
    }

    public String getCpMobileNo() {
        return CpMobileNo;
    }

    public void setCpMobileNo(String cpMobileNo) {
        CpMobileNo = cpMobileNo;
    }

    public String getCpName() {
        return CpName;
    }

    public void setCpName(String cpName) {
        CpName = cpName;
    }

    public String getCollTime() {
        return CollTime;
    }

    public void setCollTime(String collTime) {
        CollTime = collTime;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public String getFIPGUID() {
        return FIPGUID;
    }

    public void setFIPGUID(String FIPGUID) {
        this.FIPGUID = FIPGUID;
    }

    public String getInstrumentNo() {
        return InstrumentNo;
    }

    public void setInstrumentNo(String instrumentNo) {
        InstrumentNo = instrumentNo;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getInvoiceBalanceAmount() {
        return InvoiceBalanceAmount;
    }

    public void setInvoiceBalanceAmount(String invoiceBalanceAmount) {
        InvoiceBalanceAmount = invoiceBalanceAmount;
    }

    public String getFIPDocNo() {
        return FIPDocNo;
    }

    public void setFIPDocNo(String FIPDocNo) {
        this.FIPDocNo = FIPDocNo;
    }

    public String getFIPDate() {
        return FIPDate;
    }

    public void setFIPDate(String FIPDate) {
        this.FIPDate = FIPDate;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getPaymentModeDesc() {
        return PaymentModeDesc;
    }

    public void setPaymentModeDesc(String paymentModeDesc) {
        PaymentModeDesc = paymentModeDesc;
    }

    public String getPaymentModeID() {
        return PaymentModeID;
    }

    public void setPaymentModeID(String paymentModeID) {
        PaymentModeID = paymentModeID;
    }

    public String getFIPItemNo() {
        return FIPItemNo;
    }

    public void setFIPItemNo(String FIPItemNo) {
        this.FIPItemNo = FIPItemNo;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getInvoiceAmount() {
        return InvoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        InvoiceAmount = invoiceAmount;
    }

    public String getInvoiceClearedAmount() {
        return InvoiceClearedAmount;
    }

    public void setInvoiceClearedAmount(String invoiceClearedAmount) {
        InvoiceClearedAmount = invoiceClearedAmount;
    }

    public ArrayList<CollectionHistoryBean> getAlCollItemList() {
        return alCollItemList;
    }

    public void setAlCollItemList(ArrayList<CollectionHistoryBean> alCollItemList) {
        this.alCollItemList = alCollItemList;
    }

    public String getDueDays() {
        return dueDays;
    }

    public void setDueDays(String dueDays) {
        this.dueDays = dueDays;
    }

    public String getReferenceTypeID() {
        return ReferenceTypeID;
    }

    public void setReferenceTypeID(String referenceTypeID) {
        ReferenceTypeID = referenceTypeID;
    }

    public String getInstrumentDate() {
        return InstrumentDate;
    }

    public void setInstrumentDate(String instrumentDate) {
        InstrumentDate = instrumentDate;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getBankID() {
        return BankID;
    }

    public void setBankID(String bankID) {
        BankID = bankID;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getPayable() {
        return payable;
    }

    public void setPayable(String payable) {
        this.payable = payable;
    }

    public String getCashDiscountPercentage() {
        return CashDiscountPercentage;
    }

    public void setCashDiscountPercentage(String cashDiscountPercentage) {
        CashDiscountPercentage = cashDiscountPercentage;
    }

    public String getCashDiscount() {
        return CashDiscount;
    }

    public void setCashDiscount(String cashDiscount) {
        CashDiscount = cashDiscount;
    }

    public String getNetPayable() {
        return netPayable;
    }

    public void setNetPayable(String netPayable) {
        this.netPayable = netPayable;
    }
}
