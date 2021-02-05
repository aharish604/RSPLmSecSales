package com.arteriatech.ss.msecsales.rspl.feedback.list;

import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;

public interface FeedBackListPresenter {
    void onStart();

    void onDestroy();

    void onItemClick(FeedbackBean itemBean);

    void onRefresh();

    void onUploadData();
}
