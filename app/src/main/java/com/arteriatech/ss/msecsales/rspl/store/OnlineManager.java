package com.arteriatech.ss.msecsales.rspl.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.AsyncTaskCallBackInterface;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.store.OnlineRequestListeners;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.GetSessionIdAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.SessionIDAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.common.MyUtils;
import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.sap.smp.client.httpc.HttpConversationManager;
import com.sap.smp.client.httpc.HttpMethod;
import com.sap.smp.client.httpc.IManagerConfigurator;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.httpc.listeners.ICommunicationErrorListener;
import com.sap.smp.client.httpc.listeners.IConversationFlowListener;
import com.sap.smp.client.httpc.listeners.IResponseListener;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataNavigationProperty;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataContractViolationException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataNetworkException;
import com.sap.smp.client.odata.exception.ODataParserException;
import com.sap.smp.client.odata.impl.ODataEntityDefaultImpl;
import com.sap.smp.client.odata.impl.ODataEntitySetDefaultImpl;
import com.sap.smp.client.odata.impl.ODataErrorDefaultImpl;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;
import com.sap.smp.client.odata.impl.ODataPropertyDefaultImpl;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataRequestListener;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.ODataResponseSingle;
import com.sap.smp.client.odata.store.ODataStore;
import com.sap.smp.client.odata.store.impl.ODataRequestParamSingleDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataResponseSingleDefaultImpl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by e10769 on 12-Apr-18.
 */

public class OnlineManager {


    public static boolean openOnlineStore(Context context,boolean metaData) throws OnlineODataStoreException {
       /* //OnlineOpenListener implements OpenListener interface
        //Listener to be invoked when the opening process of an OnlineODataStore object finishes
        Constants.onlineStore = null;
        OnlineStoreListener.instance = null;
        Constants.IsOnlineStoreFailed = false;
        Constants.IsOnlineStoreStarted = true;
        Constants.Error_Msg = "";
        try {
            Constants.printLogInfo("Get online store instance");
            OnlineStoreListener openListener = OnlineStoreListener.getInstance();
            Constants.printLogInfo("online store instance assigned");
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            Constants.printLogInfo("logon core context instance assigned :"+lgCtx);

            //The logon configurator uses the information obtained in the registration
            IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
            HttpConversationManager manager = new HttpConversationManager(context);
            configurator.configure(manager);

            OnlineODataStore.OnlineStoreOptions onlineOptions = new OnlineODataStore.OnlineStoreOptions();
            if(ConstantsUtils.getFirstTimeRun(context) == 2){
                onlineOptions.forceMetadataDownload = true;
            }else {
                onlineOptions.forceMetadataDownload = metaData;
            }

            //XCSRFTokenRequestFilter implements IRequestFilter
            //Request filter that is allowed to preprocess the request before sending
            XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(lgCtx);
            XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
                    requestFilter);
            manager.addFilter(requestFilter);
            manager.addFilter(responseFilter);

            try {
                String endPointURL = lgCtx.getAppEndPointUrl();
                Constants.printLogInfo("end point url "+endPointURL);
                Constants.printLogInfo("appid "+lgCtx.getAppId());
                Constants.printLogInfo("connection id "+lgCtx.getConnId());
                Constants.printLogInfo("resource path "+lgCtx.getResourcePath());
                Constants.printLogInfo("backend user "+lgCtx.getBackendUser());
                Constants.printLogInfo("host name "+lgCtx.getHost());



                URL url = new URL(endPointURL);
                //Method to open a new online store asynchronously
                Constants.printLogInfo("request for open online store");
                OnlineODataStore.open(context, url, manager, openListener, onlineOptions);

                Constants.printLogInfo("request for open online store completed");
                //            openListener.waitForCompletion();

                if (openListener.getError() != null) {
                    Constants.printLog("open online store ended with error");
                    throw openListener.getError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Constants.printLog("open online store ended with exception "+e.getMessage());
                throw new OnlineODataStoreException(e);
            }
            //Check if OnlineODataStore opened successfully

            while (!Constants.IsOnlineStoreFailed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Constants.IsOnlineStoreFailed = false;


            if (Constants.onlineStore != null) {
                return true;
            } else {
                return false;
            }
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }
    public static void requestQuery(final OnlineODataInterface onlineODataInterface, final Bundle bundle, final Context mContext) {
        String resourcePath = "";
        String sessionId = "";
        boolean isSessionRequired = false;
        int sessionType = 0;
        try {
            if (bundle == null) {
//            throw new IllegalArgumentException("bundle is null");
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "bundle is null", bundle);
            } else {
                resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
                sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
                isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
                sessionType = bundle.getInt(Constants.BUNDLE_SESSION_TYPE, 0);
            }
            if (TextUtils.isEmpty(resourcePath)) {
                if (onlineODataInterface != null)
                    onlineODataInterface.responseFailed(null, "resource path is null", bundle);
            } else {
                final Map<String, String> createHeaders = new HashMap<String, String>();
                if (isSessionRequired) {
                    if (!TextUtils.isEmpty(sessionId)) {
                        if (sessionType == ConstantsUtils.SESSION_HEADER) {
                            createHeaders.put(Constants.arteria_session_header, sessionId);
                        } else if (sessionType == ConstantsUtils.SESSION_QRY) {
                            resourcePath = getSessionResourcePath(resourcePath, sessionId);
                        } else if (sessionType == ConstantsUtils.SESSION_QRY_HEADER) {
                            createHeaders.put(Constants.arteria_session_header, sessionId);
                            resourcePath = getSessionResourcePath(resourcePath, sessionId);
                        }
                        requestScheduled(resourcePath, createHeaders, onlineODataInterface, bundle);
                    } else {
                        final String finalResourcePath = resourcePath;
                        final int finalsessionType = sessionType;
                        final Bundle finalBundle = bundle;
                        new SessionIDAsyncTask(mContext, new AsyncTaskCallBackInterface<ErrorBean>() {
                            @Override
                            public void asyncResponse(boolean status, ErrorBean errorBean, String values) {
                                String resourcePath = finalResourcePath;
                                if (status) {
                                    if (UtilConstants.isNetworkAvailable(mContext)) {
                                        if (finalsessionType == ConstantsUtils.SESSION_HEADER) {
                                            createHeaders.put(Constants.arteria_session_header, values);
                                        } else if (finalsessionType == ConstantsUtils.SESSION_QRY) {
//                                            resourcePath = String.format(resourcePath, values);
                                            resourcePath = getSessionResourcePath(resourcePath, values);
                                        } else if (finalsessionType == ConstantsUtils.SESSION_QRY_HEADER) {
                                            createHeaders.put(Constants.arteria_session_header, values);
                                            resourcePath = getSessionResourcePath(resourcePath, values);
                                        }
                                        try {
                                            bundle.putString(Constants.BUNDLE_SESSION_ID, values);
                                            requestScheduled(resourcePath, createHeaders, onlineODataInterface, finalBundle);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            if (onlineODataInterface != null)
                                                onlineODataInterface.responseFailed(null, e.getMessage(), finalBundle);
                                        }
                                    } else {
                                        if (onlineODataInterface != null)
                                            onlineODataInterface.responseFailed(null, mContext.getString(R.string.msg_no_network), finalBundle);
                                    }
                                } else {
                                    if (onlineODataInterface != null)
                                        onlineODataInterface.responseFailed(null, values, finalBundle);
                                }
                            }
                        }).execute();
                    }
                } else {
                    requestScheduled(resourcePath, createHeaders, onlineODataInterface, bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onlineODataInterface != null)
                onlineODataInterface.responseFailed(null, e.getMessage(), bundle);
        }
    }

    private static void requestScheduled(String resourcePath, Map<String, String> createHeaders, OnlineODataInterface onlineODataInterface, Bundle bundle) throws ODataException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            boolean isTechnicalCacheEnable = false;
            if (store.isOpenCache()) {
                if (bundle != null)
                    isTechnicalCacheEnable = bundle.getBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
                store.setPassive(isTechnicalCacheEnable);
            } else {
                if (bundle != null)
                    bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
            }
            OnlineRequestListeners getOnlineRequestListener = new OnlineRequestListeners(onlineODataInterface, bundle);
            scheduledReqEntity(resourcePath, getOnlineRequestListener, createHeaders, store);

        } else {
            throw new IllegalArgumentException("Store not opened");
        }*/
    }

    /*private static ODataRequestExecution scheduledReqEntity(String resourcePath, ODataRequestListener listener, Map<String, String> options, OnlineODataStore store) throws ODataContractViolationException {
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resourcePath is null");
        } else if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        } else {
            ODataRequestParamSingleDefaultImpl requestParam = new ODataRequestParamSingleDefaultImpl();
            requestParam.setMode(ODataRequestParamSingle.Mode.Read);
            requestParam.setResourcePath(resourcePath);
            requestParam.setOptions(options);
            requestParam.getCustomHeaders().putAll(options);

            return store.scheduleRequest(requestParam, listener);
        }
    }*/
    private static String getSessionResourcePath(String resourcePath, String sessionId) {
        if (resourcePath.contains("%1$s")) {
            resourcePath = String.format(resourcePath, sessionId);
        } else if (resourcePath.contains("?")) {
            resourcePath = resourcePath + "+and+LoginID+eq+'" + sessionId + "'";
        } else {
            resourcePath = resourcePath + "?$filter=LoginID+eq+'" + sessionId + "'";
        }
        return resourcePath;
    }


    public static void createFeedBack(Hashtable<String, String> tableHdr, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload

                ODataEntity feedBackEntity = createFeedBackEntity(tableHdr, itemtable, store);

                OnlineRequestListener feedbackListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);


                String feedbackGUID32 = tableHdr.get(Constants.FeebackGUID).replace("-", "");

                String feedbackCreatedOn = tableHdr.get(Constants.CreatedOn);
                String feedbackCreatedAt = tableHdr.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(feedbackCreatedOn) + "T" + UtilConstants.convertTimeOnly(feedbackCreatedAt);


                Map<String, String> collHeaders = new HashMap<String, String>();
                collHeaders.put("RequestID", feedbackGUID32);
                collHeaders.put("RepeatabilityCreation", mStrDateTime);

                ODataRequestParamSingle feedbackReq = new ODataRequestParamSingleDefaultImpl();
                feedbackReq.setMode(ODataRequestParamSingle.Mode.Create);
                feedbackReq.setResourcePath(feedBackEntity.getResourcePath());
                feedbackReq.setPayload(feedBackEntity);
                feedbackReq.getCustomHeaders().putAll(collHeaders);

                store.scheduleRequest(feedbackReq, feedbackListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        //END

    }

    /*private static ODataEntity createFeedBackEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity headerEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                // CreateOperation the parent Entity
                headerEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store)+""+Constants.FeedbackEntity);

                headerEntity.setResourcePath(Constants.Feedbacks, Constants.Feedbacks);


                try {
                    store.allocateProperties(headerEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }

                store.allocateNavigationProperties(headerEntity);

                headerEntity.getProperties().put(Constants.FeebackGUID,
                        new ODataPropertyDefaultImpl(Constants.FeebackGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.FeebackGUID))));
                headerEntity.getProperties().put(Constants.Remarks,
                        new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));
                headerEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                headerEntity.getProperties().put(Constants.FromCPGUID,
                        new ODataPropertyDefaultImpl(Constants.FromCPGUID, hashtable.get(Constants.CPGUID)));

                headerEntity.getProperties().put(Constants.FeedbackType,
                        new ODataPropertyDefaultImpl(Constants.FeedbackType, hashtable.get(Constants.FeedbackType)));

                headerEntity.getProperties().put(Constants.FeedbackTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.FeedbackTypeDesc, hashtable.get(Constants.FeedbackTypeDesc)));

                headerEntity.getProperties().put(Constants.FromCPTypeID,
                        new ODataPropertyDefaultImpl(Constants.FromCPTypeID, hashtable.get(Constants.CPTypeID)));

                headerEntity.getProperties().put(Constants.ParentID,
                        new ODataPropertyDefaultImpl(Constants.ParentID, hashtable.get(Constants.ParentID)));

                headerEntity.getProperties().put(Constants.ParentName,
                        new ODataPropertyDefaultImpl(Constants.ParentName, hashtable.get(Constants.ParentName)));

                headerEntity.getProperties().put(Constants.ParentTypeID,
                        new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));

                headerEntity.getProperties().put(Constants.ParentTypDesc,
                        new ODataPropertyDefaultImpl(Constants.ParentTypDesc, hashtable.get(Constants.ParentTypDesc)));

                if (hashtable.get(Constants.FeedbackDate)!=null) {
                    headerEntity.getProperties().put(Constants.FeedbackDate,
                            new ODataPropertyDefaultImpl(Constants.FeedbackDate, UtilConstants.convertDateFormat(hashtable.get(Constants.FeedbackDate))));
                }

                try {
                    headerEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                headerEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));

                // CreateOperation the item Entity

                ODataEntity itemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store)+""+Constants.FeedbackItemDetailEntity);
//

                try {
                    store.allocateProperties(itemEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < itemhashtable.size(); i++) {
                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    try {
                        store.allocateProperties(itemEntity, ODataStore.PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    itemEntity.getProperties().put(Constants.Remarks, new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));
                    itemEntity.getProperties().put(Constants.FeebackGUID, new ODataPropertyDefaultImpl(Constants.FeebackGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FeebackGUID))));
                    itemEntity.getProperties().put(Constants.FeebackItemGUID, new ODataPropertyDefaultImpl(Constants.FeebackItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FeebackItemGUID))));
                    itemEntity.getProperties().put(Constants.FeedbackType, new ODataPropertyDefaultImpl(Constants.FeedbackType, singleRow.get(Constants.FeedbackType)));
                    itemEntity.getProperties().put(Constants.FeedbackTypeDesc, new ODataPropertyDefaultImpl(Constants.FeedbackTypeDesc, singleRow.get(Constants.FeedbackTypeDesc)));
                    itemEntity.getProperties().put(Constants.FeedbackSubTypeID, new ODataPropertyDefaultImpl(Constants.FeedbackSubTypeID, singleRow.get(Constants.FeedbackSubTypeID)));
                    itemEntity.getProperties().put(Constants.FeedbackSubTypeDesc, new ODataPropertyDefaultImpl(Constants.FeedbackSubTypeDesc, singleRow.get(Constants.FeedbackSubTypeDesc)));
                    itemEntity.getProperties().put(Constants.ParentID,
                            new ODataPropertyDefaultImpl(Constants.ParentID, hashtable.get(Constants.ParentID)));

                    itemEntity.getProperties().put(Constants.ParentName,
                            new ODataPropertyDefaultImpl(Constants.ParentName, hashtable.get(Constants.ParentName)));

                    itemEntity.getProperties().put(Constants.ParentTypeID,
                            new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));

                    itemEntity.getProperties().put(Constants.ParentTypDesc,
                            new ODataPropertyDefaultImpl(Constants.ParentTypDesc, hashtable.get(Constants.ParentTypDesc)));
                    tempArray.add(i, itemEntity);
                }

                ODataEntitySetDefaultImpl itmEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itmEntity.getEntities().add(entity);
                }
                itmEntity.setResourcePath(Constants.FeedbackItemDetails);


                ODataNavigationProperty navProp = headerEntity.getNavigationProperty(Constants.FeedbackItemDetails);
                navProp.setNavigationContent(itmEntity);
                headerEntity.setNavigationProperty(Constants.FeedbackItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerEntity;

    }
*/
    public static void createSOEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createSOCreateEntity(table, itemtable, store);

                OnlineRequestListener onlineReqListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String ssoGUID32 = table.get(Constants.SSSOGuid).replace("-", "");

                String soCreatedOn = table.get(Constants.CreatedOn);
                String soCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(soCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(soCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, ssoGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, onlineReqListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        //END

    }
   /* private static ODataEntity createSOCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store)+""+Constants.SalesOrderEntity);

                newHeaderEntity.setResourcePath(Constants.SSSOs, Constants.SSSOs);

                try {
                    store.allocateProperties(newHeaderEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.SSSOGuid,
                        new ODataPropertyDefaultImpl(Constants.SSSOGuid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SSSOGuid))));
                try {
                    newHeaderEntity.getProperties().put(Constants.BeatGuid,
                            new ODataPropertyDefaultImpl(Constants.BeatGuid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGuid))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));

                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPType,
                        new ODataPropertyDefaultImpl(Constants.CPType, hashtable.get(Constants.CPType)));
                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.TestRun,
                        new ODataPropertyDefaultImpl(Constants.TestRun, hashtable.get(Constants.TestRun)));
                newHeaderEntity.getProperties().put(Constants.SoldToCPGUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SoldToCPGUID))));
                newHeaderEntity.getProperties().put(Constants.SoldToId,
                        new ODataPropertyDefaultImpl(Constants.SoldToId, hashtable.get(Constants.SoldToId)));
                newHeaderEntity.getProperties().put(Constants.SoldToUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToUID, hashtable.get(Constants.SoldToUID)));
               *//* newHeaderEntity.getProperties().put(Constants.SoldToDesc,
                        new ODataPropertyDefaultImpl(Constants.SoldToDesc, hashtable.get(Constants.SoldToDesc)));*//*
                newHeaderEntity.getProperties().put(Constants.SoldToType,
                        new ODataPropertyDefaultImpl(Constants.SoldToType, hashtable.get(Constants.SoldToType)));
                newHeaderEntity.getProperties().put(Constants.DmsDivision,
                        new ODataPropertyDefaultImpl(Constants.DmsDivision, hashtable.get(Constants.DmsDivision)));
                newHeaderEntity.getProperties().put(Constants.DmsDivisionDesc,
                        new ODataPropertyDefaultImpl(Constants.DmsDivisionDesc, hashtable.get(Constants.DmsDivisionDesc)));

                newHeaderEntity.getProperties().put(Constants.OrderType,
                        new ODataPropertyDefaultImpl(Constants.OrderType, hashtable.get(Constants.OrderType)));
                newHeaderEntity.getProperties().put(Constants.OrderTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.OrderTypeDesc, hashtable.get(Constants.OrderTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));

                newHeaderEntity.getProperties().put(Constants.GrossAmt,
                        new ODataPropertyDefaultImpl(Constants.GrossAmt, BigDecimal.valueOf(Double.parseDouble("0"))));//hashtable.get(Constants.GrossAmt)
                newHeaderEntity.getProperties().put(Constants.NetPrice,
                        new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble("0"))));//hashtable.get(Constants.NetPrice)

               *//* newHeaderEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*//*

                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));

                newHeaderEntity.getProperties().put(Constants.FromCPGUID,
                        new ODataPropertyDefaultImpl(Constants.FromCPGUID, hashtable.get(Constants.FromCPGUID).replace("-", "")));
//                newHeaderEntity.getProperties().put(Constants.FromCPNo,
//                        new ODataPropertyDefaultImpl(Constants.FromCPNo, hashtable.get(Constants.FromCPNo)));
                newHeaderEntity.getProperties().put(Constants.FromCPName,
                        new ODataPropertyDefaultImpl(Constants.FromCPName, hashtable.get(Constants.FromCPName)));
                newHeaderEntity.getProperties().put(Constants.FromCPTypId,
                        new ODataPropertyDefaultImpl(Constants.FromCPTypId, hashtable.get(Constants.FromCPTypId)));

                if(!hashtable.get(Constants.SPGUID).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID)))
                    );
                    newHeaderEntity.getProperties().put(Constants.SPNo,
                            new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                }

                newHeaderEntity.getProperties().put(Constants.FirstName,
                        new ODataPropertyDefaultImpl(Constants.FirstName, hashtable.get(Constants.FirstName)));

                try {
                    newHeaderEntity.getProperties().put(Constants.PONo,
                            new ODataPropertyDefaultImpl(Constants.PONo, hashtable.get(Constants.PONo)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));

                try {
                    newHeaderEntity.getProperties().put(Constants.Source,
                            new ODataPropertyDefaultImpl(Constants.Source, Constants.Mobile));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);
                    if (!singleRow.get(Constants.IsfreeGoodsItem).equalsIgnoreCase("X")) {

                        newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SalesOrderItemEntity);

                        newItemEntity.setResourcePath(Constants.SSSoItemDetails + "(" + singleRow.get(Constants.ItemNo) + ")", Constants.SSSoItemDetails + "(" + singleRow.get(Constants.ItemNo) + ")");
                        try {
                            store.allocateProperties(newItemEntity, ODataStore.PropMode.Keys);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }


                        newItemEntity.getProperties().put(Constants.SSSOItemGUID,
                                new ODataPropertyDefaultImpl(Constants.SSSOItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSSOItemGUID))));

                        newItemEntity.getProperties().put(Constants.SSSOGuid,
                                new ODataPropertyDefaultImpl(Constants.SSSOGuid, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSSOGuid))));

                        newItemEntity.getProperties().put(Constants.ItemNo,
                                new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));


                        newItemEntity.getProperties().put(Constants.MaterialNo,
                                new ODataPropertyDefaultImpl(Constants.MaterialNo, singleRow.get(Constants.MaterialNo)));

                        newItemEntity.getProperties().put(Constants.MaterialDesc,
                                new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                        newItemEntity.getProperties().put(Constants.OrderMatGrp,
                                new ODataPropertyDefaultImpl(Constants.OrderMatGrp, singleRow.get(Constants.OrderMatGrp)));

                        newItemEntity.getProperties().put(Constants.OrderMatGrpDesc,
                                new ODataPropertyDefaultImpl(Constants.OrderMatGrpDesc, singleRow.get(Constants.OrderMatGrpDesc)));

             *//*           newItemEntity.getProperties().put(Constants.LoginId,
                                new ODataPropertyDefaultImpl(Constants.LoginId, hashtable.get(Constants.LOGINID)));*//*


                        newItemEntity.getProperties().put(Constants.Currency,
                                new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                        newItemEntity.getProperties().put(Constants.Uom,
                                new ODataPropertyDefaultImpl(Constants.Uom, singleRow.get(Constants.Uom)));

                        newItemEntity.getProperties().put(Constants.NetPrice,
                                new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble("0"))));//singleRow.get(Constants.NetPrice)

                        try {
                            newItemEntity.getProperties().put(Constants.UnitPrice,
                                    new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.UnitPrice)))));
                        }catch (Exception e){
                            newItemEntity.getProperties().put(Constants.UnitPrice,
                                    new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble("0"))));
                            e.printStackTrace();
                        }
                       //singleRow.get(Constants.UnitPrice)
                        newItemEntity.getProperties().put(Constants.MRP,
                                new ODataPropertyDefaultImpl(Constants.MRP, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.MRP)))));
                        newItemEntity.getProperties().put(Constants.Quantity,
                                new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));

                        newItemEntity.getProperties().put(Constants.PriDiscount,
                                new ODataPropertyDefaultImpl(Constants.PriDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.PriDiscount)))));

                        newItemEntity.getProperties().put(Constants.SecDiscount,
                                new ODataPropertyDefaultImpl(Constants.SecDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.SecDiscount)))));

                        newItemEntity.getProperties().put(Constants.CashDiscount,
                                new ODataPropertyDefaultImpl(Constants.CashDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CashDiscount)))));

                        newItemEntity.getProperties().put(Constants.PrimaryDiscountPerc,
                                new ODataPropertyDefaultImpl(Constants.PrimaryDiscountPerc, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.PrimaryDiscountPerc)))));

                        *//*newItemEntity.getProperties().put(Constants.SecondaryDiscountPerc,
                                new ODataPropertyDefaultImpl(Constants.SecondaryDiscountPerc, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.SecondaryDiscountPerc)))));*//*

                        newItemEntity.getProperties().put(Constants.CashDiscountPerc,
                                new ODataPropertyDefaultImpl(Constants.CashDiscountPerc, BigDecimal.valueOf(Double.parseDouble("0"))));//singleRow.get(Constants.CashDiscountPerc)

                        newItemEntity.getProperties().put(Constants.TAX,
                                new ODataPropertyDefaultImpl(Constants.TAX, BigDecimal.valueOf(Double.parseDouble("0"))));//singleRow.get(Constants.TAX)

                        if (!singleRow.get(Constants.MFD).equalsIgnoreCase("")) {
                            newItemEntity.getProperties().put(Constants.MFD,
                                    new ODataPropertyDefaultImpl(Constants.MFD, UtilConstants.convertDateFormat(singleRow.get(Constants.MFD))));
                        }

                        newItemEntity.getProperties().put(Constants.IsfreeGoodsItem,
                                new ODataPropertyDefaultImpl(Constants.IsfreeGoodsItem, singleRow.get(Constants.IsfreeGoodsItem)));

                        newItemEntity.getProperties().put(Constants.Batch,
                                new ODataPropertyDefaultImpl(Constants.Batch, singleRow.get(Constants.Batch)));

                        newItemEntity.getProperties().put(Constants.TransRefTypeID,
                                new ODataPropertyDefaultImpl(Constants.TransRefTypeID, singleRow.get(Constants.TransRefTypeID)));

                        newItemEntity.getProperties().put(Constants.TransRefNo,
                                new ODataPropertyDefaultImpl(Constants.TransRefNo, singleRow.get(Constants.TransRefNo)));

                        newItemEntity.getProperties().put(Constants.TransRefItemNo,
                                new ODataPropertyDefaultImpl(Constants.TransRefItemNo, singleRow.get(Constants.TransRefItemNo)));

                        tempArray.add(incrementVal, newItemEntity);

                    }
                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SSSoItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SSSoItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SSSoItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }
*/
    public static String getUserRollInfo(String resourcePath,Context context) throws OnlineODataStoreException, ODataContractViolationException, ODataParserException, ODataNetworkException {
/*
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        ODataProperty property;
        ODataPropMap properties;
        if (store != null) {
            ODataRequestParamSingle request = new ODataRequestParamSingleDefaultImpl();
            request.setMode(ODataRequestParamSingle.Mode.Read);
            request.setResourcePath(resourcePath);
            ODataResponseSingle response = (ODataResponseSingle) store.executeRequest(request);
            //Check if the response is an error
            if (response.getPayloadType() == ODataPayload.Type.Error) {
                ODataErrorDefaultImpl error = (ODataErrorDefaultImpl)
                        response.getPayload();
                throw new OnlineODataStoreException(error.getMessage());
                //Check if the response contains EntitySet
            } else if (response.getPayloadType() == ODataPayload.Type.Entity) {
                ODataEntity entity = (ODataEntity) response.getPayload();
//                ODataEntitySet feed = (ODataEntitySet) response.getPayload();
//                List<ODataEntity> entities = feed.getEntities();
                //Retrieve the data from the response
//                for (ODataEntity entity : entities) {
                properties = entity.getProperties();
                property = properties.get(Constants.LoginID);
                if (property != null) {
                    String loginID = property.getValue().toString();
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.USERROLELOGINID, loginID);
                    editor.apply();
                }

                property = properties.get(Constants.RoleID);
                if (property != null) {
                    return property.getValue().toString();
                }
//                }
            }
        } else {
            throw new OnlineODataStoreException("Store not opened");
        }*/
        return "";
    }

    public static String getUserPartnersInfo(String resourcePath) throws OnlineODataStoreException, ODataContractViolationException, ODataParserException, ODataNetworkException {

        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        ODataProperty property;
        ODataPropMap properties;
        if (store != null) {
            ODataRequestParamSingle request = new ODataRequestParamSingleDefaultImpl();
            request.setMode(ODataRequestParamSingle.Mode.Read);
            request.setResourcePath(resourcePath);
            ODataResponseSingle response = (ODataResponseSingle) store.executeRequest(request);
            //Check if the response is an error
            if (response.getPayloadType() == ODataPayload.Type.Error) {
                ODataErrorDefaultImpl error = (ODataErrorDefaultImpl)
                        response.getPayload();
                throw new OnlineODataStoreException(error.getMessage());
                //Check if the response contains EntitySet
            } else if (response.getPayloadType() == ODataPayload.Type.EntitySet) {
                ODataEntitySet feed = (ODataEntitySet) response.getPayload();
                List<ODataEntity> entities = feed.getEntities();
                //Retrieve the data from the response
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.PartnerTypeID);
                    if(property!=null) {
                        return (String) property.getValue();
                    }
                }
//                }
            }
        } else {
            throw new OnlineODataStoreException("Store not opened");
        }*/
        return "";
    }


    /*public static ArrayList<RetailerBean> getRetailerList(String retListQry) throws OfflineODataStoreException {

        ArrayList<RetailerBean> retailerList = new ArrayList<>();
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            try {
                //Define the resource path

                ODataRequestParamSingle request = new ODataRequestParamSingleDefaultImpl();
                request.setMode(ODataRequestParamSingle.Mode.Read);
                request.setResourcePath(retListQry);
                //Send a request to read the ChannelPartners from the local database
                ODataResponseSingle response = (ODataResponseSingle) store.
                        executeRequest(request);
                //Check if the response is an error
                if (response.getPayloadType() == ODataPayload.Type.Error) {
                    ODataErrorDefaultImpl error = (ODataErrorDefaultImpl)
                            response.getPayload();
                    throw new OfflineODataStoreException(error.getMessage());
                    //Check if the response contains EntitySet
                } else if (response.getPayloadType() == ODataPayload.Type.EntitySet) {
                    ODataEntitySet feed = (ODataEntitySet) response.getPayload();
                    List<ODataEntity> entities = feed.getEntities();
                    retailerList.addAll(OfflineManager.getRetailerList(entities));
                } else {
                    throw new OfflineODataStoreException(Constants.invalid_payload_entityset_expected + response.getPayloadType().name());
                }
            } catch (Exception e) {
                throw new OfflineODataStoreException(e);
            }
        }
        return retailerList;

    }*/

    public static void createCollectionEntry(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity collectionCreateEntity = createCollectionEntryEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String fipGUID32 = table.get(Constants.FIPGUID).replace("-", "");

                String collCreatedOn = table.get(Constants.CreatedOn);
                String collCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(collCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(collCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, fipGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(collectionCreateEntity.getResourcePath());
                collectionReq.setPayload(collectionCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        //END

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
  /*  private static ODataEntity createCollectionEntryEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpace(OfflineManager.offlineStore)+""+Constants.FinancialPostingsEntity);

                newHeaderEntity.setResourcePath(Constants.FinancialPostings, Constants.FinancialPostings);

                try {
                    store.allocateProperties(newHeaderEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.FIPGUID,
                        new ODataPropertyDefaultImpl(Constants.FIPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.FIPGUID))));

                try {
                    newHeaderEntity.getProperties().put(Constants.ExtRefID,
                            new ODataPropertyDefaultImpl(Constants.ExtRefID, hashtable.get(Constants.FIPGUID).replace("-","")));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));

                try {
                    newHeaderEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGuid))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                try {
                    newHeaderEntity.getProperties().put(Constants.DMSDivision,
                            new ODataPropertyDefaultImpl(Constants.DMSDivision, hashtable.get(Constants.DMSDivision)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!hashtable.get(Constants.BankID).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.BankID,
                            new ODataPropertyDefaultImpl(Constants.BankID, hashtable.get(Constants.BankID)));

                    newHeaderEntity.getProperties().put(Constants.BankName,
                            new ODataPropertyDefaultImpl(Constants.BankName, hashtable.get(Constants.BankName)));
                }
                if (!hashtable.get(Constants.InstrumentNo).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.InstrumentNo,
                            new ODataPropertyDefaultImpl(Constants.InstrumentNo, hashtable.get(Constants.InstrumentNo)));
                }

                if (!hashtable.get(Constants.InstrumentDate).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.InstrumentDate,
                            new ODataPropertyDefaultImpl(Constants.InstrumentDate, UtilConstants.convertDateFormat(hashtable.get(Constants.InstrumentDate))));
                }
                newHeaderEntity.getProperties().put(Constants.Amount,
                        new ODataPropertyDefaultImpl(Constants.Amount, new BigDecimal(hashtable.get(Constants.Amount))));

                if (!hashtable.get(Constants.Remarks).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));
                }
                newHeaderEntity.getProperties().put(Constants.FIPDocType,
                        new ODataPropertyDefaultImpl(Constants.FIPDocType, hashtable.get(Constants.FIPDocType)));

                try {
                    newHeaderEntity.getProperties().put(Constants.Source,
                            new ODataPropertyDefaultImpl(Constants.Source, hashtable.get(Constants.Source)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.PaymentModeID,
                        new ODataPropertyDefaultImpl(Constants.PaymentModeID, hashtable.get(Constants.PaymentModeID)));

                newHeaderEntity.getProperties().put(Constants.FIPDate,
                        new ODataPropertyDefaultImpl(Constants.FIPDate, UtilConstants.convertDateFormat(hashtable.get(Constants.FIPDate))));

          *//*      newHeaderEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*//*
                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));
                newHeaderEntity.getProperties().put(Constants.BranchName,
                        new ODataPropertyDefaultImpl(Constants.BranchName, hashtable.get(Constants.BranchName)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.ParentNo,
                        new ODataPropertyDefaultImpl(Constants.ParentNo, hashtable.get(Constants.ParentNo)));
                try {
                    newHeaderEntity.getProperties().put(Constants.ParentTypeID,
                            new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.SPFirstName,
                        new ODataPropertyDefaultImpl(Constants.SPFirstName, hashtable.get(Constants.SPFirstName)));

                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));

                try {
                    newHeaderEntity.getProperties().put(Constants.Source,
                            new ODataPropertyDefaultImpl(Constants.Source, Constants.Mobile));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (!hashtable.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                        newHeaderEntity.getProperties().put(Constants.BeatGUID,
                                new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGUID))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int incremntVal = 0;
                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);

                    incremntVal = i + 1;
                    String itemNo = ConstantsUtils.addZeroBeforeValue(incremntVal, ConstantsUtils.ITEM_MAX_LENGTH);
                    newItemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpace(OfflineManager.offlineStore)+""+Constants.FinancialPostingsItemEntity);

                    newItemEntity.setResourcePath(Constants.FinancialPostingItemDetails + "(" + itemNo + ")", Constants.FinancialPostingItemDetails + "(" + itemNo + ")");
                    try {
                        store.allocateProperties(newItemEntity, ODataStore.PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.FIPItemGUID,
                            new ODataPropertyDefaultImpl(Constants.FIPItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FIPItemGUID))));

                    newItemEntity.getProperties().put(Constants.FIPGUID,
                            new ODataPropertyDefaultImpl(Constants.FIPGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FIPGUID))));

                    newItemEntity.getProperties().put(Constants.FIPItemNo,
                            new ODataPropertyDefaultImpl(Constants.FIPItemNo, itemNo));


                    newItemEntity.getProperties().put(Constants.ReferenceTypeID,
                            new ODataPropertyDefaultImpl(Constants.ReferenceTypeID, singleRow.get(Constants.ReferenceTypeID)));

*//*
                    newItemEntity.getProperties().put(Constants.LOGINID,
                            new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*//*


                    if (!singleRow.get(Constants.ReferenceID).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.FIPAmount,
                                new ODataPropertyDefaultImpl(Constants.FIPAmount, new BigDecimal(singleRow.get(Constants.FIPAmount))));
                        newItemEntity.getProperties().put(Constants.ReferenceID,
                                new ODataPropertyDefaultImpl(Constants.ReferenceID, singleRow.get(Constants.ReferenceID).toUpperCase()));
                        newItemEntity.getProperties().put(Constants.Amount,
                                new ODataPropertyDefaultImpl(Constants.Amount, new BigDecimal(singleRow.get(Constants.Amount))));

                        if (singleRow.get(Constants.CashDiscountPercentage)!=null && !TextUtils.isEmpty(singleRow.get(Constants.CashDiscountPercentage))) {
                            newItemEntity.getProperties().put(Constants.CashDiscountPercentage,
                                    new ODataPropertyDefaultImpl(Constants.CashDiscountPercentage, new BigDecimal(singleRow.get(Constants.CashDiscountPercentage))));
                        }
                        if (singleRow.get(Constants.CashDiscount)!=null && !TextUtils.isEmpty(singleRow.get(Constants.CashDiscount))) {
                            newItemEntity.getProperties().put(Constants.CashDiscount,
                                    new ODataPropertyDefaultImpl(Constants.CashDiscount, new BigDecimal(singleRow.get(Constants.CashDiscount))));
                        }
                    } else {
                        newItemEntity.getProperties().put(Constants.FIPAmount,
                                new ODataPropertyDefaultImpl(Constants.FIPAmount, new BigDecimal(singleRow.get(Constants.FIPAmount))));
                    }

                    if (!singleRow.get(Constants.InstrumentDate).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.InstrumentDate,
                                new ODataPropertyDefaultImpl(Constants.InstrumentDate, UtilConstants.convertDateFormat(singleRow.get(Constants.InstrumentDate))));
                    }

                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                    newItemEntity.getProperties().put(Constants.InstrumentNo,
                            new ODataPropertyDefaultImpl(Constants.InstrumentNo, singleRow.get(Constants.InstrumentNo)));

                    newItemEntity.getProperties().put(Constants.PaymentMode,
                            new ODataPropertyDefaultImpl(Constants.PaymentMode, singleRow.get(Constants.PaymentModeID)));

                    newItemEntity.getProperties().put(Constants.PaymetModeDesc,
                            new ODataPropertyDefaultImpl(Constants.PaymetModeDesc, singleRow.get(Constants.PaymetModeDesc)));

                    try {
                        if (!singleRow.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                            newItemEntity.getProperties().put(Constants.BeatGUID,
                                    new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.BeatGUID))));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    tempArray.add(i, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.FinancialPostingItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.FinancialPostingItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.FinancialPostingItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }
  */
    public static void requestOnline(final GetOnlineODataInterface getOnlineODataInterface, final Bundle bundle, final Context mContext) throws Exception {
        String resourcePath = "";
        String sessionId = "";
        int operation = 0;
        int requestCode = 0;
        boolean isSessionRequired = false;
        boolean isSessionInResourceRequired = false;
        if (bundle == null) {
            throw new IllegalArgumentException("bundle is null");
        } else {
            resourcePath = bundle.getString(Constants.BUNDLE_RESOURCE_PATH, "");
            sessionId = bundle.getString(Constants.BUNDLE_SESSION_ID, "");
            operation = bundle.getInt(Constants.BUNDLE_OPERATION, 0);
            requestCode = bundle.getInt(Constants.BUNDLE_REQUEST_CODE, 0);
            //   isSessionRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
            isSessionRequired = false;
            isSessionInResourceRequired = bundle.getBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, false);
        }
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resource path is null");
        } else {
            final Map<String, String> createHeaders = new HashMap<String, String>();
//            createHeaders.put(Constants.arteria_dayfilter, Constants.NO_OF_DAYS);
            if (!TextUtils.isEmpty(sessionId)) {
                createHeaders.put(Constants.arteria_session_header, sessionId);
                if (isSessionInResourceRequired) {
                    resourcePath = String.format(resourcePath, sessionId);
                }
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            } else if (isSessionRequired) {
                final int finalOperation = operation;
                final String finalResourcePath = resourcePath;
                final int finalRequestCode = requestCode;
                final boolean finalIsSessionInResourceRequired = isSessionInResourceRequired;
                new GetSessionIdAsyncTask(mContext, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        String resourcePath = finalResourcePath;
                        if (status) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                if (finalIsSessionInResourceRequired) {
                                    resourcePath = String.format(resourcePath, values);
                                }
                                createHeaders.put(Constants.arteria_session_header, values);
                                try {
                                    bundle.putString(Constants.BUNDLE_SESSION_ID, values);
                                    requestOnlineWithSessionId(resourcePath, finalOperation, finalRequestCode, createHeaders, getOnlineODataInterface, bundle);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (getOnlineODataInterface != null)
                                        getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, e.getMessage(), bundle);
                                }
                            } else {
                                if (getOnlineODataInterface != null)
                                    getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, mContext.getString(R.string.msg_no_network), bundle);
//                                throw new IllegalArgumentException(mContext.getString(R.string.msg_no_network));
                            }
                        } else {
                            if (getOnlineODataInterface != null)
                                getOnlineODataInterface.responseFailed(null, finalOperation, finalRequestCode, resourcePath, values, bundle);
//                            throw new IllegalArgumentException(values);
                        }
                    }
                }).execute();

            } else {
                requestOnlineWithSessionId(resourcePath, operation, requestCode, createHeaders, getOnlineODataInterface, bundle);
            }
        }
    }

    private static void requestOnlineWithSessionId(String resourcePath, int operation, int requestCode, Map<String, String> createHeaders, GetOnlineODataInterface getOnlineODataInterface, Bundle bundle) throws ODataContractViolationException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
//        LogManager.writeLogInfo(Constants.ERROR_ARCHIVE_ENTRY_REQUEST_URL + " : " + resourcePath);
        if (store != null) {
            GetOnlineRequestListener getOnlineRequestListener = new GetOnlineRequestListener(getOnlineODataInterface, operation, requestCode, resourcePath, bundle);
            scheduleReadEntity(resourcePath, getOnlineRequestListener, createHeaders, store);
        } else {
            throw new IllegalArgumentException("Store not opened");
        }*/
    }

    /*private static ODataRequestExecution scheduleReadEntity(String resourcePath, ODataRequestListener listener, Map<String, String> options, OnlineODataStore store) throws ODataContractViolationException {
        if (TextUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("resourcePath is null");
        } else if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        } else {
            ODataRequestParamSingleDefaultImpl requestParam = new ODataRequestParamSingleDefaultImpl();
            requestParam.setMode(ODataRequestParamSingle.Mode.Read);
            requestParam.setResourcePath(resourcePath);
            requestParam.setOptions(options);

            requestParam.getCustomHeaders().putAll(options);

            return store.scheduleRequest(requestParam, listener);
        }
    }*/
    public static RetailerBean getRetailerApprovalDetails(ODataRequestExecution oDataRequestExecution,RetailerBean retailerBean) throws Exception {

        ODataProperty property;
        ODataPropMap properties;
        try {
            ODataResponseSingle oDataResponseSingle = (ODataResponseSingleDefaultImpl) oDataRequestExecution.getResponse();
            ODataEntity entity = (ODataEntity) oDataResponseSingle.getPayload();
            retailerBean = new RetailerBean();
            properties = entity.getProperties();
            property = properties.get(Constants.OwnerName);
            retailerBean.setOwnerName((String) property.getValue());

            property = properties.get(Constants.EmailID);
            if (property != null)
                retailerBean.setEmailId((String) property.getValue());

            property = properties.get(Constants.MobileNo);
            retailerBean.setMobileNumber((String) property.getValue());

            property = properties.get(Constants.WeeklyOffDesc);
            retailerBean.setWeeklyOffDesc((String) property.getValue());

            property = properties.get(Constants.Tax1);
            retailerBean.setTax1((String) property.getValue());

            String strAddress = SOUtils.getCustomerDetailsAddressValue(properties);
            retailerBean.setAddress1(strAddress);

//            property = properties.get(Constants.NetAmount);
//            String invAmtStr = "0";
//            if (property != null) {
//                BigDecimal mStrAmount = (BigDecimal) property.getValue();
//                invAmtStr = mStrAmount.toString();
//            }
//            retailerBean.setNetAmount(invAmtStr);

            property = properties.get(Constants.Latitude);
            BigDecimal mDecimalLatitude = (BigDecimal) property.getValue();  //---------> Decimal property
            double mDouLatVal;
            try {

                if (mDecimalLatitude != null) {
                    mDouLatVal = mDecimalLatitude.doubleValue();
                } else {
                    mDouLatVal = 0.0;
                }
            } catch (Exception e) {
                mDouLatVal = 0.0;
            }
            retailerBean.setLatVal(mDouLatVal);

            property = properties.get(Constants.Longitude);
            BigDecimal mDecimalLongitude = (BigDecimal) property.getValue();  //---------> Decimal property

            double mDouLongVal;

            try {
                if (mDecimalLongitude != null) {

                    mDouLongVal = mDecimalLongitude.doubleValue();
                } else {
                    mDouLongVal = 0.0;
                }
            } catch (Exception e) {
                mDouLongVal = 0.0;
            }
            retailerBean.setLongVal(mDouLongVal);

            String mStrLandmark;
            property = properties.get(Constants.Landmark);
            mStrLandmark = property.getValue() != null ? (String) property.getValue() : "";
            retailerBean.setLandMark(mStrLandmark);


            ODataNavigationProperty soItemDetailsProp = entity.getNavigationProperty(Constants.CPDMSDivisions);
            ODataEntitySet feed = (ODataEntitySet) soItemDetailsProp.getNavigationContent();
            List<ODataEntity> entities = feed.getEntities();
            for (ODataEntity soItemEntity : entities) {
                properties = soItemEntity.getProperties();
                property = properties.get(Constants.ParentName);
                retailerBean.setParentName(property.getValue().toString());

                property = properties.get(Constants.Group4Desc);
                retailerBean.setClassification(property.getValue().toString());
                property = properties.get(Constants.Group3Desc);
                retailerBean.setCPTypeDesc(property.getValue().toString());

                property = properties.get(Constants.Currency);
                retailerBean.setCurrency(property.getValue().toString());
            }
            return retailerBean;


        } catch (Exception e) {
            e.printStackTrace();
            throw new OnlineODataStoreException(e.getMessage());
        }
    }

    public static void updateTasksEntity(Hashtable<String, String> table, UIListener uiListener) throws com.arteriatech.mutils.common.OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createTaskEntity(store, table);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Update.getValue(), uiListener);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Update);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);

                final Map<String, String> createHeaders = new HashMap<>();
            *//*    if (!TextUtils.isEmpty(sessionId)) {
                    createHeaders.put(Constants.arteria_session_header, sessionId);
                }*//*
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new com.arteriatech.mutils.common.OnlineODataStoreException(e);
            }
        }*/

    }
    /*private static ODataEntity createTaskEntity(OnlineODataStore store, Hashtable<String, String> hashtable) throws ODataParserException {
        ODataEntity headerEntity = null;
//        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                // CreateOperation the parent Entity
                headerEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store) + "" + Constants.SOS_SO_TASK_ENTITY);

                headerEntity.setResourcePath(Constants.Tasks + "(InstanceID='" + hashtable.get(Constants.InstanceID) + "',EntityType='CP')", Constants.Tasks + "(InstanceID='" + hashtable.get(Constants.InstanceID) + "',EntityType='CP')");


                try {
                    store.allocateProperties(headerEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }

                store.allocateNavigationProperties(headerEntity);

                headerEntity.getProperties().put(Constants.InstanceID,
                        new ODataPropertyDefaultImpl(Constants.InstanceID, hashtable.get(Constants.InstanceID)));
                headerEntity.getProperties().put(Constants.EntityType,
                        new ODataPropertyDefaultImpl(Constants.EntityType, hashtable.get(Constants.EntityType)));
                headerEntity.getProperties().put(Constants.DecisionKey,
                        new ODataPropertyDefaultImpl(Constants.DecisionKey, hashtable.get(Constants.DecisionKey)));

                headerEntity.getProperties().put(Constants.LoginID,
                        new ODataPropertyDefaultImpl(Constants.LoginID, hashtable.get(Constants.LoginID)));
                headerEntity.getProperties().put(Constants.EntityKey,
                        new ODataPropertyDefaultImpl(Constants.EntityKey, hashtable.get(Constants.EntityKey)));

                headerEntity.getProperties().put(Constants.Comments,
                        new ODataPropertyDefaultImpl(Constants.Comments, hashtable.get(Constants.Comments)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerEntity;

    }
*/
    public static void createSSInvoiceEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createSSInvoiceCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        //END

    }

    /*private static ODataEntity createSSInvoiceCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store)+""+Constants.InvoiceEntity);

                newHeaderEntity.setResourcePath(Constants.SSINVOICES, Constants.SSINVOICES);

                try {
                    store.allocateProperties(newHeaderEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.InvoiceGUID,
                        new ODataPropertyDefaultImpl(Constants.InvoiceGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.InvoiceGUID))));

                try {
                    newHeaderEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
          *//*      newHeaderEntity.getProperties().put(Constants.LoginID,
                        new ODataPropertyDefaultImpl(Constants.LoginID, hashtable.get(Constants.LoginID)));*//*
                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID)));
                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));

                newHeaderEntity.getProperties().put(Constants.DmsDivision,
                        new ODataPropertyDefaultImpl(Constants.DmsDivision, hashtable.get(Constants.DmsDivision)));
//                newHeaderEntity.getProperties().put(Constants.DmsDivisionDesc,
//                        new ODataPropertyDefaultImpl(Constants.DmsDivisionDesc, hashtable.get(Constants.DmsDivisionDesc)));

                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
//
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.SPName,
                        new ODataPropertyDefaultImpl(Constants.SPName, hashtable.get(Constants.SPName)));
                newHeaderEntity.getProperties().put(Constants.InvoiceNo,
                        new ODataPropertyDefaultImpl(Constants.InvoiceNo, hashtable.get(Constants.InvoiceNo)));
                newHeaderEntity.getProperties().put(Constants.InvoiceTypeID,
                        new ODataPropertyDefaultImpl(Constants.InvoiceTypeID, hashtable.get(Constants.InvoiceTypeID)));
                newHeaderEntity.getProperties().put(Constants.InvoiceTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.InvoiceTypeDesc, hashtable.get(Constants.InvoiceTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.InvoiceDate,
                        new ODataPropertyDefaultImpl(Constants.InvoiceDate, UtilConstants.convertDateFormat(hashtable.get(Constants.InvoiceDate))));

                newHeaderEntity.getProperties().put(Constants.PONo,
                        new ODataPropertyDefaultImpl(Constants.PONo, hashtable.get(Constants.PONo)));
                newHeaderEntity.getProperties().put(Constants.PODate,
                        new ODataPropertyDefaultImpl(Constants.PODate, UtilConstants.convertDateFormat(hashtable.get(Constants.PODate))));


                newHeaderEntity.getProperties().put(Constants.BillToGuid,
                        new ODataPropertyDefaultImpl(Constants.BillToGuid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BillToGuid))));
                newHeaderEntity.getProperties().put(Constants.ShipToCPGUID,
                        new ODataPropertyDefaultImpl(Constants.ShipToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.ShipToCPGUID))));
                newHeaderEntity.getProperties().put(Constants.SoldToCPGUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SoldToCPGUID))));
                newHeaderEntity.getProperties().put(Constants.Source,
                        new ODataPropertyDefaultImpl(Constants.Source, hashtable.get(Constants.Source)));

                newHeaderEntity.getProperties().put(Constants.SoldToID,
                        new ODataPropertyDefaultImpl(Constants.SoldToID, hashtable.get(Constants.SoldToID)));
                newHeaderEntity.getProperties().put(Constants.SoldToName,
                        new ODataPropertyDefaultImpl(Constants.SoldToName, hashtable.get(Constants.SoldToName)));
                newHeaderEntity.getProperties().put(Constants.SoldToTypeID,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypeID, hashtable.get(Constants.SoldToTypeID)));
                newHeaderEntity.getProperties().put(Constants.SoldToTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypeDesc, hashtable.get(Constants.SoldToTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));

                try {
                    if (!hashtable.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                        newHeaderEntity.getProperties().put(Constants.BeatGUID,
                                new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGUID))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store)+""+Constants.InvoiceItemEntity);

                    newItemEntity.setResourcePath(Constants.SSInvoiceItemDetails + "(" + incremntVal + ")", Constants.SSInvoiceItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, ODataStore.PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.InvoiceItemGUID,
                            new ODataPropertyDefaultImpl(Constants.InvoiceItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.InvoiceItemGUID))));

                    newItemEntity.getProperties().put(Constants.InvoiceGUID,
                            new ODataPropertyDefaultImpl(Constants.InvoiceGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.InvoiceGUID))));

                    newItemEntity.getProperties().put(Constants.StockGuid,
                            new ODataPropertyDefaultImpl(Constants.StockGuid, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.StockGuid))));

                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));

                    newItemEntity.getProperties().put(Constants.InvoiceNo,
                            new ODataPropertyDefaultImpl(Constants.InvoiceNo, singleRow.get(Constants.InvoiceNo)));

                    newItemEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));

                    newItemEntity.getProperties().put(Constants.Quantity,
                            new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));


                    newItemEntity.getProperties().put(Constants.MaterialNo,
                            new ODataPropertyDefaultImpl(Constants.MaterialNo, singleRow.get(Constants.MaterialNo)));

                    newItemEntity.getProperties().put(Constants.MaterialDesc,
                            new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));

                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                    newItemEntity.getProperties().put(Constants.InvoiceDate,
                            new ODataPropertyDefaultImpl(Constants.InvoiceDate, UtilConstants.convertDateFormat(singleRow.get(Constants.InvoiceDate))));

                    try {
                        if (!singleRow.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                            newItemEntity.getProperties().put(Constants.BeatGUID,
                                    new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.BeatGUID))));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    tempArray.add(incrementVal, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SSInvoiceItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SSInvoiceItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SSInvoiceItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }
*/
    public static void createCP(Hashtable<String, String> tableHdr, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
       /* OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity cpEntity = createCPEntity(tableHdr, itemtable, store);

                OnlineRequestListener CPListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);


                String cpGUID32 = tableHdr.get(Constants.CPGUID).replace("-", "");

                String mStrCreatedOn = tableHdr.get(Constants.CreatedOn);
                String mStrCreatedAt = tableHdr.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(mStrCreatedOn) + "T" + UtilConstants.convertTimeOnly(mStrCreatedAt);


                Map<String, String> collHeaders = new HashMap<String, String>();
                collHeaders.put(Constants.RequestID, cpGUID32);
                collHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle cpReq = new ODataRequestParamSingleDefaultImpl();
                cpReq.setMode(ODataRequestParamSingle.Mode.Create);
                cpReq.setResourcePath(cpEntity.getResourcePath());
                cpReq.setPayload(cpEntity);
                cpReq.getCustomHeaders().putAll(collHeaders);

                store.scheduleRequest(cpReq, CPListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        //END

    }
   /* private static ODataEntity createCPEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity headerEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempCPPartnerArray = new ArrayList();
        ArrayList<String> mALCPFunctionID = new ArrayList<>();
        mALCPFunctionID.add("01");
        mALCPFunctionID.add("02");
        try {
            if (hashtable != null) {
                // CreateOperation the parent Entity
                headerEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store)+""+Constants.ChannelPartnerEntity);
                headerEntity.setResourcePath(Constants.ChannelPartners, Constants.ChannelPartners);
                try {
                    store.allocateProperties(headerEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                store.allocateNavigationProperties(headerEntity);

                headerEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID).toUpperCase())));
                headerEntity.getProperties().put(Constants.Address1,
                        new ODataPropertyDefaultImpl(Constants.Address1, hashtable.get(Constants.Address1)));
                headerEntity.getProperties().put(Constants.Address2,
                        new ODataPropertyDefaultImpl(Constants.Address2, hashtable.get(Constants.Address2)));
                headerEntity.getProperties().put(Constants.Address3,
                        new ODataPropertyDefaultImpl(Constants.Address3, hashtable.get(Constants.Address3)));
                headerEntity.getProperties().put(Constants.Address4,
                        new ODataPropertyDefaultImpl(Constants.Address4, hashtable.get(Constants.Address4)));
                headerEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));
                headerEntity.getProperties().put(Constants.Country,
                        new ODataPropertyDefaultImpl(Constants.Country, hashtable.get(Constants.Country)));
                headerEntity.getProperties().put(Constants.DistrictDesc,
                        new ODataPropertyDefaultImpl(Constants.DistrictDesc, hashtable.get(Constants.DistrictDesc)));
                headerEntity.getProperties().put(Constants.DistrictID,
                        new ODataPropertyDefaultImpl(Constants.DistrictID, hashtable.get(Constants.DistrictID)));
                headerEntity.getProperties().put(Constants.StateID,
                        new ODataPropertyDefaultImpl(Constants.StateID, hashtable.get(Constants.StateID)));
                headerEntity.getProperties().put(Constants.StateDesc,
                        new ODataPropertyDefaultImpl(Constants.StateDesc, hashtable.get(Constants.StateDesc)));
                headerEntity.getProperties().put(Constants.CityID,
                        new ODataPropertyDefaultImpl(Constants.CityID, hashtable.get(Constants.CityID)));
                headerEntity.getProperties().put(Constants.CityDesc,
                        new ODataPropertyDefaultImpl(Constants.CityDesc, hashtable.get(Constants.CityDesc)));
                headerEntity.getProperties().put(Constants.Landmark,
                        new ODataPropertyDefaultImpl(Constants.Landmark, hashtable.get(Constants.Landmark)));
                headerEntity.getProperties().put(Constants.PostalCode,
                        new ODataPropertyDefaultImpl(Constants.PostalCode, hashtable.get(Constants.PostalCode)));
                headerEntity.getProperties().put(Constants.MobileNo,
                        new ODataPropertyDefaultImpl(Constants.MobileNo, hashtable.get(Constants.MobileNo)));
                headerEntity.getProperties().put(Constants.Mobile2,
                        new ODataPropertyDefaultImpl(Constants.Mobile2, hashtable.get(Constants.Mobile2)));
                headerEntity.getProperties().put(Constants.Landline,
                        new ODataPropertyDefaultImpl(Constants.Landline, hashtable.get(Constants.Landline)));
                headerEntity.getProperties().put(Constants.EmailID,
                        new ODataPropertyDefaultImpl(Constants.EmailID, hashtable.get(Constants.EmailID)));
                headerEntity.getProperties().put(Constants.PAN,
                        new ODataPropertyDefaultImpl(Constants.PAN, hashtable.get(Constants.PAN)));
                headerEntity.getProperties().put(Constants.VATNo,
                        new ODataPropertyDefaultImpl(Constants.VATNo, hashtable.get(Constants.VATNo)));
                headerEntity.getProperties().put(Constants.OutletName,
                        new ODataPropertyDefaultImpl(Constants.OutletName, hashtable.get(Constants.OutletName)));
                headerEntity.getProperties().put(Constants.OwnerName,
                        new ODataPropertyDefaultImpl(Constants.OwnerName, hashtable.get(Constants.OwnerName)));
                headerEntity.getProperties().put(Constants.RetailerProfile,
                        new ODataPropertyDefaultImpl(Constants.RetailerProfile, hashtable.get(Constants.RetailerProfile)));

                try {
                    headerEntity.getProperties().put(Constants.Source,
                            new ODataPropertyDefaultImpl(Constants.Source, Constants.Mobile));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if(!hashtable.get(Constants.DOB).equalsIgnoreCase("")) {
                        headerEntity.getProperties().put(Constants.DOB,
                                new ODataPropertyDefaultImpl(Constants.DOB, UtilConstants.convertDateFormat(hashtable.get(Constants.DOB))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Double mDouLatitude = 0.0;
                    try {
                        mDouLatitude = UtilConstants.round(Double.parseDouble(hashtable.get(Constants.Latitude)),12);
                    } catch (NumberFormatException e) {
                        mDouLatitude = 0.0;
                        e.printStackTrace();
                    }

                    Double mDouLongitude = 0.0;
                    try {
                        mDouLongitude = UtilConstants.round(Double.parseDouble(hashtable.get(Constants.Longitude)),12);
                    } catch (NumberFormatException e) {
                        mDouLongitude = 0.0;
                        e.printStackTrace();
                    }

                    headerEntity.getProperties().put(Constants.Latitude,
                            new ODataPropertyDefaultImpl(Constants.Latitude, BigDecimal.valueOf(mDouLatitude)));
                    headerEntity.getProperties().put(Constants.Longitude,
                            new ODataPropertyDefaultImpl(Constants.Longitude, BigDecimal.valueOf(mDouLongitude)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    if(!hashtable.get(Constants.Anniversary).equalsIgnoreCase("")) {
                        headerEntity.getProperties().put(Constants.Anniversary,
                                new ODataPropertyDefaultImpl(Constants.Anniversary, UtilConstants.convertDateFormat(hashtable.get(Constants.Anniversary))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    headerEntity.getProperties().put(Constants.PartnerMgrGUID,
                            new ODataPropertyDefaultImpl(Constants.PartnerMgrGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.PartnerMgrGUID).toUpperCase())));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                headerEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
                headerEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));

                headerEntity.getProperties().put(Constants.RouteID,
                        new ODataPropertyDefaultImpl(Constants.RouteID, hashtable.get(Constants.RouteID)));
                headerEntity.getProperties().put(Constants.ParentID,
                        new ODataPropertyDefaultImpl(Constants.ParentID, hashtable.get(Constants.ParentID)));
                headerEntity.getProperties().put(Constants.ParentTypeID,
                        new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));
                headerEntity.getProperties().put(Constants.ParentName,
                        new ODataPropertyDefaultImpl(Constants.ParentName, hashtable.get(Constants.ParentName)));

                headerEntity.getProperties().put(Constants.WeeklyOff,
                        new ODataPropertyDefaultImpl(Constants.WeeklyOff, hashtable.get(Constants.WeeklyOff)));
                headerEntity.getProperties().put(Constants.Tax1,
                        new ODataPropertyDefaultImpl(Constants.Tax1, hashtable.get(Constants.Tax1)));
                headerEntity.getProperties().put(Constants.Tax2,
                        new ODataPropertyDefaultImpl(Constants.Tax2, hashtable.get(Constants.Tax2)));
                headerEntity.getProperties().put(Constants.Tax3,
                        new ODataPropertyDefaultImpl(Constants.Tax3, hashtable.get(Constants.Tax3)));
                headerEntity.getProperties().put(Constants.Tax4,
                        new ODataPropertyDefaultImpl(Constants.Tax4, hashtable.get(Constants.Tax4)));
                headerEntity.getProperties().put(Constants.CPUID,
                        new ODataPropertyDefaultImpl(Constants.CPUID, hashtable.get(Constants.CPUID)));
                headerEntity.getProperties().put(Constants.TaxRegStatus,
                        new ODataPropertyDefaultImpl(Constants.TaxRegStatus, hashtable.get(Constants.TaxRegStatus)));
                headerEntity.getProperties().put(Constants.IsKeyCP,
                        new ODataPropertyDefaultImpl(Constants.IsKeyCP, hashtable.get(Constants.IsKeyCP)));

                headerEntity.getProperties().put(Constants.ID1,
                        new ODataPropertyDefaultImpl(Constants.ID1, hashtable.get(Constants.ID1)));
                headerEntity.getProperties().put(Constants.ID2,
                        new ODataPropertyDefaultImpl(Constants.ID2, hashtable.get(Constants.ID2)));
                headerEntity.getProperties().put(Constants.BusinessID1,
                        new ODataPropertyDefaultImpl(Constants.BusinessID1, hashtable.get(Constants.BusinessID1)));
                headerEntity.getProperties().put(Constants.BusinessID2,
                        new ODataPropertyDefaultImpl(Constants.BusinessID2, hashtable.get(Constants.BusinessID2)));

                // CreateOperation the item Entity



                for (int i = 0; i < itemhashtable.size(); i++) {
                    ODataEntity itemEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store)+""+Constants.CPDMSDivisionEntity);
                    try {
                        store.allocateProperties(itemEntity, ODataStore.PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }

                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    try {
                        store.allocateProperties(itemEntity, ODataStore.PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    itemEntity.getProperties().put(Constants.CPGUID,
                            new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID).toUpperCase())));
                    itemEntity.getProperties().put(Constants.CP1GUID,
                            new ODataPropertyDefaultImpl(Constants.CP1GUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.CP1GUID).toString().toUpperCase())));

                    try {
                        itemEntity.getProperties().put(Constants.PartnerMgrGUID,
                                new ODataPropertyDefaultImpl(Constants.PartnerMgrGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.PartnerMgrGUID).toUpperCase())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        itemEntity.getProperties().put(Constants.RouteGUID,
                                new ODataPropertyDefaultImpl(Constants.RouteGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.RouteGUID).toUpperCase())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    itemEntity.getProperties().put(Constants.DMSDivision,
                            new ODataPropertyDefaultImpl(Constants.DMSDivision, singleRow.get(Constants.DMSDivision)));
                    itemEntity.getProperties().put(Constants.StatusID,
                            new ODataPropertyDefaultImpl(Constants.StatusID, singleRow.get(Constants.StatusID)));
                    itemEntity.getProperties().put(Constants.PartnerMgrNo,
                            new ODataPropertyDefaultImpl(Constants.PartnerMgrNo, singleRow.get(Constants.PartnerMgrNo)));
                    itemEntity.getProperties().put(Constants.ParentID,
                            new ODataPropertyDefaultImpl(Constants.ParentID, singleRow.get(Constants.ParentID)));
                    itemEntity.getProperties().put(Constants.ParentTypeID,
                            new ODataPropertyDefaultImpl(Constants.ParentTypeID, singleRow.get(Constants.ParentTypeID)));
                    itemEntity.getProperties().put(Constants.ParentName,
                            new ODataPropertyDefaultImpl(Constants.ParentName, singleRow.get(Constants.ParentName)));
                    itemEntity.getProperties().put(Constants.Group1,
                            new ODataPropertyDefaultImpl(Constants.Group1, singleRow.get(Constants.Group1)));
                    itemEntity.getProperties().put(Constants.Group2,
                            new ODataPropertyDefaultImpl(Constants.Group2, singleRow.get(Constants.Group2)));
                    itemEntity.getProperties().put(Constants.Group3,
                            new ODataPropertyDefaultImpl(Constants.Group3, singleRow.get(Constants.Group3)));
                    itemEntity.getProperties().put(Constants.Group4,
                            new ODataPropertyDefaultImpl(Constants.Group4, singleRow.get(Constants.Group4)));
                    itemEntity.getProperties().put(Constants.Group5,
                            new ODataPropertyDefaultImpl(Constants.Group5, singleRow.get(Constants.Group5)));
                    itemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));
                    itemEntity.getProperties().put(Constants.RouteID,
                            new ODataPropertyDefaultImpl(Constants.RouteID, singleRow.get(Constants.RouteID)));
                    itemEntity.getProperties().put(Constants.RouteDesc,
                            new ODataPropertyDefaultImpl(Constants.RouteDesc, singleRow.get(Constants.RouteDesc)));

                    try {
                        itemEntity.getProperties().put(Constants.DiscountPer,
                                new ODataPropertyDefaultImpl(Constants.DiscountPer, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.DiscountPer)))));
                    } catch (NumberFormatException e) {
                        itemEntity.getProperties().put(Constants.DiscountPer,
                                new ODataPropertyDefaultImpl(Constants.DiscountPer, BigDecimal.valueOf(Double.parseDouble("0.00"))));
                        e.printStackTrace();
                    }
                    try {
                        itemEntity.getProperties().put(Constants.CreditLimit,
                                new ODataPropertyDefaultImpl(Constants.CreditLimit, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CreditLimit)))));
                    } catch (NumberFormatException e) {
                        itemEntity.getProperties().put(Constants.CreditLimit,
                                new ODataPropertyDefaultImpl(Constants.CreditLimit, BigDecimal.valueOf(Double.parseDouble("0.00"))));
                        e.printStackTrace();
                    }
                    try {
                        itemEntity.getProperties().put(Constants.CreditDays,
                                new ODataPropertyDefaultImpl(Constants.CreditDays, Integer.valueOf(Integer.parseInt(singleRow.get(Constants.CreditDays)))));
                    } catch (NumberFormatException e) {
                        itemEntity.getProperties().put(Constants.CreditDays,
                                new ODataPropertyDefaultImpl(Constants.CreditDays, Integer.valueOf(Integer.parseInt("0"))));
                        e.printStackTrace();
                    }
                    try {
                        itemEntity.getProperties().put(Constants.CreditBills,
                                new ODataPropertyDefaultImpl(Constants.CreditBills, Integer.valueOf(Integer.parseInt(singleRow.get(Constants.CreditBills)))));
                    } catch (NumberFormatException e) {
                        itemEntity.getProperties().put(Constants.CreditBills,
                                new ODataPropertyDefaultImpl(Constants.CreditBills, Integer.valueOf(Integer.parseInt("0"))));
                        e.printStackTrace();
                    }

                    tempArray.add(i, itemEntity);
                }

                ODataEntitySetDefaultImpl itmEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itmEntity.getEntities().add(entity);
                }
                itmEntity.setResourcePath(Constants.CPDMSDivisions);


                ODataNavigationProperty navProp = headerEntity.getNavigationProperty(Constants.CPDMSDivisions);
                navProp.setNavigationContent(itmEntity);
                headerEntity.setNavigationProperty(Constants.CPDMSDivisions, navProp);


                try {

                    for (int i = 0; i < mALCPFunctionID.size(); i++) {
                        ODataEntity itemCPPartnerEntity = new ODataEntityDefaultImpl(UtilConstants.getNameSpaceOnline(store)+""+Constants.CPPartnerFunctionEntity);
                        try {
                            store.allocateProperties(itemCPPartnerEntity, ODataStore.PropMode.All);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }

                        String cpID= mALCPFunctionID.get(i);
                        try {
                            store.allocateProperties(itemCPPartnerEntity, ODataStore.PropMode.All);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }
                        itemCPPartnerEntity.getProperties().put(Constants.CPGUID,
                                new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID).toUpperCase())));

                        GUID guidItem = GUID.newRandom();
                        itemCPPartnerEntity.getProperties().put(Constants.PFGUID,
                                new ODataPropertyDefaultImpl(Constants.PFGUID, ODataGuidDefaultImpl.initWithString32(guidItem.toString().toUpperCase())));

                        itemCPPartnerEntity.getProperties().put(Constants.PartnerFunction,
                                new ODataPropertyDefaultImpl(Constants.PartnerFunction, cpID));

                        itemCPPartnerEntity.getProperties().put(Constants.PartnarName,
                                new ODataPropertyDefaultImpl(Constants.PartnarName, hashtable.get(Constants.OutletName)));

                        itemCPPartnerEntity.getProperties().put(Constants.PartnerMobileNo,
                                new ODataPropertyDefaultImpl(Constants.PartnerMobileNo,hashtable.get(Constants.MobileNo)));

                        try {
                            itemCPPartnerEntity.getProperties().put(Constants.PartnarCPGUID,
                                    new ODataPropertyDefaultImpl(Constants.PartnarCPGUID, hashtable.get(Constants.CPGUID).replace("-","").toUpperCase()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        itemCPPartnerEntity.getProperties().put(Constants.StatusID,
                                new ODataPropertyDefaultImpl(Constants.StatusID, Constants.str_01));

                        tempCPPartnerArray.add(i, itemCPPartnerEntity);
                    }

                    ODataEntitySetDefaultImpl itmCPPartnerEntity = new ODataEntitySetDefaultImpl(tempCPPartnerArray.size(), null, null);
                    for (ODataEntity entity : tempCPPartnerArray) {
                        itmCPPartnerEntity.getEntities().add(entity);
                    }
                    itmCPPartnerEntity.setResourcePath(Constants.CPPartnerFunctions);


                    ODataNavigationProperty navPropCPPartner = headerEntity.getNavigationProperty(Constants.CPPartnerFunctions);
                    navPropCPPartner.setNavigationContent(itmCPPartnerEntity);
                    headerEntity.setNavigationProperty(Constants.CPPartnerFunctions, navPropCPPartner);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerEntity;

    }
*/    /**
     * Create Entity for collection creation and Schedule in Online Manager
     *
     * @throws OnlineODataStoreException
     */
    public static void createROEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createROCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String ssoGUID32 = table.get(Constants.SSROGUID).replace("-", "");

                String soCreatedOn = table.get(Constants.CreatedOn);
                String soCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(soCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(soCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, ssoGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/
        //END

    }
    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    /*private static ODataEntity createROCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store)+""+Constants.ReturnOrderEntity);

                newHeaderEntity.setResourcePath(Constants.SSROs, Constants.SSROs);

                try {
                    store.allocateProperties(newHeaderEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.SSROGUID,
                        new ODataPropertyDefaultImpl(Constants.SSROGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SSROGUID))));

                *//*try {
                    newHeaderEntity.getProperties().put(Constants.BeatGuid,
                            new ODataPropertyDefaultImpl(Constants.BeatGuid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGuid))));
                } catch (Exception e) {
                    e.printStackTrace();
                }*//*
//                newHeaderEntity.getProperties().put(Constants.OrderNo,
//                        new ODataPropertyDefaultImpl(Constants.OrderNo, hashtable.get(Constants.OrderNo)));
                newHeaderEntity.getProperties().put(Constants.OrderType,
                        new ODataPropertyDefaultImpl(Constants.OrderType, hashtable.get(Constants.OrderType)));
                newHeaderEntity.getProperties().put(Constants.OrderTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.OrderTypeDesc, hashtable.get(Constants.OrderTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));
                if(!TextUtils.isEmpty(hashtable.get(Constants.DmsDivision))) {
                    newHeaderEntity.getProperties().put(Constants.DmsDivision,
                            new ODataPropertyDefaultImpl(Constants.DmsDivision, hashtable.get(Constants.DmsDivision)));
                }
//                newHeaderEntity.getProperties().put(Constants.DmsDivisionDesc,
//                        new ODataPropertyDefaultImpl(Constants.DmsDivisionDesc, hashtable.get(Constants.DmsDivisionDesc)));

                newHeaderEntity.getProperties().put(Constants.FromCPGUID,
                        new ODataPropertyDefaultImpl(Constants.FromCPGUID, hashtable.get(Constants.FromCPGUID).replace("-", "")));
//                newHeaderEntity.getProperties().put(Constants.FromCPNo,
//                        new ODataPropertyDefaultImpl(Constants.FromCPNo, hashtable.get(Constants.FromCPNo)));
                newHeaderEntity.getProperties().put(Constants.FromCPName,
                        new ODataPropertyDefaultImpl(Constants.FromCPName, hashtable.get(Constants.FromCPName)));
//                newHeaderEntity.getProperties().put("FromCPTypID",
//                        new ODataPropertyDefaultImpl("FromCPTypID", hashtable.get(Constants.FromCPTypId)));
                newHeaderEntity.getProperties().put(Constants.FromCPTypDs,
                        new ODataPropertyDefaultImpl(Constants.FromCPTypDs, hashtable.get(Constants.FromCPTypId)));


                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));
                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));


                newHeaderEntity.getProperties().put(Constants.SoldToCPGUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SoldToCPGUID))));
                newHeaderEntity.getProperties().put(Constants.SoldToID,
                        new ODataPropertyDefaultImpl(Constants.SoldToID, hashtable.get(Constants.SoldToID)));
      *//*          newHeaderEntity.getProperties().put(Constants.SoldToUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToUID, hashtable.get(Constants.SoldToUID)));*//*
                newHeaderEntity.getProperties().put(Constants.SoldToDesc,
                        new ODataPropertyDefaultImpl(Constants.SoldToDesc, hashtable.get(Constants.SoldToDesc)));

                newHeaderEntity.getProperties().put(Constants.SoldToTypeID,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypeID, hashtable.get(Constants.SoldToTypeID)));
                newHeaderEntity.getProperties().put(Constants.SoldToTypDs,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypDs, hashtable.get(Constants.SoldToTypDesc)));


                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));


                newHeaderEntity.getProperties().put(Constants.SPGUID,
                        new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.FirstName,
                        new ODataPropertyDefaultImpl(Constants.FirstName, hashtable.get(Constants.FirstName)));
             *//*   newHeaderEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*//*
                newHeaderEntity.getProperties().put(Constants.StatusID,
                        new ODataPropertyDefaultImpl(Constants.StatusID, hashtable.get(Constants.StatusID)));
                newHeaderEntity.getProperties().put(Constants.ApprovalStatusID,
                        new ODataPropertyDefaultImpl(Constants.ApprovalStatusID, hashtable.get(Constants.ApprovalStatusID)));

                newHeaderEntity.getProperties().put(Constants.TestRun,
                        new ODataPropertyDefaultImpl(Constants.TestRun, hashtable.get(Constants.TestRun)));


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store)+""+Constants.ReturnOrderItemEntity);

                    newItemEntity.setResourcePath(Constants.SSROItemDetails + "(" + incremntVal + ")", Constants.SSROItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, ODataStore.PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.SSROItemGUID,
                            new ODataPropertyDefaultImpl(Constants.SSROItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSROItemGUID))));
                    try {
                        newItemEntity.getProperties().put(Constants.StockRefGUID,
                                new ODataPropertyDefaultImpl(Constants.StockRefGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.StockRefGUID))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    newItemEntity.getProperties().put(Constants.SSROGUID,
                            new ODataPropertyDefaultImpl(Constants.SSROGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSROGUID))));

                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));


                    newItemEntity.getProperties().put(Constants.MaterialNo,
                            new ODataPropertyDefaultImpl(Constants.MaterialNo, singleRow.get(Constants.MaterialNo)));

                    newItemEntity.getProperties().put(Constants.MaterialDesc,
                            new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                    newItemEntity.getProperties().put(Constants.OrdMatGrp,
                            new ODataPropertyDefaultImpl(Constants.OrdMatGrp, singleRow.get(Constants.OrdMatGrp)));
//
                    newItemEntity.getProperties().put(Constants.OrdMatGrpDesc,
                            new ODataPropertyDefaultImpl(Constants.OrdMatGrpDesc, singleRow.get(Constants.OrdMatGrpDesc)));
//
                    newItemEntity.getProperties().put(Constants.Quantity,
                            new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));
                    try {
                        newItemEntity.getProperties().put(Constants.MRP,
                                new ODataPropertyDefaultImpl(Constants.MRP, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.MRP)))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        newItemEntity.getProperties().put(Constants.UnitPrice,
                                new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.UnitPrice)))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));
                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.Uom)));

                    newItemEntity.getProperties().put(Constants.Batch,
                            new ODataPropertyDefaultImpl(Constants.Batch, singleRow.get(Constants.Batch)));

                    try {
                        newItemEntity.getProperties().put(Constants.RefDocNo,
                                new ODataPropertyDefaultImpl(Constants.RefDocNo, singleRow.get(Constants.RefDocNo)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    newItemEntity.getProperties().put(Constants.RejectionReasonID,
                            new ODataPropertyDefaultImpl(Constants.RejectionReasonID, singleRow.get(Constants.RejectionReasonID)));

                    newItemEntity.getProperties().put(Constants.RejectionReasonDesc,
                            new ODataPropertyDefaultImpl(Constants.RejectionReasonDesc, singleRow.get(Constants.RejectionReasonDesc)));

                    try {
                        newItemEntity.getProperties().put(Constants.RefdocItmGUID,
                                new ODataPropertyDefaultImpl(Constants.RefdocItmGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.RefdocItmGUID))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tempArray.add(incrementVal, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SSROItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SSROItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SSROItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }*/
    /*create daily expense*/
    public static void createDailyExpense(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        /*OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                ODataEntity soCreateEntity = createDailyExpenseCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
//                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }*/

    }

    /*entity for expense */
    /*public static ODataEntity createDailyExpenseCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataEntity newItemImageEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> docmentArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store)+""+Constants.ExpenseEntity);

                newHeaderEntity.setResourcePath(Constants.Expenses, Constants.Expenses);

                try {
                    store.allocateProperties(newHeaderEntity, ODataStore.PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.ExpenseGUID,
                        new ODataPropertyDefaultImpl(Constants.ExpenseGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.ExpenseGUID))));
//                newHeaderEntity.getProperties().put(Constants.OrderNo,
//                        new ODataPropertyDefaultImpl(Constants.OrderNo, hashtable.get(Constants.OrderNo)));
                newHeaderEntity.getProperties().put(Constants.ExpenseNo,
                        new ODataPropertyDefaultImpl(Constants.ExpenseNo, hashtable.get(Constants.ExpenseNo)));
                newHeaderEntity.getProperties().put(Constants.FiscalYear,
                        new ODataPropertyDefaultImpl(Constants.FiscalYear, hashtable.get(Constants.FiscalYear)));
             *//*   newHeaderEntity.getProperties().put(Constants.LoginID,
                        new ODataPropertyDefaultImpl(Constants.LoginID, hashtable.get(Constants.LoginID)));*//*
               *//* newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));*//*

                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID)));
                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPType,
                        new ODataPropertyDefaultImpl(Constants.CPType, hashtable.get(Constants.CPType)));
                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));


                newHeaderEntity.getProperties().put(Constants.ExpenseType,
                        new ODataPropertyDefaultImpl(Constants.ExpenseType, hashtable.get(Constants.ExpenseType)));
                newHeaderEntity.getProperties().put(Constants.ExpenseTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.ExpenseTypeDesc, hashtable.get(Constants.ExpenseTypeDesc)));
                try {
                    newHeaderEntity.getProperties().put(Constants.ExpenseDate,
                            new ODataPropertyDefaultImpl(Constants.ExpenseDate, UtilConstants.convertDateFormat(hashtable.get(Constants.ExpenseDate))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.Status,
                        new ODataPropertyDefaultImpl(Constants.Status, hashtable.get(Constants.Status)));
                newHeaderEntity.getProperties().put(Constants.StatusDesc,
                        new ODataPropertyDefaultImpl(Constants.StatusDesc, hashtable.get(Constants.StatusDesc)));

                newHeaderEntity.getProperties().put(Constants.Amount,
                        new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Amount)))));
               *//* newHeaderEntity.getProperties().put(Constants.CreatedOn,
                        new ODataPropertyDefaultImpl(Constants.CreatedOn, hashtable.get(Constants.CreatedOn)));*//*
//                newHeaderEntity.getProperties().put(Constants.CreatedBy,
//                        new ODataPropertyDefaultImpl(Constants.CreatedBy, hashtable.get(Constants.CreatedBy)));


                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));


                newHeaderEntity.getProperties().put(Constants.SPGUID,
                        new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.SPName,
                        new ODataPropertyDefaultImpl(Constants.SPName, hashtable.get(Constants.SPName)));


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store)+""+Constants.ExpenseItemEntity);

                    newItemEntity.setResourcePath(Constants.ExpenseItemDetails + "(" + incremntVal + ")", Constants.ExpenseItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, ODataStore.PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    *//*try {
                        store.allocateProperties(newItemEntity, PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }*//*
                    //If available, it populates the navigation properties of an OData Entity
                    store.allocateNavigationProperties(newItemEntity);

                    newItemEntity.getProperties().put(Constants.ExpenseItemGUID,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.ExpenseItemGUID))));

                    newItemEntity.getProperties().put(Constants.ExpenseGUID,
                            new ODataPropertyDefaultImpl(Constants.ExpenseGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.ExpenseGUID))));

                    newItemEntity.getProperties().put(Constants.ExpeseItemNo,
                            new ODataPropertyDefaultImpl(Constants.ExpeseItemNo, singleRow.get(Constants.ExpeseItemNo)));


                    *//*newItemEntity.getProperties().put(Constants.LoginID,
                            new ODataPropertyDefaultImpl(Constants.LoginID, singleRow.get(Constants.LoginID)));*//*

//                    newItemEntity.getProperties().put(Constants.MaterialDesc,
//                            new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                    newItemEntity.getProperties().put(Constants.ExpenseItemType,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemType, singleRow.get(Constants.ExpenseItemType)));
//
                    newItemEntity.getProperties().put(Constants.ExpenseItemTypeDesc,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemTypeDesc, singleRow.get(Constants.ExpenseItemTypeDesc)));
                    if (!singleRow.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.BeatGUID,
                                new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.BeatGUID))));
                    }
                    newItemEntity.getProperties().put(Constants.Location,
                            new ODataPropertyDefaultImpl(Constants.Location, singleRow.get(Constants.Location)));
                    if(!singleRow.get(Constants.ConvenyanceMode).equals("")) {
                        newItemEntity.getProperties().put(Constants.ConvenyanceMode,
                                new ODataPropertyDefaultImpl(Constants.ConvenyanceMode, singleRow.get(Constants.ConvenyanceMode)));
                        newItemEntity.getProperties().put(Constants.ConvenyanceModeDs,
                                new ODataPropertyDefaultImpl(Constants.ConvenyanceModeDs, singleRow.get(Constants.ConvenyanceModeDs)));
                    }
                    if(!singleRow.get(Constants.BeatDistance).equals("")) {
                        newItemEntity.getProperties().put(Constants.BeatDistance,
                                new ODataPropertyDefaultImpl(Constants.BeatDistance, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.BeatDistance)))));
                    }
                    if (!singleRow.get(Constants.Amount).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.Amount,
                                new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Amount)))));
                    }
                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));
                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));
                    newItemEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));



                    *//*image document*//*
                    *//*String imageStringArray = singleRow.get("item_no" + incrementVal);
                    ArrayList<HashMap<String, String>> convertedImage = UtilConstants.convertToArrayListMap(imageStringArray);
                    int incremntImgVal = 0;
                    for (int incrementImgVal = 0; incrementImgVal < convertedImage.size(); incrementImgVal++) {
                        HashMap<String, String> singleImgRow = convertedImage.get(incrementImgVal);
                        incremntImgVal = incrementImgVal + 1;

                        newItemImageEntity = new ODataEntityDefaultImpl(Constants.ExpenseItemDocumentEntity);

                        newItemImageEntity.setResourcePath(Constants.ExpenseDocuments + "(" + incremntImgVal + ")", Constants.ExpenseDocuments + "(" + incremntImgVal + ")");
                        try {
                            store.allocateProperties(newItemImageEntity, PropMode.Keys);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }
                        newItemImageEntity.getProperties().put(Constants.ExpenseImgGUID,
                                new ODataPropertyDefaultImpl(Constants.ExpenseImgGUID, singleImgRow.get(Constants.ExpenseImgGUID)));

                        newItemImageEntity.getProperties().put(Constants.ExpenseItemGUID,
                                new ODataPropertyDefaultImpl(Constants.ExpenseItemGUID, ODataGuidDefaultImpl.initWithString32(singleImgRow.get(Constants.ExpenseItemGUID))));

                        newItemImageEntity.getProperties().put(Constants.LoginID,
                                new ODataPropertyDefaultImpl(Constants.LoginID, singleImgRow.get(Constants.LoginID)));

                        newItemImageEntity.getProperties().put(Constants.DocumentTypeID,
                                new ODataPropertyDefaultImpl(Constants.DocumentTypeID, singleImgRow.get(Constants.DocumentTypeID)));

                        newItemImageEntity.getProperties().put(Constants.DocumentTypeDesc,
                                new ODataPropertyDefaultImpl(Constants.DocumentTypeDesc, singleImgRow.get(Constants.DocumentTypeDesc)));

                        newItemImageEntity.getProperties().put(Constants.DocumentStatusID,
                                new ODataPropertyDefaultImpl(Constants.DocumentStatusID, singleImgRow.get(Constants.DocumentStatusID)));

                        newItemImageEntity.getProperties().put(Constants.DocumentStatusDesc,
                                new ODataPropertyDefaultImpl(Constants.DocumentStatusDesc, singleImgRow.get(Constants.DocumentStatusDesc)));

                        newItemImageEntity.getProperties().put(Constants.ValidFrom,
                                new ODataPropertyDefaultImpl(Constants.ValidFrom, UtilConstants.convertDateFormat(singleImgRow.get(Constants.ValidFrom))));

                        newItemImageEntity.getProperties().put(Constants.ValidTo,
                                new ODataPropertyDefaultImpl(Constants.ValidTo, UtilConstants.convertDateFormat(singleImgRow.get(Constants.ValidTo))));

                        newItemImageEntity.getProperties().put(Constants.DocumentLink,
                                new ODataPropertyDefaultImpl(Constants.DocumentLink, singleImgRow.get(Constants.DocumentLink)));

                        newItemImageEntity.getProperties().put(Constants.FileName,
                                new ODataPropertyDefaultImpl(Constants.FileName, singleImgRow.get(Constants.FileName)));

                        newItemImageEntity.getProperties().put(Constants.DocumentMimeType,
                                new ODataPropertyDefaultImpl(Constants.DocumentMimeType, singleImgRow.get(Constants.DocumentMimeType)));

                        newItemImageEntity.getProperties().put(Constants.DocumentSize,
                                new ODataPropertyDefaultImpl(Constants.DocumentSize, singleImgRow.get(Constants.DocumentSize)));

                        docmentArray.add(incrementImgVal,newItemImageEntity);
                    }

                    ODataEntitySetDefaultImpl itemImageEntity = new ODataEntitySetDefaultImpl(docmentArray.size(), null, null);
                    for (ODataEntity entity : docmentArray) {
                        itemImageEntity.getEntities().add(entity);
                    }
                    itemImageEntity.setResourcePath(Constants.ExpenseDocuments);

                    ODataNavigationProperty navProp = newItemEntity.getNavigationProperty(Constants.ExpenseDocuments);
                    navProp.setNavigationContent(itemImageEntity);
                    newItemEntity.setNavigationProperty(Constants.ExpenseDocuments, navProp);*//*

                    tempArray.add(incrementVal, newItemEntity);


                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.ExpenseItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.ExpenseItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.ExpenseItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }*/
    public static String doOnlineGetRequest(String resourcePath, Context mContext, IResponseListener iResponseListener, ICommunicationErrorListener iCommunicationErrorListener){

        HttpConversationManager manager = new HttpConversationManager(mContext);
        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                .setMethod(HttpMethod.GET)
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .addHeader("x-smp-appid", Configuration.APP_ID)
                .setResponseListener(iResponseListener).setFlowListener(IConversationFlowListener
                .prepareFor()
                .communicationError(iCommunicationErrorListener).build())
                .start();

        return "";
    }

    public static RetailerBean getRetailerApprovalDetails(JSONObject jsonObject, RetailerBean retailerBean) throws Exception {

        try {

            retailerBean = new RetailerBean();
            retailerBean.setOwnerName(jsonObject.optString(Constants.OwnerName));

            if (!TextUtils.isEmpty(jsonObject.optString(Constants.EmailID)))
                retailerBean.setEmailId(jsonObject.optString(Constants.EmailID));

            retailerBean.setMobileNumber(jsonObject.optString(Constants.MobileNo));

            retailerBean.setWeeklyOffDesc(jsonObject.optString(Constants.WeeklyOffDesc));

            retailerBean.setTax1(jsonObject.optString(Constants.Tax1));

            String strAddress = SOUtils.getCustomerDetailsAddressValue(jsonObject);
            retailerBean.setAddress1(strAddress);

//            property = properties.get(Constants.NetAmount);
//            String invAmtStr = "0";
//            if (property != null) {
//                BigDecimal mStrAmount = (BigDecimal) property.getValue();
//                invAmtStr = mStrAmount.toString();
//            }
//            retailerBean.setNetAmount(invAmtStr);


            if(!TextUtils.isEmpty(jsonObject.optString(Constants.Latitude))) {
                retailerBean.setLatVal(Double.parseDouble(jsonObject.optString(Constants.Latitude)));
            }else {
                retailerBean.setLatVal(0.0);
            }

            if(!TextUtils.isEmpty(jsonObject.optString(Constants.Longitude))) {
                retailerBean.setLongVal(Double.parseDouble(jsonObject.optString(Constants.Longitude)));
            }else {
                retailerBean.setLongVal(0.0);
            }

            String mStrLandmark;
            mStrLandmark = jsonObject.optString(Constants.Landmark) != null ? jsonObject.optString(Constants.Landmark) : "";
            retailerBean.setLandMark(mStrLandmark);


            JSONObject object = jsonObject.getJSONObject(Constants.CPDMSDivisions);
            JSONArray resultArray = object.getJSONArray("results");
            for (int i=0;i<resultArray.length();i++) {
                JSONObject object1 = resultArray.getJSONObject(i);
                retailerBean.setParentName(object1.optString(Constants.ParentName));

                retailerBean.setClassification(object1.optString(Constants.Group4Desc));
                retailerBean.setCPTypeDesc(object1.optString(Constants.Group3Desc));

                retailerBean.setCurrency(object1.optString(Constants.Currency));
            }
            return retailerBean;


        } catch (Throwable e) {
            e.printStackTrace();
            throw new OnlineODataStoreException(e.getMessage());
        }
    }

    public static void createEntity(final String requestID,final String requestString, final String resourcePath, UIListener uiListener, Context mContext) {
        OnlineRequestListener onlineReqListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (TextUtils.isEmpty(sharedPref.getString(UtilRegistrationActivity.KEY_xCSRF_TOKEN, ""))){
            createCSRFToken(mContext, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    createEntity(requestID,requestString, resourcePath, uiListener, mContext);
                }
            });
        }else {
            HttpConversationManager manager = new HttpConversationManager(mContext);
            // Create the conversation.
            manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                    .addHeader("x-csrf-token", sharedPref.getString(UtilRegistrationActivity.KEY_xCSRF_TOKEN, ""))
                    .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                    .addHeader("x-smp-appid", Configuration.APP_ID)
                    .addHeader("Accept", "application/json")
                    .setMethod(HttpMethod.POST)
                    .setRequestListener(event -> {
                        if (!TextUtils.isEmpty(requestString))
                            event.getWriter().write(requestString);
                        return null;
                    }).setResponseListener(event -> {
                // Process the results.
                if (event.getReader()!=null) {
                                        String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                    //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                    if (event.getResponseStatusCode() == 201) {
                        onlineReqListener.notifySuccessToListener(responseBody);
                    } else if (event.getResponseStatusCode() == 403) {
                        createCSRFToken(mContext, iReceiveEvent -> {
                            if (iReceiveEvent.getResponseStatusCode() == 200) {
                                createEntity(requestID,requestString, resourcePath, uiListener, mContext);
                            } else {
                                if (iReceiveEvent.getReader()!=null) {
                                    String responseBodytemp = IReceiveEvent.Util.getResponseBody(iReceiveEvent.getReader());
                                    //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                                    onlineReqListener.notifyErrorToListener(responseBodytemp);
                                }
                            }
                        });
                    } else {
                        onlineReqListener.notifyErrorToListener(responseBody);
                    }
                }
            }).start();
        }
    }

    private static void createCSRFToken(Context mContext, IResponseListener var1) {
        HttpConversationManager manager = new HttpConversationManager(mContext);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL()))
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                .addHeader("x-csrf-token", "fetch")
                .addHeader("x-smp-appid", Configuration.APP_ID)
                .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                .addHeader("Accept", "application/json")
                .setMethod(HttpMethod.GET)
                .setResponseListener(event1 -> {
                    // Process the results.
//                    String responseBody1 = IReceiveEvent.Util.getResponseBody(event1.getReader());
//                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody1 + " " + event1.getResponseStatusCode());
                    if (event1.getResponseStatusCode() == 200) {
                        Map<String, List<String>> mapList = event1.getResponseHeaders();
                        if (mapList != null && mapList.size() > 0) {
                            List<String> arrayList = mapList.get("x-csrf-token");
                            if (arrayList != null && arrayList.size() > 0) {
                                SharedPreferences.Editor spEdit = sharedPref.edit();
                                spEdit.putString(UtilRegistrationActivity.KEY_xCSRF_TOKEN, arrayList.get(0));
                                spEdit.apply();
//                                createSOEntity(table,itemtable,uiListener,mContext);
                            }
                        }
                    }
                    var1.onResponseReceived(event1);
                }).start();
    }

    public static void updateEntity(final String requestID,final String requestString, final String resourcePath, UIListener uiListener, Context mContext) {
        OnlineRequestListener onlineReqListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (TextUtils.isEmpty(sharedPref.getString(UtilRegistrationActivity.KEY_xCSRF_TOKEN, ""))){
            createCSRFToken(mContext, iReceiveEvent -> {
                if (iReceiveEvent.getResponseStatusCode() == 200) {
                    updateEntity(requestID,requestString, resourcePath, uiListener, mContext);
                }
            });
        }else {
            HttpConversationManager manager = new HttpConversationManager(mContext);
            // Create the conversation.
            manager.create(Uri.parse(MyUtils.getDefaultOnlineQryURL() + resourcePath))
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("X-SMP-APPCID", sharedPref.getString(UtilRegistrationActivity.KEY_appConnID, ""))
                    .addHeader("x-csrf-token", sharedPref.getString(UtilRegistrationActivity.KEY_xCSRF_TOKEN, ""))
                    .addHeader("RequestID", requestID)
                    .addHeader("x-smp-appid", Configuration.APP_ID)
                    .addHeader("Authorization", MyUtils.getBasicAuthCredential(mContext))
                    .addHeader("Accept", "application/json")
                    .setMethod(HttpMethod.PUT)
                    .setRequestListener(event -> {
                        if (!TextUtils.isEmpty(requestString))
                            event.getWriter().write(requestString);
                        return null;
                    }).setResponseListener(event -> {
                // Process the results.
//                if (event.getReader()!=null) {
                //                    String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                //                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                if (event.getResponseStatusCode() == 201 || event.getResponseStatusCode() == 204) {
                    onlineReqListener.notifySuccessToListener(null);
                } else if (event.getResponseStatusCode() == 403) {
                    createCSRFToken(mContext, iReceiveEvent -> {
                        if (iReceiveEvent.getResponseStatusCode() == 200) {
                            updateEntity(requestID,requestString, resourcePath, uiListener, mContext);
                        } else {
                            onlineReqListener.notifyErrorToListener(iReceiveEvent);
                        }
                    });
                } else {
                    onlineReqListener.notifyErrorToListener(event);
                }
//                }
            }).start();
        }
    }
}
