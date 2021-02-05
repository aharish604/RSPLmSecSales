package com.arteriatech.ss.msecsales.rspl.reports.salesorder.filter;




import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public interface SOFilterView {
    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);
    void showMessage(String message);
    void showProgressDialog();
    void hideProgressDialog();
}
