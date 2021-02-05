package com.arteriatech.ss.msecsales.rspl.customers;

import android.content.Intent;


import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MTPHeaderBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MTPRoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.WeekDetailsList;
import com.arteriatech.ss.msecsales.rspl.mbo.WeekHeaderList;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface ICustomerPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    void loadAsyncTask(String beatID);

    void sendResult(MTPHeaderBean mtpResultHeaderBean, MTPHeaderBean mtpHeaderBean, boolean isAsmLogin);

    void loadMTPCustomerList(ArrayList<MTPRoutePlanBean> mtpRoutePlanBeanArrayList, boolean isAsmLogin);

    void sendResultRTGS(WeekHeaderList mtpResultHeaderBean, WeekHeaderList mtpHeaderBean, ArrayList<RetailerBean> alSelectedList);

    void loadRTGSCustomerList(ArrayList<WeekDetailsList> rtgsBeanArrayList);
    void loadRTGSList(ArrayList<WeekDetailsList> rtgsBeanArrayList);
    void getOtherBeatList();

}
