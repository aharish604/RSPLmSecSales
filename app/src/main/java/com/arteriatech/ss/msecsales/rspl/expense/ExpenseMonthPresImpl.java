package com.arteriatech.ss.msecsales.rspl.expense;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate.ExpenseImageBean;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate.SelfDisplayFragment;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ExpenseMonthPresImpl implements ExpenseMonthlyPresenter, OnlineODataInterface, UIListener {

    int mMonth = 0;
    private Context mContext;
    private Bundle bundle = null;
    private ExpenseMonthlyView expenseView = null;
    private ArrayList<ExpenseBean> expenseBeanList = new ArrayList<>();
    private String[][] mArraySalesPerson = null;
    private String expenseFreq = "", stAmountCatType = "", stMaxAllowancePer = "", stBillAllowance = "", isOtherExpenseMandatory = "", stOtherExpenseAmountCategory = "", stOtherMaxPer = "", stOtherExpense = "", stRemarks = "", mStrSeleExpenseTypeId = "", mStrSeleExpenseTypeDesc = "", mStrCurrentDate = "";
    private String[][] mArrayDefaultExpenseAllowance = null;
//    private double finalPhoneAmount = 0;
//    private double finalOtherExpenseAmount = 0;
    private double finalOtherAmount = 0;
    private String[][] mArrayDefaultOtherExAllowance = null;
    private ArrayList<ExpenseImageBean> finalImageBeanList = new ArrayList<>();
    private boolean secondItemImage = true;
    private Hashtable masterHashTable = new Hashtable();
    private ArrayList<HashMap<String, String>> arrItemTable = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrImageItemTable = new ArrayList<HashMap<String, String>>();


    public ExpenseMonthPresImpl(Context mContext, Bundle bundle, ExpenseMonthlyView expenseView) {
        this.mContext = mContext;
        this.expenseView = expenseView;
        if (bundle != null) {
            expenseFreq = bundle.getString(Constants.ExpenseFreq);
        }
    }

    @Override
    public void onStart() {
        if (expenseView != null) {
            expenseView.showProgress();
        }
//        String mStrQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "' and " + Constants.DefaultItemCat +" eq '000010' and (ExpenseItemType eq '0000000004' or ExpenseItemType eq '0000000001') &$orderby = ExpenseItemType asc &$top=2";
        String mStrQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "' and (ExpenseItemType eq '0000000004' or ExpenseItemType eq '0000000001') &$orderby = ExpenseItemType asc &$top=2";
        ConstantsUtils.onlineRequest(mContext, mStrQry, false, 4, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onAllowanceChange(String s) {
        stBillAllowance = s;
        displayAndSetValue();
    }

    @Override
    public void onOtherExpChange(String s) {
        stOtherExpense = s.toString();
        displayAndSetValue();
    }

    @Override
    public void onRemarksChange(String s) {
        stRemarks = s.toString();
        displayAndSetValue();
    }

    private boolean checkValidationAndShowDialogs() {
        return ExpenseDailyPresenterImpl.validateAlreadyDataSaved(mContext, expenseFreq, mStrCurrentDate, mStrSeleExpenseTypeId);
    }

    @Override
    public void onSaveData(View remarksView, View imageView, int fiscalYear) {
        if (!displayValidation()) {
            if (checkValidations(remarksView, imageView)) {
                saveDataToDataValt(mContext, fiscalYear);
            }
        } else {
            if (expenseView != null) {
                expenseView.showMessage("Expense already submited for " + Constants.ORG_MONTHS[mMonth - 1]);
            }
        }
    }

    private boolean displayValidation() {
        boolean isValid = checkValidationAndShowDialogs();
        if (isValid) {
            if (expenseView != null) {
                expenseView.showMessage("Expense already submited for " + Constants.ORG_MONTHS[mMonth - 1]);
            }
        }
        return isValid;
    }

    private void saveDataToDataValt(Context context, int fiscalYear) {
        GUID guid = GUID.newRandom();
        String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        masterHashTable.clear();
        masterHashTable.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
        masterHashTable.put(Constants.ExpenseNo, doc_no);
        masterHashTable.put(Constants.FiscalYear, fiscalYear + "");
        masterHashTable.put(Constants.LoginID, loginIdVal);
        masterHashTable.put(Constants.CPGUID, "");
        masterHashTable.put(Constants.CPNo, "");
        masterHashTable.put(Constants.CPName, "");
        masterHashTable.put(Constants.CPType, "");
        masterHashTable.put(Constants.CPTypeDesc, "");
        masterHashTable.put(Constants.SPGUID, mArraySalesPerson[4][0]);
        masterHashTable.put(Constants.SPNo, mArraySalesPerson[6][0]);
        masterHashTable.put(Constants.SPName, mArraySalesPerson[7][0]);
        masterHashTable.put(Constants.ExpenseType, mStrSeleExpenseTypeId);
        masterHashTable.put(Constants.ExpenseTypeDesc, mStrSeleExpenseTypeDesc);
        masterHashTable.put(Constants.ExpenseDate, mStrCurrentDate);
        masterHashTable.put(Constants.Status, "");
        masterHashTable.put(Constants.StatusDesc, "");
        masterHashTable.put(Constants.Amount, "0.0");
        masterHashTable.put(Constants.Currency, mArraySalesPerson[10][0]);
        int itemIncVal = 0;
        arrItemTable.clear();
        for (ExpenseBean expenseBean : expenseBeanList) {
//            if(!secondItemImage && itemIncVal==0) {

            if (secondItemImage || itemIncVal > 0) {
                HashMap<String, String> singleItem = new HashMap<String, String>();
                GUID itemGuid = GUID.newRandom();
                singleItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
                singleItem.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
                singleItem.put(Constants.ExpeseItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                singleItem.put(Constants.LoginID, loginIdVal);
                singleItem.put(Constants.ExpenseItemType, expenseBean.getExpenseItemType());
                singleItem.put(Constants.ExpenseItemTypeDesc, expenseBean.getExpenseItemTypeDesc());
                singleItem.put(Constants.BeatGUID, expenseBean.getBeatGUID());
                singleItem.put(Constants.Location, expenseBean.getLocation());
                singleItem.put(Constants.ConvenyanceMode, expenseBean.getConvenyanceMode());
                singleItem.put(Constants.ConvenyanceModeDs, expenseBean.getConvenyanceModeDs());
                singleItem.put(Constants.BeatDistance, expenseBean.getBeatDistance());
                singleItem.put(Constants.UOM, expenseBean.getUOM());
                singleItem.put(Constants.Amount, expenseBean.getAmount());
                singleItem.put(Constants.Currency, mArraySalesPerson[10][0]);
                singleItem.put(Constants.Remarks, expenseBean.getRemarks());

//                expenseImageBeanArrayList = expenseBean.getExpenseImageBeanArrayList();
                if (itemIncVal == 0) {
                    if (finalImageBeanList.size() > 0) {
                        for (ExpenseImageBean expenseImageBean : finalImageBeanList) {
                            HashMap<String, String> singleImageItem = new HashMap<String, String>();
                            GUID itemImageGuid = GUID.newRandom();
                            singleImageItem.put(Constants.ExpenseDocumentID, itemImageGuid.toString36().toUpperCase());
                            singleImageItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
                            singleImageItem.put(Constants.DocumentStore, "");
                            singleImageItem.put(Constants.DocumentTypeID, "");
                            singleImageItem.put(Constants.DocumentTypeDesc, "");
                            singleImageItem.put(Constants.LoginID, loginIdVal);
                            singleImageItem.put(Constants.DocumentStatusID, "");
                            singleImageItem.put(Constants.DocumentStatusDesc, "");
                            singleImageItem.put(Constants.ValidFrom, UtilConstants.getNewDateTimeFormat());
                            singleImageItem.put(Constants.ValidTo, UtilConstants.getNewDateTimeFormat());
                            singleImageItem.put(Constants.DocumentLink, expenseImageBean.getImagePath());
                            singleImageItem.put(Constants.FileName, expenseImageBean.getFileName() + "." + expenseImageBean.getImageExtensions());
                            singleImageItem.put(Constants.DocumentMimeType, expenseImageBean.getDocumentMimeType());
                            singleImageItem.put(Constants.DocumentSize, expenseImageBean.getDocumentSize());
                            singleImageItem.put(Constants.Remarks, expenseBean.getRemarks());
                            arrImageItemTable.add(singleImageItem);
                        }
                        singleItem.put("item_no" + itemIncVal, UtilConstants.convertArrListToGsonString(arrImageItemTable));
                    }

                }
                arrItemTable.add(singleItem);
            }
            itemIncVal++;
        }
        masterHashTable.put(Constants.entityType, Constants.Expenses);
        masterHashTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrItemTable));
        Constants.saveDeviceDocNoToSharedPref(mContext, Constants.Expenses, doc_no);
        JSONObject jsonHeaderObject = new JSONObject(masterHashTable);
        ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),context);
        saveDocumentEntityToTable();
        if (expenseView != null) {
            expenseView.showSuccessMsg(mContext.getString(R.string.expense_monthly_created_success));
        }
//        displayCompletedDialogBox(getContext());
    }

    private void saveDocumentEntityToTable() {
        try {
            //noinspection unchecked
            int getNumb = 0;
            for (HashMap<String, String> arrItemTables : arrItemTable) {
                String imageStringArray = arrItemTables.get("item_no" + getNumb);
                if (imageStringArray != null) {
                    ArrayList<HashMap<String, String>> convertedString = UtilConstants.convertToArrayListMap(imageStringArray);

                    for (HashMap<String, String> hashItem : convertedString) {
                        OfflineManager.createExpensesingItem(arrItemTables, hashItem, this,mContext);
                    }
//                    Log.d(TAG, "onSaveData: " + imageStringArray);
//                    Log.d(TAG, "convertedString: " + convertedString);
                }
                getNumb++;
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private boolean checkValidations(View remarksView, View imageView) {
        int isMandetory = 0;
        finalImageBeanList.clear();
        secondItemImage = false;
        if (stAmountCatType.equalsIgnoreCase("000020")) {
            if (TextUtils.isEmpty(stBillAllowance)) {
                isMandetory = 1;
                if (expenseView != null) {
                    expenseView.errorDailyAlowce("Enter Phone/Mobile Bill");
                }
//                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            } else {
                try {
                    double doubQty = Double.parseDouble(stBillAllowance);
                    double maxValuePer = Double.parseDouble(stMaxAllowancePer);
                    double finalAmount = (doubQty / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
                        isMandetory = 4;
                        if (expenseView != null) {
                            expenseView.errorDailyAlowce("Enter Valid Amount");
                        }
//                        etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                    } else {
                        expenseBeanList.get(1).setAmount(finalAmount + "");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    isMandetory = 3;
                    expenseBeanList.get(1).setAmount("0.0");
                    if (expenseView != null) {
                        expenseView.errorDailyAlowce("Enter Valid Amount");
                    }
//                    etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                }
            }

        } else if (stAmountCatType.equalsIgnoreCase("000010")) {
            if (TextUtils.isEmpty(stBillAllowance)) {
                isMandetory = 1;
                if (expenseView != null) {
                    expenseView.errorDailyAlowce("Enter Phone/Mobile Bill");
                }
//                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            }
        }
        if (isOtherExpenseMandatory.equalsIgnoreCase("000010")) {
            secondItemImage = true;
            if (stOtherExpenseAmountCategory.equalsIgnoreCase("000020")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                    isMandetory = 1;
                    if (expenseView != null) {
                        expenseView.errorOtherExp("Enter Other Expenses");
                    }
//                    etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                } else if (stOtherExpense.equals(".") || stOtherExpense.equals("0")) {
                    isMandetory = 1;
                    if (expenseView != null) {
                        expenseView.errorOtherExp("Enter Valid Expenses");
                    }
//                    etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                } else {
                    try {
                        double doubQty = Double.parseDouble(stOtherExpense);
                        double maxValuePer = Double.parseDouble(stOtherMaxPer);
                        double finalAmount = (doubQty / 100.0f) * maxValuePer;
                        if (finalAmount > Double.parseDouble(mArrayDefaultOtherExAllowance[0][0])) {
                            isMandetory = 4;
                            if (expenseView != null) {
                                expenseView.errorOtherExp("Enter Valid Expenses");
                            }
//                            etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                        } else {
                            expenseBeanList.get(0).setAmount(finalAmount + "");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        isMandetory = 3;
                        expenseBeanList.get(0).setAmount("0.0");
                        if (expenseView != null) {
                            expenseView.errorOtherExp("Enter Valid Expenses");
                        }
//                        etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                    }
                }

            } else if (stOtherExpenseAmountCategory.equalsIgnoreCase("000010")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                    isMandetory = 1;
                    if (expenseView != null) {
                        expenseView.errorOtherExp("Enter Other Expenses");
                    }
//                    etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                }
            }
            if (imageView.getVisibility() == View.VISIBLE) {
                if (SelfDisplayFragment.imageBeanList.size() < 2) {
                    isMandetory = 5;
                } else {
                    for (ExpenseImageBean expenseImageBean : SelfDisplayFragment.imageBeanList) {
                        if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                            finalImageBeanList.add(expenseImageBean);
                    }
                }
            }
            if (remarksView.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(stRemarks)) {
                    isMandetory = 1;
                    if (expenseView != null) {
                        expenseView.errorRemarks("Enter Remarks");
                    }
//                    etRemarks.setBackgroundResource(R.drawable.edittext_border);
                }
            }
        } else {
            secondItemImage = false;
            if (stOtherExpenseAmountCategory.equalsIgnoreCase("000020")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                } else {
                    try {
                        double doubQty = Double.parseDouble(stOtherExpense);
                        double maxValuePer = Double.parseDouble(stOtherMaxPer);
                        double finalAmount = (doubQty / 100.0f) * maxValuePer;
                        if (finalAmount > Double.parseDouble(mArrayDefaultOtherExAllowance[0][0])) {
                            isMandetory = 4;
                            if (expenseView != null) {
                                expenseView.errorOtherExp("Enter Valid Expenses");
                            }
//                            etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                        } else {
                            expenseBeanList.get(0).setAmount(finalAmount + "");
                            secondItemImage = true;
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        isMandetory = 3;
                        expenseBeanList.get(0).setAmount("0.0");
                        if (expenseView != null) {
                            expenseView.errorOtherExp("Enter Valid Expenses");
                        }
//                        etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                    }
                }

            } else if (stOtherExpenseAmountCategory.equalsIgnoreCase("000010")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                } else {
                    secondItemImage = true;
                }
            }
            if (imageView.getVisibility() == View.VISIBLE) {
                if (SelfDisplayFragment.imageBeanList.size() < 2) {
                } else {
                    for (ExpenseImageBean expenseImageBean : SelfDisplayFragment.imageBeanList) {
                        if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                            finalImageBeanList.add(expenseImageBean);
                    }
                    secondItemImage = true;
                }
            }
            if (remarksView.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(stRemarks)) {
                } else {
                    secondItemImage = true;
                    expenseBeanList.get(0).setRemarks(stRemarks);
                }
            }
        }
        String messages = "";
        if (isMandetory == 0) {
            return true;
        } else if (isMandetory == 1) {
            messages = mContext.getString(R.string.validation_plz_fill_mandatory_flds);
        } else if (isMandetory == 3) {
            messages = mContext.getString(R.string.expense_error_enter_valid_amount);
        } else if (isMandetory == 4) {
            messages = mContext.getString(R.string.expense_error_amount);
        } else if (isMandetory == 5) {
            messages = "Please upload atleast one image";
        }
        if (expenseView != null) {
            expenseView.showMessage(messages);
        }
//        Constants.dialogBoxWithButton(getContext(), "", messages, getString(R.string.ok), "", null);
        return false;
    }

    @Override
    public void expenseDate(String mStrCurrentDte, int mMonth) {
        mStrCurrentDate = mStrCurrentDte;
        this.mMonth = mMonth;
        displayValidation();
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        expenseBeanList = OfflineManager.getConfigExpense(list, Constants.None);
        if (!expenseBeanList.isEmpty()) {
            mStrSeleExpenseTypeId = expenseBeanList.get(0).getExpenseType();
            mStrSeleExpenseTypeDesc = expenseBeanList.get(0).getExpenseTypeDesc();
        }
        try {
            mArraySalesPerson = Constants.getDistributors();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayValidation();
                if (expenseView != null) {
                    expenseView.hideProgress();
                    displayAndSetValue();

                }
            }
        });
    }

    private void displayAndSetValue() {
        if (!expenseBeanList.isEmpty()) {
            if (expenseBeanList.size() > 1) {
                ExpenseBean otherExpenseBean = expenseBeanList.get(0);
                ExpenseBean mobileExpenseBean = expenseBeanList.get(1);
                displayOthersUI(otherExpenseBean);
                displayMobileUI(mobileExpenseBean);
            }
        }
    }

    private void displayMobileUI(ExpenseBean mobileExpenseBean) {
        String displayMobileBill = "";
        int etDailyAllowance = 0;
        int tvMobileBill = 0;
        stAmountCatType = mobileExpenseBean.getAmountCategory();
        stMaxAllowancePer = mobileExpenseBean.getMaxAllowancePer();
        String query = Constants.ExpenseAllowances + "?$filter= ExpenseItemType eq '0000000004'";
        if (mobileExpenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            try {

                String[][] mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    BigDecimal maxPer = new BigDecimal(mobileExpenseBean.getMaxAllowancePer());
                    BigDecimal tempAmount = new BigDecimal(mArrayDefaultExpenseAllowance[0][0]);
                    BigDecimal finalAmount = (tempAmount.divide(new BigDecimal("100"))).multiply(maxPer);

//                    double maxPer = Double.parseDouble(mobileExpenseBean.getMaxAllowancePer());
//                    double tempAmount = Double.parseDouble(mArrayDefaultExpenseAllowance[0][0]);
//                    double finalAmount = (tempAmount / 100.0f) * maxPer;
                    displayMobileBill = UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0];
                    mobileExpenseBean.setAmount(finalAmount + "");
//                    finalPhoneAmount = finalAmount;
                } else {
                    displayMobileBill = "";
                    mobileExpenseBean.setAmount("0.0");
//                    finalPhoneAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                displayMobileBill = "";
                mobileExpenseBean.setAmount("0.0");
//                finalPhoneAmount = 0.0;
            }
            etDailyAllowance = View.GONE;
            tvMobileBill = View.VISIBLE;
        } else if (mobileExpenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            etDailyAllowance = View.VISIBLE;
            tvMobileBill = View.GONE;
           /* try {
                mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    double enteredValue = Double.parseDouble(stBillAllowance);
                    double maxValuePer = Double.parseDouble(mobileExpenseBean.getMaxAllowancePer());
                    double finalAmount = (enteredValue / 100.0f) * maxValuePer;
//                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
//                        finalPhoneAmount = 0.0;
//                    } else {
//                        finalPhoneAmount = finalAmount;
//                    }
                } else {
//                    finalPhoneAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
//                finalPhoneAmount = 0.0;
            }*/
            mobileExpenseBean.setAmount(stBillAllowance);

        } else if (mobileExpenseBean.getAmountCategory().equalsIgnoreCase("000010")) {
            etDailyAllowance = View.VISIBLE;
            tvMobileBill = View.GONE;
//            try {
//                finalPhoneAmount = Double.parseDouble(stBillAllowance);
//            } catch (Exception e) {
//                e.printStackTrace();
//                finalPhoneAmount = 0.0;
//            }
            mobileExpenseBean.setAmount(stBillAllowance);
        }
        if (expenseView != null) {
            expenseView.showMobile(displayMobileBill, etDailyAllowance, tvMobileBill);
        }
    }

    private void displayOthersUI(ExpenseBean otherExpenseBean) {
        String query = Constants.ExpenseAllowances + "?$filter= ExpenseItemType eq '0000000001' &$top =1";
        int displayRemarks = 0;
        int displayPhoto = 0;
        int tvOtherExpense = 0;
        int etOtherExpenses = 0;
        String uom = "";
        String otherExpanse = "";
        isOtherExpenseMandatory = otherExpenseBean.getDefaultItemCat();
        /*if (isOtherExpenseMandatory.equalsIgnoreCase("000010")) {
            tvOtherExpenseMandatory.setVisibility(View.VISIBLE);
            tvRemarksMandatory.setVisibility(View.VISIBLE);
        } else {
            tvOtherExpenseMandatory.setVisibility(View.GONE);
            tvRemarksMandatory.setVisibility(View.GONE);
        }*/
        stOtherExpenseAmountCategory = otherExpenseBean.getAmountCategory();
        stOtherMaxPer = otherExpenseBean.getMaxAllowancePer();
        if (otherExpenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            try {

                String[][] mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    BigDecimal maxPer = new BigDecimal(otherExpenseBean.getMaxAllowancePer());
                    BigDecimal tempAmount = new BigDecimal(mArrayDefaultExpenseAllowance[0][0]);
                    BigDecimal finalAmount = (tempAmount.divide(new BigDecimal("100"))).multiply(maxPer);
                    otherExpanse = UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0];
                    otherExpenseBean.setAmount(String.valueOf(ConstantsUtils.decimalRoundOff(finalAmount,2)));
//                    finalOtherExpenseAmount = finalAmount;
                } else {
                    otherExpanse = "";
                    otherExpenseBean.setAmount("0.0");
//                    finalOtherExpenseAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                otherExpanse = "";
                otherExpenseBean.setAmount("0.0");
//                finalOtherExpenseAmount = 0.0;
            }
            etOtherExpenses = View.GONE;
            tvOtherExpense = View.VISIBLE;
        } else if (otherExpenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            etOtherExpenses = View.VISIBLE;
            tvOtherExpense = View.GONE;
            try {
                mArrayDefaultOtherExAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultOtherExAllowance[0].length > 0) {
                    double enteredValue = Double.parseDouble(stOtherExpense);
                    double maxValuePer = Double.parseDouble(otherExpenseBean.getMaxAllowancePer());
                    double finalAmount = (enteredValue / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultOtherExAllowance[0][0])) {
                        finalOtherAmount = 0.0;
                    } else {
                        finalOtherAmount = finalAmount;
                    }
                } else {
                    finalOtherAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                finalOtherAmount = 0.0;
            }
            uom = mArraySalesPerson[10][0];
            otherExpenseBean.setAmount(stOtherExpense);

        } else if (otherExpenseBean.getAmountCategory().equalsIgnoreCase("000010")) {

            etOtherExpenses = View.VISIBLE;
            tvOtherExpense = View.GONE;
            try {
                finalOtherAmount = Double.parseDouble(stOtherExpense);
            } catch (Exception e) {
                e.printStackTrace();
                finalOtherAmount = 0.0;
            }
            uom = mArraySalesPerson[10][0];
            otherExpenseBean.setAmount(stOtherExpense);
        }
        if (!otherExpenseBean.getIsSupportDocReq().equalsIgnoreCase("")) {
            displayPhoto = View.VISIBLE;
        } else {
            displayPhoto = View.GONE;
        }
        if (!otherExpenseBean.getIsRemarksReq().equalsIgnoreCase("")) {
            otherExpenseBean.setRemarks(stRemarks);
            displayRemarks = View.VISIBLE;
        } else {
            otherExpenseBean.setRemarks("");
            displayRemarks = View.GONE;
        }
        if (expenseView != null) {
            expenseView.showOtherExp(displayRemarks, displayPhoto, tvOtherExpense, etOtherExpenses, uom, otherExpanse);
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String s, Bundle bundle) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (expenseView != null) {
                    expenseView.hideProgress();
                    expenseView.showMessage(s);
                }
            }
        });
    }

    @Override
    public void onRequestError(int i, Exception e) {
//        Log.d(TAG, "onRequestError: ");
        e.printStackTrace();
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

    }
}
