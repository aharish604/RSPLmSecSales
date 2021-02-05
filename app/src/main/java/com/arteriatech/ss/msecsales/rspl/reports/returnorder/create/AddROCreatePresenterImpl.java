package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.List;

public class AddROCreatePresenterImpl implements AddROCreatePresenter, OnlineODataInterface, UIListener {
    // data members
    private Context mContext = null;
    private AddROCreateView addROCreateView = null;
    private boolean isSessionRequired = false;
    private Activity activity;
    private ArrayList<ReturnOrderBean> list;
    private ArrayList<ReturnOrderBean> searchBeanArrayList = new ArrayList<>();
    private String searchText = "";
    private String mStrSelBrandId = "";
    private String mStrSelCategoryId = "";
    private String mStrSelOrderMaterialID = "";

    public AddROCreatePresenterImpl(Context mContext, AddROCreateView addROCreateView, boolean isSessionRequired, Activity activity, ArrayList<ReturnOrderBean> list) {
        this.mContext = mContext;
        this.addROCreateView = addROCreateView;
        this.isSessionRequired = isSessionRequired;
        this.activity = activity;
        this.list = list;
    }

    @Override
    public void onRequestError(int i, Exception e) {

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

    }

    @Override
    public void onStart() {
        addROCreateView.displayList(list);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean validateFields(CompetitorBean competitorBean) {
        return false;
    }

    @Override
    public boolean isValidMargin(String margin) {
        return false;
    }

    @Override
    public void onAsignData(CompetitorBean competitorBean) {

    }

    @Override
    public void onSaveData() {

    }

    @Override
    public void onSearch(String query) {
        this.searchText = query;
        onFilter();
    }

    private void onFilter() {
        searchBeanArrayList.clear();
        boolean brandStatus = false;
        boolean categoryStatus = false;
        boolean omgStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (list != null) {
            if (TextUtils.isEmpty(searchText) && (TextUtils.isEmpty(mStrSelBrandId) || mStrSelBrandId.equalsIgnoreCase(Constants.None)) && (TextUtils.isEmpty(mStrSelCategoryId) || mStrSelCategoryId.equalsIgnoreCase(Constants.None)) && (TextUtils.isEmpty(mStrSelOrderMaterialID) || mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None))) {
                searchBeanArrayList.addAll(list);
            } else {
                for (ReturnOrderBean item : list) {
                    if (!TextUtils.isEmpty(mStrSelBrandId) && !mStrSelBrandId.equalsIgnoreCase(Constants.None)) {
                        brandStatus = item.getBrand().toLowerCase().contains(mStrSelBrandId.toLowerCase());
                    } else {
                        brandStatus = true;
                    }
                    if (!TextUtils.isEmpty(mStrSelCategoryId) && !mStrSelCategoryId.equalsIgnoreCase(Constants.None)) {
                        categoryStatus = item.getProductCategoryID().toLowerCase().contains(mStrSelCategoryId.toLowerCase());
                    } else {
                        categoryStatus = true;
                    }
                    if (!TextUtils.isEmpty(mStrSelOrderMaterialID) && !mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None)) {
                        omgStatus = item.getOrderMaterialGroupID().toLowerCase().contains(mStrSelOrderMaterialID.toLowerCase());
                    } else {
                        omgStatus = true;
                    }

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = (item.getMaterialDesc().toLowerCase().contains(searchText.toLowerCase()) || item.getMaterialNo().toLowerCase().contains(searchText.toLowerCase()));
                    } else {
                        soSearchStatus = true;
                    }
                     if (brandStatus && categoryStatus && omgStatus && soSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (addROCreateView != null) {
            addROCreateView.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onFragmentInteraction(String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {
        this.mStrSelBrandId = brand;
        this.mStrSelCategoryId = category;
        this.mStrSelOrderMaterialID = creskuGrp;
        filterType(brandName, categoryName, creskuGrpName);
        onFilter();
    }

    @Override
    public Bundle openFilter() {
        Bundle bundle = new Bundle();
        bundle.putString(ROFilterDialogFragment.KEY_BRAND, mStrSelBrandId);
        bundle.putString(ROFilterDialogFragment.KEY_CATEGORY, mStrSelCategoryId);
        bundle.putString(ROFilterDialogFragment.KEY_SKUGRP, mStrSelOrderMaterialID);
        return bundle;
    }

    private void filterType(String brandName, String categoryName, String creskuGrpName) {
        try {
            String filteredResult = "";
            if (!TextUtils.isEmpty(brandName) && !brandName.equalsIgnoreCase(Constants.None)) {
                filteredResult = brandName;
            }
            if (!TextUtils.isEmpty(categoryName) && !categoryName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + categoryName;
            }
            if (!TextUtils.isEmpty(creskuGrpName) && !creskuGrpName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + creskuGrpName;
            }
            if (addROCreateView != null) {
                addROCreateView.setFilterDate(filteredResult);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @desc adding return order requests
     */
    private void getRequests() {
    }
}
