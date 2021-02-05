package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.retailerStockEntry.add.RetailerAddStockActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RetailerStockCrtPresenterImpl implements RetailerStockCrtPresenter, OnlineODataInterface, UIListener {
    private Activity mContext;
    private String filterType = "";
    private RetailerStockCrtView createView = null;
    private String mStrComingFrom;
    private boolean isSessionRequired = false;
    private ArrayList<RetailerStockBean> dlrStockBeanArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> dlrStockBeanReviewAL = new ArrayList<>();
    private ArrayList<RetailerStockBean> searchBeanArrayList = new ArrayList<>();
    private String custNo = "";
    private ArrayList<RetailerStockBean> stockRevItmList = null;
    private boolean isReviewScreen = false;
    private Activity activity;
    private String mStrCPGUID32 = "";
    private String custName;
    private int totalCount = 0;
    private int currentCount = 0;
    private String mStrVisitActRefID = "";
    private String mStrUID = "";
    private String mStrCPGUID = "";
    private String beatGUID = "";
    private String parentID = "";
    private String[][] mArrayDistributors = null;
    private String stockOwner = "";
    private String skugroupValue = "";
    private String[][] mArrayCPDMSDivisoins = null;

    public RetailerStockCrtPresenterImpl(Activity mContext, RetailerStockCrtView createView, String mStrComingFrom, boolean isSessionRequired, String custNo, boolean isReviewScreen, String custName, String mStrCPGUID32, String mStrUID, String mStrCPGUID, String beatGUID, String parentID) {
        this.mContext = mContext;
        this.createView = createView;
        this.mStrComingFrom = mStrComingFrom;
        this.isSessionRequired = isSessionRequired;
        this.filterType = filterType;
        this.custNo = custNo;
        this.isReviewScreen = isReviewScreen;
        this.custName = custName;
        this.activity = mContext;
        this.mStrCPGUID32 = mStrCPGUID32;
        this.mStrUID = mStrUID;
        this.mStrCPGUID = mStrCPGUID;
        this.parentID = parentID;
        this.beatGUID = beatGUID;

    }

    public RetailerStockCrtPresenterImpl(Activity mContext, RetailerStockCrtView createView,
                                         String mStrComingFrom, boolean isSessionRequired,
                                         ArrayList<RetailerStockBean> stockRevItmList, String custNo,
                                         boolean isReviewScreen, String custName, String mStrCPGUID32, String mStrCPGUID) {
        this.mContext = mContext;
        this.createView = createView;
        this.mStrComingFrom = mStrComingFrom;
        this.isSessionRequired = isSessionRequired;
        this.stockRevItmList = stockRevItmList;
        this.custNo = custNo;
        this.custName = custName;
        this.isReviewScreen = isReviewScreen;
        this.activity = mContext;
        this.mStrCPGUID32 = mStrCPGUID32;
        this.mStrCPGUID = mStrCPGUID;
    }

    @Override
    public void onStart() {
        if (!isReviewScreen) {
            getDistributorValues();
            requestMaterial();
        } else {
            viewReview();
        }

    }

    private void viewReview() {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    createView.hideProgressDialog();
                    createView.displayList(stockRevItmList, skugroupValue);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        createView = null;
    }

    @Override
    public void onFilter() {
    }

    @Override
    public boolean onSearch(String searchText, Object objects) {
        return false;
    }

    @Override
    public void onSearch(String searchText) {
        searchBeanArrayList.clear();
        boolean isID = false;
        boolean isName = false;
        if (dlrStockBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(stockRevItmList);
            } else {
                for (RetailerStockBean item : stockRevItmList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        isID = item.getOrderMaterialGroupDesc().toLowerCase().contains(searchText.toLowerCase());
                        isName = item.getOrderMaterialGroupID().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isID = true;
                        isName = true;
                    }
                    if (isID || isName)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (createView != null) {
            createView.displaySearchList(searchBeanArrayList);
        }
    }

    public void removeItem(RetailerStockBean item) {
        if (dlrStockBeanArrayList != null)
            dlrStockBeanArrayList.remove(item);
    }

    public void onSearchMaterial(String searchText) {
        searchBeanArrayList.clear();
        boolean isID = false;
        boolean isName = false;
        if (dlrStockBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(dlrStockBeanArrayList);
            } else {
                for (RetailerStockBean item : dlrStockBeanArrayList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        isID = item.getOrderMaterialGroupDesc().toLowerCase().contains(searchText.toLowerCase());
                        isName = item.getOrderMaterialGroupID().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isID = true;
                        isName = true;
                    }
                    if (isID || isName)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (createView != null) {
            createView.displaySearchList(searchBeanArrayList);
        }
    }


    @Override
    public void validateItem(final int activityRedirectType, RecyclerView recyclerView) {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (createView != null) {
                            createView.hideProgressDialog();
                            boolean isSelAtleastOneMat = false;
                            dlrStockBeanReviewAL.clear();
                            if (!dlrStockBeanArrayList.isEmpty()) {
                                for (RetailerStockBean soItemBean : dlrStockBeanArrayList) {
                                    if(soItemBean.isAddButtonEnabled()){
                                        if(!isSelAtleastOneMat){
                                            isSelAtleastOneMat =true;
                                        }
                                        if (TextUtils.isEmpty(soItemBean.getEnterdQty()) || (Double.parseDouble(soItemBean.getEnterdQty()) < 0)) {
                                            createView.displayMessage(mContext.getString(R.string.retailer_stock_error_valid_qty));
                                            return;
                                        }else{
                                            dlrStockBeanReviewAL.add(soItemBean);
                                        }
                                    }

                                }
                                if(isSelAtleastOneMat){
                                    navigateReviewScreen(dlrStockBeanReviewAL);
                                }else{
                                    createView.displayMessage(mContext.getString(R.string.retailer_atleast_one_item));
                                }

                            } else {
                                createView.displayMessage(mContext.getString(R.string.retailer_items_selected));
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void navigateReviewScreen(ArrayList<RetailerStockBean> dealerStockHeaderBean) {
        Intent intent = new Intent(mContext, RetailerStkReviewActivity.class);
        intent.putExtra(Constants.EXTRA_STOCK_BEAN, dealerStockHeaderBean);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
        intent.putExtra(Constants.CPUID, mStrUID);
        intent.putExtra(Constants.CPNo, custNo);
        intent.putExtra(Constants.RetailerName, custName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.BeatGUID, beatGUID);
        intent.putExtra(Constants.ParentID, parentID);
        intent.putExtra(Constants.EXTRA_SEARCH_HINT, skugroupValue);

        mContext.startActivity(intent);
    }

    private void locationPerGranted(final ArrayList<RetailerStockBean> filteredCrsList) {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.gps_progress));
        }
        Constants.getLocation(activity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                createView.hideProgressDialog();
                if (status) {
                    new onCreateRetailerStockAsyncTask(filteredCrsList).execute();
                }
            }
        });
    }

    @Override
    public void onRequestError(int i, Exception e) {
        currentCount++;
        if (totalCount == currentCount) {
            if (createView != null) {
                createView.hideProgressDialog();
                createView.displayMessage(e.getMessage());
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        currentCount++;
        if (totalCount == currentCount) {
            if (createView != null)
                createView.hideProgressDialog();
            Constants.onVisitActivityUpdate(mContext, mStrCPGUID32, mStrVisitActRefID, Constants.RetailerStockID, Constants.RetailerStock, UtilConstants.getOdataDuration());
            if (i == Operation.Create.getValue()) {
                UtilConstants.dialogBoxWithCallBack(mContext, "", mContext.getString(R.string.msg_retailer_stk_created), mContext.getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        if (createView != null) {
                            createView.onCreateUpdateSuccess();
                        }
                    }
                });

            } else if (i == Operation.Update.getValue()) {
                UtilConstants.dialogBoxWithCallBack(mContext, "", mContext.getString(R.string.msg_retailer_stk_created), mContext.getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        if (createView != null) {
                            createView.onCreateUpdateSuccess();
                        }
                    }
                });
            }
        }
    }

    //Save All stock items in Offline
    private void saveAllDealerStockItems(ArrayList<RetailerStockBean> filteredCrsList) {
        getCPDMSDivisions();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        boolean mBoolOneTimeSavedVisitAct = false;
        mStrVisitActRefID = "";
        for (int i = 0; i < filteredCrsList.size(); i++) {
            Hashtable<String, String> singleItem = new Hashtable<>();

            singleItem.put(Constants.LOGINID, loginIdVal.toUpperCase());
            singleItem.put(Constants.CPTypeID, Constants.str_02);
            singleItem.put(Constants.CPNo, Constants.getName(Constants.ChannelPartners, Constants.CPNo, Constants.CPUID, custNo));
            singleItem.put(Constants.CPName, custName);
            singleItem.put(Constants.CPTypeDesc, Constants.getName(Constants.ChannelPartners, Constants.CPTypeDesc, Constants.CPUID, custNo));
            singleItem.put(Constants.OrderMaterialGroupID, filteredCrsList.get(i).getOrderMaterialGroupID());
            singleItem.put(Constants.OrderMaterialGroupDesc, filteredCrsList.get(i).getOrderMaterialGroupDesc());
            singleItem.put(Constants.UOM, filteredCrsList.get(i).getEnterdUOM());
            singleItem.put(Constants.AlternativeUOM2, filteredCrsList.get(i).getAlternativeUOM2());
            singleItem.put(Constants.AlternativeUOM1, filteredCrsList.get(i).getAlternativeUOM1());
            singleItem.put(Constants.MRP, !filteredCrsList.get(i).getMRP().equalsIgnoreCase("") ? filteredCrsList.get(i).getMRP() : "0.00");
            singleItem.put(Constants.StockValue, !filteredCrsList.get(i).getStockValue().equalsIgnoreCase("") ? filteredCrsList.get(i).getStockValue() : "0.00");
            singleItem.put(Constants.LandingPrice, !filteredCrsList.get(i).getLandingPrice().equalsIgnoreCase("") ? filteredCrsList.get(i).getLandingPrice() : "0.00");
            singleItem.put(Constants.StockOwner, "02");
            singleItem.put(Constants.AsOnDate, UtilConstants.getNewDateTimeFormat());

            singleItem.put(Constants.DMSDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
            singleItem.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");

            singleItem.put(Constants.UnrestrictedQty, filteredCrsList.get(i).getEnterdQty());


            singleItem.put(Constants.Currency, filteredCrsList.get(i).getCurrency());

            singleItem.put(Constants.Etag, filteredCrsList.get(i).getEtag());


            if (filteredCrsList.get(i).getStockType().equalsIgnoreCase("Dist")) {
                GUID guid = GUID.newRandom();
                singleItem.put(Constants.CPGUID, mStrCPGUID32);
                singleItem.put(Constants.CPStockItemGUID, guid.toString36().toUpperCase());
                try {

                    OfflineManager.createCPStockItems(singleItem, this,mContext);

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (!mBoolOneTimeSavedVisitAct) {
                    mStrVisitActRefID = guid.toString36().toUpperCase();
                    mBoolOneTimeSavedVisitAct = true;
                }
            } else {
                singleItem.put(Constants.CPStockItemGUID, filteredCrsList.get(i).getCPStockItemGUID());
                singleItem.put(Constants.CPGUID, mStrCPGUID32);
                try {

                    OfflineManager.updateCPStockItems(singleItem, this,mContext);

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (!mBoolOneTimeSavedVisitAct) {
                    mStrVisitActRefID = filteredCrsList.get(i).getCPStockItemGUID().toUpperCase();
                    mBoolOneTimeSavedVisitAct = true;
                }
            }
        }
    }

    private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(mStrCPGUID,mContext);
    }

    @Override
    public void saveItem(RecyclerView recyclerView, ArrayList<RetailerStockBean> filteredCrsList) {
        totalCount = filteredCrsList.size();
        currentCount = 0;
        locationPerGranted(filteredCrsList);
    }

    @Override
    public void getCheckedCount() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RetailerStkCrtActivity.INTENT_RESULT_STOCK_CREATE) {
            try {
                dlrStockBeanArrayList.addAll((ArrayList<RetailerStockBean>) data.getSerializableExtra(Constants.EXTRA_STOCK_BEAN));
                if (createView != null) {
                    createView.displayList(dlrStockBeanArrayList, skugroupValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void requestMaterial() {
        if (createView != null) {
            createView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        String qry = Constants.CPStockItems + "?$orderby=AsOnDate desc &$filter=" + Constants.StockOwner + " eq '02' and " + Constants.OrderMaterialGroupID + " ne '' and " + Constants.CPGUID + " eq '" + mStrCPGUID32 + "'";
        ConstantsUtils.onlineRequest(mContext, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, false);

    }

    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mContext, mStrCPGUID);
        if (mArrayDistributors != null) {
            try {
                stockOwner = mArrayDistributors[5][0];
            } catch (Exception e) {
                stockOwner = "";
            }
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, Bundle bundle) {
        dlrStockBeanArrayList.clear();
        try {
            skugroupValue = Constants.getTypesetValueForSkugrp(mContext);
            if (skugroupValue.equalsIgnoreCase(mContext.getString(R.string.lbl_sku_group))) {
                skugroupValue = mContext.getString(R.string.lbl_Search_by_skugroupdesc);
            } else {
                skugroupValue = mContext.getString(R.string.lbl_Search_by_crsskugroupdesc);
            }
            String typesetUOM = Constants.getTypesetValueForRetUOM(mContext);
            dlrStockBeanArrayList = OfflineManager.getDBStockMaterials(entities, mContext, typesetUOM);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    createView.hideProgressDialog();
                    createView.displayList(dlrStockBeanArrayList, skugroupValue);
                }
            }
        });
    }


    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String errorMsg, Bundle bundle) {
        showErrorResponse(errorMsg);
    }

    private void showErrorResponse(final String errorMsg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (createView != null) {
                    createView.hideProgressDialog();
                    createView.displayMessage(errorMsg);
                }
            }
        });
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        requestMaterial();
    }

    @Override
    public void addRetailerStock(ArrayList<RetailerStockBean> searchSOItemBean) {
        Intent intent = new Intent(mContext, RetailerAddStockActivity.class);
        intent.putExtra(Constants.EXTRA_STOCK_BEAN, searchSOItemBean);
        intent.putExtra(Constants.EXTRA_STOCK_OWNER, stockOwner);
        intent.putExtra(Constants.EXTRA_SEARCH_HINT, skugroupValue);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
        mContext.startActivityForResult(intent, RetailerStkCrtActivity.INTENT_RESULT_STOCK_CREATE);
    }

    /*AsyncTask to create retailer*/
    public class onCreateRetailerStockAsyncTask extends AsyncTask<Void, Void, Void> {
        ArrayList<RetailerStockBean> filteredCrsList;

        onCreateRetailerStockAsyncTask(ArrayList<RetailerStockBean> filteredCrsList) {
            this.filteredCrsList = filteredCrsList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createView.showProgressDialog(mContext.getString(R.string.pop_up_msg_retailer_stock));

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                saveAllDealerStockItems(filteredCrsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
