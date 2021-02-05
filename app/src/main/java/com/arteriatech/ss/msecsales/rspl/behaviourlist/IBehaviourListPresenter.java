package com.arteriatech.ss.msecsales.rspl.behaviourlist;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface IBehaviourListPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh(String statusID);
   // void startFilter(int requestCode, int resultCode, Intent data);
    void loadAsyncTask(String status);
    void onDestroy();
}
