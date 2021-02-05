package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;


public interface RetailerStockCrtPresenter {
    void onStart();

    void onDestroy();

    boolean onSearch(String searchText, Object objects);

    void onSearch(String searchText);

    void validateItem(int activityRedirectType, RecyclerView recyclerView);

    void saveItem(RecyclerView recyclerView, ArrayList<RetailerStockBean> filteredCrsList);

    void getCheckedCount();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onFilter();

    void startFilter(int requestCode, int resultCode, Intent data);

    void addRetailerStock(ArrayList<RetailerStockBean> searchSOItemBean);
}
