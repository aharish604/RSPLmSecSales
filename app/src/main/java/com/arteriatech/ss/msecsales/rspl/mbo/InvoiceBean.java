package com.arteriatech.ss.msecsales.rspl.mbo;

/**
 * Created by e10604 on 16/4/2016.
 */
public class InvoiceBean {

    String matCode;
    String matDesc;
    String invoiceNo = "";
    String invoiceAmount = "";
    String CPStockItemGUID = "";
    String SPSNoGUID = "";
    String SerialNoFrom = "";
    String SerialNoTo = "";
    String SelectedSerialNoFrom = "";
    String SelectedSerialNoTo = "";
    String OldSPSNoGUID = "";
    String invoiceGUID = "";
    String Zzindicator = "";

    String UnitPrice = "0";
    String DueDays = "";
    String PaidAmount = "0";
    String InvoiceDate = "";
    String TaxAmt = "0";
    String NetAmt = "0";
    String CollectionAmount = "";
    String Currency = "";
    String StockTypeID = "";
    String UnrestrictedQty = "";
    String invQty = "";
    String StockValue = "";
    String DeviceInvStatus = "";
    String invoiceOutstanding = "";
    String uom = "";
    String Sequence = "";
    String TempSpSnoGuid = "";
    String Etag = "";
    String MatGrp = "";
    String PrefixLength = "";
    String Status = "";
    String InputInvAmount = "";
    boolean ItemSelected = false;
    String Option = "";
    int position;
    String matGroupCode;
    String matGroupDesc;
    private String discountPer="";
    private String discountAmt="";
    private String discountEnteredAmt="";

    public String getDueDays() {
        return DueDays;
    }

    public void setDueDays(String dueDays) {
        DueDays = dueDays;
    }

    public String getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        PaidAmount = paidAmount;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(String taxAmt) {
        TaxAmt = taxAmt;
    }

    public String getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(String netAmt) {
        NetAmt = netAmt;
    }

    public String getCollectionAmount() {
        return CollectionAmount;
    }

    public void setCollectionAmount(String collectionAmount) {
        CollectionAmount = collectionAmount;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getStockTypeID() {
        return StockTypeID;
    }

    public void setStockTypeID(String stockTypeID) {
        StockTypeID = stockTypeID;
    }

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    public String getInvQty() {
        return invQty;
    }

    public void setInvQty(String invQty) {
        this.invQty = invQty;
    }

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getZzindicator() {
        return Zzindicator;
    }

    public void setZzindicator(String zzindicator) {
        Zzindicator = zzindicator;
    }

    public String getDeviceInvStatus() {
        return DeviceInvStatus;
    }

    public void setDeviceInvStatus(String deviceInvStatus) {
        DeviceInvStatus = deviceInvStatus;
    }

    public String getInvoiceOutstanding() {
        return invoiceOutstanding;
    }

    public void setInvoiceOutstanding(String invoiceOutstanding) {
        this.invoiceOutstanding = invoiceOutstanding;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getInvoiceGUID() {
        return invoiceGUID;
    }

    public void setInvoiceGUID(String invoiceGUID) {
        this.invoiceGUID = invoiceGUID;
    }

    public String getSequence() {
        return Sequence;
    }

    public void setSequence(String sequence) {
        Sequence = sequence;
    }

    public String getTempSpSnoGuid() {
        return TempSpSnoGuid;
    }

    public void setTempSpSnoGuid(String tempSpSnoGuid) {
        TempSpSnoGuid = tempSpSnoGuid;
    }

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }

    public String getMatGrp() {
        return MatGrp;
    }

    public void setMatGrp(String matGrp) {
        MatGrp = matGrp;
    }

    public String getPrefixLength() {
        return PrefixLength;
    }

    public void setPrefixLength(String prefixLength) {
        PrefixLength = prefixLength;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getOldSPSNoGUID() {
        return OldSPSNoGUID;
    }

    public void setOldSPSNoGUID(String oldSPSNoGUID) {
        OldSPSNoGUID = oldSPSNoGUID;
    }

    public String getInputInvAmount() {
        return InputInvAmount;
    }

    public void setInputInvAmount(String inputInvAmount) {
        InputInvAmount = inputInvAmount;
    }

    public boolean isItemSelected() {
        return ItemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        ItemSelected = itemSelected;
    }

    public String getSelectedSerialNoTo() {
        return SelectedSerialNoTo;
    }

    public void setSelectedSerialNoTo(String selectedSerialNoTo) {
        SelectedSerialNoTo = selectedSerialNoTo;
    }

    public String getSelectedSerialNoFrom() {
        return SelectedSerialNoFrom;
    }

    public void setSelectedSerialNoFrom(String selectedSerialNoFrom) {
        SelectedSerialNoFrom = selectedSerialNoFrom;
    }

    public String getOption() {
        return Option;
    }

    public void setOption(String option) {
        Option = option;
    }

    public String getSerialNoTo() {
        return SerialNoTo;
    }

    public void setSerialNoTo(String serialNoTo) {
        SerialNoTo = serialNoTo;
    }

    public String getSPSNoGUID() {
        return SPSNoGUID;
    }

    public void setSPSNoGUID(String SPSNoGUID) {
        this.SPSNoGUID = SPSNoGUID;
    }

    public String getSerialNoFrom() {
        return SerialNoFrom;
    }

    public void setSerialNoFrom(String serialNoFrom) {
        SerialNoFrom = serialNoFrom;
    }

    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getMatGroupCode() {
        return matGroupCode;
    }

    public void setMatGroupCode(String matGroupCode) {
        this.matGroupCode = matGroupCode;
    }

    public String getMatGroupDesc() {
        return matGroupDesc;
    }

    public void setMatGroupDesc(String matGroupDesc) {
        this.matGroupDesc = matGroupDesc;
    }

    public String getMatDesc() {
        return matDesc;
    }

    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }


    public String getDiscountPer() {
        return discountPer;
    }

    public void setDiscountPer(String discountPer) {
        this.discountPer = discountPer;
    }

    public String getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(String discountAmt) {
        this.discountAmt = discountAmt;
    }

    public String getDiscountEnteredAmt() {
        return discountEnteredAmt;
    }

    public void setDiscountEnteredAmt(String discountEnteredAmt) {
        this.discountEnteredAmt = discountEnteredAmt;
    }
}
