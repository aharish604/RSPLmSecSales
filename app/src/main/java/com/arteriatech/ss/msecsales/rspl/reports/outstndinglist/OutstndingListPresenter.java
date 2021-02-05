package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist;

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
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.OutstandingBean;
import com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.outstandingFilter.OutstandingFilterActivity;
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

public class OutstndingListPresenter implements OutStndingInvoicePresenter, UIListener {

    private Context context;
    private OutStndingInvoiceListView iReqListViewPresenter;
    private Activity activity;
    private ArrayList<OutstandingBean> invoiceBeanArrayList;
    private ArrayList<OutstandingBean> searchBeanArrayList;
    private Hashtable<String, String> headerTable;
    private String SPGUID = "";
    private String searchText = "";
    private String CPGUID = "", CPUID = "", cpNo = "", cpName = "";
    private String mStrBundleRetID = "", mStrBundleCPGUID = "" ,mStrBundleCPGUID36 = "",mStrBundleParentID = "";
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    ArrayList<String> alAssignColl = null;
    private boolean isErrorFromBackend = false;
    private GUID refguid =null;

    public OutstndingListPresenter(Context context, OutStndingInvoiceListView iReqListViewPresenter, Activity activity, String mStrBundleRetID, String mStrBundleCPGUID,String mStrBundleCPGUID36,String mStrBundleParentID) {
        this.context = context;
        this.iReqListViewPresenter = iReqListViewPresenter;
        this.invoiceBeanArrayList = new ArrayList<>();
        this.searchBeanArrayList = new ArrayList<>();
        this.headerTable = new Hashtable<>();
        this.activity = activity;
        this.mStrBundleRetID = mStrBundleRetID;
        this.mStrBundleCPGUID = mStrBundleCPGUID;
        this.mStrBundleParentID = mStrBundleParentID;
        this.mStrBundleCPGUID36 = mStrBundleCPGUID36;
    }


    @Override
    public void onFilter() {
        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText,ArrayList<OutstandingBean> invoiceBeanArrayList ) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText,invoiceBeanArrayList);
        }
    }

    private void onSearchQuery(String searchText,ArrayList<OutstandingBean> invoiceBeanArrayList ) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (invoiceBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(invoiceBeanArrayList);
            } else {
                for (OutstandingBean item : invoiceBeanArrayList) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getInvoiceNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
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
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        statusId = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_STATUS);
        statusName = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_STATUS_NAME);
        delvStatusId = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_GR_STATUS);
        delvStatusName = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);
//        requestSOList(startDate, endDate);
        displayFilterType();
    }

    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();

    @Override
    public ArrayList<OutstandingBean> getInvoiceList() {
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
//            String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPNo + " eq '" + mStrBundleRetID + "' and ("+strUserAuthDivisionID+")";
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
                    strDmsDivision = strDmsDivision + "DMSDivisionID eq '" + alDmsDivision.get(i).getDMSDivisionID()+"'";
                }else {
                    strDmsDivision = strDmsDivision + "DMSDivisionID eq '" + alDmsDivision.get(i).getDMSDivisionID() + "' or ";
                }
            }
        }
        try {
            if(!TextUtils.isEmpty(strDmsDivision)) {
                String tempParentID = "";
                if(!TextUtils.isEmpty(mStrBundleParentID)){
                    tempParentID=String.valueOf(Integer.parseInt(mStrBundleParentID));
                }
//                invoiceBeanArrayList = OfflineManager.getOutstandingList(Constants.SSOutstandingInvoices + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and " + Constants.PaymentStatusID + " ne '" + "03" + "' and (StatusID eq '03' or StatusID eq '04' or StatusID eq '05') and (" +strDmsDivision+")", context, "", mStrBundleCPGUID);

                invoiceBeanArrayList = OfflineManager.getOutstandingList(Constants.SSOutstandingInvoices + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and "+ Constants.CPGUID + " eq '" + tempParentID + "' and " + Constants.PaymentStatusID + " ne '" + "03" + "' and (StatusID eq '03' or StatusID eq '04' or StatusID eq '05') and (" +strDmsDivision+")", context, "", mStrBundleCPGUID);
//                invoiceBeanArrayList = OfflineManager.getOutstandingList(Constants.SSOutstandingInvoices + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and " + Constants.PaymentStatusID + " ne '" + "03" + "' and (StatusID eq '03' or "+Constants.StatusID+" eq '04') and (" +strDmsDivision+")", context, "", mStrBundleCPGUID);
                //invoiceBeanArrayList = OfflineManager.getOutstandingList(Constants.SSOutstandingInvoices + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID36 + "' and " + Constants.PaymentStatusID + " ne '" + "03" + "' and StatusID eq '03'", context, "", mStrBundleCPGUID);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return invoiceBeanArrayList;
    }

    @Override
    public void calTotalAmount(ArrayList<OutstandingBean> alOutList) {
        Double totalOutVal = 0.0;
        Double totalNetVal = 0.0;
        String mStrCurency = "";
        if(alOutList!=null && alOutList.size()>0){
            for (OutstandingBean invoice : alOutList) {
                try {
                    totalOutVal = totalOutVal + (Double.parseDouble(invoice.getInvoiceBalanceAmount()));
                    totalNetVal = totalNetVal + (Double.parseDouble(invoice.getInvoiceAmount()));
                } catch (NumberFormatException e) {
                    totalOutVal = 0.0;
                    totalNetVal = 0.0;
                    e.printStackTrace();
                }
                mStrCurency = invoice.getCurrency();
            }
        }


        if (iReqListViewPresenter != null) {
            iReqListViewPresenter.displayTotalValue(totalOutVal+"",totalNetVal+"",mStrCurency);
        }
    }

    /**
     * Getting the Invoice Items List from DB
     *
     * @return
     */
    public ArrayList<OutstandingBean> getInvoiceItemsList() {
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

    public void getInvoiceDetails(String invoiceNumber) {
        try {
            new InvoiceDetailsAsyncTask(invoiceNumber).execute();
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
                Constants.updateLastSyncTimeToTable(context,alAssignColl,Constants.OSPD_sync,refguid.toString().toUpperCase());
                Constants.isSync = false;
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
                if (iReqListViewPresenter != null) {
                    iReqListViewPresenter.hideProgressDialog();
                    iReqListViewPresenter.invoiceListFresh();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }

    }

    public class InvoiceDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        private String soNo = "";
        ArrayList<OutstandingBean> invoiceListBean = new ArrayList<>();

        public InvoiceDetailsAsyncTask(String soNo) {
            this.soNo = soNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            iReqListViewPresenter.showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String query = Constants.SSOutstandingInvoiceItemDetails+"?$filter="+Constants.InvoiceGUID+" eq guid'"+soNo+"' &$orderby="+Constants.ItemNo+" asc" ;
            try {
                invoiceListBean = OfflineManager.getOutstandingDetails(query/*, context*/);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            iReqListViewPresenter.invoiceDetails(invoiceListBean);
            iReqListViewPresenter.hideProgressDialog();
        }
    }

    private void onRefreshInvoices() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getFIPCollection());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(context.getString(R.string.alert_auto_sync_is_progress), context);
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.OSPD_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (iReqListViewPresenter != null) {
                iReqListViewPresenter.hideProgressDialog();
            }
            UtilConstants.showAlert(context.getString(R.string.no_network_conn), context);
        }
    }

}
