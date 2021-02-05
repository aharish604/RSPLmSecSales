package com.arteriatech.ss.msecsales.rspl.expenselist.expenselistdetails;

import android.app.Activity;
import android.content.Context;

import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;

public class ExpenseListDetailsPresenterImpl implements ExpenseListDetailsPresenter {
    Context context;
    String ExpenseCPGUId;
    ExpenseListdetailsView expenseListdetailsView;
    ArrayList<ExpenseDetailsBean> expenseDetailsBeanslist = new ArrayList<>();

    public ExpenseListDetailsPresenterImpl(Context context, String ExpenseCPGUId) {

        this.context = context;
        this.ExpenseCPGUId = ExpenseCPGUId;
        if (context instanceof ExpenseListdetailsView) {
            expenseListdetailsView = (ExpenseListdetailsView) context;
        }
    }

    @Override
    public void onStart() {
        if (expenseListdetailsView != null) {
            expenseListdetailsView.loadProgressBar();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    expenseDetailsBeanslist.clear();
                    String qry = Constants.ExpenseItemDetails + "?$filter=" + Constants.ExpenseGUID + " eq guid'" + ExpenseCPGUId + "' ";
                    expenseDetailsBeanslist.addAll(OfflineManager.getExpenseListDetails(qry, context));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (expenseListdetailsView != null) {
                            expenseListdetailsView.displayDetailsListr(expenseDetailsBeanslist);
                            expenseListdetailsView.hideProgressBar();

                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void getImages(final String ExpenseItemGUID) {

    }

    @Override
    public void dataValt() {
        if (expenseListdetailsView != null) {
            expenseListdetailsView.loadProgressBar();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    expenseDetailsBeanslist.clear();
                    expenseDetailsBeanslist.addAll(OfflineManager.getExpenseDetails(context, ExpenseCPGUId));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (expenseListdetailsView != null) {
                            expenseListdetailsView.displayDetailsListr(expenseDetailsBeanslist);
                            expenseListdetailsView.hideProgressBar();

                        }
                    }
                });
            }
        }).start();
    }
}
