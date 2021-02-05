package com.arteriatech.ss.msecsales.rspl.so.soreview;

import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface SOReviewPresenter {
    void onStart();

    void onDestroy();

    boolean validateFields(SOCreateBean soCreateBean);

    void getProductRelInfo(String FeedbackId);

    void onAsignData(String save, String strRejReason, String strRejReasonDesc, SOCreateBean feedbackBean);
    void approveData(String ids, String description, String approvalStatus);
    void onSaveData();
}
