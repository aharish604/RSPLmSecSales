package com.arteriatech.ss.msecsales.rspl.complaintcreate;


import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

public interface ComplaintCreateView {

    void showProgressDialog(String message);
    void hideProgressDialog();
    void displayMessage(String message);
    void displayComplaintCategoryType(ArrayList<ValueHelpBean> feedbackType);
    void displayComplaintType(String[][] complaintType);
    void displayOrderedMaterialGroup(String[][] orderBy);
    void displayMaterialItemDescription(ArrayList<ComplaintCreateBeanUOMandDescription> orderBy);
  //  void displayByProductRelInfo(ArrayList<ValueHelpBean> feedbackType);
    void navigateRetailer();
    void errorCategoryType(String message);
    void errorComplaintType(String message);
    void errorOrderType(String message);
    void errorItemDetail(String message);
    void errorRemarks(String message);
    void errorQuantity(String message);
    void errorDate(String message);
    void errorBatch(String message);
    void conformationDialog(String message, int from);
    void showMessage(String message, boolean isSimpleDialog);
}
