package com.arteriatech.ss.msecsales.rspl.retailercreate;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerClassificationBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerCreateBean;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public interface RetailerCreatePresenter {
    void onStart();


    void onDestroy();

    boolean validateFields(RetailerCreateBean retailerBean);
    boolean validateDMSDiv(RetailerCreateBean retailerBean);
    boolean validateDMSDivFields(RetailerClassificationBean retailerClassificationBean);

    void requestCPType();

    void onReqSalesData();
    void onReqDMSDivsion(String mStrDistId);
    void onReqGrp2byGrpOne(String mStrTypeValue);
    void onReqGrp3byGrpTwo(String mStrTypeValue);

    void onAsignData(String save, String strRejReason, String strRejReasonDesc, RetailerCreateBean retailerBean);
    void approveData(String ids, String description, String approvalStatus);
    void onSaveData();
}
