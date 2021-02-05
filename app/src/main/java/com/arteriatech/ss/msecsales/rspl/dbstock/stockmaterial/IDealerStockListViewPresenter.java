package com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 21-04-2018.
 */

public interface IDealerStockListViewPresenter {
    void initializeUI();

    void initializeClickListeners();

    void initializeObjects();

    void initializeRecyclerViewAdapter(LinearLayoutManager layoutManager);

    void showProgressDialog();

    void hideProgressDialog();

    void showMessage(String message);

    void refreshAdapter(ArrayList<?> arrayList, String stockType);
    void displayRefreshTime(String refreshTime);

    void loadIntentData(Intent intent);

    void searchResult(ArrayList<DBStockBean> searchBeanArrayList);

    void brandList(ArrayList<BrandBean> arrBrand);

    void divisionList(ArrayList<DMSDivisionBean> finalDistListDms);

    void categoryList(String[][] arrCategory);

    void crsSKUList(ArrayList<SKUGroupBean> arrCrsSku);

    void setFilterDate(String filterType);

    interface IDealerStockMaterialPresenter {

        void loadMaterialData(String skuGroupData);

        void onSearch(String s);

        void onDestroy();

        void loadDistributor();

        void loadCategory(String dmsDivisionId, String brand);

        void loadBrands(String dmsDivisionId, DMSDivisionBean dmsDivisionBean, final String previousBrandId, final String previousCategoryId);

        void loadCRSSKU(String dmsDivisionId, String brand, String categoryId);

        void onItemClick(DBStockBean dealerStockBean);

        void initialLoad(String strDivisionQry, DMSDivisionBean dmsDivisionBean);

        void onFragmentInteraction(DMSDivisionBean dmsDivisionBean, String distributor, String divisionName, String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName);

        Bundle openFilter();

        void onRefresh();
    }

}
