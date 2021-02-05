package com.arteriatech.ss.msecsales.rspl.scheme.schemelist;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arteriatech.ss.msecsales.rspl.scheme.SchemeListBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface ISchemeListViewPresenter {
    void initializeUI(Context context);
    void initializeClickListeners();
    void initializeObjects(Context context);
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showMessage(String message);
    void dialogMessage(String message, String msgType);
    void showProgressDialog();
    void hideProgressDialog();
    void openFilter(String startDate, String endDate, String filterType, String status, String grStatus);
    void searchResult(ArrayList<SchemeListBean> feedbackBeanArrayList);
    void setFilterDate(String filterType);
    void schemeListFresh();
    void displayRefreshTime(String refreshTime);


}
