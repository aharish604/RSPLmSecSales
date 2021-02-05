package com.arteriatech.ss.msecsales.rspl.visitsummaryreport;

import android.util.ArrayMap;

import java.util.ArrayList;

public class VisitSummaryReportBean {
    private String SPName="";

    public String getSPNo() {
        return SPNo;
    }

    public void setSPNo(String SPNo) {
        this.SPNo = SPNo;
    }

    private String SPNo="";
    private String TotalBeat="";
    private String VisitedBeat="";
    private String todayVisitedBeat="";

    public String getTodayVisitedRetailer() {
        return todayVisitedRetailer;
    }

    public void setTodayVisitedRetailer(String todayVisitedRetailer) {
        this.todayVisitedRetailer = todayVisitedRetailer;
    }

    private String todayVisitedRetailer="";

    private ArrayMap<String, String> beatMap = new ArrayMap<>();

    public ArrayMap<String, String> getBeatMap() {
        return beatMap;
    }

    public void setBeatMap(ArrayMap<String, String> beatMap) {
        this.beatMap = beatMap;
    }

    public ArrayList<String> getBeatGuidList() {
        return beatGuidList;
    }

    public void setBeatGuidList(ArrayList<String> beatGuidList) {
        this.beatGuidList = beatGuidList;
    }

    private ArrayList<String> beatGuidList=new ArrayList<>();
    private String TotalRetailers="";
    private String VisitedRetailer="";

    public String getSPName() {
        return SPName;
    }

    public void setSPName(String SPName) {
        this.SPName = SPName;
    }

    public String getTotalBeat() {
        return TotalBeat;
    }

    public void setTotalBeat(String totalBeat) {
        TotalBeat = totalBeat;
    }

    public String getVisitedBeat() {
        return VisitedBeat;
    }

    public String getTodayVisitedBeat() {
        return todayVisitedBeat;
    }

    public void setTodayVisitedBeat(String todayVisitedBeat) {
        this.todayVisitedBeat = todayVisitedBeat;
    }

    public void setVisitedBeat(String visitedBeat) {
        VisitedBeat = visitedBeat;
    }

    public String getTotalRetailers() {
        return TotalRetailers;
    }

    public void setTotalRetailers(String totalRetailers) {
        TotalRetailers = totalRetailers;
    }

    public String getVisitedRetailer() {
        return VisitedRetailer;
    }

    public void setVisitedRetailer(String visitedRetailer) {
        VisitedRetailer = visitedRetailer;
    }
}
