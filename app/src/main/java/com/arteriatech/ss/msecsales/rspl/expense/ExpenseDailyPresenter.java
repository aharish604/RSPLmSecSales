package com.arteriatech.ss.msecsales.rspl.expense;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

public interface ExpenseDailyPresenter {
    void onStart();

    void onDestroy();

    void expenseConfig(ExpenseConfig expenseConfig);

    void onModeConvItemSel(ValueHelpBean valueHelpBean);

    void onNonBeatItemSel(String[][] arrayExpNonBeatTypeVal, int position);

    void onBeatWorkItemSel(ValueHelpBean valueHelpBean);

    void onBeatNameItemSel(RetailerBean retailerBean);

    void onDailyAllowance(String dailyAllowance);

    void onDistanceValue(String distance);

    void onOtherConv(String otherConv);

    void expenseDate(String mStrExpDate);
}
