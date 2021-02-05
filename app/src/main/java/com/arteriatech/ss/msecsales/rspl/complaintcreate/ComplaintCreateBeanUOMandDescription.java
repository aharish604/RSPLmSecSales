package com.arteriatech.ss.msecsales.rspl.complaintcreate;

import java.util.ArrayList;

public class ComplaintCreateBeanUOMandDescription {

    private String mStrSelectedCRSItem="";
    private String mStrSelectedCRSItemDesc="";
    private String displayData="";

    public String getSelectedUOM() {
        return selectedUOM;
    }

    public void setSelectedUOM(String selectedUOM) {
        this.selectedUOM = selectedUOM;
    }

    String selectedUOM;





    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    private String UOM="";

    public String getAlternativeUOM1() {
        return AlternativeUOM1;
    }

    public void setAlternativeUOM1(String alternativeUOM1) {
        AlternativeUOM1 = alternativeUOM1;
    }

    private String AlternativeUOM1="";

    public ArrayList<String> getUOMList() {
        return UOMList;
    }

    public void setUOMList(ArrayList<String> UOMList) {
        this.UOMList = UOMList;
    }

    private ArrayList<String> UOMList;

    public String getmStrSelectedCRSItem() {
        return mStrSelectedCRSItem;
    }

    public void setmStrSelectedCRSItem(String mStrSelectedCRSItem) {
        this.mStrSelectedCRSItem = mStrSelectedCRSItem;
    }

    public String getmStrSelectedCRSItemDesc() {
        return mStrSelectedCRSItemDesc;
    }

    public void setmStrSelectedCRSItemDesc(String mStrSelectedCRSItemDesc) {
        this.mStrSelectedCRSItemDesc = mStrSelectedCRSItemDesc;
    }

    @Override
    public String toString() {
        return mStrSelectedCRSItemDesc.toString();
    }

    public String getDisplayData() {
        return displayData;
    }

    public void setDisplayData(String displayData) {
        this.displayData = displayData;
    }

}
