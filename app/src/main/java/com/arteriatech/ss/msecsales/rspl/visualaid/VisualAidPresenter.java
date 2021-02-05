package com.arteriatech.ss.msecsales.rspl.visualaid;

import com.arteriatech.ss.msecsales.rspl.mbo.DocumentsBean;

public interface VisualAidPresenter {
    void onStart();

    void onDestroy();

    void onItemClick(DocumentsBean documentsBean);

    void onRefresh();

    void onDigitalPrdt();
}
