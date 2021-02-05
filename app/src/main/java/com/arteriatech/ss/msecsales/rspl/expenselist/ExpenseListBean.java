package com.arteriatech.ss.msecsales.rspl.expenselist;

import android.os.Parcel;
import android.os.Parcelable;
public class ExpenseListBean implements Parcelable {
    private String expenseNo="";
    private String expenseType="";
    private String expenseTypeDesc="";
    private String amount;
    private String date;
    private String currency;
    private String expenseGUID36;
    private String expenseGUID32;
    String CPType="";
    String deviceNo;

    protected ExpenseListBean(Parcel in) {
        expenseNo = in.readString();
        expenseType = in.readString();
        expenseTypeDesc = in.readString();
        amount = in.readString();
        date = in.readString();
        currency = in.readString();
        expenseGUID36 = in.readString();
        expenseGUID32 = in.readString();
        CPType = in.readString();
        deviceNo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(expenseNo);
        dest.writeString(expenseType);
        dest.writeString(expenseTypeDesc);
        dest.writeString(amount);
        dest.writeString(date);
        dest.writeString(currency);
        dest.writeString(expenseGUID36);
        dest.writeString(expenseGUID32);
        dest.writeString(CPType);
        dest.writeString(deviceNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExpenseListBean> CREATOR = new Creator<ExpenseListBean>() {
        @Override
        public ExpenseListBean createFromParcel(Parcel in) {
            return new ExpenseListBean(in);
        }

        @Override
        public ExpenseListBean[] newArray(int size) {
            return new ExpenseListBean[size];
        }
    };

    public String getCPType() {
        return CPType;
    }

    public void setCPType(String CPType) {
        this.CPType = CPType;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public ExpenseListBean()
    {

    }

    public String getCPTypeDesc() {
        return CPType;
    }

    public void setCPTypeDesc(String CPType) {
        this.CPType = CPType;
    }

    public String getExpenseTypeDesc() {
        return expenseTypeDesc;
    }

    public void setExpenseTypeDesc(String expenseTypeDesc) {
        this.expenseTypeDesc = expenseTypeDesc;
    }

    public String getExpenseGUID36() {
        return expenseGUID36;
    }

    public void setExpenseGUID36(String expenseGUID36) {
        this.expenseGUID36 = expenseGUID36;
    }

    public String getExpenseGUID32() {
        return expenseGUID32;
    }

    public void setExpenseGUID32(String expenseGUID32) {
        this.expenseGUID32 = expenseGUID32;
    }
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExpenseNo() {
        return expenseNo;
    }

    public void setExpenseNo(String expenseNo) {
        this.expenseNo = expenseNo;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
