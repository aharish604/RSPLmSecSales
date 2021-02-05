package com.arteriatech.ss.msecsales.rspl.mbo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by e10762 on 16-02-2017.
 */

public class ReturnOrderBean implements Parcelable {
    private String MRP = "";
    private String RLPrice = "";
    private String UnrestrictedQty = "";
    private String Uom = "";
    private String Tax = "";
    private String SerialNoTo = "";
    private String CPStockItemGUID = "";

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getAlterInvQty() {
        return AlterInvQty;
    }

    public void setAlterInvQty(String alterInvQty) {
        AlterInvQty = alterInvQty;
    }

    private String AlterInvQty = "";

    protected ReturnOrderBean(Parcel in) {
        AlterInvQty = in.readString();
        MRP = in.readString();
        RLPrice = in.readString();
        UnrestrictedQty = in.readString();
        Uom = in.readString();
        SerialNoTo = in.readString();
        CPStockItemGUID = in.readString();
        RefDocNo = in.readString();
        MaterialDesc = in.readString();
        MaterialNo = in.readString();
        QAQty = in.readString();
        BlockedQty = in.readString();
        StockValue = in.readString();
        LandingPrice = in.readString();
        OrderMaterialGroupID = in.readString();
        OrderMaterialGroupDesc = in.readString();
        OrderNo = in.readString();
        OrderDate = in.readString();
        SSROGUID = in.readString();
        NetAmount = in.readString();
        StatusID = in.readString();
        byte tmpIsDetailEnabled = in.readByte();
        IsDetailEnabled = tmpIsDetailEnabled == 0 ? null : tmpIsDetailEnabled == 1;
        sItemNo = in.readString();
        deviceNo = in.readString();
        Brand = in.readString();
        ProductCategoryID = in.readString();
        SSSOItemGUID = in.readString();
        byte tmpIsSelected = in.readByte();
        isSelected = tmpIsSelected == 0 ? null : tmpIsSelected == 1;
        byte tmpIsDisplayed = in.readByte();
        isDisplayed = tmpIsDisplayed == 0 ? null : tmpIsDisplayed == 1;
        itemNo = in.readInt();
        CPGUID = in.readString();
        SerialNoFrom = in.readString();
        Currency = in.readString();
        Batch = in.readString();
        returnQty = in.readString();
        returnDesc = in.readString();
        returnMrp = in.readString();
        returnBatchNumber = in.readString();
        returnReason = in.readString();
        byte tmpIsItemToReturn = in.readByte();
        isItemToReturn = tmpIsItemToReturn == 0 ? null : tmpIsItemToReturn == 1;
        MFD = in.readString();
        CrsSksGroup = in.readString();
        AlternativeUOM2 = in.readString();
        AlternativeUOM1 = in.readString();
        returnUOM = in.readString();
        AlternativeUOM1Num = in.readString();
        AlternativeUOM1Den = in.readString();
        altReturnMrp = in.readString();
        InvoiceGuid = in.readString();
        InvoiceNo = in.readString();
        RefDocItmGUID = in.readString();
        InvoiceDate = in.readString();
        actualQty = in.readString();
        UnitPrice = in.readString();
        Tax = in.readString();
        uomList = in.createStringArrayList();
        stockRefGUID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(AlterInvQty);
        dest.writeString(MRP);
        dest.writeString(RLPrice);
        dest.writeString(UnrestrictedQty);
        dest.writeString(Uom);
        dest.writeString(SerialNoTo);
        dest.writeString(CPStockItemGUID);
        dest.writeString(RefDocNo);
        dest.writeString(MaterialDesc);
        dest.writeString(MaterialNo);
        dest.writeString(QAQty);
        dest.writeString(BlockedQty);
        dest.writeString(StockValue);
        dest.writeString(LandingPrice);
        dest.writeString(OrderMaterialGroupID);
        dest.writeString(OrderMaterialGroupDesc);
        dest.writeString(OrderNo);
        dest.writeString(OrderDate);
        dest.writeString(SSROGUID);
        dest.writeString(NetAmount);
        dest.writeString(StatusID);
        dest.writeByte((byte) (IsDetailEnabled == null ? 0 : IsDetailEnabled ? 1 : 2));
        dest.writeString(sItemNo);
        dest.writeString(deviceNo);
        dest.writeString(Brand);
        dest.writeString(ProductCategoryID);
        dest.writeString(SSSOItemGUID);
        dest.writeByte((byte) (isSelected == null ? 0 : isSelected ? 1 : 2));
        dest.writeByte((byte) (isDisplayed == null ? 0 : isDisplayed ? 1 : 2));
        dest.writeInt(itemNo);
        dest.writeString(CPGUID);
        dest.writeString(SerialNoFrom);
        dest.writeString(Currency);
        dest.writeString(Batch);
        dest.writeString(returnQty);
        dest.writeString(returnDesc);
        dest.writeString(returnMrp);
        dest.writeString(returnBatchNumber);
        dest.writeString(returnReason);
        dest.writeByte((byte) (isItemToReturn == null ? 0 : isItemToReturn ? 1 : 2));
        dest.writeString(MFD);
        dest.writeString(CrsSksGroup);
        dest.writeString(AlternativeUOM2);
        dest.writeString(AlternativeUOM1);
        dest.writeString(returnUOM);
        dest.writeString(AlternativeUOM1Num);
        dest.writeString(AlternativeUOM1Den);
        dest.writeString(altReturnMrp);
        dest.writeString(InvoiceGuid);
        dest.writeString(InvoiceNo);
        dest.writeString(RefDocItmGUID);
        dest.writeString(InvoiceDate);
        dest.writeString(actualQty);
        dest.writeString(UnitPrice);
        dest.writeString(Tax);
        dest.writeStringList(uomList);
        dest.writeString(stockRefGUID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReturnOrderBean> CREATOR = new Creator<ReturnOrderBean>() {
        @Override
        public ReturnOrderBean createFromParcel(Parcel in) {
            return new ReturnOrderBean(in);
        }

        @Override
        public ReturnOrderBean[] newArray(int size) {
            return new ReturnOrderBean[size];
        }
    };

    public String getRefDocNo() {
        return RefDocNo;
    }

    public void setRefDocNo(String refDocNo) {
        RefDocNo = refDocNo;
    }

    private String RefDocNo = "";
    private String MaterialDesc = "";
    private String MaterialNo = "";
    private String QAQty = "";
    private String BlockedQty = "";
    private String StockValue = "";
    private String LandingPrice = "";
    private String OrderMaterialGroupID = "";
    private String OrderMaterialGroupDesc = "";
    private String OrderNo;
    private String OrderDate;
    private String SSROGUID;
    private String NetAmount;
    private String StatusID;
    private Boolean IsDetailEnabled = false;
    private String sItemNo;
    private String deviceNo;
    private String Brand = "";

    public String getZZIsBomMaterial() {
        return ZZIsBomMaterial;
    }

    public void setZZIsBomMaterial(String ZZIsBomMaterial) {
        this.ZZIsBomMaterial = ZZIsBomMaterial;
    }

    private String ZZIsBomMaterial = "";

    public String getHigherLevelItm() {
        return HigherLevelItm;
    }

    public void setHigherLevelItm(String higherLevelItm) {
        HigherLevelItm = higherLevelItm;
    }

    private String RefDocItmGUID = "";
    private String HigherLevelItm = "";
    private String ProductCategoryID = "";
    private String SSSOItemGUID = "";
    private Boolean isSelected = false;
    private Boolean isDisplayed = false;
    private int itemNo = 0;
    private String CPGUID = "";
    private String SerialNoFrom = "";
    private String Currency = "";
    private String Batch = "";
    private String returnQty = "";
    private String returnDesc = "";
    private String returnMrp = "";
    private String returnBatchNumber = "";
    private String returnReason = "";
    private Boolean isItemToReturn = false;
    private String MFD = "";
    private String CrsSksGroup = "";
    private String AlternativeUOM2 = "";
    private String AlternativeUOM1 = "";
    private String returnUOM = "";
    private String AlternativeUOM1Num = "";
    private String AlternativeUOM1Den = "";
    private String altReturnMrp = "";
    private String InvoiceGuid = "";
    private String InvoiceNo = "";

    public String getRefDocItmGUID() {
        return RefDocItmGUID;
    }

    public void setRefDocItmGUID(String refDocItmGUID) {
        RefDocItmGUID = refDocItmGUID;
    }

    private String InvoiceDate = "";
    private String actualQty = "";
    private String UnitPrice = "";

    public String getStockRefGUID() {
        return stockRefGUID;
    }

    public void setStockRefGUID(String stockRefGUID) {
        this.stockRefGUID = stockRefGUID;
    }

    private String stockRefGUID = "";
    private ArrayList<String> uomList = new ArrayList<>();

    public ReturnOrderBean() {

    }



    public String getInvoiceGuid() {
        return InvoiceGuid;
    }

    public void setInvoiceGuid(String invoiceGuid) {
        InvoiceGuid = invoiceGuid;
    }

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getSSSOItemGUID() {
        return SSSOItemGUID;
    }

    public void setSSSOItemGUID(String SSSOItemGUID) {
        this.SSSOItemGUID = SSSOItemGUID;
    }

    public String getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(String returnQty) {
        this.returnQty = returnQty;
    }

    public String getReturnMrp() {
        return returnMrp;
    }

    public void setReturnMrp(String returnMrp) {
        this.returnMrp = returnMrp;
    }

    public String getReturnBatchNumber() {
        return returnBatchNumber;
    }

    public void setReturnBatchNumber(String returnBatchNumber) {
        this.returnBatchNumber = returnBatchNumber;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public Boolean getItemToReturn() {
        return isItemToReturn;
    }

    public void setItemToReturn(Boolean itemToReturn) {
        isItemToReturn = itemToReturn;
    }

    public String getLandingPrice() {
        return LandingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        LandingPrice = landingPrice;
    }

    public String getOrderMaterialGroupID() {
        return OrderMaterialGroupID;
    }

    public void setOrderMaterialGroupID(String orderMaterialGroupID) {
        OrderMaterialGroupID = orderMaterialGroupID;
    }

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }

    public String getMFD() {
        return MFD;
    }

    public void setMFD(String MFD) {
        this.MFD = MFD;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getRLPrice() {
        return RLPrice;
    }

    public void setRLPrice(String RLPrice) {
        this.RLPrice = RLPrice;
    }

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getSerialNoTo() {
        return SerialNoTo;
    }

    public void setSerialNoTo(String serialNoTo) {
        SerialNoTo = serialNoTo;
    }

    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getQAQty() {
        return QAQty;
    }

    public void setQAQty(String QAQty) {
        this.QAQty = QAQty;
    }

    public String getBlockedQty() {
        return BlockedQty;
    }

    public void setBlockedQty(String blockedQty) {
        BlockedQty = blockedQty;
    }

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getSerialNoFrom() {
        return SerialNoFrom;
    }

    public void setSerialNoFrom(String serialNoFrom) {
        SerialNoFrom = serialNoFrom;
    }

    public String getCrsSksGroup() {
        return CrsSksGroup;
    }

    public void setCrsSksGroup(String crsSksGroup) {
        CrsSksGroup = crsSksGroup;
    }

    public String getReturnDesc() {
        return returnDesc;
    }

    public void setReturnDesc(String returnDesc) {
        this.returnDesc = returnDesc;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(Boolean displayed) {
        isDisplayed = displayed;
    }

    public int getItemNo() {
        return itemNo;
    }

    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getSSROGUID() {
        return SSROGUID;
    }

    public void setSSROGUID(String SSROGUID) {
        this.SSROGUID = SSROGUID;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getStatusID() {
        return StatusID;
    }

    public void setStatusID(String statusID) {
        StatusID = statusID;
    }

    public Boolean getDetailEnabled() {
        return IsDetailEnabled;
    }

    public void setDetailEnabled(Boolean detailEnabled) {
        IsDetailEnabled = detailEnabled;
    }

    public String getsItemNo() {
        return sItemNo;
    }

    public void setsItemNo(String sItemNo) {
        this.sItemNo = sItemNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getProductCategoryID() {
        return ProductCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        ProductCategoryID = productCategoryID;
    }



    public ArrayList<String> getUomList() {
        return uomList;
    }

    public void setUomList(ArrayList<String> uomList) {
        this.uomList = uomList;
    }

    public String getAlternativeUOM2() {
        return AlternativeUOM2;
    }

    public void setAlternativeUOM2(String alternativeUOM2) {
        AlternativeUOM2 = alternativeUOM2;
    }

    public String getAlternativeUOM1() {
        return AlternativeUOM1;
    }

    public void setAlternativeUOM1(String alternativeUOM1) {
        AlternativeUOM1 = alternativeUOM1;
    }

    public String getReturnUOM() {
        return returnUOM;
    }

    public void setReturnUOM(String returnUOM) {
        this.returnUOM = returnUOM;
    }

    public String getAlternativeUOM1Num() {
        return AlternativeUOM1Num;
    }

    public void setAlternativeUOM1Num(String alternativeUOM1Num) {
        AlternativeUOM1Num = alternativeUOM1Num;
    }

    public String getAlternativeUOM1Den() {
        return AlternativeUOM1Den;
    }

    public void setAlternativeUOM1Den(String alternativeUOM1Den) {
        AlternativeUOM1Den = alternativeUOM1Den;
    }

    public String getAltReturnMrp() {
        return altReturnMrp;
    }

    public void setAltReturnMrp(String altReturnMrp) {
        this.altReturnMrp = altReturnMrp;
    }

    public String getActualQty() {
        return actualQty;
    }

    public void setActualQty(String actualQty) {
        this.actualQty = actualQty;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }
}
