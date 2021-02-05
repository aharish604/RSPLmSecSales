package com.arteriatech.ss.msecsales.rspl.alerts.alertsList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory.AlertsHistoryBean;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.BirthdaysBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.google.gson.Gson;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10860 on 3/30/2018.
 */

public class AlertsListPresenterImpl implements AlertsListPresenter, OnlineODataInterface {

    String[][] oneWeekDay = null;
    String splitDayMonth[] = null;
    ArrayList<BirthdaysBean> alRetBirthDayList = new ArrayList<>();
    ArrayList<BirthdaysBean> alAppointmentList = null;
    ArrayList<BirthdaysBean> alertsOrderBeanList = new ArrayList<>();
    ArrayList<BirthdaysBean> alertsHistBeanList = new ArrayList<>();
    ArrayList<BirthdaysBean> alDataValutBirthDayList = null;
    ArrayList<BirthdaysBean> alDataValutList = null;
    private Context context;
    private AlertsListView alertsListView;
    private boolean isSessionRequired;
    private List<AlertsHistoryBean> historyBeanList = new ArrayList<>();
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();

    AlertsListPresenterImpl(Context context, AlertsListView alertsListView, boolean isSessionRequired) {
        this.context = context;
        this.alertsListView = alertsListView;
        this.isSessionRequired = isSessionRequired;
        oneWeekDay = UtilConstants.getOneweekValues(1);
        alDataValutList = new ArrayList<>();
    }

    @Override
    public void loadAlertsList() {
//        if (UtilConstants.isNetworkAvailable(context)) {
        if (alertsListView != null) {
            alertsListView.onShowProgress(context.getString(R.string.app_loading));
        }
        callAlertsRequest(1);
//        } else {
//            if (alertsListView != null) {
//                alertsListView.showMessage(context.getString(R.string.no_network_conn));
//            }
//        }
    }

    @Override
    public void callAlertsRequest(int from) {
//        String qry =  Constants.Alerts+"?$filter=Application%20eq%20%27PD%27";

        Constants.updateBirthdayAlertsStatus(Constants.BirthDayAlertsTempKey,context);
        Constants.updateBirthdayAlertsStatus(Constants.AlertsTempKey,context);
        getTodayBirthDayList();
        alertsOrderBeanList.clear();
        OfflineManager.getAlertsFromLocalDB(alertsOrderBeanList);
        alertsHistBeanList = Constants.getAlertsValuesFromDataVault(context);
        onDataVaultValidation();
        setValuesToUI();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (alertsListView != null) {
                    alertsListView.onHideProgress();
                    alertsListView.showData(alRetBirthDayList);
                }
            }
        });

//        String qry =  Constants.Alerts;
//        ConstantsUtils.onlineRequest(context, qry, isSessionRequired, from, ConstantsUtils.SESSION_QRY, this, false, false);
    }

    @Override
    public void callAlertBatch(BirthdaysBean alertsHistoryBean) {
      /*  ArrayList<Hashtable> alTable =new ArrayList<>();
        masterHeaderTable = new Hashtable<>();
        masterHeaderTable.put(Constants.AlertGUID, alertsHistoryBean.getAlertGUID());
        masterHeaderTable.put(Constants.Application, alertsHistoryBean.getApplication());
        masterHeaderTable.put(Constants.PartnerType, alertsHistoryBean.getPartnerType());
        masterHeaderTable.put(Constants.PartnerID, alertsHistoryBean.getPartnerID());
        masterHeaderTable.put(Constants.AlertTypeDesc, alertsHistoryBean.getAlertTypeDesc());
        masterHeaderTable.put(Constants.ObjectType, alertsHistoryBean.getObjectType());
        masterHeaderTable.put(Constants.ObjectID, alertsHistoryBean.getObjectID());
        masterHeaderTable.put(Constants.ObjectSubID, alertsHistoryBean.getObjectSubID());
        masterHeaderTable.put(Constants.CreatedBy, alertsHistoryBean.getCreatedBy());
        masterHeaderTable.put(Constants.CreatedOn, alertsHistoryBean.getCreatedOn());
        masterHeaderTable.put(Constants.CreatedAt, alertsHistoryBean.getCreatedAt());
        masterHeaderTable.put(Constants.ConfirmedBy, alertsHistoryBean.getConfirmedBy());
//        masterHeaderTable.put(Constants.ConfirmedOn, alertsHistoryBean.getConfirmedOn());
        masterHeaderTable.put(Constants.ConfirmedOn, UtilConstants.getNewDateTimeFormat());
        masterHeaderTable.put(Constants.AlertText, alertsHistoryBean.getAlertText());

        alTable.add(masterHeaderTable);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 3);*/
        alertsListView.onShowProgress("Confirming alert...");
//        new SendAlertDetailRequestAsyn(context, this, alTable, bundle).execute();
    }

    @Override
    public void callAlertBatch(List<BirthdaysBean> alAlertsHistoryBean) {
        ArrayList<Hashtable> alTable = new ArrayList<>();
        if (alAlertsHistoryBean != null && alAlertsHistoryBean.size() > 0) {
            for (BirthdaysBean alertsHistoryBean : alAlertsHistoryBean) {
                if (alertsHistoryBean.isSelected()) {
                    masterHeaderTable = new Hashtable<>();
                    masterHeaderTable.put(Constants.AlertGUID, alertsHistoryBean.getAlertGUID());
                    masterHeaderTable.put(Constants.Application, alertsHistoryBean.getApplication());
                    masterHeaderTable.put(Constants.PartnerType, alertsHistoryBean.getPartnerType());
                    masterHeaderTable.put(Constants.PartnerID, alertsHistoryBean.getPartnerID());
                    masterHeaderTable.put(Constants.AlertTypeDesc, alertsHistoryBean.getAlertTypeDesc());
                    masterHeaderTable.put(Constants.ObjectType, alertsHistoryBean.getObjectType());
                    masterHeaderTable.put(Constants.ObjectID, alertsHistoryBean.getObjectID());
                    masterHeaderTable.put(Constants.ObjectSubID, alertsHistoryBean.getObjectSubID());
                    masterHeaderTable.put(Constants.CreatedBy, alertsHistoryBean.getCreatedBy());
                    masterHeaderTable.put(Constants.CreatedOn, alertsHistoryBean.getCreatedOn());
                    masterHeaderTable.put(Constants.CreatedAt, alertsHistoryBean.getCreatedAt());
                    masterHeaderTable.put(Constants.ConfirmedBy, alertsHistoryBean.getConfirmedBy());
                    masterHeaderTable.put(Constants.ConfirmedOn, UtilConstants.getNewDateTimeFormat());
                    masterHeaderTable.put(Constants.AlertText, alertsHistoryBean.getAlertText());
                    alTable.add(masterHeaderTable);
                }
            }

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 3);
            alertsListView.onShowProgress("Confirming alert...");
//            new SendAlertDetailRequestAsyn(context, this, alTable, bundle).execute();
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
               /* historyBeanList.clear();
//                historyBeanList = OnlineManager.getAlertsHistory(list);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (alertsListView != null) {
                            alertsListView.onHideProgress();
                            alertsListView.showData(historyBeanList);
                        }
                    }
                });*/
                break;
            case 2:
               /* historyBeanList.clear();
//                historyBeanList = OnlineManager.getAlertsHistory(list);
//                ConstantsUtils.setAlertCountToPreference(list.size(), context);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (alertsListView != null) {
                            alertsListView.onHideProgress();
                            alertsListView.showData(historyBeanList);
                        }
                    }
                });*/
                break;
            case 3:
                callAlertsRequest(2);
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        showErrorResponse(s);
    }

    private void showErrorResponse(final String errorMsg) {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (alertsListView != null) {
                    alertsListView.onHideProgress();
                    alertsListView.showMessage(errorMsg);
                }
            }
        });
    }

    private void getTodayBirthDayList() {
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {

                splitDayMonth = oneWeekDay[0][i].split("-");

                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList.clear();
                            alRetBirthDayList.addAll(OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry));
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                String mStrAppointmentListQuery = Constants.Visits + "?$filter=" + Constants.StatusID + " eq '00' and "+Constants.CPTypeID+" eq '"+Constants.str_02+"' and (month%28" + Constants.PlannedDate + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.PlannedDate + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ")";
                try {
                    alAppointmentList = OfflineManager.getAppointmentListForAlert(mStrAppointmentListQuery);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if (alAppointmentList != null && alAppointmentList.size() > 0) {
                    for (int j = 0; j < alAppointmentList.size(); j++) {
                        alRetBirthDayList.add(alAppointmentList.get(j));
                    }
                }
            }
        }
    }

    private void onDataVaultValidation() {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

        if (mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {
            // ToDO check birthday records available  in data vault
            String mStrDataAval = null;
            try {
                mStrDataAval = ConstantsUtils.getFromDataVault(Constants.BirthDayAlertsKey,context);
            } catch (Throwable e) {
                e.printStackTrace();
                mStrDataAval = "";
            }
            if (mStrDataAval != null && !mStrDataAval.equalsIgnoreCase("")) {
                // ToDO data vault data convert into json object
                try {
                    JSONObject fetchJsonHeaderObject = new JSONObject(mStrDataAval);
                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                    alDataValutBirthDayList = new ArrayList<>();
                    alDataValutBirthDayList = Constants.convertToBirthDayArryList(itemsString);
                    alRetBirthDayList.clear();
                    alDataValutList = alDataValutBirthDayList;
                    if (alDataValutBirthDayList != null && alDataValutBirthDayList.size() > 0) {
                        for (int k = 0; k < alDataValutBirthDayList.size(); k++) {

                            if (alDataValutBirthDayList.get(k).getAlertGUID().equalsIgnoreCase("")) {
                                if (!alDataValutBirthDayList.get(k).getAppointmentAlert()) {
                                    if ((alDataValutBirthDayList.get(k).getDOBStatus().equalsIgnoreCase("")
                                            && alDataValutBirthDayList.get(k).getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0]))
                                            || (alDataValutBirthDayList.get(k).getAnniversaryStatus().equalsIgnoreCase("")
                                            && alDataValutBirthDayList.get(k).getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0]))) {
                                        alRetBirthDayList.add(alDataValutBirthDayList.get(k));
                                    }
                                } else {

                                    if (alDataValutBirthDayList.get(k).getAppointmentStatus().equalsIgnoreCase("")) {
                                        alRetBirthDayList.add(alDataValutBirthDayList.get(k));
                                    }
                                }
                            }


                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO add values into data vault
                assignValuesIntoDataVault();
            }
        } else {
            // ToDO delete old date birthday records from data vault
            try {
                //noinspection deprecation
                ConstantsUtils.storeInDataVault(Constants.BirthDayAlertsKey,"",context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            setCurrentDateTOSharedPerf();
            // TODO add values into data vault
            assignValuesIntoDataVault();
        }

    }

    private void assignValuesIntoDataVault() {

        Gson gson = new Gson();
        Hashtable dbHeaderTable = new Hashtable();
        try {
            String jsonFromMap = gson.toJson(alRetBirthDayList);
            alDataValutBirthDayList = new ArrayList<>();
            alDataValutBirthDayList = alRetBirthDayList;
            alDataValutList = alRetBirthDayList;
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(Constants.BirthDayAlertsKey,jsonHeaderObject.toString(),context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setCurrentDateTOSharedPerf() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.BirthDayAlertsDate, UtilConstants.getDate1());
        editor.commit();

    }


    public void updateDataVaultRecord(int position) {
        if (alDataValutBirthDayList.size() > 1) {
            for (int l = 0; l < alDataValutBirthDayList.size(); l++) {
                BirthdaysBean birthdaysBean = alDataValutBirthDayList.get(l);

                if (alRetBirthDayList.get(position).getAlertGUID().equalsIgnoreCase("")) {
                    if (alRetBirthDayList.get(position).getCPUID().equalsIgnoreCase(birthdaysBean.getCPUID())
                            && !alRetBirthDayList.get(position).getAppointmentAlert()) {
                        if (birthdaysBean.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                            birthdaysBean.setDOBStatus(Constants.X);
                        } else {
                            birthdaysBean.setDOBStatus("");
                        }
                        if (birthdaysBean.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                            birthdaysBean.setAnniversaryStatus(Constants.X);
                        } else {
                            birthdaysBean.setAnniversaryStatus("");
                        }
                        birthdaysBean.setStatus(Constants.Y); // alert is deleted status
                        deletedAlertsValuesUpdateToDataVault(birthdaysBean);
                        alDataValutBirthDayList.set(l, birthdaysBean);
                        alDataValutList = alDataValutBirthDayList;
                        setIntoDataVault();
                        break;
                    } else {
                        if (alRetBirthDayList.get(position).getCPUID().equalsIgnoreCase(birthdaysBean.getCPUID())
                                && alRetBirthDayList.get(position).getAppointmentAlert()) {
                            birthdaysBean.setAppointmentStatus(Constants.X);
                            birthdaysBean.setStatus(Constants.Y); // alert is deleted status
                            deletedAlertsValuesUpdateToDataVault(birthdaysBean);
                            alDataValutBirthDayList.set(l, birthdaysBean);
                            alDataValutList = alDataValutBirthDayList;
                            setIntoDataVault();
                            break;
                        }
                    }
                } else {
                    if (alRetBirthDayList.get(position).getAlertGUID().equalsIgnoreCase(birthdaysBean.getAlertGUID())) {
                        birthdaysBean.setAlertStatus(Constants.X);
                        birthdaysBean.setStatus(Constants.Y); // alert is deleted status
                        alDataValutBirthDayList.set(l, birthdaysBean);
                        alDataValutList = alDataValutBirthDayList;
                        deletedAlertsValuesUpdateToDataVault(birthdaysBean);
                        updateAlertsRecord(birthdaysBean);
                        break;
                    }
                }

            }
        } else {


            BirthdaysBean birthdaysBean = alDataValutBirthDayList.get(position);
            if (birthdaysBean.getAlertGUID().equalsIgnoreCase("")) {
                if (birthdaysBean.getAppointmentAlert()) {
                    birthdaysBean.setAppointmentStatus(Constants.X);
                } else {
                    if (birthdaysBean.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                        birthdaysBean.setDOBStatus(Constants.X);
                    } else {
                        birthdaysBean.setDOBStatus("");
                    }
                    if (birthdaysBean.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                        birthdaysBean.setAnniversaryStatus(Constants.X);
                    } else {
                        birthdaysBean.setAnniversaryStatus("");
                    }
                }
                birthdaysBean.setStatus(Constants.Y); // alert is deleted status
                alDataValutBirthDayList.set(position, birthdaysBean);
                alDataValutList = alDataValutBirthDayList;
                deletedAlertsValuesUpdateToDataVault(birthdaysBean);
                setIntoDataVault();

            } else {
                birthdaysBean.setStatus(Constants.Y); // alert is deleted status
                birthdaysBean.setAlertStatus(Constants.X);
                updateAlertsRecord(birthdaysBean);
                deletedAlertsValuesUpdateToDataVault(birthdaysBean);

                alDataValutBirthDayList.set(position, birthdaysBean);
                alDataValutList = alDataValutBirthDayList;
            }

        }
        callAlertsRequest(2);

    }

    private void deletedAlertsValuesUpdateToDataVault(BirthdaysBean birthdaysBean) {
        if (alertsHistBeanList != null) {
            alertsHistBeanList.add(birthdaysBean);
            Constants.setAlertsValToDataVault(alertsHistBeanList, Constants.AlertsDataKey,context);
        }
    }

    private void setIntoDataVault() {
        Hashtable dbHeaderTable = new Hashtable();
        Gson gson = new Gson();

        try {
            String jsonFromMap = gson.toJson(alDataValutList);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(Constants.BirthDayAlertsKey,jsonHeaderObject.toString(),context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void updateAlertsRecord(BirthdaysBean birthdaysBean) {
        try {
            OfflineManager.updateAlert(birthdaysBean,context);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void setValuesToUI() {
        if (alertsOrderBeanList != null && alertsOrderBeanList.size() > 0) {

            alRetBirthDayList.addAll(alRetBirthDayList.size(), alertsOrderBeanList);
            alDataValutBirthDayList = alRetBirthDayList;
        }
    }
}
