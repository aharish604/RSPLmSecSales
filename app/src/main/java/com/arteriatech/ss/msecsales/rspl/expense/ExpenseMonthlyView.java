package com.arteriatech.ss.msecsales.rspl.expense;

public interface ExpenseMonthlyView {
    void showProgress();

    void hideProgress();

    void showMessage(String message);

    void showMobile(String displayMobileBill, int etDailyAllowance, int tvMobileBill);

    void showOtherExp(int displayRemarks, int displayPhoto, int tvOtherExpense, int etOtherExpenses, String uom, String otherExpanse);

    void errorDailyAlowce(String s);

    void errorOtherExp(String s);

    void errorRemarks(String s);
    void showSuccessMsg(String message);
}
