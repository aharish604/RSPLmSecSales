package com.arteriatech.ss.msecsales.rspl.SampleDisbursementList;

import android.content.Intent;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;
public interface SampleDisbursementListPresenter {


    void start(SampleDisbursementListsView.SampleDisbursemwntResponse<InvoiceListBean> sampleDisbursemwntResponse);
    void onFilter();
    void startFilter(int requestCode, int resultCode, Intent data);
    void onSearch(String searchText);
    void getInvoiceDetails(InvoiceListBean invoiceListBean);
    void connectToOfflineDB(SampleDisbursementListsView.SampleDisbursemwntResponse<InvoiceListBean> sampleDisbursemwntResponse);
    void connectToOfflineDBRefresh();
    void refresh();
    void sync();







}
