package com.arteriatech.ss.msecsales.rspl.home.dashboard;

import java.util.ArrayList;

public class VisitedBeatBean extends ArrayList<VisitedBeatBean> {
    private String beatName = "";
    private String totalBeatRetailers = "";
    private String totalVisitedRetailers = "";

    public ArrayList<String> getAlCPGuid() {
        return alCPGuid;
    }

    public void setAlCPGuid(ArrayList<String> alCPGuid) {
        this.alCPGuid = alCPGuid;
    }

    private ArrayList<String> alCPGuid = new ArrayList<>();

    public String getRouteGUID() {
        return routeGUID;
    }

    public void setRouteGUID(String routeGUID) {
        this.routeGUID = routeGUID;
    }

    private boolean visitedStatus = false;
    private String routeGUID = "";

    public boolean isVisitedStatus() {
        return visitedStatus;
    }

    public void setVisitedStatus(boolean visitedStatus) {
        this.visitedStatus = visitedStatus;
    }

    public String getBeatName() {
        return beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    public String getTotalBeatRetailers() {
        return totalBeatRetailers;
    }

    public void setTotalBeatRetailers(String totalBeatRetailers) {
        this.totalBeatRetailers = totalBeatRetailers;
    }

    public String getTotalVisitedRetailers() {
        return totalVisitedRetailers;
    }

    public void setTotalVisitedRetailers(String totalVisitedRetailers) {
        this.totalVisitedRetailers = totalVisitedRetailers;
    }
}
