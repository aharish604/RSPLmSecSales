package com.arteriatech.ss.msecsales.rspl.mbo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ROCreateBean implements Parcelable{
    // data members
    private String CPStockItemGUID = "";
    private String MaterialDesc = "";
    private String MaterialNo = "";
    private String QAQty = "";

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    private String division = "";
    private String BlockedQty = "";
    private String StockValue = "";
    private String LandingPrice = "";
    private String OrderMaterialGroupID = "";
    private String OrderMaterialGroupDesc = "";
    private String OrderNo;
    private String OrderDate;
    private String SSROGUID;
    private String NetAmount;
    private String StatusID;
    private Boolean IsDetailEnabled = false;
    private Boolean IsSelected = false;
    private String sItemNo;
    private String deviceNo;
    private String Brand = "";
    private String ProductCategoryID = "";
    private String CPGUID32 = "";
    private String SPGUID = "";
    private String CPGUID = "";
    private String CPNo = "";
    private String CPUID = "";
    private String CPName = "";
    private ArrayList<ReturnOrderBean> roList = null;
    private String StackOwner;
    private String comingFrom ="";

    protected ROCreateBean(Parcel in) {
        CPStockItemGUID = in.readString();
        MaterialDesc = in.readString();
        MaterialNo = in.readString();
        QAQty = in.readString();
        BlockedQty = in.readString();
        StockValue = in.readString();
        LandingPrice = in.readString();
        OrderMaterialGroupID = in.readString();
        OrderMaterialGroupDesc = in.readString();
        OrderNo = in.readString();
        OrderDate = in.readString();
        SSROGUID = in.readString();
        NetAmount = in.readString();
        StatusID = in.readString();
        byte tmpIsDetailEnabled = in.readByte();
        IsDetailEnabled = tmpIsDetailEnabled == 0 ? null : tmpIsDetailEnabled == 1;
        byte tmpIsSelected = in.readByte();
        IsSelected = tmpIsSelected == 0 ? null : tmpIsSelected == 1;
        sItemNo = in.readString();
        deviceNo = in.readString();
        Brand = in.readString();
        ProductCategoryID = in.readString();
        CPGUID32 = in.readString();
        SPGUID = in.readString();
        CPGUID = in.readString();
        CPNo = in.readString();
        CPUID = in.readString();
        CPName = in.readString();
        roList = in.createTypedArrayList(ReturnOrderBean.CREATOR);
        StackOwner = in.readString();
        comingFrom = in.readString();
        comingFromInv = in.readString();
        division = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CPStockItemGUID);
        dest.writeString(MaterialDesc);
        dest.writeString(MaterialNo);
        dest.writeString(QAQty);
        dest.writeString(BlockedQty);
        dest.writeString(StockValue);
        dest.writeString(LandingPrice);
        dest.writeString(OrderMaterialGroupID);
        dest.writeString(OrderMaterialGroupDesc);
        dest.writeString(OrderNo);
        dest.writeString(OrderDate);
        dest.writeString(SSROGUID);
        dest.writeString(NetAmount);
        dest.writeString(StatusID);
        dest.writeByte((byte) (IsDetailEnabled == null ? 0 : IsDetailEnabled ? 1 : 2));
        dest.writeByte((byte) (IsSelected == null ? 0 : IsSelected ? 1 : 2));
        dest.writeString(sItemNo);
        dest.writeString(deviceNo);
        dest.writeString(Brand);
        dest.writeString(ProductCategoryID);
        dest.writeString(CPGUID32);
        dest.writeString(SPGUID);
        dest.writeString(CPGUID);
        dest.writeString(CPNo);
        dest.writeString(CPUID);
        dest.writeString(CPName);
        dest.writeTypedList(roList);
        dest.writeString(StackOwner);
        dest.writeString(comingFrom);
        dest.writeString(comingFromInv);
        dest.writeString(division);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ROCreateBean> CREATOR = new Creator<ROCreateBean>() {
        @Override
        public ROCreateBean createFromParcel(Parcel in) {
            return new ROCreateBean(in);
        }

        @Override
        public ROCreateBean[] newArray(int size) {
            return new ROCreateBean[size];
        }
    };

    public String getComingFromInv() {
        return comingFromInv;
    }

    public void setComingFromInv(String comingFromInv) {
        this.comingFromInv = comingFromInv;
    }

    private String comingFromInv ="";

    // default constructor
    public ROCreateBean() {
    }

    // setters and getters
    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getQAQty() {
        return QAQty;
    }

    public void setQAQty(String QAQty) {
        this.QAQty = QAQty;
    }

    public String getBlockedQty() {
        return BlockedQty;
    }

    public void setBlockedQty(String blockedQty) {
        BlockedQty = blockedQty;
    }

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getLandingPrice() {
        return LandingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        LandingPrice = landingPrice;
    }

    public String getOrderMaterialGroupID() {
        return OrderMaterialGroupID;
    }

    public void setOrderMaterialGroupID(String orderMaterialGroupID) {
        OrderMaterialGroupID = orderMaterialGroupID;
    }

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getSSROGUID() {
        return SSROGUID;
    }

    public void setSSROGUID(String SSROGUID) {
        this.SSROGUID = SSROGUID;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getStatusID() {
        return StatusID;
    }

    public void setStatusID(String statusID) {
        StatusID = statusID;
    }

    public Boolean getDetailEnabled() {
        return IsDetailEnabled;
    }

    public void setDetailEnabled(Boolean detailEnabled) {
        IsDetailEnabled = detailEnabled;
    }

    public String getsItemNo() {
        return sItemNo;
    }

    public void setsItemNo(String sItemNo) {
        this.sItemNo = sItemNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getProductCategoryID() {
        return ProductCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        ProductCategoryID = productCategoryID;
    }

    public String getCPGUID32() {
        return CPGUID32;
    }

    public void setCPGUID32(String CPGUID32) {
        this.CPGUID32 = CPGUID32;
    }

    public String getSPGUID() {
        return SPGUID;
    }

    public void setSPGUID(String SPGUID) {
        this.SPGUID = SPGUID;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getCPUID() {
        return CPUID;
    }

    public void setCPUID(String CPUID) {
        this.CPUID = CPUID;
    }

    public String getCPName() {
        return CPName;
    }

    public void setCPName(String CPName) {
        this.CPName = CPName;
    }

    public ArrayList<ReturnOrderBean> getRoList() {
        return roList;
    }

    public void setRoList(ArrayList<ReturnOrderBean> roList) {
        this.roList = roList;
    }

    public String getStackOwner() {
        return StackOwner;
    }

    public void setStackOwner(String stackOwner) {
        StackOwner = stackOwner;
    }



    public String getComingFrom() {
        return comingFrom;
    }

    public void setComingFrom(String comingFrom) {
        this.comingFrom = comingFrom;
    }



    public Boolean getSelected() {
        return IsSelected;
    }

    public void setSelected(Boolean selected) {
        IsSelected = selected;
    }

}
