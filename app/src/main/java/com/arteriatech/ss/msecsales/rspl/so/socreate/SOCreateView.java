package com.arteriatech.ss.msecsales.rspl.so.socreate;

import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface SOCreateView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);
    void conformationDialog(String message, int from);
    void showMessage(String message, boolean isSimpleDialog);
    void displaySO(ArrayList<SKUGroupBean> alCRSSKUGrpList);
    void searchResult(ArrayList<SKUGroupBean> skuSearchList);
    void displayCat(String[][] strCats);
    void displayBrands(String[][] strBrands);
    void displayMustSells(String[][] strMustSells);
    void displayDMSDivision(ArrayList<DMSDivisionBean> alDMSDiv);
    void showProgressDialog();
    void setFilterDate(String filterType);
}
