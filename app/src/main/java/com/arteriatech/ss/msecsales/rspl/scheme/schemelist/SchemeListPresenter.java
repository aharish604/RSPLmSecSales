package com.arteriatech.ss.msecsales.rspl.scheme.schemelist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterActivity;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeListBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by e10526 on 25-07-2018.
 *
 */

public class SchemeListPresenter implements ISchemeListPresenter, UIListener {

    ArrayList<String> alAssignColl = null;
    private Context context;
    private ISchemeListViewPresenter iReqListViewPresenter = null;
    private Activity activity;
    private ArrayList<SchemeListBean> schemeBeanArrayList =new ArrayList<>();
    private ArrayList<SchemeListBean> searchBeanArrayList = new ArrayList<>();
    private String searchText = "";
    private String mStrSchemeQry = "";
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private boolean isErrorFromBackend = false;
    private GUID refguid =null;

    public SchemeListPresenter(Context context, ISchemeListViewPresenter iReqListViewPresenter,
                               Activity activity, String mStrSchemeQry) {
        this.context = context;
        this.iReqListViewPresenter = iReqListViewPresenter;
        this.activity = activity;
        this.mStrSchemeQry = mStrSchemeQry;
    }


    @Override
    public void onFilter() {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText);
        }
    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean schemeNameSearchStatus = false;
        boolean schemeIDSearchStatus = false;
        if (schemeBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText) ) {
                searchBeanArrayList.addAll(schemeBeanArrayList);
            } else {

                for (SchemeListBean item : schemeBeanArrayList) {
                    schemeNameSearchStatus = false;
                    schemeIDSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        schemeNameSearchStatus = item.getSchemeDesc().toLowerCase().contains(searchText.toLowerCase());
                        schemeIDSearchStatus = item.getSchemeId().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        schemeNameSearchStatus = true;
                        schemeIDSearchStatus = true;
                    }

                    if (schemeNameSearchStatus || schemeIDSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onRefresh() {
        onRefreshInvoices();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
            statusId = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS);
            statusName = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS_NAME);
            delvStatusId = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS);
            delvStatusName = data.getStringExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);
            displayFilterType();
            onSearchQuery(searchText);
        }
    }

    @Override
    public ArrayList<SchemeListBean> getSchemeList() {
        if (iReqListViewPresenter!=null){
            iReqListViewPresenter.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    schemeBeanArrayList.clear();

                    schemeBeanArrayList.addAll(OfflineManager.getSchemesListGrp(context, getSchemeQry(mStrSchemeQry)));
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity)activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iReqListViewPresenter!=null){
                            iReqListViewPresenter.hideProgressDialog();
                            onSearchQuery(searchText);
                        }
                    }
                });
            }
        }).start();


        return null;
    }

    private String getSchemeQry(String schemeIds){
        String mStrSchemeQry = "";
        if (!schemeIds.equalsIgnoreCase("")) {
            if (schemeIds.contains(",")) {
                String schemeGUIDArray[] = schemeIds.split(",");
                int totalSize = schemeGUIDArray.length;
                int i = 0;
                for (String schemeGUIDVal : schemeGUIDArray) {
                    if (i == 0 && i == totalSize - 1) {
                        mStrSchemeQry = mStrSchemeQry
                                + "(" + Constants.SchemeGUID + " eq guid'"
                                + schemeGUIDVal + "')";

                    } else if (i == 0) {
                        mStrSchemeQry = mStrSchemeQry
                                + "(" + Constants.SchemeGUID + " eq guid'"
                                + schemeGUIDVal + "'";

                    } else if (i == totalSize - 1) {
                        mStrSchemeQry = mStrSchemeQry
                                + " or " + Constants.SchemeGUID + " eq guid'"
                                + schemeGUIDVal + "')";
                    } else {
                        mStrSchemeQry = mStrSchemeQry
                                + " or " + Constants.SchemeGUID + " eq guid'"
                                + schemeGUIDVal + "'";
                    }
                    i++;
                }
            } else {
                mStrSchemeQry = mStrSchemeQry + " " + Constants.SchemeGUID + " eq guid'" + schemeIds + "'";
            }
        }
        return mStrSchemeQry;
    }

    private void displayFilterType() {
        try {
            String statusDesc = "";
            if (!TextUtils.isEmpty(statusId)) {
                statusDesc = ", " + statusName;
            }
            if (!TextUtils.isEmpty(delvStatusId)) {
                statusDesc = statusDesc + ", " + delvStatusName;
            }
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
                if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog();
                        iReqListViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }

                   /* Constants.isSync = false;
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                            context, R.style.MyTheme);
                    builder.setMessage(context.getString(R.string.msg_error_occured_during_sync))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            dialog.cancel();
                                        }
                                    });

                    builder.show();*/


                }else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog();
                        iReqListViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            Constants.displayMsgReqError(errorBean.getErrorCode(), context);
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (i == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Schemes_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
                iReqListViewPresenter.schemeListFresh();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context, Constants.Sync_All);
            ConstantsUtils.startAutoSync(context, false);
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
                iReqListViewPresenter.schemeListFresh();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }

    }

    private void onRefreshInvoices() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getSchemes());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog();
                    UtilConstants.showAlert(context.getString(R.string.alert_auto_sync_is_progress), context);
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.Schemes_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
                UtilConstants.showAlert(context.getString(R.string.no_network_conn), context);
            }
        }
    }


}
