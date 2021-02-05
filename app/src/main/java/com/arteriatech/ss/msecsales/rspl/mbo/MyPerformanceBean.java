package com.arteriatech.ss.msecsales.rspl.mbo;

/**
 * Created by ${e10526} on ${23-06-2016}.
 *
 */
public class MyPerformanceBean {
   private String MaterialNo="";
    private String MaterialDesc="";
    private String AmtLMTD="";
    private String AmtMTD="";
    private String AmtMonth1PrevPerf="";
    private String AmtMonth2PrevPerf="";
    private String AmtMonth3PrevPerf="";
    private String GrPer="";
    private String QtyMonth1PrevPerf="";
    private String QtyMonth2PrevPerf="";
    private String QtyMonth3PrevPerf="";
    private String ReportOnID="";
    private String MaterialGroupID="";

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    private String Currency="";

    public String getMaterialGroupID() {
        return MaterialGroupID;
    }

    public void setMaterialGroupID(String materialGroupID) {
        MaterialGroupID = materialGroupID;
    }

    public String getMaterialGroupDesc() {
        return MaterialGroupDesc;
    }

    public void setMaterialGroupDesc(String materialGroupDesc) {
        MaterialGroupDesc = materialGroupDesc;
    }

    private String MaterialGroupDesc="";

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    private String UOM="";

    public String getBTD() {
        return BTD;
    }

    public void setBTD(String BTD) {
        this.BTD = BTD;
    }

    private String BTD="";

    public String getGrPer() {
        return GrPer;
    }

    public void setGrPer(String grPer) {
        GrPer = grPer;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getAmtLMTD() {
        return AmtLMTD;
    }

    public void setAmtLMTD(String amtLMTD) {
        AmtLMTD = amtLMTD;
    }

    public String getAmtMTD() {
        return AmtMTD;
    }

    public void setAmtMTD(String amtMTD) {
        AmtMTD = amtMTD;
    }

    public String getAmtMonth1PrevPerf() {
        return AmtMonth1PrevPerf;
    }

    public void setAmtMonth1PrevPerf(String amtMonth1PrevPerf) {
        AmtMonth1PrevPerf = amtMonth1PrevPerf;
    }

    public String getAmtMonth2PrevPerf() {
        return AmtMonth2PrevPerf;
    }

    public void setAmtMonth2PrevPerf(String amtMonth2PrevPerf) {
        AmtMonth2PrevPerf = amtMonth2PrevPerf;
    }

    public String getAmtMonth3PrevPerf() {
        return AmtMonth3PrevPerf;
    }

    public void setAmtMonth3PrevPerf(String amtMonth3PrevPerf) {
        AmtMonth3PrevPerf = amtMonth3PrevPerf;
    }

    public String getAvgLstThreeMonth() {
        return AvgLstThreeMonth;
    }

    public void setAvgLstThreeMonth(String avgLstThreeMonth) {
        AvgLstThreeMonth = avgLstThreeMonth;
    }

    private String AvgLstThreeMonth = "";

    public String getCMTarget() {
        return CMTarget;
    }

    public void setCMTarget(String CMTarget) {
        this.CMTarget = CMTarget;
    }

    private String CMTarget = "";

    public String getBalToDo() {
        return BalToDo;
    }

    public void setBalToDo(String balToDo) {
        BalToDo = balToDo;
    }

    private String BalToDo = "";

    private String AchivedPer = "";

    public String getLyGrowthPer() {
        return LyGrowthPer;
    }

    public void setLyGrowthPer(String lyGrowthPer) {
        LyGrowthPer = lyGrowthPer;
    }

    public String getAchivedPer() {
        return AchivedPer;
    }

    public void setAchivedPer(String achivedPer) {
        AchivedPer = achivedPer;
    }

    private String LyGrowthPer = "";


    public String getQtyMonth1PrevPerf() {
        return QtyMonth1PrevPerf;
    }

    public void setQtyMonth1PrevPerf(String qtyMonth1PrevPerf) {
        QtyMonth1PrevPerf = qtyMonth1PrevPerf;
    }

    public String getQtyMonth2PrevPerf() {
        return QtyMonth2PrevPerf;
    }

    public void setQtyMonth2PrevPerf(String qtyMonth2PrevPerf) {
        QtyMonth2PrevPerf = qtyMonth2PrevPerf;
    }

    public String getQtyMonth3PrevPerf() {
        return QtyMonth3PrevPerf;
    }

    public void setQtyMonth3PrevPerf(String qtyMonth3PrevPerf) {
        QtyMonth3PrevPerf = qtyMonth3PrevPerf;
    }

    public String getReportOnID() {
        return ReportOnID;
    }

    public void setReportOnID(String reportOnID) {
        ReportOnID = reportOnID;
    }
}
