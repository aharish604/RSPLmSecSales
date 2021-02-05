package com.arteriatech.ss.msecsales.rspl.visit.summary;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by e10769 on 12-May-18.
 */

public interface SummaryView {
    void showProgress();
    void hideProgress();
    void showMessage(String msg);
    void displayValue(ArrayList<BarEntry> barEntryArrayList, ArrayList<String> labels, String cmTarget,String crtMth,String mStrCurrency);
    void displayOpenOrder(String openOrder,String mStrVisitDate,String mStrOrderNo,String mStrOrderDate,String mAvgInvValue,String mStrCurrency);

}
