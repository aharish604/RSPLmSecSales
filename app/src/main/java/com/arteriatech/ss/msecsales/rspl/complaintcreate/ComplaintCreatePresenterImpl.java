package com.arteriatech.ss.msecsales.rspl.complaintcreate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate.ExpenseImageBean;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate.SelfDisplayFragment;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ComplaintCreatePresenterImpl implements OnlineODataInterface, UIListener, ComplaintCreatePresenter {
    
    String mStrParentID = "", mStrStockOwner = "";
    ArrayList<String> uomList = new ArrayList<>();
    String mStrSelectedCRSItem = "";
    String mStrSelectedCRSItemDesc = "";
    String mStrBundleRetID;
    String mStrBundleRetName;
    String mStrBundleCPGUID32;
    String mStrBundleCPGUID;
    String strVisitActRefId;
    Hashtable complaintsTable = null;
    private Context mContext;
    private Activity mActivity;
    private ComplaintCreateView complaintCreateView;
    private boolean isSessionRequired;
    private ArrayList<ValueHelpBean> alComplaintCategory = new ArrayList<>();
    private ArrayList<ComplaintCreateBeanUOMandDescription> complaintCreateBeanUOMandDescriptionslist = new ArrayList<>();
    private ArrayList<ValueHelpBean> alComplaintType = new ArrayList<>();
    private String[][] mArrayComplaints;
    private ArrayList<ValueHelpBean> alFeedbackSubType = new ArrayList<>();
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
    private ODataDuration mStartTimeDuration;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private ComplaintCreateBean complaintCreateBean = null;
    private String[][] mArrayOrderedMaterialGroup;
    private ArrayList<HashMap<String, String>> arrImageItemTable = new ArrayList<HashMap<String, String>>();
    private String TAG = CompCreateImageActivity.class.getSimpleName();
    private int totalImageSize = 0;
    private int currentImageSize = 0;
    private String parentId = "";

    public ComplaintCreatePresenterImpl(Context mContext, ComplaintCreateView complaintCreateView, boolean isSessionRequired, Activity mActivity, ComplaintCreateBean complaintCreateBean,String parentId) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.complaintCreateView = complaintCreateView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.complaintCreateBean = complaintCreateBean;
        this.parentId = parentId;
    }

    @Override
    public void onStart(String mStrBundleRetID, String RetailerName, String mStrBundleCPGUID32, String mStrBundleCPGUID) {
        this.mStrBundleRetID = mStrBundleRetID;
        this.mStrBundleCPGUID = mStrBundleCPGUID;
        this.mStrBundleRetName = RetailerName;
        this.mStrBundleCPGUID32 = mStrBundleCPGUID32;
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        requestComplaintCategoryType();
        requestComplaintType(complaintCreateBean);
    }

    @Override
    public void requestComplaintCategoryType() {
        if (complaintCreateView != null) {
            complaintCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ComplaintCategory + "' &$orderby=" + Constants.Description + "%20asc";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 3, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void requestComplaintType(ComplaintCreateBean complaintCreateBean) {
        final String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ComplaintType + "' and ParentID eq '" + complaintCreateBean.getmStrSeleCatId() + "' &$orderby=" + Constants.Description + "%20asc";

        new Thread(new Runnable() {
            @Override
            public void run() {
                mArrayComplaints = new String[2][1];
                mArrayComplaints[0][0] = "";
                mArrayComplaints[1][0] = Constants.None;

                try {
                    mArrayComplaints = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");

                    if (mArrayComplaints == null) {
                        mArrayComplaints = new String[2][1];
                        mArrayComplaints[0][0] = Constants.None;
                        mArrayComplaints[1][0] = Constants.None;
                    } else {
                        mArrayComplaints = Constants.CheckForOtherInConfigValue(mArrayComplaints);
                    }

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        complaintCreateView.displayComplaintType(mArrayComplaints);

                    }
                });
            }
        }).start();



    }

    @Override
    public void requestCompliantOrderMaterialType() {
        Log.d("Step 2","Started");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String qryCPDMSDivision = Constants.CPDMSDivisions + "?$filter=CPGUID eq guid'"+mStrBundleCPGUID+"'";
                    String parentID = OfflineManager.getParentID(qryCPDMSDivision);
                    String mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.StockOwner + " " +
                            "eq '" + complaintCreateBean.getStockOwner() + "' and " + Constants.OrderMaterialGroupID + " ne '' and CPGUID eq '"+parentID+"'";
                    Log.d("Step 3","Started");
                    mArrayOrderedMaterialGroup = OfflineManager.getOrderedMaterialGroupsTemp(mStrConfigQry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (mArrayOrderedMaterialGroup == null) {
                    mArrayOrderedMaterialGroup = new String[2][1];
                    mArrayOrderedMaterialGroup[0][0] = "";
                    mArrayOrderedMaterialGroup[1][0] = Constants.None;
                } else {
                    mArrayOrderedMaterialGroup = Constants.CheckForOtherInConfigValue(mArrayOrderedMaterialGroup);
                }


                Log.d("Step 4","Started");

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        complaintCreateView.displayOrderedMaterialGroup(mArrayOrderedMaterialGroup);

                    }
                });
            }
        }).start();



    }

    @Override
    public void requestItemDescription(final ComplaintCreateBean complaintCreateBean) {



        new Thread(new Runnable() {
            @Override
            public void run() {

                complaintCreateBeanUOMandDescriptionslist.clear();
                if (complaintCreateBean.getmStrSelectedCRSID().equals(Constants.None)) {

//            mArrayMaterialItem = new String[2][1];
//            mArrayMaterialItem[0][0] = "";
//            mArrayMaterialItem[1][0] = Constants.None;
                } else {
                    try {
                        String qryCPDMSDivision = Constants.CPDMSDivisions + "?$filter=CPGUID eq guid'"+mStrBundleCPGUID+"'";
                        String parentID = OfflineManager.getParentID(qryCPDMSDivision);
                        String mStrConfigQry= "";
                        if(complaintCreateBean.getmStrSelectedCRSID().equalsIgnoreCase("")){
                            mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.MaterialDesc + " &$filter=" + Constants.OrderMaterialGroupID + " " +
                                    "eq 'None' and " + Constants.StockOwner + " eq '" + complaintCreateBean.getStockOwner() + "' and CPGUID eq '"+parentID+"'";
                        }else{
                            mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.MaterialDesc + " &$filter=" + Constants.OrderMaterialGroupID + " " +
                                    "eq '" + complaintCreateBean.getmStrSelectedCRSID() + "' and " + Constants.StockOwner + " eq '" + complaintCreateBean.getStockOwner() + "' and CPGUID eq '"+parentID+"'";
                        }

                        complaintCreateBeanUOMandDescriptionslist.addAll(OfflineManager.getStockOwnerGroups(mStrConfigQry));
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                    if (complaintCreateBeanUOMandDescriptionslist == null) {
//                mArrayMaterialItem = new String[2][1];
//                mArrayMaterialItem[0][0] = "";
//                mArrayMaterialItem[1][0] = Constants.None;
                    } else {
                        // mArrayMaterialItem = Constants.CheckForOtherInConfigValue(mArrayMaterialItem);
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (complaintCreateBeanUOMandDescriptionslist != null)
                            complaintCreateView.displayMaterialItemDescription(complaintCreateBeanUOMandDescriptionslist);
                    }
                });
            }
        }).start();


    }

    @Override
    public void requestUOM(ComplaintCreateBean complaintCreateBean) {
        uomList.clear();

        if (complaintCreateBean.getmStrSelectedCRSID().equals(Constants.None)) {
//            mArrayMaterialItem = new String[2][1];
//            mArrayMaterialItem[0][0] = "";
//            mArrayMaterialItem[1][0] = Constants.None;
//            mArrayMaterialItem[1][0] = Constants.None;
            uomList.add(0, Constants.None);

        } else {
            try {
                String mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.MaterialDesc + " &$filter=" + Constants.OrderMaterialGroupID + " " +
                        "eq '" + complaintCreateBean.getmStrSelectedCRSID() + "' and " + Constants.StockOwner + " eq '" + complaintCreateBean.getStockOwner() + "'";
                uomList = OfflineManager.getStockOwnerUOM(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if (uomList == null) {

                uomList.add(0, Constants.None);
            } else {

                //mArrayMaterialItem = Constants.CheckForOtherInConfigValue(uomList);
            }
        }
        // complaintCreateView.displayMaterialItemDescription(uomList);
    }

    @Override
    public void onImageStart() {
        SelfDisplayFragment.imageBeanList.clear();
//        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(complaintCreateBean.getCPGUID()),mContext);
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(complaintCreateBean.getCPGUID(),mContext);
//        mArrayDistributors = Constants.getDistributorsByCPGUID(Constants.convertStrGUID32to36(complaintCreateBean.getCPGUID()),mContext);
        mArrayDistributors = Constants.getDistributorsByCPGUID(complaintCreateBean.getCPGUID(),mContext);
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 3:
                alComplaintCategory.clear();
                try {
                    alComplaintCategory.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.ComplaintCategory));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                mArrayDistributors = Constants.getDistributorsByCPGUID(complaintCreateBean.getCPGUID(),mContext);
                mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(complaintCreateBean.getCPGUID(),mContext);
                try {
                    complaintCreateBean.setSPGUID(mArraySPValues[4][0].toUpperCase());
                    complaintCreateBean.setSPNo(mArraySPValues[6][0].toUpperCase());
                    complaintCreateBean.setParentID(mArrayDistributors[4][0].toUpperCase());
                    complaintCreateBean.setParentName(mArrayDistributors[7][0].toUpperCase());
                    complaintCreateBean.setParentName(mArrayDistributors[5][0].toUpperCase());
                    complaintCreateBean.setParentTypDesc(mArrayDistributors[6][0].toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    mStrParentID = mArraySPValues[1][0];
                    mStrStockOwner = mArraySPValues[3][0];
                } catch (Exception e) {
                    mStrParentID = "";
                    mStrStockOwner = "";
                    e.printStackTrace();
                }
                complaintCreateBean.setStockOwner(mStrStockOwner);
                complaintCreateBean.setParentID(mStrParentID);

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (complaintCreateView != null) {
                            complaintCreateView.displayComplaintCategoryType(alComplaintCategory);
                            complaintCreateView.hideProgressDialog();
                        }
                    }
                });
                break;
            case 4:
//                alFeedbackSubType.clear();
//                try {
//                    alFeedbackSubType.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.FeedbackSubType));
//                } catch (OfflineODataStoreException e) {
//                    e.printStackTrace();
//                }
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (complaintCreateView != null) {
//                            complaintCreateView.hideProgressDialog();
//                            complaintCreateView.displayByProductRelInfo(alFeedbackSubType);
//                        }
//                    }
//                });
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {


    }

    @Override
    public void getLocation() {
        if (complaintCreateView != null) {
            complaintCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (complaintCreateView != null) {
                        complaintCreateView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }

    @Override
    public void locationPerGranted() {
        if (complaintCreateView != null) {
            complaintCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (complaintCreateView != null) {
                        complaintCreateView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (complaintCreateView != null)
                                ConstantsUtils.showAutoDateSetDialog(mActivity);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void finalSaveCondition() {
        Bundle bundle = new Bundle();
        if (complaintCreateView != null) {
            complaintCreateView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
        }
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        onSave();
    }

    @Override
    public void onSave() {
        try {
            OfflineManager.createCustomerComplaints(complaintsTable, this,mContext);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }

    @Override
    public void onSaveCustComp(ComplaintCreateBean complaintCreateBean) {
        Constants.writeDebugLog(mContext,"Complaint data to save");

        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        String sQuantity = "";
        String batchNumber = "";
        String sRemarks = complaintCreateBean.getRemarks();
        String mStrCurrentDate = complaintCreateBean.getMfDate();
        if (complaintCreateBean.getmStrSeleCatId().equalsIgnoreCase("00000001")) {
            sQuantity = complaintCreateBean.getQuanitiy();
            batchNumber = complaintCreateBean.getBatchNumber();
        } else {
            // mStrSelectedCRSID = "";
            // mStrSelectedCRSDesc = "";
            mStrSelectedCRSItem = "";
            mStrSelectedCRSItemDesc = "";

        }
        String mStrSeleCategoryId = complaintCreateBean.getmStrSeleCatId();
        if (!mStrSeleCategoryId.equalsIgnoreCase("00000001")) {
            complaintCreateBean.setmStrSelectedCRSItem("");
            complaintCreateBean.setmStrSelectedCRSItemDesc("");
            complaintCreateBean.setmStrSelectedCRSID("");
            complaintCreateBean.setmStrSelectedCRSDesc("");
        }
        String[] spGuidName = null;
        String spGuid = "";
        String fristName = "";
        try{
            spGuidName = Constants.getSPGUIDName();
            if(spGuidName != null){
                spGuid = spGuidName[0];
                fristName = spGuidName[1];
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        complaintsTable = new Hashtable();
        GUID guid = GUID.newRandom();
        complaintsTable.put(Constants.ComplaintNo, "");
        complaintsTable.put(Constants.ComplaintCategoryID, complaintCreateBean.getmStrSeleCatId());
        complaintsTable.put(Constants.ComplainCategoryDesc, complaintCreateBean.getComplainCategoryDesc());
        complaintsTable.put(Constants.ComplaintTypeID, complaintCreateBean.getmStrSeleComplaintsId());
        complaintsTable.put(Constants.ComplaintTypeDesc, complaintCreateBean.getmStrSeleComplaintsDesc());
        complaintsTable.put(Constants.ComplaintPriorityID, "");
        complaintsTable.put(Constants.ComplaintPriorityDesc, "");
        try {
            complaintsTable.put(Constants.SPGUID, spGuid);
        } catch (Exception e) {
            complaintsTable.put(Constants.SPGUID, Constants.getSPGUID());
            e.printStackTrace();
        }
        complaintsTable.put(Constants.SPNo, mArraySPValues[6][0]);
        try {
            complaintsTable.put(Constants.SPName,fristName);
        } catch (Exception e) {
            complaintsTable.put(Constants.SPName,fristName);
            e.printStackTrace();
        }
        complaintsTable.put(Constants.CPTypeID, Constants.str_02);
        complaintsTable.put(Constants.ParentNo, parentId);
        complaintsTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
        mStrBundleCPGUID32 = complaintCreateBean.getCPGUID32();
        complaintsTable.put(Constants.CPGUID, complaintCreateBean.getCPGUID32());
        complaintsTable.put(Constants.CPNo, complaintCreateBean.getCPNo());
        complaintsTable.put(Constants.CPName, complaintCreateBean.getmName());
        complaintsTable.put(Constants.MFD, complaintCreateBean.getMfDate() != null ? complaintCreateBean.getMfDate() : "");
        if (complaintCreateBean.getmStrSelectedCRSID() != null && !complaintCreateBean.getmStrSelectedCRSID().equalsIgnoreCase("")) {
            complaintsTable.put(Constants.OrderMaterialGroupID, complaintCreateBean.getmStrSelectedCRSID());
            complaintsTable.put(Constants.OrderMaterialGroupDesc, complaintCreateBean.getmStrSelectedCRSDesc() != null ? complaintCreateBean.getmStrSelectedCRSDesc() : "");
        } else {
            complaintsTable.put(Constants.OrderMaterialGroupID, "");
            complaintsTable.put(Constants.OrderMaterialGroupDesc, "");
        }

        complaintsTable.put(Constants.MaterialGrp, "");
        complaintsTable.put(Constants.MaterialGrpDesc, "");
        if (complaintCreateBean.getmStrSelectedCRSItem() != null && !complaintCreateBean.getmStrSelectedCRSItem().equalsIgnoreCase("")) {
            complaintsTable.put(Constants.Material, complaintCreateBean.getmStrSelectedCRSItem());
            complaintsTable.put(Constants.MaterialDesc, complaintCreateBean.getmStrSelectedCRSItemDesc());
        } else {
            complaintsTable.put(Constants.Material, "");
            complaintsTable.put(Constants.MaterialDesc, "");
        }

        complaintsTable.put(Constants.ComplaintDate, UtilConstants.getNewDateTimeFormat());
        complaintsTable.put(Constants.ComplaintStatusID, "");
        complaintsTable.put(Constants.ComplaintStatusDesc, "");
        complaintsTable.put(Constants.Quantity, sQuantity);
        complaintsTable.put(Constants.UOM, complaintCreateBean.getSetSelectedUOM());
        complaintsTable.put(Constants.Batch, batchNumber.toUpperCase());
        complaintsTable.put(Constants.Remarks, sRemarks);
        complaintsTable.put(Constants.CreatedBy, loginIdVal);
        complaintsTable.put(Constants.ComplaintGUID, guid.toString36().toUpperCase());
        complaintsTable.put(Constants.SetResourcePath, "guid'" + guid.toString() + "'");
        strVisitActRefId = guid.toString36().toUpperCase();
        arrImageItemTable.clear();
        for (ExpenseImageBean expenseImageBean : SelfDisplayFragment.imageBeanList) {
            if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage()) {
                HashMap<String, String> singleImageItem = new HashMap<String, String>();
                //GUID itemImageGuid = GUID.newRandom();
                singleImageItem.put(Constants.ComplaintGUID, guid.toString36().toUpperCase());
                singleImageItem.put(Constants.DocumentStore, "");
                singleImageItem.put(Constants.DocumentTypeID, "");
                singleImageItem.put(Constants.DocumentTypeDesc, "");
                singleImageItem.put(Constants.LoginID, loginIdVal);
                singleImageItem.put(Constants.DocumentStatusID, "");
                singleImageItem.put(Constants.DocumentStatusDesc, "");
                singleImageItem.put(Constants.ValidFrom, UtilConstants.getNewDateTimeFormat());
                singleImageItem.put(Constants.ValidTo, UtilConstants.getNewDateTimeFormat());
                singleImageItem.put(Constants.DocumentLink, expenseImageBean.getImagePath());
                singleImageItem.put(Constants.FileName, expenseImageBean.getFileName());
                singleImageItem.put(Constants.DocumentMimeType, expenseImageBean.getDocumentMimeType());
                singleImageItem.put(Constants.DocumentSize, expenseImageBean.getDocumentSize());
                singleImageItem.put(Constants.Remarks, sRemarks);
                arrImageItemTable.add(singleImageItem);
            }
        }


        if (complaintCreateView != null) {
            complaintCreateView.conformationDialog(mContext.getString(R.string.complaint_save_conformation_msg), 1);
        }

    }

    private void saveDocumentEntityToTable() {
        try {
            //noinspection unchecked
            totalImageSize = arrImageItemTable.size();
            if(totalImageSize>0){
                currentImageSize = 0;
                for (HashMap<String, String> hashItem : arrImageItemTable) {
                    OfflineManager.createComplaintDoc(hashItem, new UIListener() {
                        @Override
                        public void onRequestError(int i, Exception e) {
                            Log.d(TAG, "onRequestError: ");
                            currentImageSize++;
                            if (currentImageSize == totalImageSize) {
                                Log.d(TAG, "onRequestSuccess: ");
                                if (complaintCreateView != null) {
                                    complaintCreateView.hideProgressDialog();
                                    complaintCreateView.showMessage(mContext.getString(R.string.msg_customer_complaints_failed_created), false);
                                }
                            }
                        }

                        @Override
                        public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                            currentImageSize++;
                            if (currentImageSize == totalImageSize) {
                                Log.d(TAG, "onRequestSuccess: ");
                                if (complaintCreateView != null) {
                                    complaintCreateView.hideProgressDialog();
                                    complaintCreateView.showMessage(mContext.getString(R.string.msg_customer_complaints_created), false);
                                }
                            }
                        }
                    },mContext);
                }
            }else{
                if (complaintCreateView != null) {
                    complaintCreateView.hideProgressDialog();
                    complaintCreateView.showMessage(mContext.getString(R.string.msg_customer_complaints_created), false);
                }
            }


        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyTheme);
        builder.setMessage(R.string.msg_customer_complaints_failed_created).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();*/
        Constants.writeDebugLog(mContext,"Complaint Create Failed"+i+"--"+e.getMessage());
        if (complaintCreateView != null) {
            complaintCreateView.hideProgressDialog();
            complaintCreateView.showMessage(mContext.getString(R.string.msg_customer_complaints_failed_created), false);
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                strVisitActRefId, Constants.CustomerCompCreateID, Constants.CustomerComplaintsCreate);
        saveDocumentEntityToTable();
        Constants.writeDebugLog(mContext,"Complaint Created successfully");


    }


    @Override
    public boolean validateFields(ComplaintCreateBean complaintCreateBean) {
        boolean isNotError = true;

        if (TextUtils.isEmpty(complaintCreateBean.getComplaintCategoryID()) || complaintCreateBean.getComplainCategoryDesc().equalsIgnoreCase("None")) {
            complaintCreateView.errorCategoryType("Select Category");
            isNotError = false;
        }
        if (TextUtils.isEmpty(complaintCreateBean.getmStrSeleComplaintsId()) || complaintCreateBean.getmStrSeleComplaintsDesc().equalsIgnoreCase("None")) {
            complaintCreateView.errorComplaintType("Select ComplaintType");
            isNotError = false;
        }
        if (!TextUtils.isEmpty(complaintCreateBean.getComplaintCategoryID()) &&
                !complaintCreateBean.getmStrSeleComplaintsDesc().equalsIgnoreCase("None")
                && complaintCreateBean.getComplaintCategoryID().equalsIgnoreCase("00000001")) {

            if (TextUtils.isEmpty(complaintCreateBean.getmStrSelectedCRSID()) || complaintCreateBean.getmStrSelectedCRSDesc().equalsIgnoreCase("None")) {
                complaintCreateView.errorOrderType("Select SKU Group");
                isNotError = false;
            }
            if (TextUtils.isEmpty(complaintCreateBean.getmStrSelectedCRSItem()) || complaintCreateBean.getmStrSelectedCRSItemDesc().equalsIgnoreCase("None")) {
                complaintCreateView.errorItemDetail("Select Items");
                isNotError = false;
            }
            if (TextUtils.isEmpty(complaintCreateBean.getQuanitiy())) {
                complaintCreateView.errorQuantity("Select Quantity");
                isNotError = false;
            }
            if (TextUtils.isEmpty(complaintCreateBean.getBatchNumber())) {
                complaintCreateView.errorBatch("Select Batch");
                isNotError = false;
            }
            if (TextUtils.isEmpty(complaintCreateBean.getMfDate())) {
                complaintCreateView.errorDate("Select Date");
                isNotError = false;
            }

        }
        if (TextUtils.isEmpty(complaintCreateBean.getRemarks())) {
            complaintCreateView.errorRemarks("Enter Remarks");
            isNotError = false;
        }

        return isNotError;
    }

    @Override
    public void onAsignData(String save, ComplaintCreateBean collectionBean) {
//        if (SelfDisplayFragment.imageBeanList.size() > 0) {
            onSaveCustComp(collectionBean);
//        } else {
//            if (complaintCreateView != null) {
//                complaintCreateView.showMessage(mContext.getString(R.string.alert_capture_one_img), true);
//            }
//        }
    }

    @Override
    public void onSaveData() {
        getLocation();
    }


    @Override
    public void onDestroy() {

    }
}
