package com.arteriatech.ss.msecsales.rspl.customers;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.arteriatech.ss.msecsales.rspl.mbo.BeatOpeningSummaryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface ICustomerViewPresenter<T> {
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showProgressDialog();
    void hideProgressDialog();
    void onRefreshData();
    void customersListSync();
    void openFilter(String filterType, String status, String grStatus);
    void searchResult(ArrayList<RetailerBean> retailerSearchList);
    void displayBeat(ArrayList<RetailerBean> alBeat);
    void beatOpeningDetails(BeatOpeningSummaryBean beatOpeningSummaryBean);
    void setFilterDate(String filterType);
    void displayRefreshTime(String refreshTime);
    void displayMsg(String msg);
    void sendSelectedItem(Intent intent);
}
