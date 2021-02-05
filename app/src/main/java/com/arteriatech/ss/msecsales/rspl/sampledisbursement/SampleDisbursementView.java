package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import android.content.Context;

import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyPerformanceBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;

public interface SampleDisbursementView {
    void showProgress();
    void hideProgress();
    void displayMsg(String msg);
    void displayList(ArrayList<RetailerStockBean>list);
    void displayDMSDivision(ArrayList<DMSDivisionBean> alDMSDiv);
}
