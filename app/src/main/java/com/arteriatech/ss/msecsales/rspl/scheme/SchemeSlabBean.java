package com.arteriatech.ss.msecsales.rspl.scheme;

/**
 * Created by e10769 on 28-03-2017.
 */

public class SchemeSlabBean {
    private String FromQty = "";
    private String ToQty = "";
    private String PayoutAmount = "";
    private String PayoutPerc = "";
    private String FreeQty = "";
    private String MaterialDesc = "";
    private String Currency = "";
    private String FreeQtyUOM = "";

    public String getScratchCardDesc() {
        return ScratchCardDesc;
    }

    public void setScratchCardDesc(String scratchCardDesc) {
        ScratchCardDesc = scratchCardDesc;
    }

    private String ScratchCardDesc = "";

    public String getFreeArticleDesc() {
        return FreeArticleDesc;
    }

    public void setFreeArticleDesc(String freeArticleDesc) {
        FreeArticleDesc = freeArticleDesc;
    }

    private String FreeArticleDesc = "";

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getFreeQtyUOM() {
        return FreeQtyUOM;
    }

    public void setFreeQtyUOM(String freeQtyUOM) {
        FreeQtyUOM = freeQtyUOM;
    }

    public String getFromQty() {
        return FromQty;
    }

    public void setFromQty(String fromQty) {
        FromQty = fromQty;
    }

    public String getToQty() {
        return ToQty;
    }

    public void setToQty(String toQty) {
        ToQty = toQty;
    }

    public String getPayoutAmount() {
        return PayoutAmount;
    }

    public void setPayoutAmount(String payoutAmount) {
        PayoutAmount = payoutAmount;
    }

    public String getPayoutPerc() {
        return PayoutPerc;
    }

    public void setPayoutPerc(String payoutPerc) {
        PayoutPerc = payoutPerc;
    }

    public String getFreeQty() {
        return FreeQty;
    }

    public void setFreeQty(String freeQty) {
        FreeQty = freeQty;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }
}
