package com.arteriatech.ss.msecsales.rspl.merchandising.list;

import com.arteriatech.ss.msecsales.rspl.mbo.MerchandisingBean;

import java.util.ArrayList;

public interface MerchandisingListView {
    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void displayList(ArrayList<MerchandisingBean> alMercBean);

    void onRefreshView();

    void displayLSTSyncTime(String time);
}
