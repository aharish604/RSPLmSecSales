package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class WindowDisplayPresenterImpl implements WindowDisplayPresenter, UIListener {
    private static final String TAG = "WindowDisplayActivity";
    Context context;
    Activity activity;
    private String[][] arrWinDispType = null;
    private String[][] arrWinDocType = null;
    private String[][] arrWinSizeType = null;
    String mStrSchemeTypeId;
    WindowDisplayView windowDisplayView;
    private String[][] mArrayDistributors;
    String mStrBundleCPGUID;
    private String[][] arrSchemeCps = null;
    String mStrSchemeGUID;
    String mStrBundleRetID;
    boolean mBlIsSecondTime;
    String mStrBundleCPGUID32;
    private ArrayList<ExpenseImageBean> contractImageList = new ArrayList<>();
    private ArrayList<ExpenseImageBean> selfImageBeanList = new ArrayList<>();
    private ProgressDialog pdLoadDialog = null;
    WindowDisplayCreateBean windowDisplayCreateBean;
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private ArrayList<HashMap<String, String>> arrImageItemTable = new ArrayList<HashMap<String, String>>();
    private ArrayList<ExpenseImageBean> finalImageBeanList = new ArrayList<>();
    private boolean mBoolHeaderPosted = false;

    public WindowDisplayPresenterImpl(Context context, Activity activity, String mStrSchemeTypeId, String mStrBundleCPGUID, String mStrSchemeGUID, String mStrBundleCPGUID32, boolean mBlIsSecondTime, String mStrBundleRetID) {
        this.activity = activity;
        this.context = context;
        this.mStrSchemeTypeId = mStrSchemeTypeId;
        this.mStrBundleCPGUID = mStrBundleCPGUID;
        this.mStrBundleCPGUID32 = mStrBundleCPGUID32;
        this.mStrSchemeGUID = mStrSchemeGUID;
        this.mBlIsSecondTime = mBlIsSecondTime;
        this.mStrBundleRetID = mStrBundleRetID;
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        if (context instanceof WindowDisplayView) {
            windowDisplayView = (WindowDisplayView) context;
        }
    }

    @Override
    public void getWindowDispType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.SchemeCPDocType + "'";
            arrWinDocType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.WindowSizeUOM + "'";
            arrWinSizeType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + ConstantsUtils.RegistrationType + "' and " + Constants.ParentID + " eq '" + mStrSchemeTypeId + "' &$orderby = Description asc";
            arrWinDispType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (arrWinDispType == null) {
            arrWinDispType = new String[5][1];
            arrWinDispType[0][0] = "";
            arrWinDispType[1][0] = Constants.None;
            arrWinDispType[2][0] = "";
            arrWinDispType[3][0] = "";
            arrWinDispType[4][0] = "";
        } else {
            arrWinDispType = Constants.CheckForOtherInConfigValue(arrWinDispType);
        }
        if (arrWinSizeType == null) {
            arrWinSizeType = new String[5][1];
            arrWinSizeType[0][0] = "";
            arrWinSizeType[1][0] = Constants.None;
            arrWinSizeType[2][0] = "";
            arrWinSizeType[3][0] = "";
            arrWinSizeType[4][0] = "";
        }
        if (windowDisplayView != null) {
            windowDisplayView.setWindowDisplayType(arrWinDispType);
            windowDisplayView.setWindowSizeType(arrWinSizeType);
        }
    }

    @Override
    public void getDistributorsValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID,context);
        if (windowDisplayView != null) {
            windowDisplayView.setDistributorValues(mArrayDistributors);
        }
    }

    @Override
    public void getRegistrationData() {
        try {
            String mStrConfigQry = Constants.SchemeCPs + "?$filter= SchemeGUID eq guid'" + mStrSchemeGUID + "' and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' &$top=1";
            arrSchemeCps = OfflineManager.getArraySchemeCPs(mStrConfigQry, mStrSchemeGUID, mStrBundleCPGUID32);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (windowDisplayView != null) {
            windowDisplayView.setgetRegistrationData(arrSchemeCps);
        }
    }

    @Override
    public void getImageFromDb() {
        try {
            contractImageList.clear();
            String mStrConfigQry = Constants.SchemeCPDocuments + "?$filter= SchemeCPGUID eq guid'" + arrSchemeCps[0][1] + "'";//Constants.isLocalFilterQry+ ;
            contractImageList = OfflineManager.getSchemeCPDocuments(mStrConfigQry, contractImageList);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (windowDisplayView != null) {
            windowDisplayView.setContractImageList(contractImageList);
        }
        try {
            selfImageBeanList.clear();
            if (!TextUtils.isEmpty(arrSchemeCps[7][1])) {
                String mStrConfigQry = Constants.ClaimDocuments + "?$filter=ClaimGUID eq guid'" + arrSchemeCps[7][1] + "'";//Constants.isLocalFilterQry+ ;
                selfImageBeanList = OfflineManager.getSchemeCPDocuments(mStrConfigQry, selfImageBeanList);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (windowDisplayView != null) {
            windowDisplayView.setSelfImageBeanList(selfImageBeanList);
        }
    }

    @Override
    public void onSave(WindowDisplayCreateBean windowDisplayCreateBean) {
        if (validateField(windowDisplayCreateBean)) {

            pdLoadDialog = Constants.showProgressDialog(activity, "", activity.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(activity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    closingProgressDialog();
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
        //else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyTheme);
//            builder.setMessage(R.string.validation_plz_enter_mandatory_flds)
//                    .setCancelable(false)
//                    .setPositiveButton(R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog,
//                                                    int id) {
//                                    dialog.cancel();
//                                }
//                            });
//            builder.show();
    //    }
    }

    private void locationPerGranted() {
        pdLoadDialog = Constants.showProgressDialog(activity, "", activity.getString(R.string.gps_progress));
        Constants.getLocation(activity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if (status) {
                    onSaveWinDisplay();
                }
            }
        });
    }

    private void closingProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validateField(WindowDisplayCreateBean windowDisplayCreateBean) {
        this.windowDisplayCreateBean = windowDisplayCreateBean;
        boolean isNotError = true;
        if (!mBlIsSecondTime) {
            if (TextUtils.isEmpty(windowDisplayCreateBean.getDisplayType()) || windowDisplayCreateBean.getDisplayType().equalsIgnoreCase("None")) {
                windowDisplayView.errorDisplayType("Select Display Type");
                isNotError = false;
            }
            if (TextUtils.isEmpty(windowDisplayCreateBean.getSizeType()) || windowDisplayCreateBean.getSizeType().equalsIgnoreCase("None")) {
                windowDisplayView.errorSizeType("Select Window Size Type");
                isNotError = false;
            }
            if (TextUtils.isEmpty(windowDisplayCreateBean.getLength())) {
                windowDisplayView.errorLenght("Enter Length");
                isNotError = false;
            }
            if (TextUtils.isEmpty(windowDisplayCreateBean.getWidth())) {
                windowDisplayView.errorwidht("Enter Width");
                isNotError = false;
            }
            if (TextUtils.isEmpty(windowDisplayCreateBean.getHeight())) {
                windowDisplayView.errorheight("Enter Height");
                isNotError = false;
            }
            if (TextUtils.isEmpty(windowDisplayCreateBean.getRemarks())) {
                windowDisplayView.errorRemarks("Enter Remarks");
                isNotError = false;
            }

            if (ContractFragment.imageBeanList.size() < 2) {
                isNotError = false;
            } else {
                finalImageBeanList.clear();
                for (ExpenseImageBean expenseImageBean : ContractFragment.imageBeanList) {
                    if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                        finalImageBeanList.add(expenseImageBean);
                }
            }


        } else {
            if (SelfDisplayFragment.imageBeanList.size() < 2) {
                isNotError = false;
            } else {
                finalImageBeanList.clear();
                for (ExpenseImageBean expenseImageBean : SelfDisplayFragment.imageBeanList) {
                    if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                        finalImageBeanList.add(expenseImageBean);
                }
            }
        }

        if (finalImageBeanList.size() < 1) {
            isNotError = false;
        }

        return isNotError;
    }

    private void onSaveWinDisplay() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        if (!mBlIsSecondTime) {
            GUID guid = GUID.newRandom();
            String windowL = windowDisplayCreateBean.getLength();
            String windowB = windowDisplayCreateBean.getWidth();
            String windowH = windowDisplayCreateBean.getHeight();
            String remarks = windowDisplayCreateBean.getRemarks();
            Hashtable hashTableWindowDisplay = new Hashtable();
            hashTableWindowDisplay.put(Constants.SchemeCPGUID, guid.toString());
            hashTableWindowDisplay.put(Constants.SchemeGUID, mStrSchemeGUID);
            hashTableWindowDisplay.put(Constants.CPTypeID, Constants.str_02);
            hashTableWindowDisplay.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
            hashTableWindowDisplay.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());
            hashTableWindowDisplay.put(Constants.CPNo, UtilConstants.removeLeadingZeros(mStrBundleRetID));
            hashTableWindowDisplay.put(Constants.CPName, "");
            hashTableWindowDisplay.put(Constants.IsExcluded, "");
            hashTableWindowDisplay.put(Constants.WindowLength, windowL);
            hashTableWindowDisplay.put(Constants.WindowBreadth, windowB);
            hashTableWindowDisplay.put(Constants.WindowHeight, windowH);
            hashTableWindowDisplay.put(Constants.WindowSizeUOM, windowDisplayCreateBean.getSizeTypeId());
            hashTableWindowDisplay.put(Constants.Remarks, remarks);
            hashTableWindowDisplay.put(Constants.IsExcluded, "");
            hashTableWindowDisplay.put(ConstantsUtils.RegistrationTypeID, windowDisplayCreateBean.getDisplayTypeId());
            hashTableWindowDisplay.put(ConstantsUtils.RegistrationTypeDesc, windowDisplayCreateBean.getDisplayType());
            hashTableWindowDisplay.put(Constants.SetResourcePath, "guid'" + guid.toString() + "'");
            hashTableWindowDisplay.put(ConstantsUtils.RegistrationDate, UtilConstants.getNewDateTimeFormat());
            hashTableWindowDisplay.put(ConstantsUtils.EnrollmentDate, ConstantsUtils.convertDateFromString(windowDisplayCreateBean.getInvoicedate()));

            addMultipleImages(finalImageBeanList, guid.toString(), arrWinDocType[0][1], remarks);
            try {
                OfflineManager.createSchemeCPs(hashTableWindowDisplay, this,context);
                Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                        guid.toString36().toUpperCase(), Constants.WindowDisplayID, Constants.WindowDisplayValueHelp);

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } else if (TextUtils.isEmpty(arrSchemeCps[7][1])) {
            GUID sdGUID = GUID.newRandom();
            headerTable.clear();
            headerTable.put(ConstantsUtils.ClaimGUID, sdGUID.toString36().toUpperCase());
            headerTable.put(ConstantsUtils.ClaimDate, UtilConstants.getNewDate());
            headerTable.put(Constants.SchemeGUID, mStrSchemeGUID);
            headerTable.put(Constants.CPTypeID, Constants.str_02);
            headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
            headerTable.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());
            headerTable.put(ConstantsUtils.SchemeNo, windowDisplayCreateBean.getSchemeId());

            try {
                addMultipleImages(finalImageBeanList, sdGUID.toString(), ConstantsUtils.ZDMS_SCCLM, arrSchemeCps[5][1]);
                OfflineManager.createClaimHeader(headerTable, this,context);
                Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                        sdGUID.toString36().toUpperCase(), Constants.WindowDisplayClaimID, Constants.WindowDisplayValueHelp);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } else {
            /*visit activity started*/
            String claimId = arrSchemeCps[7][1];
            addMultipleImages(finalImageBeanList, claimId, ConstantsUtils.ZDMS_SCCLM, arrSchemeCps[5][1]);
            mBoolHeaderPosted = true;
            saveImagesToDB();
            Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                    claimId, Constants.WindowDisplayClaimID, Constants.WindowDisplayValueHelp);
        }
    }

    private void addMultipleImages(ArrayList<ExpenseImageBean> finalImageBeanList, String sdGUID, String docTypeId, String remarks) {
        arrImageItemTable.clear();
        for (ExpenseImageBean pictureImageBean : finalImageBeanList) {
            GUID imgGuid = GUID.newRandom();
            HashMap<String, String> schemeDocumentHashTable = new HashMap<>();
            if (!mBlIsSecondTime) {
                schemeDocumentHashTable.put(Constants.SchemeCPDocumentID, imgGuid.toString());
                schemeDocumentHashTable.put(Constants.SchemeCPGUID, sdGUID);
            } else {
                schemeDocumentHashTable.put(ConstantsUtils.ClaimDocumentID, imgGuid.toString());
                schemeDocumentHashTable.put(ConstantsUtils.ClaimGUID, sdGUID);
            }
            schemeDocumentHashTable.put(Constants.DocumentStore, Constants.A);
            schemeDocumentHashTable.put(Constants.DocumentTypeID, docTypeId);
            schemeDocumentHashTable.put(Constants.FileName, pictureImageBean.getFileName() + "." + pictureImageBean.getImageExtensions());
            schemeDocumentHashTable.put(Constants.DocumentMimeType, pictureImageBean.getDocumentMimeType());
            schemeDocumentHashTable.put(Constants.DocumentSize, pictureImageBean.getDocumentSize());
            schemeDocumentHashTable.put(Constants.ImagePath, pictureImageBean.getImagePath());
            schemeDocumentHashTable.put(Constants.Remarks, remarks);
            arrImageItemTable.add(schemeDocumentHashTable);
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        Log.d("Error", "onRequestError: " + e);
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (!mBlIsSecondTime) {
            Log.d(TAG, "onRequestSuccess: ");
            if (operation == Operation.Create.getValue() && mBoolHeaderPosted) {
                Log.d(TAG, "Document posted Success: ");
//            backToVisit();
            } else if (operation == Operation.Create.getValue() && !mBoolHeaderPosted) {
                mBoolHeaderPosted = true;
                saveImagesToDB();
            }
        } else {
            Log.d(TAG, "onRequestSuccess: ");
            if (operation == Operation.Create.getValue() && mBoolHeaderPosted) {
                Log.d(TAG, "Document posted Success: ");
            } else if (operation == Operation.Create.getValue() && !mBoolHeaderPosted) {
                mBoolHeaderPosted = true;
                saveImagesToDB();
            }
        }

    }

    private void saveImagesToDB() {
        mBoolHeaderPosted = true;
        if (!arrImageItemTable.isEmpty()) {
            if (!mBlIsSecondTime) {
                for (HashMap<String, String> schemeDocumentHashTable : arrImageItemTable) {
                    try {
                        OfflineManager.createSchemeCPDocument(schemeDocumentHashTable, this,context);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (HashMap<String, String> schemeDocumentHashTable : arrImageItemTable) {
                    try {
                        OfflineManager.createClaimDocuments(schemeDocumentHashTable, this,context);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String message = "";
        if (mBlIsSecondTime) {
            message = context.getString(R.string.window_display_second_created_success);
        } else {
            message = context.getString(R.string.window_display_created_success);
        }
        finalDialogBox(message);
    }

    private void finalDialogBox(String message) {
        Constants.dialogBoxWithButton(context, "", message, context.getString(R.string.ok), "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    if (windowDisplayView != null) {
                        windowDisplayView.navigateToRetDetailsActivity();
                    }
                }
            }
        });
    }
}
