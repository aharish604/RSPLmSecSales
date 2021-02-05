package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;

public interface SampleDisbursementPresenter {

    void start(String name,String id,String division);
    void filter(String s);
    void add(ArrayList<RetailerStockBean> list, String divisionID);
    void validateFields(ArrayList<RetailerStockBean> list);
    void seaarchAddedItem(String s);
    void addedList(ArrayList<RetailerStockBean> list);

}
