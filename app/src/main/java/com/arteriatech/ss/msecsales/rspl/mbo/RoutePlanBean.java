package com.arteriatech.ss.msecsales.rspl.mbo;

import java.util.List;

/**
 * Created by e10762 on 23-12-2016.
 *
 */

public class RoutePlanBean {

    public String getRschGuid() {
        return RschGuid;
    }

    public void setRschGuid(String rschGuid) {
        RschGuid = rschGuid;
    }

    public String getDOM() {
        return DOM;
    }

    public void setDOM(String DOM) {
        this.DOM = DOM;
    }

    public String getDOW() {
        return DOW;
    }

    public void setDOW(String DOW) {
        this.DOW = DOW;
    }

    private String RschGuid = "";
    private String RoutId = "";

    public String getSalesPersonID() {
        return SalesPersonID;
    }

    public void setSalesPersonID(String salesPersonID) {
        SalesPersonID = salesPersonID;
    }

    private String SalesPersonID = "";

    public String getSalesPersonName() {
        return SalesPersonName;
    }

    public void setSalesPersonName(String salesPersonName) {
        SalesPersonName = salesPersonName;
    }

    private String SalesPersonName = "";
    private String Description = "";

    public String getRoutId() {
        return RoutId;
    }

    public void setRoutId(String routId) {
        RoutId = routId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDisplayData() {
        return DisplayData;
    }

    public void setDisplayData(String displayData) {
        DisplayData = displayData;
    }

    private String DisplayData = "";
    private String DOM = "";
    private String DOW = "";

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    private String CPGUID = "";

    public String getDistName() {
        return DistName;
    }

    public void setDistName(String distName) {
        DistName = distName;
    }

    private String DistName = "";
    private String DistStateId = "";
    private List<UserCustomersBean> distributors;

    public List<UserCustomersBean> getDistributors() {
        return distributors;
    }

    public void setDistributors(List<UserCustomersBean> distributors) {
        this.distributors = distributors;
    }

    public String getDistStateId() {
        return DistStateId;
    }

    public void setDistStateId(String distStateId) {
        DistStateId = distStateId;
    }

    @Override
    public String toString() {
        return DisplayData.toString();
    }

}
