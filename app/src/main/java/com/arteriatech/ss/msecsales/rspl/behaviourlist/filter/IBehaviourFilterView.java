package com.arteriatech.ss.msecsales.rspl.behaviourlist.filter;


import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;

import java.util.ArrayList;

/**
 * Created by e10893 on 25-01-2018.
 */

public interface IBehaviourFilterView {
    void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList);
    void showMessage(String message);
    void showProgressDialog();
    void hideProgressDialog();
}
