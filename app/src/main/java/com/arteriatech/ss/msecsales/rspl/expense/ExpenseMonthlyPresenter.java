package com.arteriatech.ss.msecsales.rspl.expense;

import android.view.View;

public interface ExpenseMonthlyPresenter {
    void onStart();

    void onDestroy();

    void onAllowanceChange(String s);

    void onOtherExpChange(String s);

    void onRemarksChange(String s);

    void onSaveData(View remarksView, View imageView, int fiscalYear);

    void expenseDate(String mStrCurrentDate, int mMonth);
}
