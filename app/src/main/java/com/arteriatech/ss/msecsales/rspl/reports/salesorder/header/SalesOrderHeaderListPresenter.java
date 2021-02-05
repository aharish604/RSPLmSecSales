package com.arteriatech.ss.msecsales.rspl.reports.salesorder.header;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOListBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.filter.SOFilterActivity;
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

public class SalesOrderHeaderListPresenter implements ISalesOrderHeaderListPresenter, UIListener {
    // private ProgressDialog progressDialog = null;
    ArrayList<String> alAssignColl = null;
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private Activity context = null;
    private String customerNumber = "";
    private ArrayList<SalesOrderBean> salesOrderHeaderArrayList = null;
    private ArrayList<SalesOrderBean> searchBeanArrayList = null;
    private ISalesOrderListView ISalesOrderListView = null;
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
    private String mStrParentId;
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();
    private GUID refguid =null;

    public SalesOrderHeaderListPresenter(String cpGuid, Activity context, String customerNumber, String mStrParentId, ISalesOrderListView ISalesOrderListView, boolean isMaterialEnabled, View view) {
        this.context = context;
        this.customerNumber = customerNumber;
        this.ISalesOrderListView = ISalesOrderListView;
        this.salesOrderHeaderArrayList = new ArrayList<>();
        this.isMaterialEnabled = isMaterialEnabled;
        this.view = view;
        this.searchBeanArrayList = new ArrayList<>();
        this.cpGuid = cpGuid;
        this.mStrParentId = mStrParentId;
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
        ISalesOrderListView = null;
    }

    @Override
    public void onResume() {
        if (ISalesOrderListView != null) {
            if (refreshTime != 0)
                ISalesOrderListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.SSSOs));
        }
    }

    @Override
    public void onFilter() {
        if (ISalesOrderListView != null) {
            ISalesOrderListView.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearch(searchText, statusId, delvStatusId);
        }
    }

    private void onSearch(String searchText, String soStatus, String delivetyType) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (salesOrderHeaderArrayList != null) {
            if (TextUtils.isEmpty(searchText) && (TextUtils.isEmpty(delivetyType) || delivetyType.equalsIgnoreCase(Constants.All))) {
                searchBeanArrayList.addAll(salesOrderHeaderArrayList);
            } else {
                for (SalesOrderBean item : salesOrderHeaderArrayList) {
                    if (!TextUtils.isEmpty(delivetyType)) {
                        soTypeStatus = item.getStatusID().toLowerCase().contains(delivetyType.toLowerCase());
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
        if (ISalesOrderListView != null) {
            ISalesOrderListView.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onRefresh() {
        onRefreshSOrder();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
        endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
        statusId = data.getStringExtra(SOFilterActivity.EXTRA_SO_STATUS);
        statusName = data.getStringExtra(SOFilterActivity.EXTRA_SO_STATUS_NAME);
        delvStatusId = data.getStringExtra(SOFilterActivity.EXTRA_DELV_STATUS);
        delvStatusName = data.getStringExtra(SOFilterActivity.EXTRA_DELV_STATUS_NAME);
//        requestSOList(startDate, endDate);\
        displayFilterType();

        connectToOfflineDB();
    }

    @Override
    public void getRefreshTime() {
        if (ISalesOrderListView != null) {
//            if (refreshTime != 0)
            ISalesOrderListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.SSSOs));
        }
    }

    @Override
    public void getDetails(String no) {
        new GetSODetailsASync(no).execute();
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
            if (ISalesOrderListView != null) {
                ISalesOrderListView.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onRefreshSOrder() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getSOsCollection());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (ISalesOrderListView != null) {
                    ISalesOrderListView.hideProgressDialog();
                    ISalesOrderListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.SOPD_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                ISalesOrderListView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.SOPD_sync,refguid.toString().toUpperCase());
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (ISalesOrderListView != null) {
                            ISalesOrderListView.hideProgressDialog();
                            ISalesOrderListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }


                    } else {
                        if (ISalesOrderListView != null) {
                            ISalesOrderListView.hideProgressDialog();
                            ISalesOrderListView.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (ISalesOrderListView != null) {
                        ISalesOrderListView.hideProgressDialog();
                        ISalesOrderListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (ISalesOrderListView != null) {
                    ISalesOrderListView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (ISalesOrderListView != null) {
                    ISalesOrderListView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.SOPD_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
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
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                connectToOfflineDB();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

    /**
     * get SOs HeaderList from OfflineDB
     */
    private void getSalesOrderList() {
        /*String sortStr = " &$orderby=" + Constants.SONo + " desc";
        String qry = Constants.SSSOs + "?$select=SONo,DelvStatus,Status,Currency,TotalAmount,OrderDate&$filter=" + Constants.CustomerNo + " eq '" + customerNumber + "'";
        if (!TextUtils.isEmpty(statusId)) {
            qry = qry + " and Status eq '" + statusId + "'";
        }
        if (!TextUtils.isEmpty(delvStatusId)) {
            qry = qry + " and DelvStatus eq '" + delvStatusId + "'";
        }
        qry = qry + sortStr;
        try {
            if (!salesOrderHeaderArrayList.isEmpty()) {
                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = new ArrayList<>();
            }
            this.salesOrderHeaderArrayList.addAll(OfflineManager.getSecondarySalesOrderList(context, qry, customerNumber, "00"));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
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
        String query = "";
        if(!TextUtils.isEmpty(strDmsDivision)) {
            String tempParentID = "";
            if(!TextUtils.isEmpty(mStrParentId)){
                tempParentID = String.valueOf(Integer.parseInt(mStrParentId));
            }
//            query = Constants.SSSOs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' " + "and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' and ("+strDmsDivision+") &$orderby=" + Constants.OrderNo + " desc";

            query = Constants.SSSOs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' and "+Constants.FromCPGUID + " eq '" + tempParentID + "' " + "and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' and ("+strDmsDivision+") &$orderby=" + Constants.OrderNo + " desc";
            //query = Constants.SSSOs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' " + "and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' &$orderby=" + Constants.OrderNo + " desc";
            try {
                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = OfflineManager.getSecondarySalesOrderList(context, query);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get SOs with material from offline DB
     *
     * @param status
     */
    private void getSOList(String status, String delvStatusId) {
/*
        String sortStr = " &$orderby=" + Constants.SONo + " desc";
        String qry = Constants.SSSoItemDetails +"?$filter="+ Constants.SoldToCPGUID+" eq guid'"+Constants.convertStrGUID32to36(customerNumber)+"' " +
                "and "+ Constants.OrderType+" eq '" + Constants.getSOOrderType() + "' ";
        if (!TextUtils.isEmpty(status)) {
            qry = qry + " and StatusID eq '" + status + "'";
        }
        if (!TextUtils.isEmpty(delvStatusId)) {
            qry = qry + " and DelvStatusID eq '" + delvStatusId + "'";
        }
        qry = qry + sortStr;*/
        String qry = Constants.SSSoItemDetails + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + Constants.convertStrGUID32to36(customerNumber) + "' " +
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

    /**
     * Get SOs on Background Thread
     */
    public class GetSalesOrderAsyncTask extends AsyncTask<Void, Void, ArrayList<SalesOrderBean>> {
        boolean isMaterialEnabled = false;

        public GetSalesOrderAsyncTask(boolean isMaterialEnabled) {
            this.isMaterialEnabled = isMaterialEnabled;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ISalesOrderListView != null)
                ISalesOrderListView.showProgressDialog();
        }

        @Override
        protected ArrayList<SalesOrderBean> doInBackground(Void... params) {
            if (isMaterialEnabled) {
                getSOList(statusId, delvStatusId);
            } else {
                getSalesOrderList();
            }
            return salesOrderHeaderArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<SalesOrderBean> salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (ISalesOrderListView != null) {
                if (salesOrderBeenArrayList != null) {
                    onSearch(searchText, statusId, delvStatusId);
                }
                ISalesOrderListView.success();
                ISalesOrderListView.hideProgressDialog();
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
            if (ISalesOrderListView != null)
                ISalesOrderListView.showProgressDialog();
        }

        @Override
        protected SOListBean doInBackground(Void... params) {
            String query = Constants.SSSOs + "(guid'" + soNO + "')?$expand=SSSOItemDetails";
            return OfflineManager.getSODetails(query);
        }

        @Override
        protected void onPostExecute(SOListBean salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (ISalesOrderListView != null) {
                ISalesOrderListView.hideProgressDialog();
                ISalesOrderListView.openSODetail(salesOrderBeenArrayList);
            }
        }


    }

}
