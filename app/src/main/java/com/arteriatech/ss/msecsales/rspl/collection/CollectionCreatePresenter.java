package com.arteriatech.ss.msecsales.rspl.collection;


import com.arteriatech.ss.msecsales.rspl.mbo.CollectionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.InvoiceBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface CollectionCreatePresenter {
    void onStart();
    void getInvoices(String divisionID);

    void onDestroy();

    boolean validateFields(CollectionBean collectionBean, String syncType);
    boolean validateOutstanding(ArrayList<InvoiceBean> selectedInvoice, CollectionBean collectionBean);


    void onAsignData(String save, String strRefType, CollectionBean collectionBean, String comingFrom);
    void onSaveData();
}
