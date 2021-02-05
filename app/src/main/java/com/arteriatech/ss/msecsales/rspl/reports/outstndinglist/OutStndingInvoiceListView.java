package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arteriatech.ss.msecsales.rspl.mbo.OutstandingBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface OutStndingInvoiceListView {
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showMessage(String message);
    void dialogMessage(String message, String msgType);
    void showProgressDialog();
    void hideProgressDialog();
    void openFilter(String startDate, String endDate, String filterType, String status, String grStatus);
    void searchResult(ArrayList<OutstandingBean> feedbackBeanArrayList);
    void setFilterDate(String filterType);
    void invoiceDetails(ArrayList<OutstandingBean> invoiceListBean);
    void invoiceListFresh();
    void displayRefreshTime(String refreshTime);
    void displayTotalValue(String totalOutValue,String totalNetValue,String strCurrency);


}
