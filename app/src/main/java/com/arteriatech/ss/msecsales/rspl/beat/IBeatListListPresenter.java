package com.arteriatech.ss.msecsales.rspl.beat;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;

/**
 * Created by e10847 on 07-12-2017.
 */

public interface IBeatListListPresenter {
    /**
     * This will connect to offline Manager using AsyncTask and return the Result From OfflineManger to View.
     * @param
     */
    void connectToOfflineDB();
    void onDestroy();
    void onResume();
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void getRefreshTime();
    void loadBeatList(RetailerBean retailerBean);
    void getOtherBeatList();
}
