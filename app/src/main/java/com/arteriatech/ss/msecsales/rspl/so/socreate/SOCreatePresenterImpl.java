package com.arteriatech.ss.msecsales.rspl.so.socreate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DBFilterDialogFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MustSellBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyTargetsBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.scheme.GetSalesOrderListBasedOnScheme;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeIDBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by e10526 on 21-04-2018.
 */

public class SOCreatePresenterImpl implements SOCreatePresenter, OnlineODataInterface, SOCreatePresenter.IDealerStockMaterialPresenter {
    MyTargetsBean salesKpi = null;
    int mIntBalVisitRet = 0;
    HashMap<String, MustSellBean> hashMapMustSell = new HashMap<>();
    ArrayList<HashMap<String, String>> alMapDBStkUOM = new ArrayList<>();
    ArrayList<HashMap<String, String>> alMapRetStkUOM = new ArrayList<>();
    ArrayList<HashMap<String, String>> alMapMaterialDBStkUOM = new ArrayList<>();
    HashMap<String, String> hashMapDBStk = new HashMap<>();
    HashMap<String, String> hashMapUOM = new HashMap<>();
    HashSet<String> mSetCpStockItemGuid = new HashSet<>();
    HashMap<String, String> hashMapSegmentedMat = new HashMap<>();
    HashMap<String, String> hashMapTargetByCrsskugrp = new HashMap<>();
    HashMap<String, String> hashMapInvQtyByCrsskugrp = new HashMap<>();
    HashMap<String, String> hashMapRetailerStk = new HashMap<>();
    HashMap<String, String> hashMapRetailerStkUOM = new HashMap<>();
    HashMap<String, String> hashMapRetailerStkByMat = new HashMap<>();
    ArrayList<SchemeIDBean> orderMaterialGrpSchemeId = new ArrayList<>();
    HashMap<String, ArrayList<String>> hashMapSchemeGuidMatByMaterial = new HashMap<>();
    ArrayList<SKUGroupBean> alCRSSKUGrpList = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;
    private SOCreateView soCreateView;
    private boolean isSessionRequired;
    private ArrayList<ValueHelpBean> alFeedBackType = new ArrayList<>();
    private ArrayList<ValueHelpBean> alFeedbackSubType = new ArrayList<>();
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
    private ODataDuration mStartTimeDuration;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private String[][] mArrayCPDMSDivisoins = null;
    private SOCreateBean soCreateBean = null;
    private DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
    private String mStrSSOListQry = "", mStrInvCurrentMntQry = "";
    private String stockOwner = "", mStrParentID = "", mStrCPTypeID = "";
    private String mStrBMT = "", mStrTLSD = "";
    private String mTargetQry = "";
    private String searchText = "";
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();

    private String divisionID = "", stockType, distributorGUID = "", dmsDistributorID = "", mStrSelBrandId = "", mStrSelCategoryId = "", mStrMustSellType = "";

    public SOCreatePresenterImpl(Context mContext, SOCreateView soCreateView, boolean isSessionRequired, Activity mActivity, SOCreateBean soCreateBean) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.soCreateView = soCreateView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.soCreateBean = soCreateBean;
    }


    @Override
    public void onStart() {
//        requestSOData();
        getCPSPRelationDivisions();
    }

    private void getCPSPRelationDivisions() {
//        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(soCreateBean.getCPGUID()),mContext);
//        String mStrDistQry = Constants.CPSPRelations+ "?$filter=" + Constants.CPGUID + " eq '"+mArraySPValues[1][0] +"'";
        ArrayList<String> divisionAl = null;
        try {
            divisionAl = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String divisionQry = "";
        if(divisionAl!=null && !divisionAl.isEmpty()) {
            for (int i = 0; i < divisionAl.size();i++){
                if(i==divisionAl.size()-1) {
                    divisionQry = divisionQry + "DMSDivision eq '" + divisionAl.get(i)+"'";
                }else {
                    divisionQry = divisionQry + "DMSDivision eq '" + divisionAl.get(i) + "' or ";
                }
            }
        }
        List<ODataEntity> entities = null;
        if(!TextUtils.isEmpty(divisionQry)) {
            String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + soCreateBean.getCPGUID36() + "' and ("+divisionQry+") and "+Constants.ParentID +" eq '"+soCreateBean.getTempParentID()+"' &$orderby=DMSDivision asc";

            try {
                entities = Constants.getListEntities(mStrDistQry, OfflineManager.offlineStore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        alDmsDivision.clear();
        try {
//            alDmsDivision.addAll(OfflineManager.getDistributorsDmsDivision(entities));
            alDmsDivision.addAll(OfflineManager.getRetailerBAseDmsDivision(entities));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (soCreateView != null) {
//                    soCreateView.hideProgressDialog();
                    soCreateView.displayDMSDivision(alDmsDivision);
                }
            }
        });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean validateFields(SOCreateBean feedbackBean) {
        boolean isNotError = true;
        return isNotError;
    }

    @Override
    public void getProductRelInfo(String FeedbackId) {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '"
                + Constants.FeedbackSubType + "' and " + Constants.ParentID + " eq '" + FeedbackId + "' ";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 4, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void onSearch(String searchText, ArrayList<SKUGroupBean> skuSearchList) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText, skuSearchList);
        }
    }

    private void onSearchQuery(String searchText, ArrayList<SKUGroupBean> skuSearchList) {


        this.searchText = searchText;

        alCRSSKUGrpList.clear();
        if (TextUtils.isEmpty(this.searchText)) {
            alCRSSKUGrpList.addAll(skuSearchList);
        } else {
            for (SKUGroupBean skuGroupBean : skuSearchList) {
                if (skuGroupBean.getSKUGroupDesc().toLowerCase().contains(searchText.toLowerCase())) {
                    alCRSSKUGrpList.add(skuGroupBean);
                }
            }
        }


        if (soCreateView != null) {
            soCreateView.searchResult(alCRSSKUGrpList);
        }
    }

    @Override
    public void onAsignData(String save, String strRejReason, String strRejReasonDesc, SOCreateBean soCreateBean) {
        assignDataVar("", "", "", soCreateBean);
    }

    @Override
    public void approveData(String ids, String description, String approvalStatus) {

    }

    @Override
    public void onSaveData() {
        getLocation();
    }

    @Override
    public void brandList(String[][] arrBrand) {

    }

    @Override
    public void mustSellFilter(String[][] arrMustSell) {

    }

    @Override
    public void categoryList(String[][] arrCategory) {

    }

    @Override
    public void setFilterDate(String filterType) {

    }

    @Override
    public Bundle openFilter() {
        Bundle bundle = new Bundle();
        bundle.putString(DBFilterDialogFragment.KEY_DIVISION, divisionID);
        bundle.putString(DBFilterDialogFragment.KEY_BRAND, mStrSelBrandId);
        bundle.putString(DBFilterDialogFragment.KEY_CATEGORY, mStrSelCategoryId);
        bundle.putString(DBFilterDialogFragment.KEY_MUSTSELL, mStrMustSellType);
        return bundle;
    }

    private void assignDataVar(String save, String strRejReason, String strRejReasonDesc, SOCreateBean soCreateBean) {


        if (soCreateView != null) {
            soCreateView.conformationDialog(mContext.getString(R.string.feedback_save_conformation_msg), 1);
        }
    }

    private void finalSaveCondition() {
        Bundle bundle = new Bundle();
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
        }
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        onSave();
    }

    private void onSave() {

        Constants.saveDeviceDocNoToSharedPref(mContext, Constants.Feedbacks, masterHeaderTable.get(Constants.FeedbackNo));

        JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);

        ConstantsUtils.storeInDataVault(masterHeaderTable.get(Constants.FeedbackNo), jsonHeaderObject.toString(),mContext);

        Constants.onVisitActivityUpdate(mContext, masterHeaderTable.get(Constants.CPGUID),
                masterHeaderTable.get(Constants.FeebackGUID),
                Constants.FeedbackID, Constants.Feedbacks, mStartTimeDuration);

        navigateToDetails();
    }

    private void navigateToDetails() {
        if (soCreateView != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    soCreateView.hideProgressDialog();
                    soCreateView.showMessage(mContext.getString(R.string.msg_feedback_created), false);
                }
            });
        }
    }

    public void requestSOData(final String divisionId, final String divsionDesc, final ArrayList<DMSDivisionBean> alDMSDiv, final String parentID) {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mArrayDistributors = Constants.getDistributorsByCPGUID(mContext,Constants.convertStrGUID32to36(soCreateBean.getCPGUID()),parentID);
                    mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(soCreateBean.getCPGUID()),mContext);
                    mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(soCreateBean.getCPGUID36(),mContext);
                    try {
                        stockOwner = mArrayDistributors[5][0];
                        mStrCPTypeID = mArrayDistributors[8][0];
                        mStrParentID = mArrayDistributors[4][0];
                    } catch (Exception e) {
                        stockOwner = "";
                        mStrCPTypeID = "";
                        mStrParentID = "";
                        e.printStackTrace();
                    }
                    try {
//                        soCreateBean.setDMSDivision(mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
                        soCreateBean.setDMSDivision(divisionId);
//                        soCreateBean.setDMSDivisionDesc(mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");
                        soCreateBean.setDMSDivisionDesc(divsionDesc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    soCreateBean.setSPGUID(mArrayDistributors[0][0]);
                    soCreateBean.setSPNo(mArrayDistributors[2][0]);
                    soCreateBean.setCPTypeID(mArrayDistributors[8][0]);
                    soCreateBean.setParentID(mArrayDistributors[4][0]);
                    soCreateBean.setParentTypeID(mArrayDistributors[5][0]);
                    soCreateBean.setCurrency(mArrayDistributors[10][0]);
                    soCreateBean.setParentName(mArrayDistributors[7][0]);
                    soCreateBean.setSPName(mArrayDistributors[3][0]);
                    getDMSDivision();
                    soCreateBean.setDmsDivQryBean(dmsDivQryBean);
                    soCreateBean.setPriceBatchCalType(Constants.getPRICALBATCHType());
                    getSystemKPI();
                    getSSSOQry();
                    getInvQry();
                    getBalVisit();
                    getMustSellTemp();
                    getDBStock();
                    getMaterialDBStock(divisionId);
                    getSegmentedMat();
                    if (hashMapDBStk.size() > 0) {
                        getCrsskuGrpQry();
                        getTargets();
                        getInvQtyByCrsSkuGrp();
                    }
                    monthTarget();
                    getRetailerStock();
                    getMatRetailerStock();
                    getValidScheme();

                    /*String mStrStkQry = Constants.CPStockItems + "?$filter= "
                            + Constants.OrderMaterialGroupID + " ne '' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "'" +
                            " and " + Constants.CPGUID + " eq '" + mStrParentID + "' and " + dmsDivQryBean.getDMSDivisionQry() + " ";*/
                    String queryDivId ="" ;
                    alCRSSKUGrpList.clear();
                    if(alDMSDiv!=null && !alDMSDiv.isEmpty()) {
                        for (int i = 0; i < alDMSDiv.size();i++) {

                            if (!TextUtils.isEmpty(alDMSDiv.get(i).getDMSDivisionID())){
                                queryDivId = alDMSDiv.get(i).getDMSDivisionID();
                            String mStrStkQry = Constants.CPStockItems + "?$filter= "
                                    + Constants.OrderMaterialGroupID + " ne '' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "'" +
                                    " and " + Constants.CPGUID + " eq '" + mStrParentID + "' and " + "startswith(" + Constants.DMSDivision + ",'" + divisionId + "')";
                            try {
                        /*alCRSSKUGrpList.addAll(OfflineManager.getCRSSKUGroup(mStrStkQry, dmsDivQryBean.getDMSDivisionQry(),
                                hashMapDBStk, hashMapRetailerStk,hashMapRetailerStkUOM, hashMapTargetByCrsskugrp, hashMapInvQtyByCrsskugrp,
                                alMapMaterialDBStkUOM, hashMapRetailerStkByMat, stockOwner, mStrParentID, hashMapMustSell,
                                orderMaterialGrpSchemeId, hashMapSchemeGuidMatByMaterial,hashMapSegmentedMat));*/
                                alCRSSKUGrpList.addAll(OfflineManager.getCRSSKUGroup(mStrStkQry, queryDivId,
                                        hashMapDBStk, hashMapRetailerStk, hashMapRetailerStkUOM, hashMapTargetByCrsskugrp, hashMapInvQtyByCrsskugrp,
                                        alMapMaterialDBStkUOM, hashMapRetailerStkByMat, stockOwner, mStrParentID, hashMapMustSell,
                                        orderMaterialGrpSchemeId, hashMapSchemeGuidMatByMaterial, hashMapSegmentedMat));
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                        }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (soCreateView != null) {
                            soCreateView.hideProgressDialog();
                            soCreateView.displaySO(alCRSSKUGrpList);
                        }
                    }
                });
            }
        }).start();

//        String mStrStkQry = Constants.CPStockItems + "?$filter= "
//                + Constants.OrderMaterialGroupID + " ne '' and "+dmsDivQryBean.getDMSDivisionQry()+" ";
//
//        ConstantsUtils.onlineRequest(mContext, mStrStkQry, isSessionRequired, 3, ConstantsUtils.SESSION_HEADER, this,false);
    }

    private void getDMSDivision() {
        dmsDivQryBean = Constants.getDMSDIVForSchemes(mArraySPValues[1][0]);
    }

    private void getSystemKPI() {
        try {
            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '06' and " + Constants.CalculationBase + " eq '02' ", dmsDivQryBean.getCVGValueQry());

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    private void getSSSOQry() {
        try {
            mStrSSOListQry = OfflineManager.makeSSSOQry(Constants.SSSOs + "?$select=" + Constants.SSSOGuid + " " +
                    "&$filter=" + Constants.SoldToId + " eq '" + soCreateBean.getCPNo() + "' " +
                    "and " + Constants.OrderDate + " eq datetime'" + UtilConstants.getNewDate() + "' ", Constants.SSSOGuid);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getInvQry() {
        try {
            mStrInvCurrentMntQry = OfflineManager.makeSSSOQry(Constants.SSINVOICES + "?$select=" + Constants.InvoiceGUID + " " +
                    "&$filter=" + Constants.SoldToID + " eq '" + soCreateBean.getCPNo() + "' " +
                    "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and " + dmsDivQryBean.getDMSDivisionSSInvQry() + " ", Constants.InvoiceGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getBalVisit() {

        String mStrBalVisitQry = Constants.RouteSchedulePlans + "?$filter = " + Constants.VisitCPGUID + " eq '"
                + soCreateBean.getCPGUID().toUpperCase() + "' ";
        try {
            mIntBalVisitRet = OfflineManager.getBalanceRetVisitRoute(mStrBalVisitQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMustSellTemp() {
       /* try {
            hashMapMustSell = OfflineManager.getMustSellMatListTemp(Constants.MustSells
                    + "?$filter=" + Constants.OrderMatGrp + " ne '' and "
                    + Constants.ParentType + " eq '"+stockOwner+"'  and "+Constants.CPNo+" eq '"+mStrBundleRetID+"' " +
                    "and "+Constants.CPType+" eq '"+mStrCPTypeID+"' " +
                    "and "+Constants.ParentNo+" eq '"+mStrParentID+"' and "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+dmsDivQryBean.getDMSDivisionSSInvQry()+" ");

//            if (alMapDBStkUOM.size() > 0) {
//                hashMapDBStk = alMapDBStkUOM.get(0);
//                hashMapUOM = alMapDBStkUOM.get(1);
//            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
    }

    private void getDBStock() {
        try {
            alMapDBStkUOM = OfflineManager.getDBStockList(Constants.CPStockItems
                    + "?$filter=" + Constants.MaterialNo + " ne '' and "
                    + Constants.StockOwner + " eq '" + stockOwner + "' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "'" +
                    " and " + Constants.CPGUID + " eq '" + mStrParentID + "' and " + dmsDivQryBean.getDMSDivisionQry() + " &$orderby=" + Constants.OrderMaterialGroupID + " ", mSetCpStockItemGuid);

            if (alMapDBStkUOM.size() > 0) {
                hashMapDBStk = alMapDBStkUOM.get(0);
                hashMapUOM = alMapDBStkUOM.get(1);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMaterialDBStock(String divisionId) {
        try {
            alMapMaterialDBStkUOM = OfflineManager.getDBStockListMaterial(Constants.CPStockItems
                    + "?$filter=" + Constants.MaterialNo + " ne '' and "
                    + Constants.StockOwner + " eq '" + stockOwner + "' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' and " + Constants.CPGUID + " eq '" + mStrParentID + "' and " + dmsDivQryBean.getDMSDivisionQry() + " &$orderby=" + Constants.MaterialNo + " ");
           /* alMapMaterialDBStkUOM = OfflineManager.getDBStockListMaterial(Constants.CPStockItems
                    + "?$filter=" + Constants.MaterialNo + " ne '' and "
                    + Constants.StockOwner + " eq '" + stockOwner + "' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' and " + Constants.CPGUID + " eq '" + mStrParentID + "' and "+"startswith(" + Constants.DMSDivision + ",'" + divisionId+"')" + " &$orderby=" + Constants.MaterialNo + " ");*/
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getSegmentedMat() {
        try {
            hashMapSegmentedMat = OfflineManager.getSegmentedMaterialsList(Constants.SegmentedMaterials);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getCrsskuGrpQry() {
        mTargetQry = Constants.convertHashMapToString(hashMapDBStk, Constants.OrderMaterialGroupID);
    }

    private void getTargets() {
        try {
            hashMapTargetByCrsskugrp = OfflineManager.getTargetByOrderMatGrp(hashMapDBStk, soCreateBean.getCPGUID(), mTargetQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getInvQtyByCrsSkuGrp() {
        try {
            hashMapInvQtyByCrsskugrp = OfflineManager.getSSInvItmQtyByOrderMatGrp(hashMapDBStk, soCreateBean.getCPGUID(), mTargetQry.replaceAll(Constants.OrderMaterialGroupID, Constants.OrderMaterialGroup));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void monthTarget() {
        Double mDoubleDayTarget = 0.0, mDoubleBMT = 0.0;
        String mTodayOrderQty = "0", mMonthInvQty = "0";
        if (salesKpi != null) {
            try {
                ArrayList<MyTargetsBean> alMyTargets = null;
                try {
                    alMyTargets = OfflineManager.getMyTargetsByKPI(salesKpi,
                            soCreateBean.getCPGUID());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                Map<String, MyTargetsBean> mapSalesKPIVal = OfflineManager.getALMyTargetList(alMyTargets);

                mDoubleDayTarget = Double.parseDouble(mapSalesKPIVal.get(salesKpi.getKPICode()).getMonthTarget());

                if (mDoubleDayTarget > 0) {

                    mTodayOrderQty = Constants.getOrderQtyByRetiler(soCreateBean.getCPNo(), UtilConstants.getNewDate(), mContext, mStrSSOListQry);

                    mMonthInvQty = Constants.getInvQtyByInvQry(mStrInvCurrentMntQry);

                    try {
                        mDoubleBMT = mDoubleDayTarget - Double.parseDouble(mMonthInvQty) - Double.parseDouble(mTodayOrderQty);
                    } catch (Exception e) {
                        mDoubleBMT = 0.0;
                    }
                } else {
                    mDoubleBMT = 0.0;
                }
                mStrBMT = (mDoubleBMT > 0 ? mDoubleBMT : 0) + "";
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            mStrBMT = "0.0";
        }
    }

    private void getRetailerStock() {
        try {
            alMapRetStkUOM = OfflineManager.getRetStockList(Constants.CPStockItems + "?$filter=" + Constants.CPGUID + " eq '" + soCreateBean.getCPGUID() + "'" +
                    " and " + Constants.StockOwner + " eq '02' &$orderby=" + Constants.AsOnDate + "%20desc ");
            if (alMapDBStkUOM.size() > 0) {
                hashMapRetailerStk = alMapRetStkUOM.get(0);
                hashMapRetailerStkUOM = alMapRetStkUOM.get(1);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMatRetailerStock() {
        try {
            hashMapRetailerStkByMat = OfflineManager.getMaterialRetStockList(Constants.CPStockItems + "?$filter=" + Constants.CPGUID + " eq '" + soCreateBean.getCPGUID() + "'" +
                    " and " + Constants.StockOwner + " eq '02' &$orderby=" + Constants.AsOnDate + "%20desc  ");
//                    " and " + Constants.StockOwner + " eq '02' &$orderby=" + Constants.MaterialNo + " ");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getValidScheme() {
        orderMaterialGrpSchemeId.clear();
        hashMapSchemeGuidMatByMaterial.clear();
        Constants.HashMapSchemeIsInstantOrQPS.clear();
        Constants.HashMapSchemeListByMaterial.clear();
        Constants.SchemeQRY = "";
        try {
            orderMaterialGrpSchemeId = GetSalesOrderListBasedOnScheme.getOrderMaterialId(soCreateBean.getCPGUID(), soCreateBean.getParentID(),
                    soCreateBean.getParentTypeID(), soCreateBean.getCPTypeID(), soCreateBean.getSPGUID(), dmsDivQryBean.getDMSDivisionQry(), dmsDivQryBean.getDMSDivisionIDQry(), null);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (!Constants.SchemeQRY.equalsIgnoreCase("")) {
            String validScheme = Constants.SchemeItemDetails + "?$filter= (" + Constants.SchemeGUID + " eq " + Constants.SchemeQRY + ") and " + Constants.OnSaleOfCatID + " eq '" + Constants.OnSaleOfCatIDMaterial + "' ";
            try {
                hashMapSchemeGuidMatByMaterial = OfflineManager.getSchemeGuidByMaterial(validScheme, hashMapSchemeGuidMatByMaterial);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 3:
               /* alCRSSKUGrpList.clear();

                try {
                    alCRSSKUGrpList.addAll(OfflineManager.getCRSSKUGroup(list,dmsDivQryBean.getDMSDivisionQry(),
                            hashMapDBStk,hashMapRetailerStk,hashMapTargetByCrsskugrp,hashMapInvQtyByCrsskugrp,
                            alMapMaterialDBStkUOM,hashMapRetailerStkByMat,stockOwner,mStrParentID,hashMapMustSell,
                            orderMaterialGrpSchemeId,hashMapSchemeGuidMatByMaterial));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (soCreateView != null) {
                            soCreateView.hideProgressDialog();
                            soCreateView.displaySO(alCRSSKUGrpList);
                        }
                    }
                });
                break;
            case 4:
                alFeedbackSubType.clear();
                try {
                    alFeedbackSubType.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.FeedbackSubType));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (soCreateView != null) {
                            soCreateView.hideProgressDialog();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

    }

    private void getLocation() {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (soCreateView != null) {
                        soCreateView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }

    private void locationPerGranted() {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (soCreateView != null) {
                        soCreateView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (soCreateView != null)
                                ConstantsUtils.showAutoDateSetDialog(mActivity);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onFragmentInteraction(String brand, String brandName, String category, String categoryName,
                                      String mstrMustSellType, String mstSellName, ArrayList<SKUGroupBean> skuGroupBeanArrayList, String searchText) {
        this.mStrSelBrandId = brand;
        this.mStrSelCategoryId = category;
        this.mStrMustSellType = mstrMustSellType;
        filterType(brandName, category, mstSellName);
        getFilterList(skuGroupBeanArrayList, mstrMustSellType, category, brand, searchText);
    }

    private void filterType(String brandName, String categoryName, String mustSellName) {
        try {
            String filteredResult = "";
            if (!TextUtils.isEmpty(brandName) && !brandName.equalsIgnoreCase(Constants.All)) {
                filteredResult = filteredResult + ", " + brandName;
            }
            if (!TextUtils.isEmpty(categoryName) && !categoryName.equalsIgnoreCase(Constants.All)) {
                filteredResult = filteredResult + ", " + categoryName;
            }
            if (!TextUtils.isEmpty(mustSellName) && !mustSellName.equalsIgnoreCase(Constants.All)) {
                filteredResult = filteredResult + ", " + mustSellName;
            }
            if (soCreateView != null) {
                soCreateView.setFilterDate(filteredResult);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void getFilterList(ArrayList<SKUGroupBean> skuGroupBeanArrayList, String mStrSelType, String mStrSelCatType, String mStrSelBrand, String searchText) {
        Set<String> mSetOrderMatGrp = new HashSet<>();
        if (skuGroupBeanArrayList.size() > 0) {
            alCRSSKUGrpList.clear();
            switch (mStrSelType) {
                case "": {
                    if (!mStrSelCatType.equalsIgnoreCase("") && !mStrSelBrand.equalsIgnoreCase("")) {
                        mSetOrderMatGrp = Constants.getOrderMatGrpByBrandAndCategory(mStrSelCatType, mStrSelBrand, dmsDivQryBean.getDMSDivisionQry());
                    } else if (!mStrSelCatType.equalsIgnoreCase("") && mStrSelBrand.equalsIgnoreCase("")) {
                        mSetOrderMatGrp = Constants.getOrderMatGrpByBrandAndCategory(mStrSelCatType, "", dmsDivQryBean.getDMSDivisionQry());
                    } else if (mStrSelCatType.equalsIgnoreCase("") && !mStrSelBrand.equalsIgnoreCase("")) {
                        mSetOrderMatGrp = Constants.getOrderMatGrpByBrandAndCategory("", mStrSelBrand, dmsDivQryBean.getDMSDivisionQry());
                    } else if (mStrSelCatType.equalsIgnoreCase("") && mStrSelBrand.equalsIgnoreCase("")) {
                        mSetOrderMatGrp = new HashSet<>();
                    }

                    for (SKUGroupBean item : skuGroupBeanArrayList) {
                        if (mSetOrderMatGrp.size() > 0) {
                            if (mSetOrderMatGrp.contains(item.getSKUGroup()) && (TextUtils.isEmpty(searchText) || item.getSKUGroupDesc().toLowerCase().contains(searchText.toLowerCase())))
                                alCRSSKUGrpList.add(item);
                        } else {
                            if (mStrSelCatType.equalsIgnoreCase("") && mStrSelBrand.equalsIgnoreCase("") && (TextUtils.isEmpty(searchText) || item.getSKUGroupDesc().toLowerCase().contains(searchText.toLowerCase())))
                                alCRSSKUGrpList.add(item);
                        }
                    }
                }
                break;
                case Constants.str_01: {
                    for (SKUGroupBean item : skuGroupBeanArrayList) {
                        if (!item.getMatTypeVal().equalsIgnoreCase("") && (TextUtils.isEmpty(searchText) || item.getSKUGroupDesc().toLowerCase().contains(searchText.toLowerCase())))
                            alCRSSKUGrpList.add(item);
                    }
                }
                break;
            }
        }

        if (soCreateView != null) {
            soCreateView.searchResult(alCRSSKUGrpList);
        }
    }
}
