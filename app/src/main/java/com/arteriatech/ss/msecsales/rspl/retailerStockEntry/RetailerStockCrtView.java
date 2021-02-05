package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;


import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;


public interface RetailerStockCrtView {
    void displayList(ArrayList<RetailerStockBean> retailerStockBeanArrayListList, String searchHint);
    void displaySearchList(ArrayList<RetailerStockBean> retailerStockBeanArrayListList);
    void showProgressDialog(String message);
    void hideProgressDialog();
    void displayMessage(String message);

    void displayTotalSelectedMat(int finalSelectedCount);
    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);
    void setFilterDate(String filterType);
    void onCreateUpdateSuccess();
}
