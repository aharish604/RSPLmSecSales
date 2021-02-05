package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

public class WindowDisplayCreateBean{
    String remarks="";
    String length="";
    String width="";
    String height="";
    String SizeType="";
    String SizeTypeId="";
    String promocode="";
    String displayType="";
    String Invoicedate="";

    public String getSchemeId() {
        return SchemeId;
    }

    public void setSchemeId(String schemeId) {
        SchemeId = schemeId;
    }

    String SchemeId="";

    public String getInvoicedate() {
        return Invoicedate;
    }

    public void setInvoicedate(String invoicedate) {
        Invoicedate = invoicedate;
    }

    public String getSizeTypeId() {
        return SizeTypeId;
    }

    public void setSizeTypeId(String sizeTypeId) {
        SizeTypeId = sizeTypeId;
    }

    public String getDisplayTypeId() {
        return displayTypeId;
    }

    public void setDisplayTypeId(String displayTypeId) {
        this.displayTypeId = displayTypeId;
    }

    String displayTypeId;

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSizeType() {
        return SizeType;
    }

    public void setSizeType(String sizeType) {
        SizeType = sizeType;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }
}
