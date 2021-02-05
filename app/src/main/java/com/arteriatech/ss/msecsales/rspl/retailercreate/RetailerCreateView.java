package com.arteriatech.ss.msecsales.rspl.retailercreate;

import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface RetailerCreateView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);

    void displayCPType(ArrayList<ValueHelpBean> cpType, ArrayList<ValueHelpBean> alCountryList, ArrayList<ValueHelpBean> alStateList, ArrayList<RoutePlanBean> alBeat,String stateID);
    void displayWeeklyOff(ArrayList<ValueHelpBean> alWeeklyOff,ArrayList<ValueHelpBean> alTaxStatus);
    void displayDMSDivision(ArrayList<DMSDivisionBean> alDmsDiv, ArrayList<ValueHelpBean> alGrpOne,ArrayList<ValueHelpBean> alGrpFour,ArrayList<ValueHelpBean> alGrpFive);
    void displayGrpTwoValue(ArrayList<ValueHelpBean> alGrpTwo);
    void displayGrpThreeValue(ArrayList<ValueHelpBean> alGrpThree);

    void errorRetailerType(String message);
    void errorOutletName(String message);
    void errorOwnerName(String message);

    void errorAddressOne(String message);
    void errorLandMArk(String message);
    void errorCountry(String message);
    void errorState(String message);
    void errorRouteName(String message);
    void errorPostlcode(String message);
    void errorMobileOne(String message);
    void errorMobileTwo(String message);
    void errorID(String message);
    void errorBussnessID(String message);
    void errorTelNo(String message);
    void errorFaxNo(String message);
    void errorEmailId(String message);
    void errorAnniversary(String message);

    void errorRemarks(String message);

    void errorOthers(String message);
    void errorDMSDiv(String message);
    void errorGroupOne(String message);
    void errorGroupTwo(String message);
    void errorGroupThree(String message);
    void errorGroupFour(String message);
    void errorGroupFive(String message);
    void errorDiscountPercentage(String message);

    void conformationDialog(String message, int from);

    void showMessage(String message, boolean isSimpleDialog);
}
