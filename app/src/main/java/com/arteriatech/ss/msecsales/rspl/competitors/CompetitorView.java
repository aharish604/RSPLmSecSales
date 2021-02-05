package com.arteriatech.ss.msecsales.rspl.competitors;

import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface CompetitorView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);

    void displayByCompetitor(ArrayList<ValueHelpBean> competitor);

    void displayByScheme(ArrayList<String> schemes);

    void errorProductName(String message);

    void errorRetailerMargin(String message);

    void errorCompetitorName(String message);

    void errorMrp(String message);

    void errorRemarks(String message);

    void errorSchemeLaunched(String message);

    void errorSchemeName(String message);

    void conformationDialog(String message, int from);

    void showMessage(String message, boolean isSimpleDialog);

    void errorMessage(String message);
}
