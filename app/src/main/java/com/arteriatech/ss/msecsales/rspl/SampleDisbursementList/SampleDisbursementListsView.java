package com.arteriatech.ss.msecsales.rspl.SampleDisbursementList;

import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplaintListModel;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;

import java.util.ArrayList;

public interface SampleDisbursementListsView {

    void setSampleDisbursementListDatatoAdapter(ArrayList<InvoiceListBean> listModels);
    void loadProgressBar();
    void hideProgressBar();
    void setSearchedSampleDisbursementListDatatoAdapter(ArrayList<ComplaintListModel> listModels);
    void showMessage(String msg);
    void openfilter(String startDate, String endDate, String filterType, String status, String grStatus);
    void searchResult(ArrayList<InvoiceListBean> feedbackBeanArrayList);
    void setFilterDate(String filterType);
    void invoiceDetails(InvoiceListBean invoiceListBean);
    void syncSuccess();
    void reloadData();

    interface SampleDisbursemwntResponse<T>{
        void success(ArrayList<T> success);
        void error(String message);
    }



}
