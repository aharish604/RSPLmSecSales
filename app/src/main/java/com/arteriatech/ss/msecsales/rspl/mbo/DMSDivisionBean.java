package com.arteriatech.ss.msecsales.rspl.mbo;

import java.io.Serializable;

/**
 * Created by e10526 on 07-08-2018.
 */

public class DMSDivisionBean implements Serializable {
    private String CPTypeID ="";
    private String CPGUID ="";
    private String CPNo ="";

    public String getCPName() {
        return CPName;
    }

    public void setCPName(String CPName) {
        this.CPName = CPName;
    }

    private String CPName ="";
    private String SPGUID ="";
    private String SPNo ="";
    private String FirstName ="";
    private String DMSDivisionID ="";
    private String displayData="";
    @Override
    public String toString() {
        return displayData.toString();
    }

    public String getDisplayData() {
        return displayData;
    }

    public void setDisplayData(String displayData) {
        this.displayData = displayData;
    }

    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getSPGUID() {
        return SPGUID;
    }

    public void setSPGUID(String SPGUID) {
        this.SPGUID = SPGUID;
    }

    public String getSPNo() {
        return SPNo;
    }

    public void setSPNo(String SPNo) {
        this.SPNo = SPNo;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getDMSDivisionID() {
        return DMSDivisionID;
    }

    public void setDMSDivisionID(String DMSDivisionID) {
        this.DMSDivisionID = DMSDivisionID;
    }

    public String getDmsDivsionDesc() {
        return DmsDivsionDesc;
    }

    public void setDmsDivsionDesc(String dmsDivsionDesc) {
        DmsDivsionDesc = dmsDivsionDesc;
    }

    private String DmsDivsionDesc ="";
}
