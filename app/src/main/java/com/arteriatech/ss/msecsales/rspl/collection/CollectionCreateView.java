package com.arteriatech.ss.msecsales.rspl.collection;


import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.InvoiceBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface CollectionCreateView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);
    void displayOutstandingAmount(String ousStndingValue);

    void displayByCollectionData(ArrayList<ValueHelpBean> alColltype,
                                 ArrayList<ValueHelpBean> alPaymentMode,
                                 ArrayList<ValueHelpBean> alBankName, String outstandingData,ArrayList<DMSDivisionBean> alDMSDiv);

    void displayInvoiceData(ArrayList<InvoiceBean> alInvList);


    void errorRefType(String message);

    void errorPaymentMode(String message);

    void errorBankName(String message);
    void errorDivision(String message);

    void errorRemarks(String message);
    void errorChequeDate(String message);
    void errorUTRNoOrChequeDD(String message);

    void errorAmount(String message);
    void conformationDialog(String message, int from);
    void showMessage(String message, boolean isSimpleDialog);
    void errorInvoiceScreen(String message);
    void errorCollScreen(String message);
}
