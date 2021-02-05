package com.arteriatech.ss.msecsales.rspl.reports.salesorder.header;

import com.arteriatech.ss.msecsales.rspl.mbo.SOListBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 29-06-2017.
 */

public interface ISalesOrderListView {
//    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);

    void success();
    void error(String message);
    void showMessage(String message);

    void dialogMessage(String message, String msgType);

    void showProgressDialog();

    void hideProgressDialog();

    void searchResult(ArrayList<SalesOrderBean> salesOrderBeen);

    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);

    void setFilterDate(String filterType);

    void displayRefreshTime(String refreshTime);

    void openSODetail(SOListBean soListBean);
}
