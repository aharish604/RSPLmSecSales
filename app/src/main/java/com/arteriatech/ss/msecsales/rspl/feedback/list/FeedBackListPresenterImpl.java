package com.arteriatech.ss.msecsales.rspl.feedback.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

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
import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FeedBackListPresenterImpl implements FeedBackListPresenter, UIListener, MessageWithBooleanCallBack {

    private Context mContext;
    private FeedBackListView feedBackListView = null;
    private String mStrBundleCPGUID = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String comingFrom = "";
    private String syncType = "";
    private String parentId = "";
    private ArrayList<FeedbackBean> alFeedBackBean = new ArrayList<>();
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private int pendingROVal = 0;
    private String[] tempRODevList = null;
    private int penROReqCount = 0;
    private Activity mActivity;
    private boolean isErrorFromBackend = false;
    private GUID refguid =null;

    public FeedBackListPresenterImpl(Activity mActivity, Context mContext, FeedBackListView feedBackListView, Bundle bundleExtras) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.feedBackListView = feedBackListView;
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            comingFrom = bundleExtras.getString(Constants.comingFrom);
            syncType = bundleExtras.getString(Constants.SyncType);
            parentId = bundleExtras.getString(Constants.ParentId);
        }
    }

    @Override
    public void onStart() {
        if (feedBackListView != null) {
            feedBackListView.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    alFeedBackBean.clear();
                    if (comingFrom.equalsIgnoreCase(Constants.Device)) {
                        alFeedBackBean.addAll(OfflineManager.getDeviceFeedBackList(mContext, mStrBundleCPGUID,parentId));
                    } else {
                        /*String temParentID = "";

                        if(!TextUtils.isEmpty(parentId)){
                            temParentID=String.valueOf(Integer.parseInt(parentId));
                        }*/
                        alFeedBackBean.addAll(OfflineManager.getFeedBackList(Constants.Feedbacks + "?$filter=" +Constants.FromCPGUID + " eq '" + mStrBundleCPGUID+ "' &$orderby= FeedbackNo desc",mContext));

//                        alFeedBackBean.addAll(OfflineManager.getFeedBackList(Constants.Feedbacks + "?$filter=" +Constants.FromCPGUID + " eq '" + mStrBundleCPGUID+"' and "+Constants.ParentID + " eq '" + temParentID+ "' &$orderby= FeedbackNo desc",mContext));
                    }

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (feedBackListView != null) {
                            feedBackListView.hideProgress();
                            feedBackListView.displayList(alFeedBackBean);
                            feedBackListView.displayLSTSyncTime(SyncUtils.getCollectionSyncTime(mContext, Constants.Feedbacks));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        feedBackListView = null;
    }

    @Override
    public void onItemClick(FeedbackBean itemBean) {
        Intent toInvoiceHisdetails = new Intent(mContext, FeedBackDetailsActivity.class);
        toInvoiceHisdetails.putExtra(Constants.CPNo, mStrBundleRetID);
        toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
        toInvoiceHisdetails.putExtra(Constants.FeedbackNo, itemBean.getFeedbackNo());
        toInvoiceHisdetails.putExtra(Constants.FeedBackGuid, itemBean.getFeebackGUID());

        toInvoiceHisdetails.putExtra(Constants.FeedbackDesc, itemBean.getFeedbackTypeDesc());
        toInvoiceHisdetails.putExtra(Constants.BTSID, itemBean.getBTSID());
        toInvoiceHisdetails.putExtra(Constants.Location, itemBean.getLocation1());
        toInvoiceHisdetails.putExtra(Constants.Remarks, itemBean.getRemarks());

        toInvoiceHisdetails.putExtra(Constants.DeviceStatus, itemBean.getDeviceStatus());
        toInvoiceHisdetails.putExtra(Constants.DeviceNo, itemBean.getDeviceNo());
        mContext.startActivity(toInvoiceHisdetails);
    }

    @Override
    public void onRefresh() {
        isErrorFromBackend = false;
        if (feedBackListView != null) {
            feedBackListView.showProgress();
        }
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(mContext)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getFeedBack());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (feedBackListView != null) {
                    feedBackListView.hideProgress();
                    feedBackListView.showMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
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
            if (feedBackListView != null) {
                feedBackListView.hideProgress();
                feedBackListView.showMessage(mContext.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onUploadData() {
        isErrorFromBackend = false;
        Constants.mBoolIsReqResAval = true;
        Constants.mBoolIsNetWorkNotAval = false;
        try {
            if (!alFeedBackBean.isEmpty()) {

                if (UtilConstants.isNetworkAvailable(mContext)) {
                    alAssignColl.clear();
                    alAssignColl.addAll(SyncUtils.getFeedBack());
                    alAssignColl.add(Constants.ConfigTypsetTypeValues);
                    pendingROVal = 0;
                    if (tempRODevList != null) {
                        tempRODevList = null;
                        penROReqCount = 0;
                    }

                    if (alFeedBackBean != null && alFeedBackBean.size() > 0) {
                        tempRODevList = new String[alFeedBackBean.size()];

                        for (FeedbackBean feedbackBean : alFeedBackBean) {
                            tempRODevList[pendingROVal] = feedbackBean.getDeviceNo();
                            pendingROVal++;
                        }
                        if (Constants.iSAutoSync) {
                            if (feedBackListView != null) {
                                feedBackListView.hideProgress();
                                feedBackListView.showMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
                            }
                        }else {
                            if (feedBackListView != null) {
                                feedBackListView.showProgress();
                            }
                            Constants.isSync = true;
                            refguid = GUID.newRandom();
                            SyncUtils.updatingSyncStartTime(mContext,syncType,Constants.StartSync,refguid.toString().toUpperCase());
                            new SyncFromDataValtAsyncTask(mContext, tempRODevList, this, this).execute();
                        }
                    }
                } else {
                    if (feedBackListView != null) {
                        feedBackListView.hideProgress();
                        feedBackListView.showMessage(mContext.getString(R.string.no_network_conn));
                    }
                }
            } else {
                if (feedBackListView != null) {
                    feedBackListView.hideProgress();
                    feedBackListView.showMessage(mContext.getString(R.string.no_req_to_update_merchant));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (feedBackListView != null) {
                feedBackListView.hideProgress();
            }
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        onError(i, e);
    }

    @Override
    public void onRequestSuccess(int i, String s) {
        onSuccess(i, s);
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (feedBackListView != null) {
                feedBackListView.hideProgress();
                feedBackListView.showMessage(errorMsg);
            }
        }
    }

    public void onError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, mContext);
        isErrorFromBackend = true;
        if (errorBean.hasNoError()) {
            penROReqCount++;
            Constants.mBoolIsReqResAval = true;
            if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                String concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
            }

            if (operation == Operation.OfflineFlush.getValue()) {
                new RefreshAsyncTask(mContext, Constants.Visits, this).execute();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                if (feedBackListView != null) {
                    feedBackListView.hideProgress();
                    feedBackListView.onRefreshView();
                    feedBackListView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync));
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (feedBackListView != null) {
                    feedBackListView.hideProgress();
                    feedBackListView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync));
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (feedBackListView != null) {
                    feedBackListView.showProgress();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (feedBackListView != null) {
                    feedBackListView.showProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        } else {
            Constants.mBoolIsReqResAval = true;
            Constants.isSync = false;
            if (feedBackListView != null) {
                feedBackListView.showProgress();
                feedBackListView.onRefreshView();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }
    }

    public void onSuccess(int operation, String s) {
       /* if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 2) {
            Constants.updateLastSyncTimeToTable(mContext, pendingCollectionList);
            Constants.isSync = false;
            if (feedBackListView != null) {
                feedBackListView.hideProgress();
                feedBackListView.showMessage(mContext.getString(R.string.msg_sync_successfully_completed));
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mActivity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }*/
//        if (isFromWhere == 4) {
        if (operation == Operation.Create.getValue() && pendingROVal > 0) {
            Constants.mBoolIsReqResAval = true;
           /* Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
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
            Constants.removeDataValtFromSharedPref(mContext, Constants.Feedbacks, tempRODevList[penROReqCount], false);

            try {
                ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount],"",mContext);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            penROReqCount++;
        }

        String concatCollectionStr = "";
        if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
        } else if (operation == Operation.OfflineFlush.getValue()) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.isSync = false;
            Constants.updateLastSyncTimeToTable(mContext, alAssignColl,syncType,refguid.toString().toUpperCase());
            FeedbackListActivity.mBoolRefreshDone = true;
            ConstantsUtils.startAutoSync(mContext, false);
            if (feedBackListView != null) {
                feedBackListView.hideProgress();
                if (comingFrom.equalsIgnoreCase(Constants.Device)) {
                    if (!isErrorFromBackend) {
                        feedBackListView.showMessage(mContext.getString(R.string.msg_sync_successfully_completed));
                    } else {
                        feedBackListView.showMessage(mContext.getString(R.string.msg_error_occured_during_sync));
                    }
                }
                feedBackListView.onRefreshView();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mActivity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(mContext, Constants.Sync_All);
            ConstantsUtils.startAutoSync(mContext, false);
            if (feedBackListView != null) {
                feedBackListView.onRefreshView();
                feedBackListView.hideProgress();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mActivity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }
}
