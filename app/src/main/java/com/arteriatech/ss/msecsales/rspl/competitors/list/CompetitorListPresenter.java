package com.arteriatech.ss.msecsales.rspl.competitors.list;

import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;

public interface CompetitorListPresenter {
    void onStart();

    void onDestroy();

    void onItemClick(CompetitorBean itemBean);

    void onRefresh();

    void onUploadData();
}
