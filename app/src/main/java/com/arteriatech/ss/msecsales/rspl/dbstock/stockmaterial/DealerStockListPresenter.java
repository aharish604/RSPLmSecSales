package com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class DealerStockListPresenter implements IDealerStockListViewPresenter.IDealerStockMaterialPresenter, UIListener {
    private Context context;
    private Activity activity;
    private IDealerStockListViewPresenter presenter = null;
    private ArrayList<DBStockBean> stocksInfoBeanArrayList, searchBeanArrayList;
    private DMSDivisionBean dmsDivisionBean = null;
    private String searchText = "", divisionID = "", stockOwner = "", stockType, distributorGUID = "", dmsDistributorID = "", mStrSelBrandId = "", mStrSelCategoryId = "", mStrSelOrderMaterialID = "";
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private boolean isErrorFromBackend = false;
    private GUID refguid =null;

    public DealerStockListPresenter(Context context, Activity activity, IDealerStockListViewPresenter presenter) {
        this.context = context;
        this.activity = activity;
        this.presenter = presenter;
        stocksInfoBeanArrayList = new ArrayList<>();
        searchBeanArrayList = new ArrayList<>();
    }

    private void configType() {
        try {
            String mStrConfigTypeQry = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Types + " eq '" + Constants.DSTSTKVIEW + "'";
            if (OfflineManager.getVisitStatusForCustomer(mStrConfigTypeQry)) {
                stockType = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.DSTSTKVIEW);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        stockType = "X";
    }

    private void getDistributorDMS() {
        try {
            configType();
            if (dmsDivisionBean != null) {
                distributorGUID = dmsDivisionBean.getDistributorGuid();
                divisionID = dmsDivisionBean.getDMSDivisionQuery();
                stockOwner = dmsDivisionBean.getStockOwner();
                dmsDistributorID = dmsDivisionBean.getDistributorId();
                getMaterials(divisionID, stockOwner, stockType, distributorGUID, dmsDistributorID);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void getMaterials(@NonNull String divisionID, @NonNull String stockOwner, @NonNull String stockType, @NonNull String distributorGUID, @NonNull String dmsDistributorID) {

        String mStrMyStockQry = "";
        try {
            String additionalQuery = "";
            if (!TextUtils.isEmpty(distributorGUID) && !distributorGUID.equalsIgnoreCase(Constants.None)) {
                additionalQuery = additionalQuery + " and " + Constants.CPGUID + " eq '" + distributorGUID + "'";
            }
            if (!TextUtils.isEmpty(mStrSelBrandId) && !mStrSelBrandId.equalsIgnoreCase(Constants.None)) {
                additionalQuery = additionalQuery + " and " + ConstantsUtils.Brand + " eq '" + mStrSelBrandId + "'";
            }
            if (!TextUtils.isEmpty(mStrSelCategoryId) && !mStrSelCategoryId.equalsIgnoreCase(Constants.None)) {
                additionalQuery = additionalQuery + " and " + ConstantsUtils.ProductCategoryID + " eq '" + mStrSelCategoryId + "'";
            }
            if (!TextUtils.isEmpty(mStrSelOrderMaterialID) && !mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None)) {
                additionalQuery = additionalQuery + " and " + Constants.OrderMaterialGroupID + " eq '" + mStrSelOrderMaterialID + "'";
            }

            mStrMyStockQry = Constants.CPStockItems + "?$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.StockOwner + " eq '" + stockOwner + "' " +
                    "and " + Constants.CPNo + " eq '" + dmsDistributorID + "'  and (" + divisionID + ") " + additionalQuery;
            stocksInfoBeanArrayList = OfflineManager.getDBStockList(mStrMyStockQry, divisionID, stockOwner, stockType, distributorGUID);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }


//        String query = Constants.CPStockItems + "?$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.StockOwner + " eq '" + stockOwner + "' " + " and (" + divisionID + ") ";
//        try {
//            stocksInfoBeanArrayList = OfflineManager.getDBStockList(query, divisionID, stockOwner, stockType);
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void loadMaterialData(String skuGroupId) {
        this.mStrSelOrderMaterialID = skuGroupId;
        try {
            new GetMaterialStockAsyncTask().execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSearch(String searchText) {
        this.searchText = searchText;
        onSearchQuery(searchText);
    }

    @Override
    public void onDestroy() {
        presenter = null;
    }

    @Override
    public void loadDistributor() {
        if (presenter != null) {
            presenter.showProgressDialog();
        }
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
        String spGuid = Constants.getSPGUID();
        ArrayList<DMSDivisionBean> distListDms = new ArrayList<>();
        try {
            String mStrDistQry = Constants.CPSPRelations + "?$select=CPNo,DMSDivisionID,CPGUID,CPName,DMSDivisionID,CPTypeID ";//&$filter="+ Constants.SPGUID+" eq '"+spGuid.replace("-","")+"' ";
            distListDms = OfflineManager.getDistributorsDms(mStrDistQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        final ArrayList<DMSDivisionBean> finalDistListDms = distListDms;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (presenter != null) {
                    presenter.hideProgressDialog();
                    presenter.divisionList(finalDistListDms);
                }
            }
        });
            /*}
        }).start();*/
    }

    @Override
    public void loadCategory(final String dmsDivisionId, final String brand) {
        this.mStrSelBrandId = brand;
        if (presenter != null) {
            presenter.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[][] mArrayCateogryTypeVal = null;
                if (!brand.equalsIgnoreCase(Constants.None) && !TextUtils.isEmpty(brand)) {
                    try {
                        String mStrConfigQry = Constants.BrandsCategories + "?$select=MaterialCategoryID,MaterialCategoryDesc&$orderby=" + Constants.MaterialCategoryDesc + " &$filter= " + Constants.BrandID + " eq '" + brand + "' and (" + dmsDivisionId + ")";
                        mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                    if (mArrayCateogryTypeVal == null) {
                        mArrayCateogryTypeVal = new String[2][1];
                        mArrayCateogryTypeVal[0][0] = "";
                        mArrayCateogryTypeVal[1][0] = "";
                    }
                } else {
                    try {
                        String mStrConfigQry = Constants.MaterialCategories + "?$select=MaterialCategoryID,MaterialCategoryDesc&$orderby=" + Constants.MaterialCategoryDesc + " &$filter=(" + dmsDivisionId + ")";
                        mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                    if (mArrayCateogryTypeVal == null) {
                        mArrayCateogryTypeVal = new String[2][1];
                        mArrayCateogryTypeVal[0][0] = "";
                        mArrayCateogryTypeVal[1][0] = "";
                    }
                }
                final String[][] finalMArrayCateogryTypeVal = mArrayCateogryTypeVal;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (presenter != null) {
                            presenter.hideProgressDialog();
                            presenter.categoryList(finalMArrayCateogryTypeVal);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void loadBrands(final String dmsDivisionId, DMSDivisionBean dmsDivisionBean, final String previousBrandId, final String previousCategoryId) {
        if (presenter != null) {
            presenter.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<BrandBean> brandBeanArrayList = new ArrayList<>();
                if (!previousCategoryId.equalsIgnoreCase(Constants.None) && !TextUtils.isEmpty(previousCategoryId)) {
                    try {
                        brandBeanArrayList = OfflineManager.getBrandListValues(Constants.BrandsCategories + "?$orderby=" + Constants.BrandDesc + " &$filter= " + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "' and " + "(" + dmsDivisionId + ")");
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (previousBrandId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousBrandId)) {
                        try {
                            String mStrConfigQry = Constants.Brands + "?$select=BrandID,BrandDesc&$orderby=" + Constants.BrandDesc;// + " &$filter=(" + dmsDivisionId + ")"; TODO need to enable this
                            brandBeanArrayList = OfflineManager.getBrandListValues(mStrConfigQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                }
                if (brandBeanArrayList != null) {
                    final ArrayList<BrandBean> finalMArrayBrandTypeVal = brandBeanArrayList;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (presenter != null) {
                                presenter.hideProgressDialog();
                                presenter.brandList(finalMArrayBrandTypeVal);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void loadCRSSKU(final String mStrSelDMSDIVID, final String previousBrandId, final String previousCategoryId) {
        this.mStrSelCategoryId = previousCategoryId;
        if (presenter != null) {
            presenter.showProgressDialog();
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                ArrayList<SKUGroupBean> mArrayOrderedGroup = new ArrayList<>();
                if ((previousCategoryId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousCategoryId)) && (previousBrandId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousBrandId))) {
                    mArrayOrderedGroup = null;
                    try {
                        String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=(" + mStrSelDMSDIVID + ")";
                        mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                } else if (previousCategoryId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousCategoryId)) {
                    try {
                        String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.BrandID + " eq '" + previousBrandId + "' and (" + mStrSelDMSDIVID + ")";
                        mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                } else if (previousBrandId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousBrandId)) {
                    try {
                        String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "' and (" + mStrSelDMSDIVID + ")";
                        mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                } else {
                    try {
                        String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "' and " + Constants.BrandID + " eq '" + previousBrandId + "' and (" + mStrSelDMSDIVID + ")";
                        mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }
                final ArrayList<SKUGroupBean> finalMArrayOrderedGroup = mArrayOrderedGroup;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (presenter != null) {
                            presenter.hideProgressDialog();
                            presenter.crsSKUList(finalMArrayOrderedGroup);
                        }
                    }
                });
//            }
//        }).start();
    }

    @Override
    public void onItemClick(DBStockBean dealerStockBean) {
        ArrayList<DBStockBean> alDBStockList = null;
        try {
            String mStrItemGuidQry = getDBStockItemQryByOrderMatGrp(dealerStockBean);
            if (!mStrItemGuidQry.equalsIgnoreCase("")) {
                String mStrMyStockQry = Constants.CPStockItemSnos + "?$filter=" + mStrItemGuidQry + " and StockTypeID eq '1'";
                alDBStockList = OfflineManager.getCPStockSNosList(mStrMyStockQry, dealerStockBean);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
        if (alDBStockList != null) {
            Intent intent = new Intent(context, DBStockDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_BEAN, alDBStockList);
            intent.putExtra(DBStockDetailsActivity.KEY_STOCKTYPE, stockType);
            context.startActivity(intent);
        }
    }

    @Override
    public void initialLoad(String strDivisionQry, DMSDivisionBean dmsDivisionBean) {
        this.dmsDivisionBean = dmsDivisionBean;
        try {
            new GetMaterialStockAsyncTask().execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(DMSDivisionBean dmsDivisionBean, String distributor, String divisionName, String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {
        this.dmsDivisionBean = dmsDivisionBean;
        this.mStrSelBrandId = brand;
        this.mStrSelCategoryId = category;
        this.mStrSelOrderMaterialID = creskuGrp;
        filterType(divisionName, brandName, categoryName, creskuGrpName);
        try {
            new GetMaterialStockAsyncTask().execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Bundle openFilter() {
        Bundle bundle = new Bundle();
        bundle.putString(DBFilterDialogFragment.KEY_DIVISION, divisionID);
        bundle.putString(DBFilterDialogFragment.KEY_BRAND, mStrSelBrandId);
        bundle.putString(DBFilterDialogFragment.KEY_CATEGORY, mStrSelCategoryId);
        bundle.putString(DBFilterDialogFragment.KEY_SKUGRP, mStrSelOrderMaterialID);
        return bundle;
    }

    @Override
    public void onRefresh() {
        refreshCollection();
    }

    private void refreshCollection() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getDBStockCollection());
            alAssignColl.add(Constants.ConfigTypesetTypes);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (presenter != null) {
                    presenter.hideProgressDialog();
                    presenter.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (presenter != null) {
                presenter.hideProgressDialog();
                presenter.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    private void filterType(String divisionName, String brandName, String categoryName, String creskuGrpName) {
        try {
            String filteredResult = "";
            if (!TextUtils.isEmpty(divisionName)) {
                filteredResult = ", " + divisionName;
            }
            if (!TextUtils.isEmpty(brandName) && !brandName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + brandName;
            }
            if (!TextUtils.isEmpty(categoryName) && !categoryName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + categoryName;
            }
            if (!TextUtils.isEmpty(creskuGrpName) && !creskuGrpName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + creskuGrpName;
            }
            if (presenter != null) {
                presenter.setFilterDate(filteredResult);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String getDBStockItemQryByOrderMatGrp(DBStockBean dealerStockBean) {
        String mStrItemQry = "";

        try {
            if (stockType.equalsIgnoreCase(Constants.str_01)) {
                mStrItemQry = OfflineManager.makeCPStockItemQryByOrderMatGrp(Constants.CPStockItems + "?$filter=" + Constants.Material_No + " eq '" + dealerStockBean.getMaterialNo() + "'" +
                        " and (" + dmsDivisionBean.getDMSDivisionQuery() + ") and " + Constants.CPGUID + " eq '" + dmsDivisionBean.getDistributorGuid() + "' and " + Constants.StockOwner + " eq '" + dmsDivisionBean.getStockOwner() + "'", Constants.CPStockItemGUID);

            } else {
                mStrItemQry = OfflineManager.makeCPStockItemQryByOrderMatGrp(Constants.CPStockItems + "?$filter=" + Constants.OrderMaterialGroupID + " eq '" + dealerStockBean.getOrderMaterialGroupID() + "'" +
                        " and (" + dmsDivisionBean.getDMSDivisionQuery() + ") and " + Constants.CPGUID + " eq '" + dmsDivisionBean.getDistributorGuid() + "' and " + Constants.StockOwner + " eq '" + dmsDivisionBean.getStockOwner() + "'", Constants.CPStockItemGUID);

            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        return mStrItemQry;
    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isCustomerID = false;
        boolean isCustomerName = false;
        if (stocksInfoBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(stocksInfoBeanArrayList);
            } else {
                for (DBStockBean item : stocksInfoBeanArrayList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        isCustomerID = item.getMaterialNo().toLowerCase().contains(searchText.toLowerCase());
                        isCustomerName = item.getMaterialDesc().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isCustomerID = true;
                        isCustomerName = true;
                    }
                    if (isCustomerID || isCustomerName)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (presenter != null) {
            presenter.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad,refguid.toString().toUpperCase());
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (presenter != null) {
                            presenter.hideProgressDialog();
                            presenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }
                    } else {
                        if (presenter != null) {
                            presenter.hideProgressDialog();
                            presenter.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (presenter != null) {
                        presenter.hideProgressDialog();
                        presenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (presenter != null) {
                    presenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (presenter != null) {
                    presenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (presenter != null) {
                presenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(activity,false);
            if (presenter != null) {
                presenter.hideProgressDialog();
                new GetMaterialStockAsyncTask().execute();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context, Constants.Sync_All);
            ConstantsUtils.startAutoSync(activity,false);
            if (presenter != null) {
                presenter.hideProgressDialog();
                new GetMaterialStockAsyncTask().execute();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetMaterialStockAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (presenter != null)
                presenter.showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getDistributorDMS();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (presenter != null) {
                presenter.hideProgressDialog();
                presenter.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.CPStockItems));
                presenter.refreshAdapter(stocksInfoBeanArrayList, stockType);
            }
        }
    }
}
