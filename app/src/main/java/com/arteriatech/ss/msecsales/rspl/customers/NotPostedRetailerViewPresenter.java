package com.arteriatech.ss.msecsales.rspl.customers;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public interface NotPostedRetailerViewPresenter {
    void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager);
    void showProgressDialog();
    void hideProgressDialog();
    void initializeObjects(Context context);
    void displayDuplicateCPList(final ArrayList retailerBeans);
    void showMessage(String s);
    void reload();

}
