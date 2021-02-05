package com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.BirthdaysBean;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 3/29/2018.
 *
 */

public class AlertsPresenterImple implements AlertsPresenter, OnlineODataInterface {

    private Context context;
    private AlertsView alertsView;
    private boolean isSessionRequired;
    private boolean readFromTecCache = false;
    ArrayList<BirthdaysBean> alertsOrderBeanList = new ArrayList<>();


    public AlertsPresenterImple(Context context, boolean isSessionRequired, AlertsView alertsView) {
        this.context = context;
        this.alertsView = alertsView;
    }

    @Override
    public void alertsCallHistory() {
        alertsView.onShowProgress(context.getString(R.string.app_loading));
        alertsOrderBeanList.clear();
        alertsOrderBeanList.addAll(Constants.getAlertsValuesFromDataVault(context));

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (alertsView != null) {
                    alertsView.onHideProgress();
                    alertsView.showData(alertsOrderBeanList);
                }
            }
        });

//        String qry = Constants.AlertHistory + "?AlertGUID=%27%27&Application=%27PD%27&PartnerID=%27%27&LoginID=%27%27&PartnerType=%27%27&AlertType=%27%27&ObjectType=%27%27";
//        ConstantsUtils.onlineRequest(context, qry, isSessionRequired, 1, ConstantsUtils.SESSION_QRY, this, true, false);
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
//                final List<AlertsHistoryBean> historyBeanList = OnlineManager.getAlertsHistory(list);/*
               /* final List<AlertsHistoryBean> historyBeanList=null;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (alertsView != null) {
                            alertsView.onHideProgress();
                            alertsView.showData(historyBeanList);
                        }
                    }
                });*/
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
                if (alertsView != null) {
                    alertsView.onHideProgress();
                    alertsView.showMessage(errorMsg);
                }
            }
        });
    }
}
