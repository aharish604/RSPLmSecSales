package com.arteriatech.ss.msecsales.rspl.retailerStockEntry.add;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.List;

public class RetailerAddStockPresenter implements RetailerAddStockViewPresenter.RetailerAddStkPresenter, OnlineODataInterface {
    private Context context;
    private Activity activity;
    private RetailerAddStockViewPresenter presenter = null;
    private ArrayList<RetailerStockBean> stocksInfoBeanArrayList, searchBeanArrayList;
    private String searchText = "";
    private ArrayList<RetailerStockBean> dealerStockBeanArrayList = null;
    private String stockOwner = "";
    private String mStrCPGUID32 = "";
    private ArrayList<RetailerStockBean> oldStockBeanArrayList = new ArrayList<>();
    private String typesetUOM="";

    public RetailerAddStockPresenter(Context context, Activity activity, RetailerAddStockViewPresenter presenter, ArrayList<RetailerStockBean> dealerStockBeanArrayList, String stockOwner, String mStrCPGUID32) {
        this.context = context;
        this.activity = activity;
        this.presenter = presenter;
        stocksInfoBeanArrayList = new ArrayList<>();
        searchBeanArrayList = new ArrayList<>();
        this.dealerStockBeanArrayList = dealerStockBeanArrayList;
        this.stockOwner = stockOwner;
        this.mStrCPGUID32 = mStrCPGUID32;
    }

    @Override
    public void loadMaterialData() {
        if (presenter != null) {
            presenter.showProgressDialog();
        }
        String qry = Constants.CPStockItems + "?$orderby=OrderMaterialGroupID asc &$filter=" + Constants.StockOwner + " eq '02' and " + Constants.OrderMaterialGroupID + " ne '' and " + Constants.CPGUID + " eq '" + mStrCPGUID32 + "'";
        ConstantsUtils.onlineRequest(context, qry, false, 2, ConstantsUtils.SESSION_QRY, this, false);
    }

    @Override
    public void onSearch(String searchText) {
        this.searchText = searchText;
        onSearchQuery(searchText);
    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isID = false;
        boolean isName = false;
        if (stocksInfoBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(stocksInfoBeanArrayList);
            } else {
                for (RetailerStockBean item : stocksInfoBeanArrayList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        isID = item.getOrderMaterialGroupDesc().toLowerCase().contains(searchText.toLowerCase());
                        isName = item.getOrderMaterialGroupID().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isID = true;
                        isName = true;
                    }
                    if (isID || isName)
                        searchBeanArrayList.add(item);
                }
            }
        }

        if (presenter != null) {
            presenter.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                stocksInfoBeanArrayList.clear();
                try {
                    stocksInfoBeanArrayList = OfflineManager.getDBStockMaterials(list, dealerStockBeanArrayList, context, oldStockBeanArrayList,typesetUOM);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (presenter != null) {
                            presenter.hideProgressDialog();
                            presenter.refreshAdapter(stocksInfoBeanArrayList);
                        }
                    }
                });
                break;
            case 2:
                typesetUOM = Constants.getTypesetValueForRetUOM(context);
                try {
                    oldStockBeanArrayList = OfflineManager.getDBStockMaterials(list, context, typesetUOM);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                String qry = Constants.CPStockItems + "?$orderby=OrderMaterialGroupID asc &$filter=" + Constants.StockOwner + " eq '" + stockOwner + "' and " + Constants.OrderMaterialGroupID + " ne '' ";
                ConstantsUtils.onlineRequest(context, qry, false, 1, ConstantsUtils.SESSION_QRY, this, false);
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        showErrorResponse(s);
    }

    private void showErrorResponse(final String errorMsg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (presenter != null) {
                    presenter.hideProgressDialog();
                    presenter.showMessage(errorMsg, 0);
                }
            }
        });
    }
}
