package com.arteriatech.ss.msecsales.rspl.reports.collection.header;

import com.arteriatech.ss.msecsales.rspl.mbo.CollectionHistoryBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 29-06-2017.
 */

public interface ICollectionListView {
//    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);

    void success();
    void error(String message);
    void showMessage(String message);

    void dialogMessage(String message, String msgType);

    void showProgressDialog();

    void hideProgressDialog();

    void searchResult(ArrayList<CollectionHistoryBean> collHistBean);

    void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus);

    void setFilterDate(String filterType);

    void displayRefreshTime(String refreshTime);

    void openSODetail(CollectionHistoryBean collectionHistoryBean);
}
