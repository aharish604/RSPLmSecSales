package com.arteriatech.ss.msecsales.rspl.competitors.list;

import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;

import java.util.ArrayList;

public interface CompetitorListView {
    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void displayList(ArrayList<CompetitorBean> displayList);

    void onRefreshView();

    void displayLSTSyncTime(String time);
}
