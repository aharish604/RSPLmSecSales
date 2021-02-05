package com.arteriatech.ss.msecsales.rspl.mbo;

import java.util.ArrayList;

public class BeatOpeningSummaryBean {
    private String VisitDate = "";
    private String RschGUID = "";
    private String SPGUID = "";
    private String VisitSummaryGUID = "";
    private String NonProdNoOrder = "";
    private String NonProductive = "";
    private String ParntName = "";
    private String ParntNo = "";
    private String ParntCPTypDesc = "";
    private String ParntCPType = "";
    private String Productive = "";
    private String RouteDesc = "";
    private String RoutID = "";
    private String SpName = "";
    private String SpNo = "";
    private String TotalRetailers = "";
    private String CPGUID = "";
    private ArrayList<String> beatList = new ArrayList<>();

    public ArrayList<String> getBeatList() {
        return beatList;
    }

    public void setBeatList(ArrayList<String> beatList) {
        this.beatList = beatList;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getBeatGUID() {
        return BeatGUID;
    }

    public void setBeatGUID(String beatGUID) {
        BeatGUID = beatGUID;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getVisitGUID() {
        return VisitGUID;
    }

    public void setVisitGUID(String visitGUID) {
        VisitGUID = visitGUID;
    }

    private String BeatGUID = "";
    private String CPNo = "";
    private String VisitGUID = "";

    public String getVisitDate() {
        return VisitDate;
    }

    public void setVisitDate(String visitDate) {
        VisitDate = visitDate;
    }

    public String getRschGUID() {
        return RschGUID;
    }

    public void setRschGUID(String rschGUID) {
        RschGUID = rschGUID;
    }

    public String getSPGUID() {
        return SPGUID;
    }

    public void setSPGUID(String SPGUID) {
        this.SPGUID = SPGUID;
    }

    public String getVisitSummaryGUID() {
        return VisitSummaryGUID;
    }

    public void setVisitSummaryGUID(String visitSummaryGUID) {
        VisitSummaryGUID = visitSummaryGUID;
    }

    public String getNonProdNoOrder() {
        return NonProdNoOrder;
    }

    public void setNonProdNoOrder(String nonProdNoOrder) {
        NonProdNoOrder = nonProdNoOrder;
    }

    public String getNonProductive() {
        return NonProductive;
    }

    public void setNonProductive(String nonProductive) {
        NonProductive = nonProductive;
    }

    public String getParntName() {
        return ParntName;
    }

    public void setParntName(String parntName) {
        ParntName = parntName;
    }

    public String getParntNo() {
        return ParntNo;
    }

    public void setParntNo(String parntNo) {
        ParntNo = parntNo;
    }

    public String getParntCPTypDesc() {
        return ParntCPTypDesc;
    }

    public void setParntCPTypDesc(String parntCPTypDesc) {
        ParntCPTypDesc = parntCPTypDesc;
    }

    public String getParntCPType() {
        return ParntCPType;
    }

    public void setParntCPType(String parntCPType) {
        ParntCPType = parntCPType;
    }

    public String getProductive() {
        return Productive;
    }

    public void setProductive(String productive) {
        Productive = productive;
    }

    public String getRouteDesc() {
        return RouteDesc;
    }

    public void setRouteDesc(String routeDesc) {
        RouteDesc = routeDesc;
    }

    public String getRoutID() {
        return RoutID;
    }

    public void setRoutID(String routID) {
        RoutID = routID;
    }

    public String getSpName() {
        return SpName;
    }

    public void setSpName(String spName) {
        SpName = spName;
    }

    public String getSpNo() {
        return SpNo;
    }

    public void setSpNo(String spNo) {
        SpNo = spNo;
    }

    public String getTotalRetailers() {
        return TotalRetailers;
    }

    public void setTotalRetailers(String totalRetailers) {
        TotalRetailers = totalRetailers;
    }

    public String getVisitedRetailers() {
        return VisitedRetailers;
    }

    public void setVisitedRetailers(String visitedRetailers) {
        VisitedRetailers = visitedRetailers;
    }

    private String VisitedRetailers = "";

    public String getVisitedBeats() {
        return VisitedBeats;
    }

    public void setVisitedBeats(String visitedBeats) {
        VisitedBeats = visitedBeats;
    }

    private String VisitedBeats = "";
}
