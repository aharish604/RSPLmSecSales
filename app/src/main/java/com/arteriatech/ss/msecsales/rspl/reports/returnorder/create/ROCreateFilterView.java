package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;

import java.util.ArrayList;

interface ROCreateFilterView {
    void showProgressDialog();

    void hideProgressDialog();

    void showMessage(String message);

    void categoryList(String[][] arrCategory);

    void crsSKUList(ArrayList<SKUGroupBean> arrCrsSku);

    void brandList(ArrayList<BrandBean> arrBrand);
}
