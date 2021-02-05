package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

public class AddSampleDisbursementModel {

    String brandId="";
    String orderMaterialID="";
    String BrandDescription="";
    String orderMaterialDesc="";

    public String getBrandDescription() {
        return BrandDescription;
    }

    public void setBrandDescription(String brandDescription) {
        BrandDescription = brandDescription;
    }

    public String getOrderMaterialDesc() {
        return orderMaterialDesc;
    }

    public void setOrderMaterialDesc(String orderMaterialDesc) {
        this.orderMaterialDesc = orderMaterialDesc;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getOrderMaterialID() {
        return orderMaterialID;
    }

    public void setOrderMaterialID(String orderMaterialID) {
        this.orderMaterialID = orderMaterialID;
    }
}
