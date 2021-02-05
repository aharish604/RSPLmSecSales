package com.arteriatech.ss.msecsales.rspl.complaintlist;

public interface ComplaintListPresenter {

//    void callComplaintList(String cpno);
    void  onUploadData();
    void onStart(String mCPNO);
    void onRefresh();
    void searchComplaint(String query);
}
