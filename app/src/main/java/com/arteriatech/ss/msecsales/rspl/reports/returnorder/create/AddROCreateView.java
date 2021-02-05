package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;

import java.util.ArrayList;

public interface AddROCreateView {

    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);

    void conformationDialog(String message, int from);

    void showMessage(String message, boolean isSimpleDialog);

    void searchResult(ArrayList<ReturnOrderBean> skuSearchList);

    void displayList(ArrayList<ReturnOrderBean> displayList);

    void showProgressDialog();

    void setFilterDate(String filterType);
}
