package com.arteriatech.ss.msecsales.rspl.merchandising.list;

import com.arteriatech.ss.msecsales.rspl.mbo.MerchandisingBean;

public interface MerchandisingListPresenter {
    void onStart();

    void onDestroy();

    void onItemClick(MerchandisingBean merchandisingBean);

    void onRefresh();

    void onUploadData();
}
