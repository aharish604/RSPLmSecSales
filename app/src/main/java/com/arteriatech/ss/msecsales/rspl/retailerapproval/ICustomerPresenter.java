package com.arteriatech.ss.msecsales.rspl.retailerapproval;

import android.content.Intent;

import com.arteriatech.ss.msecsales.rspl.mbo.MTPHeaderBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MTPRoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
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
    void getRetailerDetails(RetailerBean retailerbean);


}
