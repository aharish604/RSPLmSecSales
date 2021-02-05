package com.arteriatech.ss.msecsales.rspl.expenselist;

public interface ExpensePendingSyncPresenter {

    void start(ExpenseListView.ExpenseListResponse<ExpenseListBean> expenseListBeanExpenseListResponse);
    void syncExpenseList();


}
