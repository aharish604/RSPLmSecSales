package com.arteriatech.ss.msecsales.rspl.feedback.list;

import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;

import java.util.ArrayList;

public interface FeedBackListView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void displayList(ArrayList<FeedbackBean> displayList);

    void onRefreshView();

    void displayLSTSyncTime(String time);
}
