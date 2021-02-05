package com.arteriatech.ss.msecsales.rspl.expenselist;

import java.util.ArrayList;

public interface ExpenseListView {
    void displayExpenseList(ArrayList<ExpenseListBean> expenseListBeans);
    void displayExpenseSearchList(ArrayList<ExpenseListBean> expenseListBeans);
    void loadProgressBar();
    void hideProgressBar();
    void showMessage(String msg);
    void syncSucces();

    interface ExpenseListResponse<T>{
        void success(ArrayList<T> success);
        void error(String message);
    }
    void reloadata();


}
