package com.arteriatech.ss.msecsales.rspl.SampleDisbursementList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.SyncFromDataValtAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterActivity;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterModelImpl;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.header.SalesOrderHeaderListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.List;

public class SampleDisbursementListPresenterImpl implements SampleDisbursementListPresenter, UIListener, MessageWithBooleanCallBack {

    Context context;
    SampleDisbursementListsView sampleDisbursementListsView;
    ArrayList<InvoiceListBean> invoiceBeanArrayList = new ArrayList<>();
    ArrayList<InvoiceListBean> searchBeanArrayList = new ArrayList<>();
    SampleDisbursementListsView.SampleDisbursemwntResponse<InvoiceListBean> sampleDisbursemwntResponse;
    ArrayList<String> alAssignColl = null;
    Activity activity;
    boolean isInvoiceItemsEnabled;
    private String comingFrom = "";
    private String mCPNO = "";
    private String CPGUID = "";
    private ArrayList<InvoiceListBean> sampleisbursementrArrayList = new ArrayList<>();
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private String searchText = "";
    private boolean isErrorFromBackend = false;

    private int isFromWhere = 0;
    private int penROReqCount = 0;
    private ArrayList<String> pendingCollectionList = null;
    private int pendingROVal = 0;
    private String concatCollectionStr = "";
    private String syncType = "";
    private String parentId = "";
    private GUID refguid =null;

    private String[] tempRODevList = null;
    public SampleDisbursementListPresenterImpl(Context context, SampleDisbursementListsView sampleDisbursementListsView, boolean isInvoiceItemsEnabled, Bundle bundle, String mCPNO, String CPGUID, Activity activity,String parentId) {
        this.context = context;
        this.mCPNO = mCPNO;
        this.CPGUID = CPGUID;
        this.isInvoiceItemsEnabled = isInvoiceItemsEnabled;
        this.sampleDisbursementListsView = sampleDisbursementListsView;
        comingFrom = bundle.getString(Constants.comingFrom);
        syncType = bundle.getString(Constants.SyncType);
        this.activity = activity;
        this.parentId = parentId;
        pendingCollectionList = new ArrayList<>();
    }

    @Override
    public void start(final SampleDisbursementListsView.SampleDisbursemwntResponse<InvoiceListBean> sampleDisbursemwntResponse) {
        this.sampleDisbursemwntResponse = sampleDisbursemwntResponse;
        if (sampleDisbursementListsView != null) {
            sampleDisbursementListsView.loadProgressBar();
        }
        invoiceBeanArrayList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (comingFrom.equalsIgnoreCase(Constants.Device)) {

                        connectToOfflineDB(sampleDisbursemwntResponse);

                    } else {
                        getSampleDisburseList();

//                        String[][] mArrayInvoiceTypeId = OfflineManager.getInVoidTypeId();
//                        String qry = Constants.SSINVOICES + "?$filter=" + Constants.SoldToID + " eq'" + mCPNO + "' and "+Constants.InvoiceTypeID+" eq '"+mArrayInvoiceTypeId[0][0]+"' ";
//                        invoiceBeanArrayList.addAll(OfflineManager.getSampleDisruptionList(qry, context, "", CPGUID));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (sampleDisbursementListsView != null) {
//                            sampleDisbursementListsView.hideProgressBar();
//                            sampleDisbursementListsView.setSampleDisbursementListDatatoAdapter(invoiceBeanArrayList);
//                        }
//                    }
//                });
            }
        }).start();
    }

    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();

    void getSampleDisburseList() {
        invoiceBeanArrayList.clear();
        ArrayList<String> divisionUserAuthAL = null;
        try {
            divisionUserAuthAL = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strUserAuthDivisionID = "";
        if(divisionUserAuthAL!=null && !divisionUserAuthAL.isEmpty()) {
            for (int i = 0; i < divisionUserAuthAL.size();i++){
                if(i==divisionUserAuthAL.size()-1) {
                    strUserAuthDivisionID = strUserAuthDivisionID + "DMSDivision eq '" + divisionUserAuthAL.get(i)+"'";
                }else {
                    strUserAuthDivisionID = strUserAuthDivisionID + "DMSDivision eq '" + divisionUserAuthAL.get(i) + "' or ";
                }
            }
        }

        List<ODataEntity> entities = null;
        if(!TextUtils.isEmpty(strUserAuthDivisionID)) {
//            String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPNo + " eq '" + mCPNO + "' and ("+strUserAuthDivisionID+")";
            String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + CPGUID + "' and ("+strUserAuthDivisionID+")";


            try {
                entities = Constants.getListEntities(mStrDistQry, OfflineManager.offlineStore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        alDmsDivision.clear();
        try {
//            alDmsDivision.addAll(OfflineManager.getDistributorsDmsDivision(entities));
            alDmsDivision.addAll(OfflineManager.getRetailerBAseDmsDivisionwithoutNone(entities));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String strDmsDivision = "";
        if(alDmsDivision!=null && !alDmsDivision.isEmpty()) {
            for (int i = 0; i < alDmsDivision.size();i++){
                if(i==alDmsDivision.size()-1) {
                    strDmsDivision = strDmsDivision + "DmsDivision eq '" + alDmsDivision.get(i).getDMSDivisionID()+"'";
                }else {
                    strDmsDivision = strDmsDivision + "DmsDivision eq '" + alDmsDivision.get(i).getDMSDivisionID() + "' or ";
                }
            }
        }
        try {

            if(!TextUtils.isEmpty(strDmsDivision)) {
                String tempparentId="";
                if(!TextUtils.isEmpty(parentId)){
                    tempparentId=String.valueOf(Integer.parseInt(parentId));
                }
                String[][] mArrayInvoiceTypeId = OfflineManager.getInVoidTypeId();
                String qry = Constants.SSINVOICES + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + CPGUID + "' and "+ Constants.CPGUID + " eq '" + tempparentId + "' and " + Constants.InvoiceTypeID + " eq '" + mArrayInvoiceTypeId[0][0] + "' and ("+strDmsDivision+")";
                invoiceBeanArrayList.addAll(OfflineManager.getSampleDisruptionList(qry, context, "", CPGUID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.hideProgressBar();
                    sampleDisbursementListsView.setSampleDisbursementListDatatoAdapter(invoiceBeanArrayList);
                }
            }
        });
    }

    @Override
    public void onFilter() {
        if (sampleDisbursementListsView != null) {
            sampleDisbursementListsView.openfilter(startDate, endDate, filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {

        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
            statusId = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS);
            statusName = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS_NAME);
            delvStatusId = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS);
            delvStatusName = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);

            displayFilterType();
            onSearchQuery(searchText, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {

        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText, statusId, delvStatusId);
        }
    }

    @Override
    public void getInvoiceDetails(InvoiceListBean reqBean) {

        try {
            new InvoiceDetailsAsyncTask(reqBean).execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void displayFilterType() {
        try {
            String statusDesc = "";
            if (!TextUtils.isEmpty(statusId)) {
                statusDesc = ", " + statusName;
            }
            if (!TextUtils.isEmpty(delvStatusId)) {
                statusDesc = statusDesc + ", " + delvStatusName;
            }
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.hideProgressBar();
                UtilConstants.showAlert(errorMsg, context);
            }
        }
    }

    private void onSearchQuery(String searchText, String paymentStatus, String delvStatusId) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (invoiceBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText) && TextUtils.isEmpty(paymentStatus)) {
                searchBeanArrayList.addAll(invoiceBeanArrayList);
            } else {
                if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_POSTED)) {
                    paymentStatus = "01";
                    delvStatusId = "01";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_CONFIRMED)) {
                    paymentStatus = "01";
                    delvStatusId = "03";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_PARTIALLY_PAID)) {
                    paymentStatus = "02";
                    delvStatusId = "";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_COMPLETELY_PAID)) {
                    paymentStatus = "03";
                    delvStatusId = "";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_CANCELED)) {
                    paymentStatus = "";
                    delvStatusId = "02";
                }
                for (InvoiceListBean item : invoiceBeanArrayList) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getInvoiceNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (!TextUtils.isEmpty(delvStatusId)) {
                        if (item.getDueDateStatus().equalsIgnoreCase(delvStatusId)) {
                            soDelStatus = true;
                        }
                    } else {
                        soDelStatus = true;
                    }
                    if (!TextUtils.isEmpty(paymentStatus)) {
                        if (item.getInvoiceStatus().equalsIgnoreCase(paymentStatus) && !item.getDueDateStatus().equalsIgnoreCase("02")) {
                            soTypeStatus = true;
                        }
                    } else {
                        soTypeStatus = true;
                    }

                    if (soSearchStatus && soTypeStatus && soDelStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (sampleDisbursementListsView != null) {
            sampleDisbursementListsView.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void connectToOfflineDB(SampleDisbursementListsView.SampleDisbursemwntResponse<InvoiceListBean> sampleDisbursemwntResponse) {
        SampleDisbursementListPresenterImpl.GetSampleDisbursementAsyncTask salesOrderAsyncTask = new SampleDisbursementListPresenterImpl.GetSampleDisbursementAsyncTask(sampleDisbursemwntResponse);
        salesOrderAsyncTask.execute();


    }

    @Override
    public void connectToOfflineDBRefresh() {

        new GetSampleDisbursementRefreshAsyncTask(isInvoiceItemsEnabled).execute();
    }

    @Override
    public void refresh() {

        onRefreshSampleDisbursement();


    }

    @Override
    public void sync() {
        onSynSampleDisbursement();

    }

    private void getSampleDisbursementList() {
        try {
            sampleisbursementrArrayList.addAll(OfflineManager.getSapleDisbursementListFromDataValt(context, CPGUID,parentId));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onRefreshSampleDisbursement() {
        isFromWhere = 6;
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getInvoice());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.hideProgressBar();
                    sampleDisbursementListsView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,syncType,syncType,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.hideProgressBar();
                sampleDisbursementListsView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {

        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere == 6) {
                if (operation == Operation.OfflineRefresh.getValue()) {

                    Constants.isSync = false;
                    if (sampleDisbursementListsView != null) {
                        sampleDisbursementListsView.hideProgressBar();
                        sampleDisbursementListsView.reloadData();
                        if (!Constants.isStoreClosed) {
                            sampleDisbursementListsView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        } else {
                            sampleDisbursementListsView.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
                    }
                }else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (sampleDisbursementListsView != null) {
                        sampleDisbursementListsView.hideProgressBar();
                        sampleDisbursementListsView.showMessage(context.getString(R.string.msg_offline_store_failure));
                    }
                }


            } else {
                Constants.mBoolIsReqResAval = true;
                penROReqCount++;
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    new RefreshAsyncTask(context, Constants.Visits, this).execute();
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    if (sampleDisbursementListsView != null) {
                        sampleDisbursementListsView.hideProgressBar();
                        sampleDisbursementListsView.reloadData();
                        UtilConstants.showAlert(context.getString(R.string.msg_error_occured_during_sync), context);
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.loadProgressBar();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.hideProgressBar();
                    sampleDisbursementListsView.reloadData();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.mBoolIsReqResAval = true;
            Constants.isSync = false;
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.hideProgressBar();
                sampleDisbursementListsView.reloadData();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
        Log.d("exception", "Exception message");

    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (isFromWhere == 6) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, alAssignColl,syncType,refguid.toString().toUpperCase());
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(context, false);
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.hideProgressBar();
                    //connectToOfflineDBRefresh();
                    getSampleDisburseList();

                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(context, Constants.Sync_All);
                ConstantsUtils.startAutoSync(context, false);
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.hideProgressBar();
                    //connectToOfflineDBRefresh();
                    getSampleDisburseList();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        } else if (isFromWhere == 4) {
            if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                Constants.mBoolIsReqResAval = true;

                Constants.removeDataValtFromSharedPref(context, Constants.SampleDisbursement, tempRODevList[penROReqCount], false);
                try {
                    ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount], "",context);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                penROReqCount++;
            }
            if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                Constants.updateLastSyncTimeToTable(context, pendingCollectionList,syncType,refguid.toString().toUpperCase());

                SalesOrderHeaderListActivity.mBoolRefreshDone = true;
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.hideProgressBar();
                    String msg = "";
                    ConstantsUtils.startAutoSync(context, false);
                    if (sampleDisbursementListsView != null) {
                        if (!isErrorFromBackend) {
                            sampleDisbursementListsView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                            sampleDisbursementListsView.reloadData();
                        } else {
                            sampleDisbursementListsView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                            sampleDisbursementListsView.reloadData();
                        }
                        AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                    }
                }
            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                Constants.mBoolIsReqResAval = true;
                try {
                    OfflineManager.getAuthorizations(context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(context, Constants.Sync_All);
                ConstantsUtils.startAutoSync(context, false);
                if (sampleDisbursementListsView != null) {
                    sampleDisbursementListsView.reloadData();
                    sampleDisbursementListsView.hideProgressBar();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        }
    }

    public void onSynSampleDisbursement() {
        isErrorFromBackend = false;
        isFromWhere = 4;
        Constants.mBoolIsReqResAval = true;
        Constants.mBoolIsNetWorkNotAval = false;
        try {
            sampleisbursementrArrayList.clear();
            sampleisbursementrArrayList = (ArrayList<InvoiceListBean>) OfflineManager.getSapleDisbursementListFromDataValt(context, CPGUID,parentId);
            if (!sampleisbursementrArrayList.isEmpty()) {
                    if (UtilConstants.isNetworkAvailable(context)) {
                        pendingCollectionList.clear();
                        pendingCollectionList.addAll(SyncUtils.getInvoice());
                        pendingCollectionList.addAll(SyncUtils.getRetailerStock());
                        pendingCollectionList.add(Constants.ConfigTypsetTypeValues);
                        pendingROVal = 0;
                        if (tempRODevList != null) {
                            tempRODevList = null;
                            penROReqCount = 0;
                        }

                        if (sampleisbursementrArrayList != null && sampleisbursementrArrayList.size() > 0) {
                            tempRODevList = new String[sampleisbursementrArrayList.size()];

                            for (InvoiceListBean SalesOrderBean : sampleisbursementrArrayList) {
                                tempRODevList[pendingROVal] = SalesOrderBean.getDeviceNo();
                                pendingROVal++;
                            }
                            if (Constants.iSAutoSync) {
                                if (sampleDisbursementListsView != null) {
                                    sampleDisbursementListsView.hideProgressBar();
                                    sampleDisbursementListsView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                                }
                            } else {
                                if (sampleDisbursementListsView != null) {
                                    sampleDisbursementListsView.loadProgressBar();
                                }
                                Constants.isSync = true;
                                refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(context,syncType,syncType,refguid.toString().toUpperCase());
                                new SyncFromDataValtAsyncTask(context, tempRODevList, this, this).execute();
                            }
                        }
                    } else {
                        if (sampleDisbursementListsView != null) {
                            sampleDisbursementListsView.hideProgressBar();
                            sampleDisbursementListsView.showMessage(context.getString(R.string.no_network_conn));
                        }
                    }
                }else if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.hideProgressBar();
                sampleDisbursementListsView.showMessage(context.getString(R.string.no_req_to_update_sap));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.hideProgressBar();
            }
        }
    }

    public class InvoiceDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        InvoiceListBean invoiceListBean = new InvoiceListBean();
        private InvoiceListBean reqBean = null;

        public InvoiceDetailsAsyncTask(InvoiceListBean reqBean) {
            this.reqBean = reqBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.loadProgressBar();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (comingFrom.equalsIgnoreCase(Constants.NonDevice)) {
                String query = Constants.SSINVOICES + "(" + Constants.InvoiceGUID + "=guid'" + reqBean.getInvoiceGuid() + "')?$expand=" + Constants.SSInvoiceItemDetails + "";
                try {
                    invoiceListBean = OfflineManager.getInvoiceDetails(query, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    invoiceListBean = OfflineManager.getSampleDisbursementDetailsListFromDataValt(context,reqBean.getDeviceNo(), reqBean);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.hideProgressBar();
                if (invoiceListBean != null) {
                    invoiceListBean.setInvoiceStatus(reqBean.getInvoiceStatus());
                    invoiceListBean.setDueDateStatus(reqBean.getDueDateStatus());
//                  invoiceListBean.setPONo(reqBean.getPONo());
                    sampleDisbursementListsView.invoiceDetails(invoiceListBean);
                }
            }
        }
    }

    public class GetSampleDisbursementAsyncTask extends AsyncTask<Void, Void, ArrayList<InvoiceListBean>> {
        SampleDisbursementListsView.SampleDisbursemwntResponse<InvoiceListBean> sampleDisbursemwntResponse;

        public GetSampleDisbursementAsyncTask(SampleDisbursementListsView.SampleDisbursemwntResponse<InvoiceListBean> sampleDisbursemwntResponse) {
            this.sampleDisbursemwntResponse = sampleDisbursemwntResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (sampleDisbursementListsView != null) {
                sampleDisbursementListsView.loadProgressBar();
            }
        }

        @Override
        protected ArrayList<InvoiceListBean> doInBackground(Void... params) {
            sampleisbursementrArrayList.clear();
            getSampleDisbursementList();
            return sampleisbursementrArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<InvoiceListBean> sampleisbursementrArrayList) {
            super.onPostExecute(sampleisbursementrArrayList);
            if (sampleDisbursementListsView != null) {
                sampleDisbursemwntResponse.success(sampleisbursementrArrayList);
                sampleDisbursementListsView.hideProgressBar();
            }
        }
    }

    public class GetSampleDisbursementRefreshAsyncTask extends AsyncTask<Void, Void, ArrayList<InvoiceListBean>> {
        boolean isMaterialEnabled = false;

        public GetSampleDisbursementRefreshAsyncTask(boolean isMaterialEnabled) {
            this.isMaterialEnabled = isMaterialEnabled;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (sampleDisbursementListsView != null)
                sampleDisbursementListsView.loadProgressBar();
        }

        @Override
        protected ArrayList<InvoiceListBean> doInBackground(Void... params) {
            try {

                String qry = Constants.SSINVOICES + "?$filter=" + Constants.SoldToID + " eq'" + mCPNO + "' ";
                invoiceBeanArrayList.addAll(OfflineManager.getSampleDisruptionList(qry, context, "", CPGUID));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return invoiceBeanArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<InvoiceListBean> salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (sampleDisbursementListsView != null) {
                if (salesOrderBeenArrayList != null) {
                    onSearch(searchText);
                }
                sampleDisbursementListsView.syncSuccess();
                sampleDisbursementListsView.hideProgressBar();
            }
        }

    }

}
