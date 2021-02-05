package com.arteriatech.ss.msecsales.rspl.competitors.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

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

import com.arteriatech.ss.msecsales.rspl.feedback.list.FeedbackListActivity;
import com.arteriatech.ss.msecsales.rspl.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.merchandising.list.MerchandisingListPresenterImpl;
import com.arteriatech.ss.msecsales.rspl.reports.merchandising.MerchandisingListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class CompetitorListPresenterImpl implements CompetitorListPresenter, UIListener, MessageWithBooleanCallBack {

    private Context mContext;
    private CompetitorListView competitorListView = null;
    private String mStrBundleCPGUID = "";
    private String mStrBundleCPGUID32 = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String comingFrom = "";
    private String syncType = "";
    private ArrayList<CompetitorBean> alCompetitorBean = new ArrayList<>();
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ArrayList<String> alFlushColl = new ArrayList<>();
    private Activity mActivity;
    private int mError = 0;
    private GUID refguid =null;


    public CompetitorListPresenterImpl(Activity mActivity, Context mContext, CompetitorListView competitorListView, Bundle bundleExtras) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.competitorListView = competitorListView;
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            comingFrom = bundleExtras.getString(Constants.comingFrom);
            syncType = bundleExtras.getString(Constants.SyncType);
        }
    }

    @Override
    public void onStart() {
        try {
            if (competitorListView != null) {
                competitorListView.showProgress();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        alCompetitorBean.clear();
                        if (comingFrom.equalsIgnoreCase(Constants.Device)) {
                            alCompetitorBean.addAll(OfflineManager.getCompetitors(mContext, "CompetitorInfos?$filter=CPGUID eq '" + mStrBundleCPGUID32 + "'and" + " sap.islocal()"));
                        } else {
                            alCompetitorBean.addAll(OfflineManager.getCompetitors(mContext, "" + Constants.CompetitorInfos + "?$filter=CPGUID eq '" + mStrBundleCPGUID32 + "'and" + " not sap.islocal()"));
                        }

                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (competitorListView != null) {
                                competitorListView.hideProgress();
                                competitorListView.displayList(alCompetitorBean);
                                competitorListView.displayLSTSyncTime(SyncUtils.getCollectionSyncTime(mContext, Constants.CompetitorInfos));
                            }
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        competitorListView = null;
    }

    @Override
    public void onItemClick(CompetitorBean itemBean) {
        Intent intent = new Intent(mContext, CompetitorDetailsActivity.class);
        intent.putExtra(Constants.CPNo, mStrBundleRetID);
        intent.putExtra(Constants.RetailerName, mStrBundleRetName);
        intent.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intent.putExtra(Constants.CPGUID32, mStrBundleCPGUID32);
        intent.putExtra(Constants.CPUID, mStrBundleRetUID);
        intent.putExtra(Constants.ItemList, itemBean);

        mContext.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (competitorListView != null) {
            competitorListView.showProgress();
        }

        mError = 0;
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(mContext)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getCompetitors());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                    competitorListView.showMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(mContext,syncType,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (competitorListView != null) {
                competitorListView.hideProgress();
                comingFrom = Constants.NonDevice;
                competitorListView.showMessage(mContext.getString(R.string.no_network_conn));
            }
        }
    }

    /**
     * @desc to upload pending competitor list
     */
    @Override
    public void onUploadData() {
        try {
            mError = 0;
            if (!alCompetitorBean.isEmpty()) {
                if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && alCompetitorBean.size() == 0) {
                    if (competitorListView != null) {
                        competitorListView.hideProgress();
                        competitorListView.showMessage(mContext.getString(R.string.no_req_to_update_competitors));
                    }
                } else {
                    if (Constants.iSAutoSync) {
                        if (competitorListView != null) {
                            competitorListView.hideProgress();
                            competitorListView.showMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
                        }
                    } else {
                        getRefreshList();
                        if (UtilConstants.isNetworkAvailable(mContext)) {
                            if (competitorListView != null) {
                                competitorListView.showProgress();
                            }
                            Constants.isSync = true;
                            try {
                                refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(mContext,syncType,Constants.StartSync,refguid.toString().toUpperCase());
                                new AsyncPostOfflineData().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (competitorListView != null) {
                                competitorListView.hideProgress();
                                competitorListView.showMessage(mContext.getString(R.string.no_network_conn));
                            }
                        }
                    }
                }
            } else {
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                    comingFrom = Constants.NonDevice;
                }
            }
        } catch (ODataException e) {
            e.printStackTrace();
            if (competitorListView != null) {
                competitorListView.hideProgress();
            }
        }
    }

    /**
     * @desc to get refreshed list
     */
    private void getRefreshList() {
        alAssignColl.clear();
        alCompetitorBean.clear();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.CompetitorInfos + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.CompetitorInfos);
                alFlushColl.add(Constants.CompetitorInfos);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.VisitActivities);
                alFlushColl.add(Constants.VisitActivities);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    /**
     * @desc async task to post or sync
     */
    public class AsyncPostOfflineData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                String concatFlushCollStr = UtilConstants.getConcatinatinFlushCollectios(alFlushColl);
                Constants.Entity_Set.clear();
                Constants.AL_ERROR_MSG.clear();
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            OfflineManager.flushQueuedRequests(CompetitorListPresenterImpl.this, concatFlushCollStr);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ODataException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (competitorListView != null) {
                competitorListView.hideProgress();
                competitorListView.showMessage(errorMsg);
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, mContext);
        if (errorBean.hasNoError()) {
            mError++;
            if (operation == Operation.OfflineFlush.getValue()) {
                Constants.isSync = false;
                MerchandisingListActivity.mBoolRefreshDone = true;
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                    Constants.displayErrorDialog(mContext, errorBean.getErrorMsg());
                }
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                MerchandisingListActivity.mBoolRefreshDone = true;
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                    Constants.displayErrorDialog(mContext, errorBean.getErrorMsg());
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                    competitorListView.showMessage(mContext.getString(R.string.msg_offline_store_failure));
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        } else {
            Constants.isSync = false;
            if (competitorListView != null) {
                competitorListView.hideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineFlush.getValue()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                String concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(mContext, false);
                CompetitorListActivity.mBoolRefreshDone = true;
                if (competitorListView != null) {
                    competitorListView.hideProgress();
                    competitorListView.showMessage(mContext.getString(R.string.no_network_conn));
                }
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(mContext, alAssignColl,syncType,refguid.toString().toUpperCase());
            Constants.isSync = false;
            CompetitorListActivity.mBoolRefreshDone = true;
            ConstantsUtils.startAutoSync(mContext, false);
            if (competitorListView != null) {
                competitorListView.hideProgress();
                if (comingFrom.equalsIgnoreCase(Constants.Device)) {
                    if (mError == 0) {
                        competitorListView.showMessage(mContext.getString(R.string.msg_sync_successfully_completed));
                    } else {
                        competitorListView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync));
                    }
                }

                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, (Activity) mContext, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                onStart();
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(mContext, false);
            Constants.setSyncTime(mContext, Constants.Sync_All);
            if (competitorListView != null) {
                competitorListView.hideProgress();
                competitorListView.showMessage(mContext.getString(R.string.msg_offline_store_success));
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, (Activity) mContext, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                onStart();
            }
        }
    }
}
