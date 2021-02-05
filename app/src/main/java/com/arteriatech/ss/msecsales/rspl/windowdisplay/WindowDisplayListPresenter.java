package com.arteriatech.ss.msecsales.rspl.windowdisplay;

import com.arteriatech.ss.msecsales.rspl.mbo.SchemeBean;

public interface WindowDisplayListPresenter {

    void getWindowDispType();
    void getDataFromOfflineDB();
    void validateWindowDisplay(String schemeGUid, SchemeBean schemeBean);
    void refreshList();


}
