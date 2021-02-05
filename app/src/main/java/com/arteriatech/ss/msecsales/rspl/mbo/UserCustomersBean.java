package com.arteriatech.ss.msecsales.rspl.mbo;

public class UserCustomersBean {
    private String distibutorName="";
    private String distributorID="";
    private String stateID="";

    public String getDistibutorName() {
        return distibutorName;
    }

    public void setDistibutorName(String distibutorName) {
        this.distibutorName = distibutorName;
    }

    public String getDistributorID() {
        return distributorID;
    }

    public void setDistributorID(String distributorID) {
        this.distributorID = distributorID;
    }

    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }
    @Override
    public String toString() {
        return distibutorName.toString();
    }
}
