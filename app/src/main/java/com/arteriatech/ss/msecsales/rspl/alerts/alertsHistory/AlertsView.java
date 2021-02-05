package com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory;

import com.arteriatech.ss.msecsales.rspl.mbo.BirthdaysBean;

import java.util.List;

/**
 * Created by e10860 on 3/29/2018.
 */

public interface AlertsView {

    void onShowProgress(String msg);
    void onHideProgress();

    void showMessage(String errorMsg);

     void showData(List<BirthdaysBean> historyBeanList);
}
