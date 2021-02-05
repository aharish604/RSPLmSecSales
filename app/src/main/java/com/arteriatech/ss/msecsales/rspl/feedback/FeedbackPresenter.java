package com.arteriatech.ss.msecsales.rspl.feedback;

import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface FeedbackPresenter {
    void onStart();

    void onDestroy();

    boolean validateFields(FeedbackBean feedbackBean);

    void getProductRelInfo(String FeedbackId);

    void onAsignData(String save, String strRejReason, String strRejReasonDesc,FeedbackBean feedbackBean);
    void approveData(String ids, String description, String approvalStatus);
    void onSaveData();
}
