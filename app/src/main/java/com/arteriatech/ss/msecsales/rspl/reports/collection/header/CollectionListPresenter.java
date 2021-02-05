package com.arteriatech.ss.msecsales.rspl.reports.collection.header;

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
import com.arteriatech.ss.msecsales.rspl.mbo.CollectionHistoryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
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

public class CollectionListPresenter implements ICollectionHeaderListPresenter, UIListener {
    ArrayList<String> alAssignColl = null;
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private Activity context = null;
    private String customerNumber = "";
    private String customerName = "";
    private ArrayList<CollectionHistoryBean> collArrayList = null;
    private ArrayList<CollectionHistoryBean> searchBeanArrayList = null;
    private ICollectionListView ICollListView = null;
    private String searchText = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private long refreshTime = 0;
    private boolean isErrorFromBackend = false;
    private View view = null;
    private String cpGuid;
    private GUID refguid =null;

    public CollectionListPresenter(String cpGuid, Activity context, String customerNumber, String customerName, ICollectionListView ICollListView, View view) {
        this.context = context;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.ICollListView = ICollListView;
        this.collArrayList = new ArrayList<>();
        this.view = view;
        this.searchBeanArrayList = new ArrayList<>();
        this.cpGuid = cpGuid;
    }

    @Override
    public void connectToOfflineDB() {
        new GetSalesOrderAsyncTask().execute();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        ICollListView = null;
    }

    @Override
    public void onResume() {
        if (ICollListView != null) {
            if (refreshTime != 0)
                ICollListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.SSSOs));
        }
    }

    @Override
    public void onFilter() {
        if (ICollListView != null) {
            ICollListView.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
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
//        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (collArrayList != null) {
            if (TextUtils.isEmpty(searchText) && (TextUtils.isEmpty(delivetyType) || delivetyType.equalsIgnoreCase(Constants.All))) {
                searchBeanArrayList.addAll(collArrayList);
            } else {
                for (CollectionHistoryBean item : collArrayList) {

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getFIPDocNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (ICollListView != null) {
            ICollListView.searchResult(searchBeanArrayList);
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
        if (ICollListView != null) {
//            if (refreshTime != 0)
            ICollListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.SSSOs));
        }
    }

    @Override
    public void getDetails(final CollectionHistoryBean collectionHistoryBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CollectionHistoryBean> alCollHisDetails = new ArrayList<>();
                String mStrCollectionItemQry = Constants.FinancialPostingItemDetails + "?$filter=" + Constants.FIPGUID + " eq " + collectionHistoryBean.getFIPGUID() + " &$orderby=" + Constants.FIPItemNo + " asc";
                try {
                    alCollHisDetails = OfflineManager.getCollectionItemDetails(context, mStrCollectionItemQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                final ArrayList<CollectionHistoryBean> finalAlCollHisDetails = alCollHisDetails;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ICollListView != null) {
                            collectionHistoryBean.setAlCollItemList(finalAlCollHisDetails);
                            ICollListView.openSODetail(collectionHistoryBean);
                        }
                    }
                });
            }
        }).start();
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
            if (ICollListView != null) {
                ICollListView.setFilterDate(statusDesc);
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
            alAssignColl.addAll(SyncUtils.getFIPCollection());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (ICollListView != null) {
                    ICollListView.hideProgressDialog();
                    ICollListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.Collection_PD_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (ICollListView != null) {
                ICollListView.hideProgressDialog();
                ICollListView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Collection_PD_sync,refguid.toString().toUpperCase());
                Constants.isSync = false;
                if (ICollListView != null) {
                    ICollListView.hideProgressDialog();
                    ICollListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                }


            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (ICollListView != null) {
                    ICollListView.hideProgressDialog();
                    ICollListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (ICollListView != null) {
                    ICollListView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (ICollListView != null) {
                    ICollListView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (ICollListView != null) {
                ICollListView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Collection_PD_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (ICollListView != null) {
                ICollListView.hideProgressDialog();
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
            Constants.setSyncTime(context,Constants.Sync_All);
            ConstantsUtils.startAutoSync(context, false);
            if (ICollListView != null) {
                ICollListView.hideProgressDialog();
                connectToOfflineDB();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

    /**
     * get SOs HeaderList from OfflineDB
     */
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();
    private void getCollList() {
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
                    strDmsDivision = strDmsDivision + "DMSDivision eq '" + alDmsDivision.get(i).getDMSDivisionID()+"'";
                }else {
                    strDmsDivision = strDmsDivision + "DMSDivision eq '" + alDmsDivision.get(i).getDMSDivisionID() + "' or ";
                }
            }
        }
        collArrayList.clear();
        if(!TextUtils.isEmpty(strDmsDivision)){
            String query = Constants.FinancialPostings + "?$filter=" + Constants.CPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGuid) + "' and ("+strDmsDivision+")";
            try {
                collArrayList = OfflineManager.getCollectionHistoryList(query, context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Get SOs on Background Thread
     */
    public class GetSalesOrderAsyncTask extends AsyncTask<Void, Void, ArrayList<CollectionHistoryBean>> {

        public GetSalesOrderAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ICollListView != null)
                ICollListView.showProgressDialog();
        }

        @Override
        protected ArrayList<CollectionHistoryBean> doInBackground(Void... params) {
            getCollList();
            return collArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<CollectionHistoryBean> collBeanArrayList) {
            super.onPostExecute(collBeanArrayList);
            if (ICollListView != null) {
                if (collBeanArrayList != null) {
                    onSearch(searchText, statusId, delvStatusId);
                }
                ICollListView.success();
                ICollListView.hideProgressDialog();
            }
        }

    }

}
