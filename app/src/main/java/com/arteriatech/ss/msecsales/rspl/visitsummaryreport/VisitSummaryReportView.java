package com.arteriatech.ss.msecsales.rspl.visitsummaryreport;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/28/2017.
 */

public interface VisitSummaryReportView {

    void showMessage(String message);

    void dialogMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();

    void displayRefreshTime(String refreshTime);

    void displayList(ArrayList<VisitSummaryReportBean> list);
}
