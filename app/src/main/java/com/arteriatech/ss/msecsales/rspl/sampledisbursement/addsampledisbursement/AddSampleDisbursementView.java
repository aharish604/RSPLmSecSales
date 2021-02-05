package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;

public interface AddSampleDisbursementView {

    void showProgress();
    void hideProgress();
    void displayMsg(String msg);
    void displayList(ArrayList<RetailerStockBean> list);
    void setFilterDate(String filterType);
}
