package com.arteriatech.ss.msecsales.rspl.retailertrends;

import com.arteriatech.ss.msecsales.rspl.mbo.MyPerformanceBean;

import java.util.ArrayList;

public interface RetailerTrendView {
    void showProgress();

    void hideProgress();

    void displayMsg(String msg);

    void displayList(ArrayList<MyPerformanceBean> alRetTrends);

    void displayLstSyncTime(String lastSeenDateFormat);
}
