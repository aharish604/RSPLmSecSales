package com.arteriatech.ss.msecsales.rspl.reports.invoicelist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterActivity;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterModelImpl;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10847 on 19-12-2017.
 */

public class InvoiceListPresenter implements IInvoiceListPresenter, UIListener {

    ArrayList<String> alAssignColl = null;
    private Context context;
    private IInvoiceListViewPresenter iReqListViewPresenter = null;
    private Activity activity;
    private ArrayList<InvoiceListBean> invoiceBeanArrayList=new ArrayList<>();
    private ArrayList<InvoiceListBean> searchBeanArrayList = new ArrayList<>();
    private Hashtable<String, String> headerTable;
    private String SPGUID = "";
    private String searchText = "";
    private String CPGUID = "", CPUID = "", cpNo = "", cpName = "";
    private String mStrBundleRetID = "", mStrBundleCPGUID = "",mStrBundleCPGUID36="",mStrParentId="";
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private boolean isErrorFromBackend = false;
    private GUID refguid =null;

    public InvoiceListPresenter(Context context, IInvoiceListViewPresenter iReqListViewPresenter, Activity activity, String mStrBundleRetID, String mStrBundleCPGUID,String mStrBundleCPGUID36,String mStrParentId) {
        this.context = context;
        this.iReqListViewPresenter = iReqListViewPresenter;
        this.headerTable = new Hashtable<>();
        this.activity = activity;
        this.mStrBundleRetID = mStrBundleRetID;
        this.mStrBundleCPGUID = mStrBundleCPGUID;
        this.mStrBundleCPGUID36 = mStrBundleCPGUID36;
        this.mStrParentId = mStrParentId;
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
            onSearchQuery(searchText,statusId,delvStatusId);
        }
    }

    private void onSearchQuery(String searchText,String paymentStatus,String delvStatusId) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (invoiceBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText) && TextUtils.isEmpty(paymentStatus)) {
                searchBeanArrayList.addAll(invoiceBeanArrayList);
            } else {
                if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_POSTED)) {
                    paymentStatus = "01";
                    delvStatusId = "01";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_CONFIRMED)) {
                    paymentStatus = "01";
                    delvStatusId = "03";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_PARTIALLY_PAID)) {
                    paymentStatus = "02";
                    delvStatusId = "";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_COMPLETELY_PAID)) {
                    paymentStatus = "03";
                    delvStatusId = "";
                } else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_CANCELED)) {
                    paymentStatus = "";
                    delvStatusId = "02";
                }
                else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_COMPLETELY_RETURN)) {
                    paymentStatus = "";
                    delvStatusId = "05";
                }
                else if (paymentStatus.equalsIgnoreCase(InvoiceFilterModelImpl.STATUS_PARTIALLY_RETURN)){
                    paymentStatus = "";
                    delvStatusId = "04";
                }

                for (InvoiceListBean item : invoiceBeanArrayList) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getInvoiceNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (!TextUtils.isEmpty(delvStatusId)) {
                        if (item.getDueDateStatus().equalsIgnoreCase(delvStatusId)) {
                            soDelStatus = true;
                        }
                    } else {
                        soDelStatus = true;
                    }
                    if (!TextUtils.isEmpty(paymentStatus)) {
                        if (item.getInvoiceStatus().equalsIgnoreCase(paymentStatus) && !item.getDueDateStatus().equalsIgnoreCase("02")) {
                            soTypeStatus = true;
                        }
                    } else {
                        soTypeStatus = true;
                    }

                    if (soSearchStatus && soTypeStatus && soDelStatus)
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
            onSearchQuery(searchText,statusId,delvStatusId);
        }
    }
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();
    @Override
    public ArrayList<InvoiceListBean> getInvoiceList() {
        if (iReqListViewPresenter!=null){
            iReqListViewPresenter.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> divisionUserAuthAL = null;
                try {
                    divisionUserAuthAL = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String strUserAuthDivisionID = "";
                if(divisionUserAuthAL!=null && !divisionUserAuthAL.isEmpty()) {
                    for (int i = 0; i < divisionUserAuthAL.size();i++){
                        if(i==divisionUserAuthAL.size()-1) {
                            strUserAuthDivisionID = strUserAuthDivisionID + "DMSDivision eq '" + divisionUserAuthAL.get(i)+"'";
                        }else {
                            strUserAuthDivisionID = strUserAuthDivisionID + "DMSDivision eq '" + divisionUserAuthAL.get(i) + "' or ";
                        }
                    }
                }

                List<ODataEntity> entities = null;
                if(!TextUtils.isEmpty(strUserAuthDivisionID)) {
//                    String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPNo + " eq '" + mStrBundleRetID + "' and ("+strUserAuthDivisionID+")";
                    String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and ("+strUserAuthDivisionID+")";

                    try {
                        entities = Constants.getListEntities(mStrDistQry, OfflineManager.offlineStore);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                alDmsDivision.clear();
                try {
//            alDmsDivision.addAll(OfflineManager.getDistributorsDmsDivision(entities));
                    alDmsDivision.addAll(OfflineManager.getRetailerBAseDmsDivisionwithoutNone(entities));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String strDmsDivision = "";
                if(alDmsDivision!=null && !alDmsDivision.isEmpty()) {
                    for (int i = 0; i < alDmsDivision.size();i++){
                        if(i==alDmsDivision.size()-1) {
                            strDmsDivision = strDmsDivision + "DmsDivision eq '" + alDmsDivision.get(i).getDMSDivisionID()+"'";
                        }else {
                            strDmsDivision = strDmsDivision + "DmsDivision eq '" + alDmsDivision.get(i).getDMSDivisionID() + "' or ";
                        }
                    }
                }
                try {
                    String tempParentID = "";
                    if(!TextUtils.isEmpty(mStrParentId)){
                        tempParentID = String.valueOf(Integer.parseInt(mStrParentId));
                    }
                    invoiceBeanArrayList.clear();
                    if(!TextUtils.isEmpty(strDmsDivision)) {
                        String[][] mArrayInvoiceTypeId = OfflineManager.getInVoidTypeId();
//                        invoiceBeanArrayList.addAll(OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and " + Constants.InvoiceTypeID + " ne '" + mArrayInvoiceTypeId[0][0] + "' and ("+strDmsDivision+")", context, "", mStrBundleCPGUID));
                        invoiceBeanArrayList.addAll(OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and " +Constants.CPGUID + " eq '" + tempParentID + "' and " + Constants.InvoiceTypeID + " ne '" + mArrayInvoiceTypeId[0][0] + "' and ("+strDmsDivision+")", context, "", mStrBundleCPGUID));
                        //invoiceBeanArrayList.addAll(OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and " + Constants.InvoiceTypeID + " ne '" + mArrayInvoiceTypeId[0][0] + "'", context, "", mStrBundleCPGUID));
                    }
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity)activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iReqListViewPresenter!=null){
                            iReqListViewPresenter.hideProgressDialog();
                            onSearchQuery(searchText,statusId,delvStatusId);
                        }
                    }
                });
            }
        }).start();


        return null;
    }

    /**
     * Getting the Invoice Items List from DB
     *
     * @return
     */
    public ArrayList<InvoiceListBean> getInvoiceItemsList() {
        /*try {
            invoiceBeanArrayList = OfflineManager.getNewInvoiceHistoryList(Constants.InvoiceItems + "?$filter=" + Constants.CustomerNo + " eq '" + mStrBundleRetID + "' " +
                    "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getLastMonthDate() + "' ", this.activity, "", mStrBundleCPGUID);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }*/

        return invoiceBeanArrayList;

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

    public void getInvoiceDetails(InvoiceListBean reqBean) {
        try {
            new InvoiceDetailsAsyncTask(reqBean).execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog();
                        iReqListViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }


                }else if (i == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (iReqListViewPresenter != null) {
                        iReqListViewPresenter.hideProgressDialog();
                        iReqListViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
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
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.InvoicePD_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
                iReqListViewPresenter.invoiceListFresh();
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
                iReqListViewPresenter.invoiceListFresh();
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
            alAssignColl.addAll(SyncUtils.getInvoice());
            alAssignColl.addAll(SyncUtils.getFIPCollection());
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
                    SyncUtils.updatingSyncStartTime(context,Constants.InvoicePD_sync,Constants.StartSync,refguid.toString().toUpperCase());
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

    public class InvoiceDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        InvoiceListBean invoiceListBean = new InvoiceListBean();
        private InvoiceListBean reqBean = null;

        public InvoiceDetailsAsyncTask(InvoiceListBean reqBean) {
            this.reqBean = reqBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.showProgressDialog();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String query = Constants.SSINVOICES + "(" + Constants.InvoiceGUID + "=guid'" + reqBean.getInvoiceGuid() + "')?$expand=" + Constants.SSInvoiceItemDetails + "";
            try {
                invoiceListBean = OfflineManager.getInvoiceDetails(query, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
                if (invoiceListBean != null) {
                    invoiceListBean.setInvoiceStatus(reqBean.getInvoiceStatus());
                    invoiceListBean.setDueDateStatus(reqBean.getDueDateStatus());
//                    invoiceListBean.setPONo(reqBean.getPONo());
                    iReqListViewPresenter.invoiceDetails(invoiceListBean);
                }
            }
        }
    }

}
