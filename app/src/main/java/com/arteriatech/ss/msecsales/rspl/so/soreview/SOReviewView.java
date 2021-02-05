package com.arteriatech.ss.msecsales.rspl.so.soreview;

import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SchemeBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface SOReviewView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);
    void conformationDialog(String message, int from);
    void showMessage(String message, boolean isSimpleDialog);
    void displaySOReview(Map<String, SKUGroupBean> mapSKUGRPVal, Map<String, BigDecimal> mapCRSSKUQTY,
                         Map<String, Double> mapPriSchemePer, Map<String, Double> mapSecSchemePer,
                         Map<String, Double> mapSecSchemeAmt, Map<String, Integer> mapCntMatByCRSKUGRP,
                         Map<String, Double> mapNetAmt, ArrayList<SchemeBean> alSchFreeProd,
                         HashMap<String, String> hashMapFreeMatByOrderMatGrp,
                         HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial,int tlsdCount,double mDobTotalOrderVal, ArrayList<SKUGroupBean> skuGroupBeanArrayList);
}
