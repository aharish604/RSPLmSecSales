package com.arteriatech.ss.msecsales.rspl.reports.salesorder.pendingsync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.arteriatech.ss.msecsales.rspl.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.header.SalesOrderHeaderListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by e10847 on 07-12-2017.
 */

public class SalesOrderHeaderPendingSyncPresenter implements ISalesOrderHeaderPendingSyncPresenter, UIListener, MessageWithBooleanCallBack {
    private Activity context = null;
    private String CPGUID = "";
    private String mStrParentId = "";
    private ArrayList<SalesOrderBean> salesOrderHeaderArrayList = new ArrayList<>();
    private ISalesOrderPendingSyncView salesOrderView = null;
    private ArrayList<String> pendingCollectionList = null;
    private View view;
    private boolean isErrorFromBackend = false;
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private int isFromWhere = 0;
    private String[] tempRODevList = null;
    private String[] sotempdocNoList = null;
    private boolean dialogCancelled = false;
    private String concatCollectionStr = "";
    private GUID refguid =null;

    public SalesOrderHeaderPendingSyncPresenter(Activity context, String CPGUID, String mStrParentId, ISalesOrderPendingSyncView salesOrderPendingSyncView, View view) {
        this.context = context;
        this.CPGUID = CPGUID;
        this.mStrParentId = mStrParentId;
        this.salesOrderView = salesOrderPendingSyncView;
        this.salesOrderHeaderArrayList = new ArrayList<>();
        this.pendingCollectionList = new ArrayList<>();
        this.view = view;
    }

    @Override
    public void connectToOfflineDB(ISalesOrderPendingSyncView.SalesOrderResponse<SalesOrderBean> salesOrderResponse) {
        GetSalesOrderAsyncTask salesOrderAsyncTask = new GetSalesOrderAsyncTask(salesOrderResponse);
        salesOrderAsyncTask.execute();
    }

    @Override
    public void onSync() {
        onSyncSOrder();
    }

    @Override
    public void onDestroy() {
        salesOrderView = null;
    }

    @Override
    public void onRequestError(int i, Exception e) {
        onError(i, e);
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        onSuccess(i, s);
    }

    /**
     * get SOs Pending Sync List from DataVault
     */

    private void getSalesOrderList() {
        try {
            salesOrderHeaderArrayList.addAll(OfflineManager.getSSSoListFromDataValt(context, CPGUID,mStrParentId));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onSyncSOrder() {
        Constants.isPullDownSync=true;
            isErrorFromBackend = false;
            isFromWhere = 4;
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = false;
            try {
                salesOrderHeaderArrayList.clear();
                salesOrderHeaderArrayList = (ArrayList<SalesOrderBean>) OfflineManager.getSSSoListFromDataValt(context, CPGUID,mStrParentId);
                if (!salesOrderHeaderArrayList.isEmpty()) {
                        if (UtilConstants.isNetworkAvailable(context)) {
                            pendingCollectionList.clear();
                            pendingCollectionList.addAll(SyncUtils.getSOsCollection());
                            pendingCollectionList.add(Constants.ConfigTypsetTypeValues);
                            pendingROVal = 0;
                            if (tempRODevList != null) {
                                tempRODevList = null;
                                penROReqCount = 0;
                            }

                        if (salesOrderHeaderArrayList != null && salesOrderHeaderArrayList.size() > 0) {
                            tempRODevList = new String[salesOrderHeaderArrayList.size()];
                            sotempdocNoList = new String[salesOrderHeaderArrayList.size()];

                                for (SalesOrderBean SalesOrderBean : salesOrderHeaderArrayList) {
                                    tempRODevList[pendingROVal] = SalesOrderBean.getDeviceNo();
                                sotempdocNoList[pendingROVal] = SalesOrderBean.getDeviceNo()+"_temp";                                    
				pendingROVal++;
                                }
                                if (Constants.iSAutoSync||Constants.isBackGroundSync) {
                                    if (salesOrderView != null) {
                                        salesOrderView.hideProgressDialog();
                                        if (Constants.iSAutoSync) {
                                            salesOrderView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                                        }else{
                                            salesOrderView.showMessage(context.getString(R.string.alert_backgrounf_sync_is_progress));
                                        }
                                        Constants.isPullDownSync=false;
                                    }
                                } else {
                                    if (salesOrderView != null) {
                                        salesOrderView.showProgressDialog();
                                    }
                                    Constants.isSync = true;
                                    refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(context,Constants.SOPOSTBG_sync,Constants.StartSync, refguid.toString().toUpperCase());
                                    new SyncFromDataValtAsyncTask(context, tempRODevList, this, this).execute();
                                }
                            }
                        } else {
                            if (salesOrderView != null) {
                                salesOrderView.hideProgressDialog();
                                salesOrderView.showMessage(context.getString(R.string.no_network_conn));

                            }
                            Constants.isPullDownSync=false;
                        }
                } else if (salesOrderView != null) {
                    salesOrderView.hideProgressDialog();
                    salesOrderView.onReloadData();
                    salesOrderView.showMessage(context.getString(R.string.no_req_to_update_sap));
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                if (salesOrderView != null) {
                    salesOrderView.hideProgressDialog();
                }
                Constants.isPullDownSync=false;
            }
    }

    public void onError(int operation, Exception e) {
        Constants.isPullDownSync=false;
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere == 1 || isFromWhere == 2) {
                if (operation == Operation.OfflineRefresh.getValue()) {

                    Constants.isSync = false;
                    if (salesOrderView != null) {
                        salesOrderView.hideProgressDialog();
                        salesOrderView.onReloadData();
                        if (!Constants.isStoreClosed) {
                            salesOrderView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        } else {
                            salesOrderView.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
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
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    if (salesOrderView != null) {
                        salesOrderView.hideProgressDialog();
                        salesOrderView.onReloadData();
                        UtilConstants.showAlert(context.getString(R.string.msg_error_occured_during_sync), context);
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (salesOrderView != null) {
                    salesOrderView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (salesOrderView != null) {
                    salesOrderView.hideProgressDialog();
                    salesOrderView.onReloadData();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.mBoolIsReqResAval = true;
            Constants.isSync = false;
            if (salesOrderView != null) {
                salesOrderView.hideProgressDialog();
                salesOrderView.onReloadData();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    public void onSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        Constants.isPullDownSync=false;
        if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 2) {
            Constants.updateLastSyncTimeToTable(context, pendingCollectionList,Constants.SOPOSTBG_sync, refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (salesOrderView != null) {
                salesOrderView.hideProgressDialog();
                salesOrderView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                salesOrderView.onReloadData();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
        if (isFromWhere == 4) {
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
                Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreate, tempRODevList[penROReqCount], false);
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
                Constants.updateLastSyncTimeToTable(context, pendingCollectionList,Constants.SOPOSTBG_sync, refguid.toString().toUpperCase());
                SalesOrderHeaderListActivity.mBoolRefreshDone = true;
                removetempSosDocNo();
                if (salesOrderView != null) {
                    salesOrderView.hideProgressDialog();
                    String msg = "";
                    ConstantsUtils.startAutoSync(context, false);
                    if (salesOrderView != null) {
                        if (!isErrorFromBackend) {
                            salesOrderView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                            salesOrderView.onReloadData();
                        } else {
                            salesOrderView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                            salesOrderView.onReloadData();
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
                if (salesOrderView != null) {
                    salesOrderView.onReloadData();
                    salesOrderView.hideProgressDialog();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        }
    }

    private void removetempSosDocNo(){
        try {
            if(sotempdocNoList!=null && sotempdocNoList.length>0) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                for(String docNo:sotempdocNoList) {
                    Constants.removeDeviceDocNoFromSharedPref(context, Constants.SecondarySOCreateTemp, docNo, sharedPreferences, false);
                    ConstantsUtils.storeInDataVault(docNo, "",context);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Constants.isBackGroundSync = false;
            Constants.isSync = false;
        }

    }

    @Override
    public void clickedStatus(boolean clickedStatus, String err_msg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (salesOrderView != null) {
                salesOrderView.hideProgressDialog();
                UtilConstants.showAlert(err_msg, context);
            }
        }
    }

    /**
     * Get SOs on Background Thread
     */
    public class GetSalesOrderAsyncTask extends AsyncTask<Void, Void, ArrayList<SalesOrderBean>> {
        ISalesOrderPendingSyncView.SalesOrderResponse<SalesOrderBean> salesOrderResponse;

        public GetSalesOrderAsyncTask(ISalesOrderPendingSyncView.SalesOrderResponse<SalesOrderBean> salesOrderResponse) {
            this.salesOrderResponse = salesOrderResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (salesOrderView != null) {
                salesOrderView.showProgressDialog();
            }
        }

        @Override
        protected ArrayList<SalesOrderBean> doInBackground(Void... params) {
            salesOrderHeaderArrayList.clear();
            getSalesOrderList();
            return salesOrderHeaderArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<SalesOrderBean> salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (salesOrderView != null) {
                salesOrderResponse.success(salesOrderBeenArrayList);
                salesOrderView.hideProgressDialog();
            }
        }
    }
}
