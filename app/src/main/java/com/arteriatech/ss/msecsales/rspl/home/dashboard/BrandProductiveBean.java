package com.arteriatech.ss.msecsales.rspl.home.dashboard;

import java.util.ArrayList;

public class BrandProductiveBean {
    private String materialItemDesc = "";
    private String orderMatGrpDesc = "";
    private String quantity = "";
    private String toatlPrice = "";
    private String retailerCount = "";
    private String UOM = "";
    private String bagValue = "";
    private String carValue = "";
    private String pcValue = "";

    public String getPcValue() {
        return pcValue;
    }

    public void setPcValue(String pcValue) {
        this.pcValue = pcValue;
    }

    private ArrayList<String> retailerCountList = new ArrayList<>();

    public String getRetailerCount() {
        return retailerCount;
    }

    public void setRetailerCount(String retailerCount) {
        this.retailerCount = retailerCount;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getBagValue() {
        return bagValue;
    }

    public void setBagValue(String bagValue) {
        this.bagValue = bagValue;
    }

    public String getCarValue() {
        return carValue;
    }

    public void setCarValue(String carValue) {
        this.carValue = carValue;
    }

    public ArrayList<String> getRetailerCountList() {
        return retailerCountList;
    }

    public void setRetailerCountList(ArrayList<String> retailerCountList) {
        this.retailerCountList = retailerCountList;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    private boolean showProgress = false;

    public ArrayList<String> getMaterialItemList() {
        return materialItemList;
    }

    public void setMaterialItemList(ArrayList<String> materialItemList) {
        this.materialItemList = materialItemList;
    }

    private ArrayList<String> materialItemList = new ArrayList<>();

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    private String brandID = "";

    public String getMaterialItemDesc() {
        return materialItemDesc;
    }

    public void setMaterialItemDesc(String materialItemDesc) {
        this.materialItemDesc = materialItemDesc;
    }

    public String getOrderMatGrpDesc() {
        return orderMatGrpDesc;
    }

    public void setOrderMatGrpDesc(String orderMatGrpDesc) {
        this.orderMatGrpDesc = orderMatGrpDesc;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getToatlPrice() {
        return toatlPrice;
    }

    public void setToatlPrice(String toatlPrice) {
        this.toatlPrice = toatlPrice;
    }
}
