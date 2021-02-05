package com.arteriatech.ss.msecsales.rspl.expense;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

public interface ExpenseDailyView {
    void showProgress();

    void hideProgress();

    void showMessage(String message);

    void displayData(ArrayList<RetailerBean> beatList, ArrayList<ValueHelpBean> locationList, ArrayList<ValueHelpBean> convenyanceList, ArrayList<ExpenseConfig> expenseConfigArrayList);

    void showConveyanceAmt(String displayFarTotal, String displayUOM);

    void setUIBasedOnType(String mStrSeleExpenseTypeId);

    void showDailyAllowance(String dispDailyAllowance, int etDailyAllowance, int tvDailyAllowance);

    void showTotalValue(String displayTotalValue);

    void errorExpenseType(String error);

    void errorBeatName(String error);

    void errorBeatWork(String error);

    void errorMode(String error);

    void errorDistance(String error);

    void errorDailyAllonce(String error);

    void errorConvMode(String error);

    void showSuccessMsg(String message);
}
