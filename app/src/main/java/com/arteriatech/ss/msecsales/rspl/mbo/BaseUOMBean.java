package com.arteriatech.ss.msecsales.rspl.mbo;

/**
 * Created by e10526 on 08-06-2018.
 */

public class BaseUOMBean {
    private String UOM = "";

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    private String MaterialNo = "";

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getAlternativeUOM2() {
        return AlternativeUOM2;
    }

    public void setAlternativeUOM2(String alternativeUOM2) {
        AlternativeUOM2 = alternativeUOM2;
    }

    public String getAlternativeUOM2Num() {
        return AlternativeUOM2Num;
    }

    public void setAlternativeUOM2Num(String alternativeUOM2Num) {
        AlternativeUOM2Num = alternativeUOM2Num;
    }

    public String getAlternativeUOM2Den() {
        return AlternativeUOM2Den;
    }

    public void setAlternativeUOM2Den(String alternativeUOM2Den) {
        AlternativeUOM2Den = alternativeUOM2Den;
    }

    public String getAlternativeUOM1() {
        return AlternativeUOM1;
    }

    public void setAlternativeUOM1(String alternativeUOM1) {
        AlternativeUOM1 = alternativeUOM1;
    }

    public String getAlternativeUOM1Num() {
        return AlternativeUOM1Num;
    }

    public void setAlternativeUOM1Num(String alternativeUOM1Num) {
        AlternativeUOM1Num = alternativeUOM1Num;
    }

    public String getAlternativeUOM1Den() {
        return AlternativeUOM1Den;
    }

    public void setAlternativeUOM1Den(String alternativeUOM1Den) {
        AlternativeUOM1Den = alternativeUOM1Den;
    }

    private String AlternativeUOM2 = "";
    private String AlternativeUOM2Num = "";
    private String AlternativeUOM2Den = "";
    private String AlternativeUOM1 = "";
    private String AlternativeUOM1Num = "";
    private String AlternativeUOM1Den = "";
}
