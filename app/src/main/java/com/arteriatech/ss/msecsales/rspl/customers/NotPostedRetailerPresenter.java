package com.arteriatech.ss.msecsales.rspl.customers;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;

import java.util.ArrayList;

public interface NotPostedRetailerPresenter {
    void getCPList(ArrayList<String> cpList) throws OfflineODataStoreException;
    void onSyncSOrder();
}
