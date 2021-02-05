package com.arteriatech.ss.msecsales.rspl.complaintlist;

import java.util.ArrayList;

public interface ComplaintListView {

    void setComplaintListDatatoAdapter(ArrayList<ComplaintListModel> listModels);
    void loadProgressBar();
    void hideProgressBar();
    void setSearchedComplaintListDatatoAdapter(ArrayList<ComplaintListModel> listModels);
    void showMessage(String msg);




}
