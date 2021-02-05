package com.arteriatech.ss.msecsales.rspl.targets;

import android.content.Intent;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface TargetPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void onStart();
}
