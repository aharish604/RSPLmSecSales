package com.arteriatech.ss.msecsales.rspl.mbo;

import android.os.Parcel;
import android.os.Parcelable;

public class CompetitorBean implements Parcelable {
    // data members
    private String SPName = "";
    private String CPName = "";
    private String RoutDesc = "";
    private String Remarks = "";
    private String Currency = "";
    private String Earnings = "";
    private String SchemeName = "";
    private String CompanyName = "";
    private String LandingPrice = "";
    private String Margin = "";
    private String MaterialDesc = "";
    private String TradeOffer = "";
    private String MRP = "";
    private String ConsumerOffer = "";
    private String ShelfLife = "";
    private String SpNo = "";
    private String UpdatedOn = "";
    private String CPGUID = "";
    private String cpUID = "";
    private String CPGUID32 = "";
    private String CPNo = "";
    private String WholesalerLandingPrice = "";
    private String retailerLandingPrice = "";
    private String SchemeLaunched = "";
    private String SPGUID = "";

    // default constructor
    public CompetitorBean() {
    }

    // setters and getters
    public String getSPName() {
        return SPName;
    }

    public void setSPName(String SPName) {
        this.SPName = SPName;
    }

    public String getCPName() {
        return CPName;
    }

    public void setCPName(String CPName) {
        this.CPName = CPName;
    }

    public String getRoutDesc() {
        return RoutDesc;
    }

    public void setRoutDesc(String routDesc) {
        RoutDesc = routDesc;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getEarnings() {
        return Earnings;
    }

    public void setEarnings(String earnings) {
        Earnings = earnings;
    }

    public String getSchemeName() {
        return SchemeName;
    }

    public void setSchemeName(String schemeName) {
        SchemeName = schemeName;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getLandingPrice() {
        return LandingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        LandingPrice = landingPrice;
    }

    public String getMargin() {
        return Margin;
    }

    public void setMargin(String margin) {
        Margin = margin;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getTradeOffer() {
        return TradeOffer;
    }

    public void setTradeOffer(String tradeOffer) {
        TradeOffer = tradeOffer;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getConsumerOffer() {
        return ConsumerOffer;
    }

    public void setConsumerOffer(String consumerOffer) {
        ConsumerOffer = consumerOffer;
    }

    public String getShelfLife() {
        return ShelfLife;
    }

    public void setShelfLife(String shelfLife) {
        ShelfLife = shelfLife;
    }

    public String getSpNo() {
        return SpNo;
    }

    public void setSpNo(String spNo) {
        SpNo = spNo;
    }

    public String getUpdatedOn() {
        return UpdatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        UpdatedOn = updatedOn;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getCpUID() {
        return cpUID;
    }

    public void setCpUID(String cpUID) {
        this.cpUID = cpUID;
    }

    public String getCPGUID32() {
        return CPGUID32;
    }

    public void setCPGUID32(String CPGUID32) {
        this.CPGUID32 = CPGUID32;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getWholesalerLandingPrice() {
        return WholesalerLandingPrice;
    }

    public void setWholesalerLandingPrice(String wholesalerLandingPrice) {
        WholesalerLandingPrice = wholesalerLandingPrice;
    }

    public String getRetailerLandingPrice() {
        return retailerLandingPrice;
    }

    public void setRetailerLandingPrice(String retailerLandingPrice) {
        this.retailerLandingPrice = retailerLandingPrice;
    }

    public String getSchemeLaunched() {
        return SchemeLaunched;
    }

    public void setSchemeLaunched(String schemeLaunched) {
        SchemeLaunched = schemeLaunched;
    }

    public String getSPGUID() {
        return SPGUID;
    }

    public void setSPGUID(String SPGUID) {
        this.SPGUID = SPGUID;
    }

    /************ Parcelable Implementations to send data from one component to other ************/
    public static final Creator<CompetitorBean> CREATOR = new Creator<CompetitorBean>() {
        @Override
        public CompetitorBean createFromParcel(Parcel in) {
            return new CompetitorBean(in);
        }

        @Override
        public CompetitorBean[] newArray(int size) {
            return new CompetitorBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected CompetitorBean(Parcel in) {
        SPName = in.readString();
        CPName = in.readString();
        RoutDesc = in.readString();
        Remarks = in.readString();
        Currency = in.readString();
        Earnings = in.readString();
        SchemeName = in.readString();
        CompanyName = in.readString();
        LandingPrice = in.readString();
        Margin = in.readString();
        MaterialDesc = in.readString();
        TradeOffer = in.readString();
        MRP = in.readString();
        ConsumerOffer = in.readString();
        ShelfLife = in.readString();
        SpNo = in.readString();
        UpdatedOn = in.readString();
        CPGUID = in.readString();
        cpUID = in.readString();
        CPGUID32 = in.readString();
        CPNo = in.readString();
        WholesalerLandingPrice = in.readString();
        retailerLandingPrice = in.readString();
        SchemeLaunched = in.readString();
        SPGUID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SPName);
        dest.writeString(CPName);
        dest.writeString(RoutDesc);
        dest.writeString(Remarks);
        dest.writeString(Currency);
        dest.writeString(Earnings);
        dest.writeString(SchemeName);
        dest.writeString(CompanyName);
        dest.writeString(LandingPrice);
        dest.writeString(Margin);
        dest.writeString(MaterialDesc);
        dest.writeString(TradeOffer);
        dest.writeString(MRP);
        dest.writeString(ConsumerOffer);
        dest.writeString(ShelfLife);
        dest.writeString(SpNo);
        dest.writeString(UpdatedOn);
        dest.writeString(CPGUID);
        dest.writeString(cpUID);
        dest.writeString(CPGUID32);
        dest.writeString(CPNo);
        dest.writeString(WholesalerLandingPrice);
        dest.writeString(retailerLandingPrice);
        dest.writeString(SchemeLaunched);
        dest.writeString(SPGUID);
    }
}
