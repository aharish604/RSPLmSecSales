package com.arteriatech.ss.msecsales.rspl.windowdisplay;

import com.arteriatech.ss.msecsales.rspl.mbo.SchemeBean;

import java.util.ArrayList;

public interface WindowDisplayListView {
    void displayList(ArrayList<SchemeBean> schemeModelArrayList);
    void showProgress();
    void hideProgress();
    void displayMsg(String msg);
    void navToWindowDisplay(boolean status,String values);
}
