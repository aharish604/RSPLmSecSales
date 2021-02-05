package com.arteriatech.ss.msecsales.rspl.reports.collection.pendingcollection;


import com.arteriatech.ss.msecsales.rspl.mbo.CollectionHistoryBean;

/**
 * Created by e10847 on 07-12-2017.
 */

public interface ICollectionPendingSyncPresenter {
    /**
     * This will connect to offline Manager using AsyncTask and return the Result From OfflineManger to View.
     * @param salesOrderResponse
     */
    void connectToOfflineDB(ICollectionPendingSyncView.SalesOrderResponse<CollectionHistoryBean> salesOrderResponse);
    void onSync();
    void getDetails(CollectionHistoryBean collectionHistoryBean);
    void onDestroy();
}
