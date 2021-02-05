package com.arteriatech.ss.msecsales.rspl.notvistedretailers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.FlushDataAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RemarkReasonBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10526 on 21-04-2018.
 */

public class NotVisitedRetailerPresenterImpl implements NotVisitedRetailersPresenter, UIListener {
    String mStrRetName = "";
    private Context mContext;
    private String closingDate = "", mStrPopUpText = "";
    private Activity mActivity;
    private NotVisitedRetailersView notVisitedRetailersView;
    private boolean isSessionRequired;
    private String[][] alRetailers = null;
    private String[][] alRetailersTemp = null;
    private ArrayList<RemarkReasonBean> alReason = new ArrayList<>();


    public NotVisitedRetailerPresenterImpl(Context mContext, NotVisitedRetailersView notVisitedRetailersView,
                                           boolean isSessionRequired, Activity mActivity, String closingDate) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.notVisitedRetailersView = notVisitedRetailersView;
        this.isSessionRequired = isSessionRequired;
        this.closingDate = closingDate;
    }

    @Override
    public void onStart() {
        requestRetailerData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onSaveData() {
//        getLocation();
    }

    private void requestRetailerData() {
        if (notVisitedRetailersView != null) {
            notVisitedRetailersView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    alRetailers = Constants.getDealer(closingDate);
                    alReason = Constants.getReasonValues(alReason);
//                    getReasonValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (notVisitedRetailersView != null) {
                            notVisitedRetailersView.hideProgressDialog();
                            notVisitedRetailersView.displayByDealers(alRetailers, alReason);
                        }
                    }
                });
            }
        }).start();


    }

    public void checkRemarksValidation(final int currentRetailerId, final String selectedReasonCode, final String selectedReasonDesc, final EditText etRemarks) {
        if (notVisitedRetailersView != null) {
            notVisitedRetailersView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        if (!selectedReasonCode.equalsIgnoreCase("00") && !selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {

            String mStrRemarks = "";
            if (etRemarks.getText() != null) {
                try {
                    mStrRemarks = etRemarks.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    mStrRemarks = "";
                }
                alRetailers[currentRetailerId][8] = mStrRemarks;
                if (mStrRemarks.equalsIgnoreCase("")) {
                    mStrRemarks = selectedReasonDesc;
                } else {
                    mStrRemarks = selectedReasonDesc + " " + mStrRemarks;
                }
            } else {
                alRetailers[currentRetailerId][8] = "";
                mStrRemarks = selectedReasonDesc;
            }

            alRetailers[currentRetailerId][5] = mStrRemarks;
            alRetailers[currentRetailerId][7] = selectedReasonCode;
            if (currentRetailerId < alRetailers.length - 1) {

            } else {
                notVisitedRetailersView.menuVisible(false);
            }

//                    closingProgressDialog();
            if (currentRetailerId < alRetailers.length - 1) {

                notVisitedRetailersView.menuVisible(true);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (notVisitedRetailersView != null) {
                            notVisitedRetailersView.hideProgressDialog();
                            notVisitedRetailersView.displayByDealers(alRetailers, alReason);
                        }
                    }
                });
//                        setDealers();
            } else {
                notVisitedRetailersView.menuVisible(false);
                notVisitedRetailersView.hideProgressDialog();

            }
        } else if (selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {

            String mStrRemarks = "";
            if (etRemarks.getText() != null) {
                try {
                    mStrRemarks = etRemarks.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    mStrRemarks = "";
                }

            } else {
                mStrRemarks = "";
            }

            if (!mStrRemarks.equalsIgnoreCase("")) {

                alRetailers[currentRetailerId][8] = mStrRemarks;
                if (mStrRemarks.equalsIgnoreCase("")) {
                    mStrRemarks = selectedReasonDesc;
                } else {
                    mStrRemarks = selectedReasonDesc + " " + mStrRemarks;
                }

                alRetailers[currentRetailerId][5] = mStrRemarks;
                alRetailers[currentRetailerId][7] = selectedReasonCode;
                if (currentRetailerId < alRetailers.length - 1) {

                } else {
                    notVisitedRetailersView.menuVisible(false);
                }
//                        closingProgressDialog();
                if (currentRetailerId < alRetailers.length - 1) {
                    notVisitedRetailersView.menuVisible(true);
//                            setDealers();
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (notVisitedRetailersView != null) {
                                notVisitedRetailersView.hideProgressDialog();
                                notVisitedRetailersView.displayByDealers(alRetailers, alReason);
                            }
                        }
                    });
                } else {
                    notVisitedRetailersView.menuVisible(false);
                    notVisitedRetailersView.hideProgressDialog();

                }
            } else {
                alRetailers[currentRetailerId][7] = selectedReasonCode;
                alRetailers[currentRetailerId][5] = "";
                alRetailers[currentRetailerId][8] = "";
//                        closingProgressDialog();
                notVisitedRetailersView.errorRemarks("Enter Remarks");
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (notVisitedRetailersView != null) {
                            notVisitedRetailersView.hideProgressDialog();
//                                    notVisitedRetailersView.displayByDealers(alRetailers,alReason);
                        }
                    }
                });
            }


        } else {
//                    closingProgressDialog();
            if (selectedReasonCode.equalsIgnoreCase("00")) {
                notVisitedRetailersView.errorReason("Select Reason");
            }
//                    notVisitedRetailersView.errorRemarks("Enter Remarks");
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (notVisitedRetailersView != null) {
                        notVisitedRetailersView.hideProgressDialog();
                    }
                }
            });

        }
    }

    private void getReasonValues() {

        String query = Constants.ValueHelps + "?$filter= PropName eq '" + "Reason" + "' and EntityType eq 'Visit' &$orderby=" + Constants.ID + "";
        try {
            alReason = OfflineManager.getRemarksReason(query);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    public void checkValidation(final String[][] alRet) {
        mStrRetName = "";
        alRetailersTemp = alRet;
        if (notVisitedRetailersView != null) {
            notVisitedRetailersView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isReasonMand = false;
                for (int i = 0; i < alRet.length; i++) {
                    if (alRet[i][7].equals("")) {
                        mStrRetName = alRet[i][2];
                        isReasonMand = true;
                        break;
                    } else if (alRet[i][7].equalsIgnoreCase(Constants.str_06)) {
                        try {
                            if (alRet[i][8].equals("")) {
                                mStrRetName = alRet[i][2];
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                final boolean finalIsReasonMand = isReasonMand;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (notVisitedRetailersView != null) {
                            notVisitedRetailersView.hideProgressDialog();
                            if (!mStrRetName.equalsIgnoreCase("")) {
                                if (finalIsReasonMand) {
                                    notVisitedRetailersView.remarksNotEnterdSpecificRetailer(mContext.getString(R.string.msg_reason_par_retailer, mStrRetName));
                                } else {
                                    notVisitedRetailersView.remarksNotEnterdSpecificRetailer(mContext.getString(R.string.msg_remarks_par_retailer, mStrRetName));
                                }
                            } else {
                                new ClosingDate().execute();
                            }
                        }
                    }
                });
            }
        }).start();


    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, mContext);
        if (errorBean.hasNoError()) {

            if (operation == Operation.Create.getValue()) {
                closeProgressDialog();
                mStrPopUpText = mContext.getString(R.string.close_update_with_err);
                displayPopUpMsg();
            } else if (operation == Operation.Update.getValue()) {
                closeProgressDialog();
                try {
                    mStrPopUpText = mContext.getString(R.string.err_msg_concat, mContext.getString(R.string.lbl_attence_end), exception.getMessage());
                } catch (Exception e) {
                    mStrPopUpText = mContext.getString(R.string.msg_end_upd_sync_error);
                }
                displayPopUpMsg();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                closeProgressDialog();
                try {
                    mStrPopUpText = mContext.getString(R.string.err_msg_concat, mContext.getString(R.string.lbl_attence_end), exception.getMessage());
                } catch (Exception e) {
                    mStrPopUpText = mContext.getString(R.string.msg_end_upd_sync_error);
                }
                displayPopUpMsg();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                closeProgressDialog();
                try {
                    mStrPopUpText = mContext.getString(R.string.err_msg_concat, mContext.getString(R.string.lbl_attence_end), exception.getMessage());
                } catch (Exception e) {
                    mStrPopUpText = mContext.getString(R.string.msg_end_upd_sync_error);
                }
                displayPopUpMsg();
            } else {
                try {
                    mStrPopUpText = mContext.getString(R.string.err_msg_concat, mContext.getString(R.string.lbl_attence_end), exception.getMessage());
                } catch (Exception e) {
                    mStrPopUpText = mContext.getString(R.string.msg_end_upd_sync_error);
                }
                Constants.isSync = false;
                closeProgressDialog();
                displayPopUpMsg();
            }
        } else {
            Constants.isSync = false;

            if (errorBean.isStoreFailed()) {
                if (!OfflineManager.isOfflineStoreOpen()) {
                    mStrPopUpText = mContext.getString(R.string.app_loading);
                    try {
                        new ClosingDate().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    closeProgressDialog();
                    mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false);
                    displayPopUpMsg();
                }
            } else {
                closeProgressDialog();
                mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false);
                displayPopUpMsg();
            }
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            mStrPopUpText = mContext.getString(R.string.Day_ended);
            closeProgressDialog();
            displayPopUpMsg();
        } else if (operation == Operation.Update.getValue()) {
            if (!UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = false;
                closeProgressDialog();
                UtilConstants.onNoNetwork(mContext);
                mActivity.onBackPressed();
            } else {
                if (Constants.iSAutoSync) {
                    closeProgressDialog();
                    mStrPopUpText = mContext.getString(R.string.alert_auto_sync_is_progress);
                    displayPopUpMsg();
                } else {
                    Constants.isSync = true;
                    ArrayList<String> flushList = new ArrayList<>();
                    flushList.add(Constants.Attendances);
                    flushList.add(Constants.Visits);
                    new FlushDataAsyncTask(this,flushList).execute();
                }
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (!UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = false;
                closeProgressDialog();
                UtilConstants.showAlert(mContext.getString(R.string.data_conn_lost_during_sync), mContext);
                mActivity.onBackPressed();
            } else {
                String allCollection = "";
                allCollection = Constants.Attendances + "," + Constants.Visits;
                new RefreshAsyncTask(mContext, allCollection, this).execute();
            }

        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.isSync = false;
            closeProgressDialog();
            mStrPopUpText = mContext.getString(R.string.Day_ended);
            UtilConstants.dialogBoxWithCallBack(mContext, "", mStrPopUpText, mContext.getString(R.string.ok), "", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    Constants.isSync = false;
                    if (!AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mActivity, BuildConfig.APPLICATION_ID, true, Constants.APP_UPGRADE_TYPESET_VALUE)) {
                        mActivity.onBackPressed();
                    }
                }
            });
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(mContext, Constants.Sync_All);
            closeProgressDialog();

            UtilConstants.dialogBoxWithCallBack(mContext, "", mContext.getString(R.string.msg_sync_successfully_completed), mContext.getString(R.string.ok), "", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    Constants.isSync = false;
                    if (!AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mActivity, BuildConfig.APPLICATION_ID, true, Constants.APP_UPGRADE_TYPESET_VALUE)) {
                        mActivity.onBackPressed();
                    }
                }
            });
        }

    }

    private void closeProgressDialog() {
        if (notVisitedRetailersView != null) {
            notVisitedRetailersView.hideProgressDialog();
        }
    }

    private void displayPopUpMsg() {
        if (notVisitedRetailersView != null) {
            notVisitedRetailersView.displayMessageAndNavigateToPreviosScreen(mStrPopUpText);
        }
    }

    private void onSaveVisit() {
        for (int incVal = 0; incVal < alRetailersTemp.length; incVal++) {
            GUID guid = GUID.newRandom();

            Hashtable table = new Hashtable();
            //noinspection unchecked
            table.put(Constants.CPNo, UtilConstants.removeLeadingZeros(alRetailersTemp[incVal][0]));

            table.put(Constants.CPName, alRetailersTemp[incVal][2]);
            //noinspection unchecked
            table.put(Constants.STARTDATE, UtilConstants.getNewDateTimeFormat());
            final Calendar calCurrentTime = Calendar.getInstance();
            int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
            int minute = calCurrentTime.get(Calendar.MINUTE);
            int second = calCurrentTime.get(Calendar.SECOND);
            ODataDuration oDataDuration = null;
            try {
                oDataDuration = new ODataDurationDefaultImpl();
                oDataDuration.setHours(hourOfDay);
                oDataDuration.setMinutes(minute);
                oDataDuration.setSeconds(BigDecimal.valueOf(second));
            } catch (Exception e) {
                e.printStackTrace();
            }

            table.put(Constants.STARTTIME, oDataDuration);
            //noinspection unchecked
            try {
                table.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude,12)));
                //noinspection unchecked
                table.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude,12)));
                //noinspection unchecked
                table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude,12)));
                //noinspection unchecked
                table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude,12)));
            } catch (Exception e) {
                e.printStackTrace();
            }
//				//noinspection unchecked
            table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());


            //noinspection unchecked
            table.put(Constants.ENDTIME, oDataDuration);
            //noinspection unchecked
            table.put(Constants.VISITKEY, guid.toString());

            table.put(Constants.ROUTEPLANKEY, Constants.convertStrGUID32to36(alRetailersTemp[incVal][4].toUpperCase()));

            table.put(Constants.StatusID, Constants.str_02);

            table.put(Constants.REMARKS, alRetailersTemp[incVal][5].trim());

            try {
                table.put(Constants.REASON, alRetailersTemp[incVal][7]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            table.put(Constants.CPTypeID, Constants.str_02);
            table.put(Constants.VisitCatID, Constants.str_01);

            table.put(Constants.VisitDate, closingDate);

            try {
                table.put(Constants.CPGUID, alRetailersTemp[incVal][3]);
            } catch (Exception e) {
                table.put(Constants.CPGUID, "");
            }

//            String[][] mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(alRetailersTemp[incVal][6].toUpperCase(),mContext);

            try {
                table.put(Constants.SPGUID,Constants.getSPGUID());
            } catch (Exception e) {
                table.put(Constants.SPGUID, Constants.getSPGUID());
            }


            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);


            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            table.put(Constants.LOGINID, loginIdVal);

            table.put(Constants.VisitSeq, "");


            try {
                //noinspection unchecked
                if (!table.get(Constants.CPGUID).equals("")) {
                    OfflineManager.createVisitStartEnd(table,mContext);
                }
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

        }


    }

    /*Close day if remarks for all not visited retailer filled*/
    private void onSaveClose() {
        Constants.MapEntityVal.clear();

        final String qry = Constants.Attendances + "?$filter=EndDate eq null ";
        Thread thread = null;
        try {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OfflineManager.getAttendance(qry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        }
        Hashtable hashTableAttendanceValues;

        if (Constants.MapEntityVal.size() > 0) {


            hashTableAttendanceValues = new Hashtable();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
            try {
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndDate, UtilConstants.getNewDateTimeFormat());

            hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID());


            hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

            if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
            } else {
                hashTableAttendanceValues.put(Constants.Etag, "");
            }

            hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
            hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
            hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

            final Calendar calCurrentTime = Calendar.getInstance();
            int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
            int minute = calCurrentTime.get(Calendar.MINUTE);
            int second = calCurrentTime.get(Calendar.SECOND);
            ODataDuration oDataDuration = null;
            try {
                oDataDuration = new ODataDurationDefaultImpl();
                oDataDuration.setHours(hourOfDay);
                oDataDuration.setMinutes(minute);
                oDataDuration.setSeconds(BigDecimal.valueOf(second));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndTime, oDataDuration);


            try {
                //noinspection unchecked
                OfflineManager.updateAttendance(hashTableAttendanceValues, this,mContext);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }else {
            if (notVisitedRetailersView!=null) {
                notVisitedRetailersView.hideProgressDialog();
            }
        }
    }

    private class ClosingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (notVisitedRetailersView!=null) {
                notVisitedRetailersView.showProgressDialog(mContext.getString(R.string.msg_update_non_visted_retilers));
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(200);
                if (!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        OfflineManager.openOfflineStore(mContext, NotVisitedRetailerPresenterImpl.this);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                } else {
                    onSaveVisit();
                    onSaveClose();
                }
            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
