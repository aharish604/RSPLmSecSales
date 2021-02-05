package com.arteriatech.ss.msecsales.rspl.expenselist.expenselistdetails;

import java.util.ArrayList;

public class ExpenseDetailsBean {
    String locationDetailsDesc;
    String beatDistance;
    String conveniencemodedesc;
    String ammount;
    String expenseItemType;
    private String expenseItemTypeDesc;
    String currency;
    String UOM;
    String expenseGUID36;
    String expenseGuid32;
    private ArrayList<DocumentBean>documentBeanslist=null;

    public ArrayList<DocumentBean> getDocumentBeanslist() {
        return documentBeanslist;
    }

    public void setDocumentBeanslist(ArrayList<DocumentBean> documentBeanslist) {
        this.documentBeanslist = documentBeanslist;
    }

    public String getExpenseGUID36() {
        return expenseGUID36;
    }

    public void setExpenseGUID36(String expenseGUID36) {
        this.expenseGUID36 = expenseGUID36;
    }

    public String getExpenseGuid32() {
        return expenseGuid32;
    }

    public void setExpenseGuid32(String expenseGuid32) {
        this.expenseGuid32 = expenseGuid32;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBeatDistance() {
        return beatDistance;
    }

    public void setBeatDistance(String beatDistance) {
        this.beatDistance = beatDistance;
    }

    public String getConveniencemodedesc() {
        return conveniencemodedesc;
    }

    public void setConveniencemodedesc(String conveniencemodedesc) {
        this.conveniencemodedesc = conveniencemodedesc;
    }

    public String getAmmount() {
        return ammount;
    }

    public void setAmmount(String ammount) {
        this.ammount = ammount;
    }

    public String getExpenseItemType() {
        return expenseItemType;
    }

    public void setExpenseItemType(String expenseItemType) {
        this.expenseItemType = expenseItemType;
    }

    public String getLocationDetailsDesc() {
        return locationDetailsDesc;
    }

    public void setLocationDetailsDesc(String locationDetailsDesc) {
        this.locationDetailsDesc = locationDetailsDesc;
    }

    public String getExpenseItemTypeDesc() {
        return expenseItemTypeDesc;
    }

    public void setExpenseItemTypeDesc(String expenseItemTypeDesc) {
        this.expenseItemTypeDesc = expenseItemTypeDesc;
    }
}
