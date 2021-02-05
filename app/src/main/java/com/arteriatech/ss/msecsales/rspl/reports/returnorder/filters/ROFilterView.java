package com.arteriatech.ss.msecsales.rspl.reports.returnorder.filters;




import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public interface ROFilterView {
    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);
    void showMessage(String message);
    void showProgressDialog();
    void hideProgressDialog();
}
