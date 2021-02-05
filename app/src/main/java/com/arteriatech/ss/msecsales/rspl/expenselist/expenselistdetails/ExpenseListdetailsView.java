package com.arteriatech.ss.msecsales.rspl.expenselist.expenselistdetails;

import java.util.ArrayList;

public interface ExpenseListdetailsView {

    void displayDetailsListr(ArrayList<ExpenseDetailsBean>list);
    void loadProgressBar();
    void hideProgressBar();
    void showMessage(String msg);

}
