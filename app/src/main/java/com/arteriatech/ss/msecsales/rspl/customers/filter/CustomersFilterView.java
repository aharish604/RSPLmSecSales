package com.arteriatech.ss.msecsales.rspl.customers.filter;


import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/2/2017.
 */

public interface CustomersFilterView {
    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);

    void showMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();
}
