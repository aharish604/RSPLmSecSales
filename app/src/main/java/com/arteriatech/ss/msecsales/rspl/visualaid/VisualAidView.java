package com.arteriatech.ss.msecsales.rspl.visualaid;

import com.arteriatech.ss.msecsales.rspl.mbo.DocumentsBean;

import java.util.ArrayList;

public interface VisualAidView {
    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void displayList(ArrayList<DocumentsBean> alDocBean);

    void onRefreshView();

    void displayLSTSyncTime(String time);
}
