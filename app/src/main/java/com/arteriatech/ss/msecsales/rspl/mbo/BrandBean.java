package com.arteriatech.ss.msecsales.rspl.mbo;

/**
 * Created by e10769 on 19-05-2018.
 */

public class BrandBean {
    private String BrandID = "";
    private String BrandDesc = "";

    public String getBrandID() {
        return BrandID;
    }

    public void setBrandID(String brandID) {
        BrandID = brandID;
    }

    public String getBrandDesc() {
        return BrandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        BrandDesc = brandDesc;
    }

    @Override
    public String toString() {
        return BrandDesc;
    }
}
