package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

import android.content.Context;

import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;

public interface AddSampleDisbursementPresenter {

     void start();
     void filter(String s,ArrayList<RetailerStockBean> filterList);
     void sendResultToOtherActivity(ArrayList<RetailerStockBean>list);
     void onFragmentInteraction(DMSDivisionBean dmsDivisionBean, String distributor, String divisionName, String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName);





}
