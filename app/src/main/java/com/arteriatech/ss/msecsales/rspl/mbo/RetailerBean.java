package com.arteriatech.ss.msecsales.rspl.mbo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sap.smp.client.odata.ODataEntity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class RetailerBean implements Parcelable {

    public static int SELECTED__SPINNER_INDEX = 0;


    public String getFetchJsonHeaderObject() {
        return fetchJsonHeaderObject;
    }

    public void setFetchJsonHeaderObject(String fetchJsonHeaderObject) {
        this.fetchJsonHeaderObject = fetchJsonHeaderObject;
    }

   private String RetailerCatDesc = "";
    private ArrayList<String> alBeatGuid = new ArrayList<>();

    public ArrayList<String> getAlBeatGuid() {
        return alBeatGuid;
    }

    public void setAlBeatGuid(ArrayList<String> alBeatGuid) {
        this.alBeatGuid = alBeatGuid;
    }

    private String customerId;
    private String customerName;
    private String street;
    private String city = "";
    private String country;
    private String website;
    private String editResourceURL;
    private String MobileNumber = "";
    private String MailId = "";
    private double latVal = 0.0;
    private double longVal = 0.0;
    private String Etag = "";
    private String purchaseQty = "";
    private String UOM = "";
    private String statusID = "";
    private boolean isChecked = false;
    private String RouteSchPlanGUID = "";
    private String VisitStatus = "";
    private String NameNumber = "";
    private String SequenceNo = "";
    private String PurchaseAmount = "";
    private String CollPlanHeaderGUID = "";
    private String state = "";
    private String SetResourcePath = "";
    private ODataEntity oDataEntity = null;
    private String CustomerType = "";
    private String RouteGuid32 = "";
    private String RouteGuid36 = "";
    //new 28112016 Ramu
    private String VisitType = "";
    private String MtdValue = "";
    //new 28112016
    private String CPTypeDesc = "";
    private String CPTypeID = "";
    private String UID = "";
    private String landMark = "";
    private boolean isAddressEnabled;
    private String RoutePlanKey = "";
    private String RouteID = "";
    private String RouteDesc = "";
    private String RouteHeading = "";
    private String CustDOB = "";
    private String Anniversary = "";
    private String SpouseDOB = "";
    private String Child1DOB = "";
    private String Child2DOB = "";
    private String Child3DOB = "";
    private String Child1Name = "";
    private String Child2Name = "";
    private String Child3Name = "";
    private String Group3Desc = "";

    protected RetailerBean(Parcel in) {
        RetailerCatDesc = in.readString();
        customerId = in.readString();
        customerName = in.readString();
        street = in.readString();
        city = in.readString();
        country = in.readString();
        website = in.readString();
        editResourceURL = in.readString();
        MobileNumber = in.readString();
        MailId = in.readString();
        latVal = in.readDouble();
        longVal = in.readDouble();
        Etag = in.readString();
        purchaseQty = in.readString();
        UOM = in.readString();
        statusID = in.readString();
        isChecked = in.readByte() != 0;
        RouteSchPlanGUID = in.readString();
        VisitStatus = in.readString();
        NameNumber = in.readString();
        SequenceNo = in.readString();
        PurchaseAmount = in.readString();
        CollPlanHeaderGUID = in.readString();
        state = in.readString();
        SetResourcePath = in.readString();
        CustomerType = in.readString();
        RouteGuid32 = in.readString();
        RouteGuid36 = in.readString();
        VisitType = in.readString();
        MtdValue = in.readString();
        visitDate = in.readString();
        CPTypeDesc = in.readString();
        CPTypeID = in.readString();
        UID = in.readString();
        landMark = in.readString();
        isAddressEnabled = in.readByte() != 0;
        RoutePlanKey = in.readString();
        RouteID = in.readString();
        RouteDesc = in.readString();
        RouteHeading = in.readString();
        CustDOB = in.readString();
        Anniversary = in.readString();
        SpouseDOB = in.readString();
        Child1DOB = in.readString();
        Child2DOB = in.readString();
        Child3DOB = in.readString();
        Child1Name = in.readString();
        Child2Name = in.readString();
        Child3Name = in.readString();
        Group3Desc = in.readString();
        Group4Desc = in.readString();
        OwnerName = in.readString();
        CpGuidStringFormat = in.readString();
        CPNo = in.readString();
        RetailerName = in.readString();
        CPGUID = in.readString();
        Address1 = in.readString();
        Address2 = in.readString();
        Address3 = in.readString();
        Address4 = in.readString();
        TownDesc = in.readString();
        ParentID = in.readString();
        ParentName = in.readString();
        DistrictDesc = in.readString();
        postalCode = in.readString();
        district = in.readString();
        emailId = in.readString();
        mobile1 = in.readString();
        RschGuid = in.readString();
        RschGuid32 = in.readString();
        RoutSchScope = in.readString();
        Currency = in.readString();
        amount = in.readString();
        remarks = in.readString();
        retailerCount = in.readString();
        tax1 = in.readString();
        classification = in.readString();
        instanceId = in.readString();
        OutletName = in.readString();
        InvoiceAval = in.readString();
        WeeklyOffDesc = in.readString();
        TimeTaken = in.readString();
        isTitle = in.readByte() != 0;
        itemList = in.createTypedArrayList(RetailerBean.CREATOR);
        fetchJsonHeaderObject = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RetailerCatDesc);
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(website);
        dest.writeString(editResourceURL);
        dest.writeString(MobileNumber);
        dest.writeString(MailId);
        dest.writeDouble(latVal);
        dest.writeDouble(longVal);
        dest.writeString(Etag);
        dest.writeString(purchaseQty);
        dest.writeString(UOM);
        dest.writeString(statusID);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeString(RouteSchPlanGUID);
        dest.writeString(VisitStatus);
        dest.writeString(NameNumber);
        dest.writeString(SequenceNo);
        dest.writeString(PurchaseAmount);
        dest.writeString(CollPlanHeaderGUID);
        dest.writeString(state);
        dest.writeString(SetResourcePath);
        dest.writeString(CustomerType);
        dest.writeString(RouteGuid32);
        dest.writeString(RouteGuid36);
        dest.writeString(VisitType);
        dest.writeString(MtdValue);
        dest.writeString(visitDate);
        dest.writeString(CPTypeDesc);
        dest.writeString(CPTypeID);
        dest.writeString(UID);
        dest.writeString(landMark);
        dest.writeByte((byte) (isAddressEnabled ? 1 : 0));
        dest.writeString(RoutePlanKey);
        dest.writeString(RouteID);
        dest.writeString(RouteDesc);
        dest.writeString(RouteHeading);
        dest.writeString(CustDOB);
        dest.writeString(Anniversary);
        dest.writeString(SpouseDOB);
        dest.writeString(Child1DOB);
        dest.writeString(Child2DOB);
        dest.writeString(Child3DOB);
        dest.writeString(Child1Name);
        dest.writeString(Child2Name);
        dest.writeString(Child3Name);
        dest.writeString(Group3Desc);
        dest.writeString(Group4Desc);
        dest.writeString(OwnerName);
        dest.writeString(CpGuidStringFormat);
        dest.writeString(CPNo);
        dest.writeString(RetailerName);
        dest.writeString(CPGUID);
        dest.writeString(Address1);
        dest.writeString(Address2);
        dest.writeString(Address3);
        dest.writeString(Address4);
        dest.writeString(TownDesc);
        dest.writeString(ParentID);
        dest.writeString(ParentName);
        dest.writeString(DistrictDesc);
        dest.writeString(postalCode);
        dest.writeString(district);
        dest.writeString(emailId);
        dest.writeString(mobile1);
        dest.writeString(RschGuid);
        dest.writeString(RschGuid32);
        dest.writeString(RoutSchScope);
        dest.writeString(Currency);
        dest.writeString(amount);
        dest.writeString(remarks);
        dest.writeString(retailerCount);
        dest.writeString(tax1);
        dest.writeString(classification);
        dest.writeString(instanceId);
        dest.writeString(OutletName);
        dest.writeString(InvoiceAval);
        dest.writeString(WeeklyOffDesc);
        dest.writeString(TimeTaken);
        dest.writeByte((byte) (isTitle ? 1 : 0));
        dest.writeTypedList(itemList);
        dest.writeString(fetchJsonHeaderObject);
    }

    public static final Creator<RetailerBean> CREATOR = new Creator<RetailerBean>() {
        @Override
        public RetailerBean createFromParcel(Parcel in) {
            return new RetailerBean(in);
        }

        @Override
        public RetailerBean[] newArray(int size) {
            return new RetailerBean[size];
        }
    };

    public String getGroup4Desc() {
        return Group4Desc;
    }

    public void setGroup4Desc(String group4Desc) {
        Group4Desc = group4Desc;
    }

    private String Group4Desc = "";
    private String OwnerName = "";
    private String ZZVisitFlag = "";
    private String CpGuidStringFormat = "";
    private String CPNo = "";
    private String RetailerName = "";
    private String CPGUID = "";
    private String Address1 = "";
    private String Address2 = "";
    private String Address3 = "";
    private String Address4 = "";
    private String TownDesc = "";
    private String ParentID = "";
    private String ParentName = "";
    private String DistrictDesc = "";
    private String postalCode = "";
    private String district;
    private String emailId;
    private String mobile1 = "";
    private String RschGuid = "";
    private String RschGuid32 = "";
    private String RoutSchScope = "";
    private String Currency = "";
    private String amount = "";
    private String remarks = "";
    private String retailerCount = "0";

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    private String visitDate = "";

    public BeatOpeningSummaryBean getBeatOpeningSummaryBean() {
        return beatOpeningSummaryBean;
    }

    public void setBeatOpeningSummaryBean(BeatOpeningSummaryBean beatOpeningSummaryBean) {
        this.beatOpeningSummaryBean = beatOpeningSummaryBean;
    }

    private BeatOpeningSummaryBean beatOpeningSummaryBean = null;
    private String tax1 = "0";
    private String classification;
    private String instanceId;

    public String getZZVisitFlag() {
        return ZZVisitFlag;
    }

    public void setZZVisitFlag(String ZZVisitFlag) {
        this.ZZVisitFlag = ZZVisitFlag;
    }

    public Hashtable getHashtable() {
        return hashtable;
    }

    public void setHashtable(Hashtable hashtable) {
        this.hashtable = hashtable;
    }

    private Hashtable hashtable=new Hashtable();

    public String getOutletName() {
        return OutletName;
    }

    public void setOutletName(String outletName) {
        OutletName = outletName;
    }
    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }
    private String OutletName="";

    public String getInvoiceAval() {
        return InvoiceAval;
    }

    public void setInvoiceAval(String invoiceAval) {
        InvoiceAval = invoiceAval;
    }

    private String InvoiceAval= "";



    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getTax1() {
        return tax1;
    }

    public void setTax1(String tax1) {
        this.tax1 = tax1;
    }



    public String getWeeklyOffDesc() {
        return WeeklyOffDesc;
    }

    public void setWeeklyOffDesc(String weeklyOffDesc) {
        WeeklyOffDesc = weeklyOffDesc;
    }

    private String WeeklyOffDesc = "0";



    private String TimeTaken = "";
    private boolean isTitle =false;
    private ArrayList<RetailerBean> itemList = new ArrayList<>();
    private String fetchJsonHeaderObject = "";

    public RetailerBean(String agencyId) {
        super();
        this.customerId = agencyId;
    }

    public RetailerBean() {
    }



    public String getVisitStatus() {
        return VisitStatus;
    }

    public void setVisitStatus(String visitStatus) {
        VisitStatus = visitStatus;
    }

    public String getNameNumber() {
        return NameNumber;
    }

    public void setNameNumber(String nameNumber) {
        NameNumber = nameNumber;
    }

    public String getSequenceNo() {
        return SequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        SequenceNo = sequenceNo;
    }

    public String getPurchaseAmount() {
        return PurchaseAmount;
    }

    public void setPurchaseAmount(String purchaseAmount) {
        PurchaseAmount = purchaseAmount;
    }

    public String getCollPlanHeaderGUID() {
        return CollPlanHeaderGUID;
    }

    public void setCollPlanHeaderGUID(String collPlanHeaderGUID) {
        CollPlanHeaderGUID = collPlanHeaderGUID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ODataEntity getoDataEntity() {
        return oDataEntity;
    }

    public void setoDataEntity(ODataEntity oDataEntity) {
        this.oDataEntity = oDataEntity;
    }

    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String customerType) {
        CustomerType = customerType;
    }

    public String getRouteGuid32() {
        return RouteGuid32;
    }

    public void setRouteGuid32(String routeGuid32) {
        RouteGuid32 = routeGuid32;
    }

    public String getRouteGuid36() {
        return RouteGuid36;
    }

    public void setRouteGuid36(String routeGuid36) {
        RouteGuid36 = routeGuid36;
    }

    public String getVisitType() {
        return VisitType;
    }

    public void setVisitType(String visitType) {
        VisitType = visitType;
    }

    public String getMtdValue() {
        return MtdValue;
    }

    public void setMtdValue(String mtdValue) {
        MtdValue = mtdValue;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getCPTypeDesc() {
        return CPTypeDesc;
    }

    public void setCPTypeDesc(String CPTypeDesc) {
        this.CPTypeDesc = CPTypeDesc;
    }

    public String getRetailerCatDesc() {
        return RetailerCatDesc;
    }

    public void setRetailerCatDesc(String retailerCatDesc) {
        RetailerCatDesc = retailerCatDesc;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public boolean isAddressEnabled() {
        return isAddressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        isAddressEnabled = addressEnabled;
    }

    public void setIsAddressEnabled(boolean isAddressEnabled) {
        this.isAddressEnabled = isAddressEnabled;
    }

    public String getSetResourcePath() {
        return SetResourcePath;
    }

    public void setSetResourcePath(String setResourcePath) {
        SetResourcePath = setResourcePath;
    }

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }

    public double getLongVal() {
        return longVal;
    }

    public void setLongVal(double longVal) {
        this.longVal = longVal;
    }

    public double getLatVal() {
        return latVal;
    }

    public void setLatVal(double latVal) {
        this.latVal = latVal;
    }

    public String getRoutePlanKey() {
        return RoutePlanKey;
    }

    public void setRoutePlanKey(String routePlanKey) {
        RoutePlanKey = routePlanKey;
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

    @Override
    public String toString() {
        return RouteDesc;
    }

    public String getRouteHeading() {
        return RouteHeading;
    }

    public void setRouteHeading(String routeHeading) {
        RouteHeading = routeHeading;
    }

    public String getGroup3Desc() {
        return Group3Desc;
    }

    public void setGroup3Desc(String group3Desc) {
        Group3Desc = group3Desc;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getCpGuidStringFormat() {
        return CpGuidStringFormat;
    }

    public void setCpGuidStringFormat(String cpGuidStringFormat) {
        CpGuidStringFormat = cpGuidStringFormat;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getRetailerName() {
        return RetailerName;
    }

    public void setRetailerName(String retailerName) {
        RetailerName = retailerName;
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

    public String getTownDesc() {
        return TownDesc;
    }

    public void setTownDesc(String townDesc) {
        TownDesc = townDesc;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getDistrictDesc() {
        return DistrictDesc;
    }

    public void setDistrictDesc(String districtDesc) {
        DistrictDesc = districtDesc;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getMailId() {
        return MailId;
    }

    public void setMailId(String mailId) {
        MailId = mailId;
    }

    public String getCustDOB() {
        return CustDOB;
    }

    public void setCustDOB(String custDOB) {
        CustDOB = custDOB;
    }

    public String getAnniversary() {
        return Anniversary;
    }

    public void setAnniversary(String anniversary) {
        Anniversary = anniversary;
    }

    public String getSpouseDOB() {
        return SpouseDOB;
    }

    public void setSpouseDOB(String spouseDOB) {
        SpouseDOB = spouseDOB;
    }

    public String getChild1DOB() {
        return Child1DOB;
    }

    public void setChild1DOB(String child1dob) {
        Child1DOB = child1dob;
    }

    public String getChild2DOB() {
        return Child2DOB;
    }

    public void setChild2DOB(String child2dob) {
        Child2DOB = child2dob;
    }

    public String getChild3DOB() {
        return Child3DOB;
    }

    public void setChild3DOB(String child3dob) {
        Child3DOB = child3dob;
    }

    public String getChild1Name() {
        return Child1Name;
    }

    public void setChild1Name(String child1Name) {
        Child1Name = child1Name;
    }

    public String getChild2Name() {
        return Child2Name;
    }

    public void setChild2Name(String child2Name) {
        Child2Name = child2Name;
    }

    public String getChild3Name() {
        return Child3Name;
    }

    public void setChild3Name(String child3Name) {
        Child3Name = child3Name;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public boolean isInitialized() {
        return (!TextUtils.isEmpty(this.customerId));
    }

    public String getAgencyId() {
        return customerId;
    }

    public void setAgencyId(String agencyId) {
        this.customerId = agencyId;
    }

    public String getAgencyName() {
        return customerName;
    }

    public void setAgencyName(String agencyName) {
        this.customerName = agencyName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEditResourceURL() {
        return editResourceURL;
    }

    public void setEditResourceURL(String editResourceURL) {
        this.editResourceURL = editResourceURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getRschGuid() {
        return RschGuid;
    }

    public void setRschGuid(String rschGuid) {
        RschGuid = rschGuid;
    }

    public String getRschGuid32() {
        return RschGuid32;
    }

    public void setRschGuid32(String rschGuid32) {
        RschGuid32 = rschGuid32;
    }

    public String getRoutSchScope() {
        return RoutSchScope;
    }

    public void setRoutSchScope(String routSchScope) {
        RoutSchScope = routSchScope;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(String purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getRouteSchPlanGUID() {
        return RouteSchPlanGUID;
    }

    public void setRouteSchPlanGUID(String routeSchPlanGUID) {
        RouteSchPlanGUID = routeSchPlanGUID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public ArrayList<RetailerBean> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<RetailerBean> itemList) {
        this.itemList = itemList;
    }

    public String getRetailerCount() {
        return retailerCount;
    }

    public void setRetailerCount(String retailerCount) {
        this.retailerCount = retailerCount;
    }
    public String getTimeTaken() {
        return TimeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        TimeTaken = timeTaken;
    }
}
