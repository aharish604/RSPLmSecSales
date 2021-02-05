package com.arteriatech.ss.msecsales.rspl.beat.dealer;

import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 05-06-2018.
 */

public interface DealerView {
    void showProgress();
    void hideProgress();
    void displayMessage(String msg);
    void displayDistList(ArrayList<DMSDivisionBean> distListDms);
}
