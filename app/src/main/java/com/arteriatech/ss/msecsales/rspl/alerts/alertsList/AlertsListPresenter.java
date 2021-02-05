package com.arteriatech.ss.msecsales.rspl.alerts.alertsList;


import com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory.AlertsHistoryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.BirthdaysBean;

import java.util.List;

/**
 * Created by e10860 on 3/30/2018.
 */

public interface AlertsListPresenter {

    void loadAlertsList();
    void callAlertsRequest(int from);

    void callAlertBatch(BirthdaysBean alertsHistoryBean);
    void callAlertBatch(List<BirthdaysBean> alAlertsHistoryBean);
}
