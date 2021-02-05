package com.arteriatech.ss.msecsales.rspl.visitsummary;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arteriatech.ss.msecsales.rspl.mbo.VisitSummaryBean;

import java.util.ArrayList;

/**
 * Created by e10893 on 25-01-2018.
 */

public interface VisitSummaryViewPresenter {

    interface TargetResponse<T>{
        void success(ArrayList<T> success);
        void error(String message);
    }
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showMessage(String message);
    void dialogMessage(String message, String msgType);
    void showProgressDialog();
    void hideProgressDialog();
    void setFilterDate(String filterType);
    void searchResult(ArrayList<VisitSummaryBean> alVisitedRetailers);
    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);
    void TargetSync();
    void displayRefreshTime(String refreshTime);
    void displayList(ArrayList<VisitSummaryBean> alVisitedRetailers,String mStrVisitStartTime,String mStrVisitEndTime);
}
