package com.arteriatech.ss.msecsales.rspl.feedback;

import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface FeedbackView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);

    void displayByFeedback(ArrayList<ValueHelpBean> feedbackType);
    void displayByProductRelInfo(ArrayList<ValueHelpBean> feedbackType);

    void errorFeedBackType(String message);

    void errorProductRelated(String message);

    void errorRemarks(String message);

    void errorOthers(String message);

    void conformationDialog(String message, int from);

    void showMessage(String message, boolean isSimpleDialog);
}
