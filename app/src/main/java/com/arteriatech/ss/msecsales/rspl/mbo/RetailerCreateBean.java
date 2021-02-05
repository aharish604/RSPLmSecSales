package com.arteriatech.ss.msecsales.rspl.mbo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10526 on 02-08-2018.
 */

public class RetailerCreateBean implements Serializable{
    public String CPGUID = "";
    public String CPNo = "";
    public String PartnerMgrGUID = "";
    public String PartnerMgrName = "";
    public String PartnerMgrNo = "";
    public String Name = "";
    public String CPUID = "";
    public String AccountGrp = "";
    public String ParentName = "";
    public String SearchTerm = "";
    public String CPTypeID = "";
    public String CPTypeDesc = "";
    public String Group1 = "";
    public String Group2 = "";
    public String Group3 = "";
    public String Group4 = "";
    public String CPStock = "";
    public String UOM = "";
    public String DOB = "";
    public String Anniversary = "";
    public String RouteID = "";

    public String getRouteDist() {
        return RouteDist;
    }

    public void setRouteDist(String routeDist) {
        RouteDist = routeDist;
    }

    public String RouteDist = "";

    public String getRouteDistName() {
        return RouteDistName;
    }

    public void setRouteDistName(String routeDistName) {
        RouteDistName = routeDistName;
    }

    public String RouteDistName = "";
    public String RouteSchGUID = "";
    public String RouteDesc = "";
    public String Latitude = "0.00";
    public String Longitude = "0.00";
    public String Address1 = "";
    public String Address2 = "";
    public String Address3 = "";
    public String Address4 = "";
    public String Landmark = "";
    public String ZoneID = "";
    public String ZoneDesc = "";
    public String TownID = "";
    public String TownDesc = "";
    public String CityID = "";
    public String CityDesc = "";
    public String StateID = "";
    public String StateDesc = "";
    public String DistrictID = "";
    public String DistrictDesc = "";
    public String Country = "";
    public String CountryName = "";
    public String PostalCode = "";
    public String Mobile1 = "";
    public String Mobile2 = "";
    public String Landline = "";
    public String EmailID = "";
    public String Currency = "";
    public String CreditLimit = "";
    public String CreditDays = "";
    public String VATNo = "";
    public String TIN = "";
    public String PAN = "";
    public String OwnerName = "";
    public String SalesOfficeID = "";
    public String SalesGrpDesc = "";
    public String SalesGroupID = "";
    public String SalesOffDesc = "";
    public boolean IsKeyCP = false;
    public String WeeklyOff = "";
    public String WeeklyOffDesc = "";
    public String ID1 = "";
    public String ID2 = "";
    public String FaxNo = "";
    public String BusinessID1 = "";
    public String BusinessID2 = "";
    public String Tax1 = "";
    public String Tax2 = "";
    public String Tax3 = "";
    public String Tax4 = "";
    public String TaxClassification = "";
    public String TaxRegStatus = "";
    public String TaxRegStatusDesc = "";
    public String DOBTemp = "";

    public String getDOBTemp() {
        return DOBTemp;
    }

    public void setDOBTemp(String DOBTemp) {
        this.DOBTemp = DOBTemp;
    }

    public String getAnniversaryTemp() {
        return AnniversaryTemp;
    }

    public void setAnniversaryTemp(String anniversaryTemp) {
        AnniversaryTemp = anniversaryTemp;
    }

    public String AnniversaryTemp = "";

    public ArrayList<RetailerClassificationBean> getAlRetClassfication() {
        return alRetClassfication;
    }

    public void setAlRetClassfication(ArrayList<RetailerClassificationBean> alRetClassfication) {
        this.alRetClassfication = alRetClassfication;
    }

    public ArrayList<RetailerClassificationBean> alRetClassfication =new ArrayList<>();
    public String getPartnerMgrName() {
        return PartnerMgrName;
    }

    public void setPartnerMgrName(String partnerMgrName) {
        PartnerMgrName = partnerMgrName;
    }

    public String getPartnerMgrNo() {
        return PartnerMgrNo;
    }

    public void setPartnerMgrNo(String partnerMgrNo) {
        PartnerMgrNo = partnerMgrNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCPUID() {
        return CPUID;
    }

    public void setCPUID(String CPUID) {
        this.CPUID = CPUID;
    }

    public String getAccountGrp() {
        return AccountGrp;
    }

    public void setAccountGrp(String accountGrp) {
        AccountGrp = accountGrp;
    }

    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }

    public String getSearchTerm() {
        return SearchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        SearchTerm = searchTerm;
    }

    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }

    public String getCPTypeDesc() {
        return CPTypeDesc;
    }

    public void setCPTypeDesc(String CPTypeDesc) {
        this.CPTypeDesc = CPTypeDesc;
    }

    public String getGroup1() {
        return Group1;
    }

    public void setGroup1(String group1) {
        Group1 = group1;
    }

    public String getGroup2() {
        return Group2;
    }

    public void setGroup2(String group2) {
        Group2 = group2;
    }

    public String getGroup3() {
        return Group3;
    }

    public void setGroup3(String group3) {
        Group3 = group3;
    }

    public String getGroup4() {
        return Group4;
    }

    public void setGroup4(String group4) {
        Group4 = group4;
    }

    public String getCPStock() {
        return CPStock;
    }

    public void setCPStock(String CPStock) {
        this.CPStock = CPStock;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAnniversary() {
        return Anniversary;
    }

    public void setAnniversary(String anniversary) {
        Anniversary = anniversary;
    }

    public String getRouteID() {
        return RouteID;
    }

    public void setRouteID(String routeID) {
        RouteID = routeID;
    }

    public String getRouteDesc() {
        return RouteDesc;
    }

    public void setRouteDesc(String routeDesc) {
        RouteDesc = routeDesc;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getAddress3() {
        return Address3;
    }

    public void setAddress3(String address3) {
        Address3 = address3;
    }

    public String getAddress4() {
        return Address4;
    }

    public void setAddress4(String address4) {
        Address4 = address4;
    }

    public String getLandmark() {
        return Landmark;
    }

    public void setLandmark(String landmark) {
        Landmark = landmark;
    }

    public String getZoneID() {
        return ZoneID;
    }

    public void setZoneID(String zoneID) {
        ZoneID = zoneID;
    }

    public String getZoneDesc() {
        return ZoneDesc;
    }

    public void setZoneDesc(String zoneDesc) {
        ZoneDesc = zoneDesc;
    }

    public String getTownID() {
        return TownID;
    }

    public void setTownID(String townID) {
        TownID = townID;
    }

    public String getTownDesc() {
        return TownDesc;
    }

    public void setTownDesc(String townDesc) {
        TownDesc = townDesc;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public String getCityDesc() {
        return CityDesc;
    }

    public void setCityDesc(String cityDesc) {
        CityDesc = cityDesc;
    }

    public String getStateID() {
        return StateID;
    }

    public void setStateID(String stateID) {
        StateID = stateID;
    }

    public String getStateDesc() {
        return StateDesc;
    }

    public void setStateDesc(String stateDesc) {
        StateDesc = stateDesc;
    }

    public String getDistrictID() {
        return DistrictID;
    }

    public void setDistrictID(String districtID) {
        DistrictID = districtID;
    }

    public String getDistrictDesc() {
        return DistrictDesc;
    }

    public void setDistrictDesc(String districtDesc) {
        DistrictDesc = districtDesc;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getMobile1() {
        return Mobile1;
    }

    public void setMobile1(String mobile1) {
        Mobile1 = mobile1;
    }

    public String getMobile2() {
        return Mobile2;
    }

    public void setMobile2(String mobile2) {
        Mobile2 = mobile2;
    }

    public String getLandline() {
        return Landline;
    }

    public void setLandline(String landline) {
        Landline = landline;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getCreditLimit() {
        return CreditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        CreditLimit = creditLimit;
    }

    public String getCreditDays() {
        return CreditDays;
    }

    public void setCreditDays(String creditDays) {
        CreditDays = creditDays;
    }

    public String getVATNo() {
        return VATNo;
    }

    public void setVATNo(String VATNo) {
        this.VATNo = VATNo;
    }

    public String getTIN() {
        return TIN;
    }

    public void setTIN(String TIN) {
        this.TIN = TIN;
    }

    public String getPAN() {
        return PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getSalesOfficeID() {
        return SalesOfficeID;
    }

    public void setSalesOfficeID(String salesOfficeID) {
        SalesOfficeID = salesOfficeID;
    }

    public String getSalesGrpDesc() {
        return SalesGrpDesc;
    }

    public void setSalesGrpDesc(String salesGrpDesc) {
        SalesGrpDesc = salesGrpDesc;
    }

    public String getSalesGroupID() {
        return SalesGroupID;
    }

    public void setSalesGroupID(String salesGroupID) {
        SalesGroupID = salesGroupID;
    }

    public String getSalesOffDesc() {
        return SalesOffDesc;
    }

    public void setSalesOffDesc(String salesOffDesc) {
        SalesOffDesc = salesOffDesc;
    }

    public boolean isKeyCP() {
        return IsKeyCP;
    }

    public void setKeyCP(boolean keyCP) {
        IsKeyCP = keyCP;
    }

    public String getWeeklyOff() {
        return WeeklyOff;
    }

    public void setWeeklyOff(String weeklyOff) {
        WeeklyOff = weeklyOff;
    }

    public String getWeeklyOffDesc() {
        return WeeklyOffDesc;
    }

    public void setWeeklyOffDesc(String weeklyOffDesc) {
        WeeklyOffDesc = weeklyOffDesc;
    }

    public String getID1() {
        return ID1;
    }

    public void setID1(String ID1) {
        this.ID1 = ID1;
    }

    public String getID2() {
        return ID2;
    }

    public void setID2(String ID2) {
        this.ID2 = ID2;
    }

    public String getFaxNo() {
        return FaxNo;
    }

    public void setFaxNo(String faxNo) {
        FaxNo = faxNo;
    }

    public String getBusinessID1() {
        return BusinessID1;
    }

    public void setBusinessID1(String businessID1) {
        BusinessID1 = businessID1;
    }

    public String getBusinessID2() {
        return BusinessID2;
    }

    public void setBusinessID2(String businessID2) {
        BusinessID2 = businessID2;
    }

    public String getTax1() {
        return Tax1;
    }

    public void setTax1(String tax1) {
        Tax1 = tax1;
    }

    public String getTax2() {
        return Tax2;
    }

    public void setTax2(String tax2) {
        Tax2 = tax2;
    }

    public String getTax3() {
        return Tax3;
    }

    public void setTax3(String tax3) {
        Tax3 = tax3;
    }

    public String getTax4() {
        return Tax4;
    }

    public void setTax4(String tax4) {
        Tax4 = tax4;
    }

    public String getTaxClassification() {
        return TaxClassification;
    }

    public void setTaxClassification(String taxClassification) {
        TaxClassification = taxClassification;
    }

    public String getTaxRegStatus() {
        return TaxRegStatus;
    }

    public void setTaxRegStatus(String taxRegStatus) {
        TaxRegStatus = taxRegStatus;
    }

    public String getTaxRegStatusDesc() {
        return TaxRegStatusDesc;
    }

    public void setTaxRegStatusDesc(String taxRegStatusDesc) {
        TaxRegStatusDesc = taxRegStatusDesc;
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

    public String getPartnerMgrGUID() {
        return PartnerMgrGUID;
    }

    public void setPartnerMgrGUID(String partnerMgrGUID) {
        PartnerMgrGUID = partnerMgrGUID;
    }


    public String getRouteSchGUID() {
        return RouteSchGUID;
    }

    public void setRouteSchGUID(String routeSchGUID) {
        RouteSchGUID = routeSchGUID;
    }

}
