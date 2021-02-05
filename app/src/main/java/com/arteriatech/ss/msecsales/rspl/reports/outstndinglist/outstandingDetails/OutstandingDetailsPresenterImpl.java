package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.outstandingDetails;

import android.app.Activity;

import com.arteriatech.ss.msecsales.rspl.mbo.OutstandingBean;


/**
 * Created by e10769 on 04-07-2017.
 */

public class OutstandingDetailsPresenterImpl implements OutstandingDetailsPresenter {
    private Activity mContext;
    private OutstndingDetailsView invoiceDetailsView;
    private int comingFrom;
    private OutstandingBean invoiceListBean;
    private boolean isSessionRequired;
    public OutstandingDetailsPresenterImpl(Activity mContext, OutstndingDetailsView invoiceDetailsView, int comingFrom, OutstandingBean invoiceListBean, boolean isSessionRequired){
        this.mContext=mContext;
        this.invoiceDetailsView=invoiceDetailsView;
        this.comingFrom=comingFrom;
        this.invoiceListBean=invoiceListBean;
        this.isSessionRequired= isSessionRequired;
    }
    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {
        invoiceDetailsView=null;
    }

    @Override
    public void pdfDownload() {
        /*if (PermissionUtils.checkStoragePermission(mContext)) {
            if (invoiceDetailsView != null) {
                invoiceDetailsView.showProgressDialog(mContext.getString(R.string.downloading_pdf));
            }
            if (isSessionRequired){
                new SessionIDAsyncTask(mContext, new AsyncTaskCallBackInterface() {
                    @Override
                    public void asyncResponse(boolean b, Object o, String s) {
                        if (b){
                            downloadFiles(s);
                        }else {
                            if (invoiceDetailsView != null) {
                                invoiceDetailsView.hideProgressDialog();
                               invoiceDetailsView.showMessage(s, true);
                            }
                        }
                    }
                });
            }else {
                downloadFiles("");
            }

        }*/
    }

    private void downloadFiles(String sessionId) {
        /*String soItemDetailsURL = "/Invoices(InvoiceNo=%27" + invoiceListBean.getInvoiceNo() + "%27)/$value";
        ConstantsUtils.downloadFiles(mContext, new AsyncTaskCallBackInterface() {
            @Override
            public void asyncResponse(boolean status, Object response, String message) {
                if (invoiceDetailsView != null) {
                    invoiceDetailsView.hideProgressDialog();
                }
                if (status) {
                    UtilConstants.openViewer(mContext, message, UtilConstants.PDF_MINE_TYPE);
                } else {
                    if (invoiceDetailsView != null) {
                        invoiceDetailsView.showMessage(message, true);
                    }
                }
            }
        },soItemDetailsURL, invoiceListBean.getInvoiceNo(), UtilConstants.PDF_MINE_TYPE, sessionId, isSessionRequired);*/
    }

}
