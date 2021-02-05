package com.arteriatech.ss.msecsales.rspl.common;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

public interface UIListenerWithReq {
    void onRequestError(int var1, Exception var2,ODataRequestExecution request);

    void onRequestSuccess(int var1, String var2) throws ODataException, OfflineODataStoreException;
}
