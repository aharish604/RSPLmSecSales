package com.arteriatech.ss.msecsales.rspl.mbo;


import java.util.ArrayList;

/**
 * Created by e10526 on 12/22/2016.
 */

public class SKUGroupBean {
    public String ISFreeTypeID = "";

    public String getOnSaleOfCatID() {
        return OnSaleOfCatID;
    }

    public void setOnSaleOfCatID(String onSaleOfCatID) {
        OnSaleOfCatID = onSaleOfCatID;
    }

    public String OnSaleOfCatID = "";
    public String RatioSchMatPrice = "";
    public String FreeMatDisAmt = "";
    public String RatioSchDisAmt = "";
    public String SlabTypeID = "";
    public String SlabTypeDesc = "";
    public String IsIncludingPrimary = "";
    public String SlabRuleID = "";
    public String SlabRuleDesc = "";
    public String TaxAmount = "";
    public String SchemeSlabRule = "";
    String LastInvoiceNo = "";
    String LastMaterialNo = "";
    String SKUGroup = "";
    String MRP = "";
    String DBSTK = "";
    String RETSTK = "";
    String RETSTKUOM = "";
    String SOQ = "";
    String ORDQty = "";
    String NetAmount = "";
    String PRMScheme = "";
    String SecScheme = "0";
    String Brand = "";
    String Category = "";
    String MatTypeVal = "";
    String MatTypeDesc = "";
    String Currency = "";
    String MaterialDesc = "";
    String MaterialNo = "";
    String MonthThree = "";
    String MonthOne = "";
    String MonthTwo = "";
    String AvgThreeMnts = "";
    String AvgPerThreeMnts = "";
    Double UnResQty = 0.0;
    String SchemeQPSActive = "";
    /*
   following Strings for Scheme Sales Area
    */
    String SalesOrgID = "";
    String DistributionChannelID = "";
    String DMSDivisionID = "";
    String DivisionID = "";
    String CPGroup1ID = "";
    String CPGroup2ID = "";
    String CPGroup3ID = "";
    String CPGroup4ID = "";
    /*
    following String for SchemeGeographies
     */
    String GeographyScopeID = "";
    String GeographyLevelID = "";
    String GeographyTypeID = "";
    String GeographyValueID = "";
    String CPTypeID = "";
    String CPGUID = "";
    String CPNo = "";
    String IsSchemeActive = "";
    String SSSOGuid = "";
    String CPStockItemGUID = "";
    String LastPurchasedMaterial = "";
    String ChildItemTag = "";
    String UOM = "";
    String UnBilledStatus = "";
    boolean IsQtyEntered = false;

    public boolean isSchemeFreeMaterial() {
        return IsSchemeFreeMaterial;
    }

    public void setSchemeFreeMaterial(boolean schemeFreeMaterial) {
        IsSchemeFreeMaterial = schemeFreeMaterial;
    }

    boolean IsSchemeFreeMaterial = false;
    String SKUGroupDesc = "";
    boolean MustSell = false;
    private ArrayList<String> SchemeGuid = new ArrayList<>();
    private String etQty = "";
    private boolean isFocusHeaderText = false;
    private boolean isHeader = false;
    private boolean isViewOpened = false;
    private boolean isItemTyping = false;
    private int setCursorPos = -1;
    private boolean matLevelImageDisplay = false;
    private ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList = new ArrayList<>();
    private ArrayList<SKUGroupBean> skuSubGroupBeanArrayList = new ArrayList<>();
    private String OrgScopeID = "";
    private String isMaterialActive = "";
    private ArrayList<SKUGroupItemBean> skuGroupItemBean = null;
    private String QPSSchemeGuid = "";
    private String NoOfCards = "";
    private String CardTitle = "";
    private String FreeArticle = "";
    private String FreeQty = "";
    private String OrderMaterialGroupID = "";
    private String OrderMaterialGroupDesc = "";
    private SchemeBean MaterialBatchBean = null;
    private String Banner = "";
    private String SKUGroupID = "";
    private String ProductCategoryID = "";
    private String SKU = "";
    private String ISFreeMat = "";
    private String Batch = "";
    private String SecSchemeAmt = "0";
    private String AlternativeUOM2 = "";
    private String AlternativeUOM2Num = "";
    private String AlternativeUOM2Den = "";
    private String AlternativeUOM1 = "";
    private String AlternativeUOM1Num = "";
    private String AlternativeUOM1Den = "";
    private String SelectedUOM = "";
    private boolean IsUOMChanged = false;

    public String getRETSTKUOM() {
        return RETSTKUOM;
    }

    public void setRETSTKUOM(String RETSTKUOM) {
        this.RETSTKUOM = RETSTKUOM;
    }
    public boolean isUOMChanged() {
        return IsUOMChanged;
    }

    public void setUOMChanged(boolean UOMChanged) {
        IsUOMChanged = UOMChanged;
    }

    public String getSelectedUOMVal() {
        return SelectedUOMVal;
    }

    public void setSelectedUOMVal(String selectedUOMVal) {
        SelectedUOMVal = selectedUOMVal;
    }

    private String SelectedUOMVal = "";

    public String getNotSelectedUOMVal() {
        return NotSelectedUOMVal;
    }

    public void setNotSelectedUOMVal(String notSelectedUOMVal) {
        NotSelectedUOMVal = notSelectedUOMVal;
    }

    private String NotSelectedUOMVal = "";

    public String getCBBQty() {
        return CBBQty;
    }

    public void setCBBQty(String CBBQty) {
        this.CBBQty = CBBQty;
    }

    private String CBBQty = "";
    private ArrayList<String> alUOM = new ArrayList<>();
    public String getSelectedUOM() {
        return SelectedUOM;
    }

    public void setSelectedUOM(String selectedUOM) {
        SelectedUOM = selectedUOM;
    }



    public ArrayList<String> getAlUOM() {
        return alUOM;
    }

    public void setAlUOM(ArrayList<String> alUOM) {
        this.alUOM = alUOM;
    }


    public String getAlternativeUOM2() {
        return AlternativeUOM2;
    }

    public void setAlternativeUOM2(String alternativeUOM2) {
        AlternativeUOM2 = alternativeUOM2;
    }

    public String getAlternativeUOM2Num() {
        return AlternativeUOM2Num;
    }

    public void setAlternativeUOM2Num(String alternativeUOM2Num) {
        AlternativeUOM2Num = alternativeUOM2Num;
    }

    public String getAlternativeUOM2Den() {
        return AlternativeUOM2Den;
    }

    public void setAlternativeUOM2Den(String alternativeUOM2Den) {
        AlternativeUOM2Den = alternativeUOM2Den;
    }

    public String getAlternativeUOM1() {
        return AlternativeUOM1;
    }

    public void setAlternativeUOM1(String alternativeUOM1) {
        AlternativeUOM1 = alternativeUOM1;
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

    /*
    following Strings for SchemeCPs
     */

    public Double getUnResQty() {
        return UnResQty;
    }

    public void setUnResQty(Double unResQty) {
        UnResQty = unResQty;
    }

    public String getRatioSchMatPrice() {
        return RatioSchMatPrice;
    }

    public void setRatioSchMatPrice(String ratioSchMatPrice) {
        RatioSchMatPrice = ratioSchMatPrice;
    }

    public String getFreeMatDisAmt() {
        return FreeMatDisAmt;
    }

    public void setFreeMatDisAmt(String freeMatDisAmt) {
        FreeMatDisAmt = freeMatDisAmt;
    }

    public String getISFreeTypeID() {
        return ISFreeTypeID;
    }

    public void setISFreeTypeID(String ISFreeTypeID) {
        this.ISFreeTypeID = ISFreeTypeID;
    }

    public String getRatioSchDisAmt() {
        return RatioSchDisAmt;
    }

    public void setRatioSchDisAmt(String ratioSchDisAmt) {
        RatioSchDisAmt = ratioSchDisAmt;
    }

    public String getSchemeQPSActive() {
        return SchemeQPSActive;
    }

    public void setSchemeQPSActive(String schemeQPSActive) {
        SchemeQPSActive = schemeQPSActive;
    }

    public ArrayList<String> getSchemeGuid() {
        return SchemeGuid;
    }

    public void setSchemeGuid(ArrayList<String> schemeGuid) {
        SchemeGuid = schemeGuid;
    }

    public String getTaxAmount() {
        return TaxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        TaxAmount = taxAmount;
    }

    public String getQPSSchemeGuid() {
        return QPSSchemeGuid;
    }

    public void setQPSSchemeGuid(String QPSSchemeGuid) {
        this.QPSSchemeGuid = QPSSchemeGuid;
    }

    public String getSlabRuleDesc() {
        return SlabRuleDesc;
    }

    public void setSlabRuleDesc(String slabRuleDesc) {
        SlabRuleDesc = slabRuleDesc;
    }

    public String getSlabRuleID() {
        return SlabRuleID;
    }

    public void setSlabRuleID(String slabRuleID) {
        SlabRuleID = slabRuleID;
    }

    public String getIsIncludingPrimary() {
        return IsIncludingPrimary;
    }

    public void setIsIncludingPrimary(String isIncludingPrimary) {
        IsIncludingPrimary = isIncludingPrimary;
    }

    public String getSlabTypeID() {
        return SlabTypeID;
    }

    public void setSlabTypeID(String slabTypeID) {
        SlabTypeID = slabTypeID;
    }

    public String getSlabTypeDesc() {
        return SlabTypeDesc;
    }

    public void setSlabTypeDesc(String slabTypeDesc) {
        SlabTypeDesc = slabTypeDesc;
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

    public String getGeographyScopeID() {
        return GeographyScopeID;
    }

    public void setGeographyScopeID(String geographyScopeID) {
        GeographyScopeID = geographyScopeID;
    }

    public String getGeographyLevelID() {
        return GeographyLevelID;
    }

    public void setGeographyLevelID(String geographyLevelID) {
        GeographyLevelID = geographyLevelID;
    }

    public String getGeographyTypeID() {
        return GeographyTypeID;
    }

    public void setGeographyTypeID(String geographyTypeID) {
        GeographyTypeID = geographyTypeID;
    }

    public String getGeographyValueID() {
        return GeographyValueID;
    }

    public void setGeographyValueID(String geographyValueID) {
        GeographyValueID = geographyValueID;
    }

    public String getCPGroup1ID() {
        return CPGroup1ID;
    }

    public void setCPGroup1ID(String CPGroup1ID) {
        this.CPGroup1ID = CPGroup1ID;
    }

    public String getCPGroup2ID() {
        return CPGroup2ID;
    }

    public void setCPGroup2ID(String CPGroup2ID) {
        this.CPGroup2ID = CPGroup2ID;
    }

    public String getCPGroup3ID() {
        return CPGroup3ID;
    }

    public void setCPGroup3ID(String CPGroup3ID) {
        this.CPGroup3ID = CPGroup3ID;
    }

    public String getCPGroup4ID() {
        return CPGroup4ID;
    }

    public void setCPGroup4ID(String CPGroup4ID) {
        this.CPGroup4ID = CPGroup4ID;
    }

    public String getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(String divisionID) {
        DivisionID = divisionID;
    }

    public String getDMSDivisionID() {
        return DMSDivisionID;
    }

    public void setDMSDivisionID(String DMSDivisionID) {
        this.DMSDivisionID = DMSDivisionID;
    }

    public String getSalesOrgID() {
        return SalesOrgID;
    }

    public void setSalesOrgID(String salesOrgID) {
        SalesOrgID = salesOrgID;
    }

    public String getDistributionChannelID() {
        return DistributionChannelID;
    }

    public void setDistributionChannelID(String distributionChannelID) {
        DistributionChannelID = distributionChannelID;
    }

    public String getIsSchemeActive() {
        return IsSchemeActive;
    }

    public void setIsSchemeActive(String isSchemeActive) {
        IsSchemeActive = isSchemeActive;
    }

    public String getSSSOGuid() {
        return SSSOGuid;
    }

    public void setSSSOGuid(String SSSOGuid) {
        this.SSSOGuid = SSSOGuid;
    }

    public String getAvgPerThreeMnts() {
        return AvgPerThreeMnts;
    }

    public void setAvgPerThreeMnts(String avgPerThreeMnts) {
        AvgPerThreeMnts = avgPerThreeMnts;
    }

    public String getAvgThreeMnts() {
        return AvgThreeMnts;
    }

    public void setAvgThreeMnts(String avgThreeMnts) {
        AvgThreeMnts = avgThreeMnts;
    }

    public String getMonthThree() {
        return MonthThree;
    }

    public void setMonthThree(String monthThree) {
        MonthThree = monthThree;
    }

    public String getMonthTwo() {
        return MonthTwo;
    }

    public void setMonthTwo(String monthTwo) {
        MonthTwo = monthTwo;
    }

    public String getMonthOne() {
        return MonthOne;
    }

    public void setMonthOne(String monthOne) {
        MonthOne = monthOne;
    }

    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getChildItemTag() {
        return ChildItemTag;
    }

    public void setChildItemTag(String childItemTag) {
        ChildItemTag = childItemTag;
    }

    public String getLastPurchasedMaterial() {
        return LastPurchasedMaterial;
    }

    public void setLastPurchasedMaterial(String lastPurchasedMaterial) {
        LastPurchasedMaterial = lastPurchasedMaterial;
    }

    public String getLastMaterialNo() {
        return LastMaterialNo;
    }

    public void setLastMaterialNo(String lastMaterialNo) {
        LastMaterialNo = lastMaterialNo;
    }

    public String getLastInvoiceNo() {
        return LastInvoiceNo;
    }

    public void setLastInvoiceNo(String lastInvoiceNo) {
        LastInvoiceNo = lastInvoiceNo;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getUnBilledStatus() {
        return UnBilledStatus;
    }

    public void setUnBilledStatus(String unBilledStatus) {
        UnBilledStatus = unBilledStatus;
    }

    public boolean isQtyEntered() {
        return IsQtyEntered;
    }

    public void setQtyEntered(boolean qtyEntered) {
        IsQtyEntered = qtyEntered;
    }

    public String getMatTypeVal() {
        return MatTypeVal;
    }

    public void setMatTypeVal(String matTypeVal) {
        MatTypeVal = matTypeVal;
    }

    public String getMatTypeDesc() {
        return MatTypeDesc;
    }

    public void setMatTypeDesc(String matTypeDesc) {
        MatTypeDesc = matTypeDesc;
    }

    public String getSKUGroupDesc() {
        return SKUGroupDesc;
    }

    public void setSKUGroupDesc(String SKUGroupDesc) {
        this.SKUGroupDesc = SKUGroupDesc;
    }

    public boolean isMustSell() {
        return MustSell;
    }

    public void setMustSell(boolean mustSell) {
        MustSell = mustSell;
    }

    public String getSKUGroup() {
        return SKUGroup;
    }

    public void setSKUGroup(String SKUGroup) {
        this.SKUGroup = SKUGroup;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getDBSTK() {
        return DBSTK;
    }

    public void setDBSTK(String DBSTK) {
        this.DBSTK = DBSTK;
    }

    public String getRETSTK() {
        return RETSTK;
    }

    public void setRETSTK(String RETSTK) {
        this.RETSTK = RETSTK;
    }

    public String getSOQ() {
        return SOQ;
    }

    public void setSOQ(String SOQ) {
        this.SOQ = SOQ;
    }

    public String getORDQty() {
        return ORDQty;
    }

    public void setORDQty(String ORDQty) {
        this.ORDQty = ORDQty;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getPRMScheme() {
        return PRMScheme;
    }

    public void setPRMScheme(String PRMScheme) {
        this.PRMScheme = PRMScheme;
    }

    public String getSecScheme() {
        return SecScheme;
    }

    public void setSecScheme(String secScheme) {
        SecScheme = secScheme;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getOrgScopeID() {
        return OrgScopeID;
    }

    public void setOrgScopeID(String orgScopeID) {
        OrgScopeID = orgScopeID;
    }

    public String getIsMaterialActive() {
        return isMaterialActive;
    }

    public void setIsMaterialActive(String isMaterialActive) {
        this.isMaterialActive = isMaterialActive;
    }

    public ArrayList<SKUGroupItemBean> getSkuGroupItemBean() {
        return skuGroupItemBean;
    }

    public void setSkuGroupItemBean(ArrayList<SKUGroupItemBean> skuGroupItemBean) {
        this.skuGroupItemBean = skuGroupItemBean;
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

    public String getCardTitle() {
        return CardTitle;
    }

    public void setCardTitle(String cardTitle) {
        CardTitle = cardTitle;
    }

    public String getFreeArticle() {
        return FreeArticle;
    }

    public void setFreeArticle(String freeArticle) {
        FreeArticle = freeArticle;
    }

    public String getFreeQty() {
        return FreeQty;
    }

    public void setFreeQty(String freeQty) {
        FreeQty = freeQty;
    }

    public String getNoOfCards() {
        return NoOfCards;
    }

    public void setNoOfCards(String noOfCards) {
        NoOfCards = noOfCards;
    }

    public SchemeBean getMaterialBatchBean() {
        return MaterialBatchBean;
    }

    public void setMaterialBatchBean(SchemeBean MaterialBatchBean) {
        this.MaterialBatchBean = MaterialBatchBean;
    }

    public String getBanner() {
        return Banner;
    }

    public void setBanner(String banner) {
        Banner = banner;
    }

    public String getSKUGroupID() {
        return SKUGroupID;
    }

    public void setSKUGroupID(String SKUGroupID) {
        this.SKUGroupID = SKUGroupID;
    }

    public String getProductCategoryID() {
        return ProductCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        ProductCategoryID = productCategoryID;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getISFreeMat() {
        return ISFreeMat;
    }

    public void setISFreeMat(String ISFreeMat) {
        this.ISFreeMat = ISFreeMat;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getSchemeSlabRule() {
        return SchemeSlabRule;
    }

    public void setSchemeSlabRule(String schemeSlabRule) {
        SchemeSlabRule = schemeSlabRule;
    }

    public String getSecSchemeAmt() {
        return SecSchemeAmt;
    }

    public void setSecSchemeAmt(String secSchemeAmt) {
        SecSchemeAmt = secSchemeAmt;
    }

    public String getEtQty() {
        return etQty;
    }

    public void setEtQty(String etQty) {
        this.etQty = etQty;
    }

    public ArrayList<SKUGroupBean> getSkuSubGroupBeanArrayList() {
        return skuSubGroupBeanArrayList;
    }

    public void setSkuSubGroupBeanArrayList(ArrayList<SKUGroupBean> skuSubGroupBeanArrayList) {
        this.skuSubGroupBeanArrayList = skuSubGroupBeanArrayList;
    }

    public boolean isFocusHeaderText() {
        return isFocusHeaderText;
    }

    public void setFocusHeaderText(boolean focusHeaderText) {
        isFocusHeaderText = focusHeaderText;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isViewOpened() {
        return isViewOpened;
    }

    public void setViewOpened(boolean viewOpened) {
        isViewOpened = viewOpened;
    }

    public boolean isItemTyping() {
        return isItemTyping;
    }

    public void setItemTyping(boolean itemTyping) {
        isItemTyping = itemTyping;
    }

    public int getSetCursorPos() {
        return setCursorPos;
    }

    public void setSetCursorPos(int setCursorPos) {
        this.setCursorPos = setCursorPos;
    }

    public boolean isMatLevelImageDisplay() {
        return matLevelImageDisplay;
    }

    public void setMatLevelImageDisplay(boolean matLevelImageDisplay) {
        this.matLevelImageDisplay = matLevelImageDisplay;
    }


    public ArrayList<SchemeCalcuBean> getSchemeCalcuBeanArrayList() {
        return schemeCalcuBeanArrayList;
    }

    public void setSchemeCalcuBeanArrayList(ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList) {
        this.schemeCalcuBeanArrayList = schemeCalcuBeanArrayList;
    }

    @Override
    public String toString() {
        return OrderMaterialGroupDesc;
    }
}
