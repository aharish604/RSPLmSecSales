package com.arteriatech.ss.msecsales.rspl.complaintlist;

import android.os.Parcel;
import android.os.Parcelable;

public class ComplaintListModel implements Parcelable {

    String ComplaintNo;
    String ComplaintTypeID;
    String ComplaintCategoryID;
    String ComplaintTypeDesc;
    String ComplaintDate;
    String ComplaintPriorityDesc;
    String Remarks;
    String ComplainCategoryDesc;
    String Batch;
    String OrderMaterialGroupDesc;
    String MaterialDesc;
    String  quantiity;


    protected ComplaintListModel(Parcel in) {
        ComplaintNo = in.readString();
        ComplaintTypeID = in.readString();
        ComplaintCategoryID = in.readString();
        ComplaintTypeDesc = in.readString();
        ComplaintDate = in.readString();
        ComplaintPriorityDesc = in.readString();
        Remarks = in.readString();
        ComplainCategoryDesc = in.readString();
        Batch = in.readString();
        OrderMaterialGroupDesc = in.readString();
        MaterialDesc = in.readString();
        quantiity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ComplaintNo);
        dest.writeString(ComplaintTypeID);
        dest.writeString(ComplaintCategoryID);
        dest.writeString(ComplaintTypeDesc);
        dest.writeString(ComplaintDate);
        dest.writeString(ComplaintPriorityDesc);
        dest.writeString(Remarks);
        dest.writeString(ComplainCategoryDesc);
        dest.writeString(Batch);
        dest.writeString(OrderMaterialGroupDesc);
        dest.writeString(MaterialDesc);
        dest.writeString(quantiity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ComplaintListModel> CREATOR = new Creator<ComplaintListModel>() {
        @Override
        public ComplaintListModel createFromParcel(Parcel in) {
            return new ComplaintListModel(in);
        }

        @Override
        public ComplaintListModel[] newArray(int size) {
            return new ComplaintListModel[size];
        }
    };

    public String getQuantiity() {
        return quantiity;
    }

    public void setQuantiity(String quantiity) {
        this.quantiity = quantiity;
    }

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public ComplaintListModel() {
    }

    public String getComplaintNo() {
        return ComplaintNo;
    }

    public void setComplaintNo(String complaintNo) {
        ComplaintNo = complaintNo;
    }
    public String getComplaintCategoryID() {
        return ComplaintCategoryID;
    }

    public void setComplaintCategoryID(String complaintCategoryID) {
        ComplaintCategoryID = complaintCategoryID;
    }
    public String getComplaintTypeID() {
        return ComplaintTypeID;
    }

    public void setComplaintTypeID(String complaintTypeID) {
        ComplaintTypeID = complaintTypeID;
    }

    public String getComplaintTypeDesc() {
        return ComplaintTypeDesc;
    }

    public void setComplaintTypeDesc(String complaintTypeDesc) {
        ComplaintTypeDesc = complaintTypeDesc;
    }

    public String getComplaintDate() {
        return ComplaintDate;
    }

    public void setComplaintDate(String complaintDate) {
        ComplaintDate = complaintDate;
    }

    public String getComplaintPriorityDesc() {
        return ComplaintPriorityDesc;
    }

    public void setComplaintPriorityDesc(String complaintPriorityDesc) {
        ComplaintPriorityDesc = complaintPriorityDesc;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getComplainCategoryDesc() {
        return ComplainCategoryDesc;
    }

    public void setComplainCategoryDesc(String complainCategoryDesc) {
        ComplainCategoryDesc = complainCategoryDesc;
    }
}
