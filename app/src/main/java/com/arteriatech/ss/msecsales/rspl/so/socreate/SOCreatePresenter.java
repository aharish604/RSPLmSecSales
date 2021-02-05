package com.arteriatech.ss.msecsales.rspl.so.socreate;

import android.os.Bundle;

import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface SOCreatePresenter {
    void onStart();

    void onDestroy();
    void onSearch(String searchText,ArrayList<SKUGroupBean> skuSearchList);

    boolean validateFields(SOCreateBean soCreateBean);

    void getProductRelInfo(String FeedbackId);

    void onAsignData(String save, String strRejReason, String strRejReasonDesc, SOCreateBean feedbackBean);
    void approveData(String ids, String description, String approvalStatus);
    void onSaveData();

    void brandList(String[][] arrBrand);

    void mustSellFilter(String[][] arrMustSell);

    void categoryList(String[][] arrCategory);

    void setFilterDate(String filterType);

    Bundle openFilter();

    interface IDealerStockMaterialPresenter {


        void onFragmentInteraction( String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName,ArrayList<SKUGroupBean> skuGroupBeanArrayList, String searchText);

    }

}
