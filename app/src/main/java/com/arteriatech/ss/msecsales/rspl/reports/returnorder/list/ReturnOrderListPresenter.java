package com.arteriatech.ss.msecsales.rspl.reports.returnorder.list;

import android.content.Intent;

/**
 * Created by e10847 on 07-12-2017.
 */

public interface ReturnOrderListPresenter {
    /**
     * This will connect to offline Manager using AsyncTask and return the Result From OfflineManger to View.
     * @param
     */
    void connectToOfflineDB();
    void onStart();
    void onDestroy();
    void onResume();
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void getRefreshTime();

    void getDetails(String no);


    void onSync();

    void connectToDataValt();

    void getPendingDetails(String ssroguid);
}
