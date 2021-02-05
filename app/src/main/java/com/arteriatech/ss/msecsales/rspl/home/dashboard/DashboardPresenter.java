package com.arteriatech.ss.msecsales.rspl.home.dashboard;

import android.content.Intent;

/**
 * Created by e10769 on 13-Apr-18.
 */

public interface DashboardPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void onStart();
}
