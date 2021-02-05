package com.arteriatech.ss.msecsales.rspl.reports.returnorder.list;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

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
import com.arteriatech.ss.msecsales.rspl.mbo.SOListBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.filters.ROFilterActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10847 on 07-12-2017.
 */
public class ReturnOrderListPresenterImpl implements ReturnOrderListPresenter, UIListener, MessageWithBooleanCallBack {
    ArrayList<String> alAssignColl = null;
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private Activity context = null;
    private String customerNumber = "";
    private ArrayList<SalesOrderBean> salesOrderHeaderArrayList = null;
    private ArrayList<SalesOrderBean> searchBeanArrayList = null;
    private ReturnOrderListView returnOrderListView = null;
    private String searchText = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private long refreshTime = 0;
    private boolean isMaterialEnabled = false;
    private boolean isErrorFromBackend = false;
    private View view = null;
    private String cpGuid;
    private ArrayList<String> pendingCollectionList = new ArrayList<>();
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private String[] tempRODevList = null;
    private int isFromWhere=0;
    private String concatCollectionStr = "";
    private String syncType = "";
    private String parentId = "";
    private GUID refguid =null;

    public ReturnOrderListPresenterImpl(String cpGuid, Activity context, String customerNumber, String parentId, ReturnOrderListView returnOrderListView, boolean isMaterialEnabled, View view, String syncType) {
        this.context = context;
        this.customerNumber = customerNumber;
        this.returnOrderListView = returnOrderListView;
        this.salesOrderHeaderArrayList = new ArrayList<>();
        this.isMaterialEnabled = isMaterialEnabled;
        this.view = view;
        this.searchBeanArrayList = new ArrayList<>();
        this.cpGuid = cpGuid;
        this.parentId = parentId;
        this.syncType = syncType;
    }

    @Override
    public void connectToOfflineDB() {
        new GetSalesOrderAsyncTask(isMaterialEnabled).execute();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        returnOrderListView = null;
    }

    @Override
    public void onResume() {
        if (returnOrderListView != null) {
            if (refreshTime != 0)
                returnOrderListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.SSSOs));
        }
    }

    @Override
    public void onFilter() {
        if (returnOrderListView != null) {
            returnOrderListView.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearch(searchText, statusId, delvStatusId);
        }
    }

    private void onSearch(String searchText, String soStatus, String deliveryType) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (salesOrderHeaderArrayList != null) {
            if (TextUtils.isEmpty(searchText) && (TextUtils.isEmpty(deliveryType) || deliveryType.equalsIgnoreCase(Constants.All))) {
                searchBeanArrayList.addAll(salesOrderHeaderArrayList);
            } else {
                for (SalesOrderBean item : salesOrderHeaderArrayList) {
                    if (!TextUtils.isEmpty(deliveryType)) {
                        soTypeStatus = item.getStatusID().toLowerCase().contains(deliveryType.toLowerCase());
                    } else {
                        soTypeStatus = true;
                    }

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getOrderNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soTypeStatus && soSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }

        if (returnOrderListView != null) {
            returnOrderListView.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onRefresh() {
        onRefreshROrder();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
        endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
        statusId = data.getStringExtra(ROFilterActivity.EXTRA_SO_STATUS);
        statusName = data.getStringExtra(ROFilterActivity.EXTRA_SO_STATUS_NAME);
        delvStatusId = data.getStringExtra(ROFilterActivity.EXTRA_DELV_STATUS);
        delvStatusName = data.getStringExtra(ROFilterActivity.EXTRA_DELV_STATUS_NAME);

        displayFilterType();

        connectToOfflineDB();
    }

    @Override
    public void getRefreshTime() {
        if (returnOrderListView != null) {
            returnOrderListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.SSROs));
        }
    }

    @Override
    public void getDetails(String no) {
        new GetSODetailsASync(no).execute();
    }

    @Override
    public void onSync() {
        onSyncOrder();
    }
    private void onSyncOrder() {
        isErrorFromBackend = false;
        isFromWhere = 4;
        Constants.mBoolIsReqResAval = true;
        Constants.mBoolIsNetWorkNotAval = false;
        try {
            salesOrderHeaderArrayList.clear();
            salesOrderHeaderArrayList = OfflineManager.getROListFromDataValt(context, cpGuid,parentId);
            if (!salesOrderHeaderArrayList.isEmpty()) {

                    if (UtilConstants.isNetworkAvailable(context)) {
                        pendingCollectionList.clear();
                        pendingCollectionList.addAll(SyncUtils.getROsCollection());
                        pendingCollectionList.addAll(SyncUtils.getInvoice());
                        pendingCollectionList.addAll(SyncUtils.getFIPCollection());
                        pendingCollectionList.add(Constants.ConfigTypsetTypeValues);
                        pendingROVal = 0;
                        if (tempRODevList != null) {
                            tempRODevList = null;
                            penROReqCount = 0;
                        }

                        if (salesOrderHeaderArrayList != null && salesOrderHeaderArrayList.size() > 0) {
                            tempRODevList = new String[salesOrderHeaderArrayList.size()];

                            for (SalesOrderBean SalesOrderBean : salesOrderHeaderArrayList) {
                                tempRODevList[pendingROVal] = SalesOrderBean.getDeviceNo();
                                pendingROVal++;
                            }
                            if (Constants.iSAutoSync) {
                                if (returnOrderListView != null) {
                                    returnOrderListView.hideProgressDialog();
                                    returnOrderListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                                }
                            } else {
                                if (returnOrderListView != null) {
                                    returnOrderListView.showProgressDialog();
                                }
                                Constants.isSync = true;
                                refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(context,Constants.UpLoad,Constants.StartSync,refguid.toString().toUpperCase());
                                new SyncFromDataValtAsyncTask(context, tempRODevList, this, this).execute();
                            }

                        }
                    } else {
                        if (returnOrderListView != null) {
                            returnOrderListView.hideProgressDialog();
                            returnOrderListView.showMessage(context.getString(R.string.no_network_conn));
                        }
                    }
                }else if (returnOrderListView != null) {
                returnOrderListView.hideProgressDialog();
                returnOrderListView.showMessage(context.getString(R.string.no_req_to_update_sap));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            if (returnOrderListView != null) {
                returnOrderListView.hideProgressDialog();
            }
        }
    }

    @Override
    public void connectToDataValt() {
        try {
            salesOrderHeaderArrayList.clear();
            salesOrderHeaderArrayList = OfflineManager.getROListFromDataValt(context, cpGuid,parentId);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (returnOrderListView != null) {
            onSearch("", "", "");
            returnOrderListView.success();
            returnOrderListView.hideProgressDialog();
        }
    }

    @Override
    public void getPendingDetails(String ssroguid) {
        SOListBean salesOrderBeenArrayList = null;
        try {
            salesOrderBeenArrayList = OfflineManager.getRODetailsFromDataValt(context, ssroguid);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (returnOrderListView != null) {
            returnOrderListView.hideProgressDialog();
            returnOrderListView.openRODetail(salesOrderBeenArrayList);
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
            if (returnOrderListView != null) {
                returnOrderListView.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onRefreshROrder() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getROsCollection());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (returnOrderListView != null) {
                    returnOrderListView.hideProgressDialog();
                    returnOrderListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();

                    SyncUtils.updatingSyncStartTime(context,syncType,Constants.StartSync,  refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (returnOrderListView != null) {
                returnOrderListView.hideProgressDialog();
                returnOrderListView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere!=4) {
                if (!Constants.isStoreClosed) {
                    if (operation == Operation.OfflineRefresh.getValue()) {
                        Constants.updateLastSyncTimeToTable(context, alAssignColl,syncType,refguid.toString().toUpperCase());
                        Constants.isSync = false;
                        if (!Constants.isStoreClosed) {
                            if (returnOrderListView != null) {
                                returnOrderListView.hideProgressDialog();
                                returnOrderListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                            }
                        } else {
                            if (returnOrderListView != null) {
                                returnOrderListView.hideProgressDialog();
                                returnOrderListView.showMessage(context.getString(R.string.msg_sync_terminated));
                            }
                        }
                    } else if (operation == Operation.GetStoreOpen.getValue()) {
                        Constants.isSync = false;
                        if (returnOrderListView != null) {
                            returnOrderListView.hideProgressDialog();
                            returnOrderListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }
                    }
                }
            }else {
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
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    if (returnOrderListView != null) {
                        returnOrderListView.hideProgressDialog();
                        connectToDataValt();
                        UtilConstants.showAlert(context.getString(R.string.msg_error_occured_during_sync), context);
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (returnOrderListView != null) {
                    returnOrderListView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (returnOrderListView != null) {
                    returnOrderListView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (returnOrderListView != null) {
                returnOrderListView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (isFromWhere != 4) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, alAssignColl,syncType,refguid.toString().toUpperCase());
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(context, false);
                if (returnOrderListView != null) {
                    returnOrderListView.hideProgressDialog();
                    connectToOfflineDB();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
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
                if (returnOrderListView != null) {
                    returnOrderListView.hideProgressDialog();
                    connectToOfflineDB();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        }else {
            if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                Constants.mBoolIsReqResAval = true;
                /*Set<String> set = new HashSet<>();
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);

                HashSet<String> setTemp = new HashSet<>();
                if (set != null && !set.isEmpty()) {
                    Iterator itr = set.iterator();
                    while (itr.hasNext()) {
                        setTemp.add(itr.next().toString());
                    }
                }

                setTemp.remove(tempRODevList[penROReqCount]);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet(Constants.SecondarySOCreate, setTemp);
                editor.commit();*/
                Constants.removeDataValtFromSharedPref(context, Constants.ROList, tempRODevList[penROReqCount], false);
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
                Constants.updateLastSyncTimeToTable(context, pendingCollectionList,syncType,refguid.toString().toUpperCase());
                ReturnOrderListActivity.mBoolRefreshDone = true;
                Constants.isSync = false;
                if (returnOrderListView != null) {
                    returnOrderListView.hideProgressDialog();
                    String msg = "";
                    ConstantsUtils.startAutoSync(context, false);
                    if (returnOrderListView != null) {
                        if (!isErrorFromBackend) {
                            returnOrderListView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                            connectToDataValt();
                        } else {
                            returnOrderListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                            connectToDataValt();
                        }
                        AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
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
                if (returnOrderListView != null) {
                    returnOrderListView.hideProgressDialog();
                    connectToDataValt();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        }
    }

    /**
     * get ROs list from OfflineDB
     */
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();
    private void getReturnOrderList() {
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
//            String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPNo + " eq '" + customerNumber + "' and ("+strUserAuthDivisionID+")";
            String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' and ("+strUserAuthDivisionID+")";


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
        if(!TextUtils.isEmpty(strDmsDivision)) {
            String tempParentID = "";
            if(!TextUtils.isEmpty(parentId)){
                tempParentID=String.valueOf(Integer.parseInt(parentId));
            }
           // String query = Constants.SSROs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' " + "and " + Constants.OrderType + " eq '" + Constants.getReturnOrderType() + "' and ("+strDmsDivision+") &$orderby=" + Constants.OrderNo + " desc";
            String query = Constants.SSROs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' and "+ Constants.FromCPGUID + " eq '" +tempParentID + "' " + "and " + Constants.OrderType + " eq '" + Constants.getReturnOrderType() + "' &$orderby=" + Constants.OrderNo + " desc";
//            String query = Constants.SSROs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' " + "and " + Constants.OrderType + " eq '" + Constants.getReturnOrderType() + "' &$orderby=" + Constants.OrderNo + " desc";
            try {
                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = OfflineManager.getSecondarySalesOrderList(context, query);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @desc get SOs with material from offline DB
     * @param status
     */
    private void getROList(String status, String delvStatusId) {
        String qry = Constants.SSROItemDetails + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(customerNumber) + "' " +
                "and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' ";
        try {
            if (!salesOrderHeaderArrayList.isEmpty()) {
                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = new ArrayList<>();
            }
            salesOrderHeaderArrayList.addAll(OfflineManager.getSOListDB(context, qry, customerNumber, status));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (returnOrderListView != null) {
                returnOrderListView.hideProgressDialog();
                UtilConstants.showAlert(errorMsg, context);
            }
        }
    }

    /**
     * @desc Get SOs on Background Thread
     */
    public class GetSalesOrderAsyncTask extends AsyncTask<Void, Void, ArrayList<SalesOrderBean>> {
        boolean isMaterialEnabled = false;

        public GetSalesOrderAsyncTask(boolean isMaterialEnabled) {
            this.isMaterialEnabled = isMaterialEnabled;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (returnOrderListView != null)
                returnOrderListView.showProgressDialog();
        }

        @Override
        protected ArrayList<SalesOrderBean> doInBackground(Void... params) {
                getReturnOrderList();
            return salesOrderHeaderArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<SalesOrderBean> salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (returnOrderListView != null) {
                if (salesOrderBeenArrayList != null) {
                    onSearch(searchText, statusId, delvStatusId);
                }
                returnOrderListView.success();
                returnOrderListView.hideProgressDialog();
            }
        }

    }

    public class GetSODetailsASync extends AsyncTask<Void, Void, SOListBean> {

        SOListBean soListBean;
        String soNO;

        public GetSODetailsASync(String soNO) {
            this.soNO = soNO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (returnOrderListView != null)
                returnOrderListView.showProgressDialog();
        }

        @Override
        protected SOListBean doInBackground(Void... params) {
            String query = Constants.SSROs + "(guid'" + soNO + "')?$expand=SSROItemDetails";
            return OfflineManager.getRODetails(query);
        }

        @Override
        protected void onPostExecute(SOListBean salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (returnOrderListView != null) {
                returnOrderListView.hideProgressDialog();
                returnOrderListView.openRODetail(salesOrderBeenArrayList);
            }
        }

    }

}
