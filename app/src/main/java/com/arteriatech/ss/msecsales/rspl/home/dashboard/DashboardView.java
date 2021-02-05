package com.arteriatech.ss.msecsales.rspl.home.dashboard;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arteriatech.ss.msecsales.rspl.mbo.MyTargetsBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 13-Apr-18.
 */

public interface DashboardView {
    void hideAttendancePB();

    void showAttendancePB();


    interface DaySummResponse<T>{
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
    void searchResult(ArrayList<MyTargetsBean> alMyTargets);
    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);
    void TargetSync();
    void displayRefreshTime(String refreshTime);
    void displayList(ArrayList<MyTargetsBean> alTargets, ArrayList<VisitedBeatBean> alVisitList, ArrayList<BrandProductiveBean> alBrandList);
    void displayBrandList(ArrayList<BrandProductiveBean> alBrandList);
    void disPlayMTPCount(String count);
    void disPlaySOCount(String count);
    void disPlayContractApprovalCount(String count);
    void displayPendingDispatch(String count);
    void displayCreditLimitCount(String count);
    void showMTPProgress();
    void showProgress();
    void hideMTPProgress();
}
