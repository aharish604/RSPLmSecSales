package com.arteriatech.ss.msecsales.rspl.expense;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ExpenseDailyPresenterImpl implements ExpenseDailyPresenter, OnlineODataInterface {

    private String expenseFreq = "";
    private ExpenseDailyView expenseView = null;
    private Context mContext;
    private int totalRequest = 0;
    private int currentRequest = 0;
    private ArrayList<RetailerBean> beatList = new ArrayList<>();
    private ArrayList<ValueHelpBean> locationList = new ArrayList<>();
    private ArrayList<ValueHelpBean> convenyanceList = new ArrayList<>();
    private ArrayList<ExpenseConfig> expenseConfigArrayList = new ArrayList<>();
    private ArrayList<ExpenseBean> expenseBeanList = new ArrayList<>();
    private String[][] mArraySalesPerson = null, mArrayDefaultExpenseAllowance = null;
    ;
    private String mStrSeleExpenseTypeId = "", mStrSeleExpenseTypeDesc = "", stBeatId = "", stBeatDesc = "", stBeatGUID = "", stLocation = "", stLocationDesc = "", stNonBeatType = "", stNonBeatTypeDesc = "", stMode = "", stModeDesc = "", mStrExpDate = "", stDistanceValue = "", stFarTotal = "", stDailyAllowance = "", stDailyTypeAllowance = "", stAmountCatType = "", stMaxAllowancePer = "", stOtherConv = "";
    private Hashtable masterHashTable = new Hashtable();
    private ArrayList<HashMap<String, String>> arrItemTable = new ArrayList<HashMap<String, String>>();
    private String mStrCurrentDate="";

    public ExpenseDailyPresenterImpl(Context mContext, Bundle bundle, ExpenseDailyView expenseView) {
        this.mContext = mContext;
        this.expenseView = expenseView;
        if (bundle != null) {
            expenseFreq = bundle.getString(Constants.ExpenseFreq);
        }
    }

    /*check daily/monthly already created*/
    public static boolean validateAlreadyDataSaved(Context mContext, String expenseFreq, String date, String expenseType) {
        boolean isDataPresent = false;
        String query = "";
        if (!expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            try {
                query = Constants.Expenses + "?$filter=ExpenseDate eq datetime'" + UtilConstants.getTimeformat2(date, "") + "' and ExpenseType eq '" + expenseType + "'";
                isDataPresent = OfflineManager.getVisitStatusForCustomer(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isDataPresent) {
                isDataPresent = OfflineManager.checkDatavaltDataisPresent(mContext, date, expenseType);
            }
        } else {
            int pastMonth = 0;
            try {
                String[] splitDate = date.split("-");
                pastMonth = Integer.parseInt(splitDate[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            query = Constants.Expenses + "?$filter= month(ExpenseDate) eq " + pastMonth + " and ExpenseType eq '" + expenseType + "'";
            try {
                isDataPresent = OfflineManager.getVisitStatusForCustomer(query);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            if (!isDataPresent) {
                try {
                    isDataPresent = OfflineManager.checkDatavaltMonthCompare(mContext, date, expenseType);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return isDataPresent;
    }

    @Override
    public void onStart() {
        if (expenseView != null) {
            expenseView.showProgress();
        }
        totalRequest = 4;
        currentRequest = 0;
        String mStrQry = Constants.RouteSchedules;
        ConstantsUtils.onlineRequest(mContext, mStrQry, false, 1, ConstantsUtils.SESSION_HEADER, this, false);
        mStrQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.Location + "'";
        ConstantsUtils.onlineRequest(mContext, mStrQry, false, 2, ConstantsUtils.SESSION_HEADER, this, false);
        mStrQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ConvenyanceMode + "'";
        ConstantsUtils.onlineRequest(mContext, mStrQry, false, 3, ConstantsUtils.SESSION_HEADER, this, false);
        mStrQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "'";
        ConstantsUtils.onlineRequest(mContext, mStrQry, false, 4, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void onDestroy() {
        expenseView = null;
    }

    @Override
    public void expenseConfig(ExpenseConfig expenseConfig) {
        mStrSeleExpenseTypeId = expenseConfig.getExpenseType();
        mStrSeleExpenseTypeDesc = expenseConfig.getExpenseTypeDesc();
        if (expenseView != null) {
            expenseView.setUIBasedOnType(mStrSeleExpenseTypeId);
        }

        stDailyAllowance = "";
        stFarTotal = "";
        stBeatGUID = "";
        stLocation = "";
        stLocationDesc = "";
        stMode = "";
        stModeDesc = "";
        stNonBeatType = "";
        stNonBeatTypeDesc = "";
        stDistanceValue = "";

        String mStrQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and " + Constants.DefaultItemCat +
                " eq '000010' and (ExpenseItemType eq '0000000002' or ExpenseItemType eq '0000000003') &$orderby = ExpenseItemType asc &$top=2";
        ConstantsUtils.onlineRequest(mContext, mStrQry, false, 5, ConstantsUtils.SESSION_HEADER, this, false);
        displayValidation();
    }

    @Override
    public void onModeConvItemSel(ValueHelpBean valueHelpBean) {
        stMode = valueHelpBean.getID();
        stModeDesc = valueHelpBean.getDescription();
        displayAndSetValue();
    }

    @Override
    public void onNonBeatItemSel(String[][] arrayExpNonBeatTypeVal, int position) {
        stNonBeatType = arrayExpNonBeatTypeVal[0][position];
        stNonBeatTypeDesc = arrayExpNonBeatTypeVal[1][position];
        displayAndSetValue();
    }

    @Override
    public void onBeatWorkItemSel(ValueHelpBean valueHelpBean) {
        stLocation = valueHelpBean.getID();
        stLocationDesc = valueHelpBean.getDescription();
        displayAndSetValue();
    }

    @Override
    public void onBeatNameItemSel(RetailerBean retailerBean) {
        stBeatId = retailerBean.getRouteID();
        stBeatDesc = retailerBean.getRouteDesc();
        stBeatGUID = retailerBean.getRschGuid();
        displayAndSetValue();
    }

    @Override
    public void onDailyAllowance(String dailyAllowance) {
        stDailyTypeAllowance = dailyAllowance;
        displayAndSetValue();
    }

    @Override
    public void onDistanceValue(String distance) {
        stDistanceValue = distance;
        displayAndSetValue();
    }

    @Override
    public void onOtherConv(String otherConv) {
        stOtherConv = otherConv;
    }

    @Override
    public void expenseDate(String mStrExpDate) {
        mStrCurrentDate = mStrExpDate;
        displayValidation();
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                beatList = OfflineManager.getBeatList(list, Constants.None);
                currentRequest++;
                break;
            case 2:
                locationList = OfflineManager.getConfigListWithDefaultValAndNone(list, "");
                currentRequest++;
                break;
            case 3:
                convenyanceList = OfflineManager.getConfigListWithDefaultValAndNone(list, "");
                currentRequest++;
                break;
            case 4:
                expenseConfigArrayList = OfflineManager.getConfigExpenseType(list, Constants.None);
                try {
                    mArraySalesPerson = Constants.getDistributors();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;
            case 5:
                expenseBeanList = OfflineManager.getConfigExpense(list, Constants.None);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (expenseView != null) {
                            expenseView.hideProgress();
                            displayAndSetValue();

                        }
                    }
                });
                break;
        }
        if (type != 5) {
            if (totalRequest == currentRequest) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (expenseView != null) {
                            expenseView.hideProgress();
                            expenseView.displayData(beatList, locationList, convenyanceList, expenseConfigArrayList);

                        }
                    }
                });
            }
        }
    }

    private void displayAndSetValue() {
        if (!expenseBeanList.isEmpty()) {
            if (expenseBeanList.size() > 1) {
                ExpenseBean expenseCBean = expenseBeanList.get(0);
                ExpenseBean expenseDBean = expenseBeanList.get(1);
                displayConveyance(expenseCBean);
                displayDailyAllow(expenseDBean);
                setTotalValues();

            }
        }
    }

    private void setTotalValues() {
        String displayTotalValue = "0.0";
        try {
            double intDailyAll = Double.parseDouble(stDailyAllowance);
            double intFareTotal = Double.parseDouble(stFarTotal);
            double totalFinalValue = intDailyAll + intFareTotal;
            displayTotalValue = UtilConstants.removeLeadingZerowithTwoDecimal(totalFinalValue + "") + " " + mArraySalesPerson[10][0];
        } catch (Exception e) {
            e.printStackTrace();
            displayTotalValue = UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0];
        }

        if (expenseView != null) {
            expenseView.showTotalValue(displayTotalValue);
        }
    }

    private void displayDailyAllow(ExpenseBean expenseBean) {
        String dispDailyAllowance = "";
        stAmountCatType = expenseBean.getAmountCategory();
        stMaxAllowancePer = expenseBean.getMaxAllowancePer();
        int etDailyAllowance = View.GONE;
        int tvDailyAllowance = View.GONE;
        String query = Constants.ExpenseAllowances + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and ExpenseItemType eq '0000000003' " +
                "and Location eq '" + stLocation + "' &$top=1";
        if (expenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            try {
                if (!TextUtils.isEmpty(expenseBean.getMaxAllowancePer())) {
                    BigDecimal maxPerBigDecimal = null;
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        decimalFormat.setParseBigDecimal(true);
                        maxPerBigDecimal = (BigDecimal) decimalFormat.parse(expenseBean.getMaxAllowancePer());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                    if (mArrayDefaultExpenseAllowance[0].length > 0) {
                        BigDecimal amountBigDec = null;
                        try {
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                            decimalFormat.setParseBigDecimal(true);
                            amountBigDec = (BigDecimal) decimalFormat.parse(mArrayDefaultExpenseAllowance[0][0]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (amountBigDec != null && maxPerBigDecimal != null) {
                            BigDecimal finalAmount = amountBigDec.divide(new BigDecimal(100.0f)).multiply(maxPerBigDecimal);
                            stDailyAllowance = finalAmount + "";
                            dispDailyAllowance = UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0];
                            expenseBean.setAmount(finalAmount + "");
                        } else {
                            stDailyAllowance = "";
                            dispDailyAllowance = UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0];
                            expenseBean.setAmount("0.0");
                        }
                    } else {
                        stDailyAllowance = "";
                        dispDailyAllowance = UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0];
                        expenseBean.setAmount("0.0");
                    }
                } else {
                    stDailyAllowance = "";
                    dispDailyAllowance = UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0];
                    expenseBean.setAmount("0.0");
                }
            } catch (Exception e) {
                e.printStackTrace();
                stDailyAllowance = "";
                dispDailyAllowance = UtilConstants.removeLeadingZerowithTwoDecimal("");
                expenseBean.setAmount("0.0");
            }
            etDailyAllowance = View.GONE;
            tvDailyAllowance = View.VISIBLE;
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            etDailyAllowance = View.VISIBLE;
            tvDailyAllowance = View.GONE;
            try {
                mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    double enteredValue = Double.parseDouble(stDailyTypeAllowance);
                    double maxValuePer = Double.parseDouble(stMaxAllowancePer);
                    double finalAmount = (enteredValue / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
                        stDailyAllowance = "";
                    } else {
                        stDailyAllowance = finalAmount + "";
                    }
                } else {
                    stDailyAllowance = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                stDailyAllowance = "";
            }
            expenseBean.setAmount(stDailyTypeAllowance);
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000010")) {
            etDailyAllowance = View.VISIBLE;
            tvDailyAllowance = View.GONE;
            stDailyAllowance = stDailyTypeAllowance;
            expenseBean.setAmount(stDailyTypeAllowance);
        }
        if (expenseView != null) {
            expenseView.showDailyAllowance(dispDailyAllowance, etDailyAllowance, tvDailyAllowance);
        }
    }

    private void displayConveyance(ExpenseBean expenseConveyance) {
        String displayUOM = expenseConveyance.getUOM();
        String displayFarTotal = "0.00";
        String itemType = expenseConveyance.getExpenseItemType();
        String stDistance = stDistanceValue;
        expenseConveyance.setBeatDistance(stDistanceValue);
        expenseConveyance.setConvenyanceMode(stMode);
        expenseConveyance.setConvenyanceModeDs(stModeDesc);
        expenseConveyance.setLocation(stLocation);
        expenseConveyance.setLocationDesc(stLocationDesc);
        String query = Constants.ExpenseAllowances + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and ExpenseItemType eq '" +
                itemType + "' and Location eq '" + stLocation + "' and ConveyanceMode eq '" + stMode + "'";
        try {
            String[][] mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
            if (mArrayDefaultExpenseAllowance[0].length > 0) {
                try {
                    double doubQty = Double.parseDouble(stDistance);
                    double maxValue = Double.parseDouble(mArrayDefaultExpenseAllowance[0][0]);
                    double finalAmount = doubQty * maxValue;
                    stFarTotal = finalAmount + "";
                    displayFarTotal = UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0];
                    expenseConveyance.setAmount(finalAmount + "");
                } catch (Exception e) {
                    e.printStackTrace();
                    stFarTotal = "0.0";
                    displayFarTotal = UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0];
                    expenseConveyance.setAmount("0.0");
                }
            } else {
                stFarTotal = "0.0";
                displayFarTotal = UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0];
                expenseConveyance.setAmount("0.0");
            }

        } catch (Exception e) {
            e.printStackTrace();
            stFarTotal = "0.0";
            displayFarTotal = "0.00";
            expenseConveyance.setAmount("0.0");
        }
        if (expenseView != null) {
            expenseView.showConveyanceAmt(displayFarTotal, displayUOM);
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String s, Bundle bundle) {
        currentRequest++;
        if (totalRequest == currentRequest) {
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
    }

    public void onSaveData(String date, int fiscalYear) {
        String errorMsg = "";
        if (!displayValidation()) {
            int validationCode = checkValidations();
            if (validationCode == 0) {
                saveDataToDataValt(mContext, date, fiscalYear);
            } else if (validationCode == 1) {
                errorMsg = mContext.getString(R.string.validation_plz_fill_mandatory_flds);
            } else if (validationCode == 2) {
                errorMsg = mContext.getString(R.string.validation_plz_fill_mandatory_flds);
            } else if (validationCode == 3) {
                errorMsg = mContext.getString(R.string.expense_error_enter_valid_amount);
            } else if (validationCode == 4) {
                errorMsg = mContext.getString(R.string.expense_error_amount);
            }
            if (validationCode > 0) {
                if (expenseView != null) {
                    expenseView.showMessage(errorMsg);
                }
            }
        }
    }
    private boolean displayValidation(){
        boolean isValid = checkValidationAndShowDialogs();
        if (isValid){
            if(expenseView!=null){
                expenseView.showMessage("Expense already submited for " + mStrSeleExpenseTypeDesc);
            }
        }
        return isValid;
    }
    private void saveDataToDataValt(Context context, String date, int fiscalYear) {
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
        masterHashTable.put(Constants.ExpenseDate, date);
        masterHashTable.put(Constants.Status, "");
        masterHashTable.put(Constants.StatusDesc, "");
        masterHashTable.put(Constants.Amount, "0.0");
        masterHashTable.put(Constants.Currency, mArraySalesPerson[10][0]);
        int itemIncVal = 0;
        arrItemTable.clear();
        for (ExpenseBean expenseBean : expenseBeanList) {
            HashMap<String, String> singleItem = new HashMap<String, String>();
            GUID itemGuid = GUID.newRandom();
            singleItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
            singleItem.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
            singleItem.put(Constants.ExpeseItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
            singleItem.put(Constants.LoginID, loginIdVal);
            singleItem.put(Constants.ExpenseItemType, expenseBean.getExpenseItemType());
            singleItem.put(Constants.ExpenseItemTypeDesc, expenseBean.getExpenseItemTypeDesc());
            singleItem.put(Constants.BeatGUID, stBeatGUID);
            singleItem.put(Constants.Location, expenseBean.getLocation());
            singleItem.put(Constants.ConvenyanceMode, expenseBean.getConvenyanceMode());
            if (expenseBean.getConvenyanceMode().equalsIgnoreCase(Constants.Conv_Mode_Type_Other)) {
                singleItem.put(Constants.ConvenyanceModeDs, stOtherConv);
            } else {
                singleItem.put(Constants.ConvenyanceModeDs, expenseBean.getConvenyanceModeDs());
            }

            singleItem.put(Constants.BeatDistance, expenseBean.getBeatDistance());
            singleItem.put(Constants.UOM, expenseBean.getUOM());
            singleItem.put(Constants.Amount, expenseBean.getAmount());
            singleItem.put(Constants.Currency, expenseBean.getCurrency());
            singleItem.put(Constants.Remarks, expenseBean.getRemarks());
            arrItemTable.add(singleItem);
            itemIncVal++;
        }
        masterHashTable.put(Constants.entityType, Constants.Expenses);
        masterHashTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrItemTable));
        Constants.saveDeviceDocNoToSharedPref(context, Constants.Expenses, doc_no);
        JSONObject jsonHeaderObject = new JSONObject(masterHashTable);
        ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),context);
        if (expenseView != null) {
            expenseView.showSuccessMsg(mContext.getString(R.string.expense_daily_created_success, mContext.getString(R.string.expense_beat_non_beat_work)));
        }
    }

    private boolean checkValidationAndShowDialogs() {
        return validateAlreadyDataSaved(mContext, expenseFreq, mStrCurrentDate, mStrSeleExpenseTypeId);
    }
    /*get day/month config*/
    public static int getDayMonthConfig(String expenseFreq) {
        String qryStr = "";
        int maxDays = 1;
        if (expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SS + "' and " + Constants.Types + " eq '" + Constants.MAXEXPALWM + "' ";
//            qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq 'SF' and " + Constants.Types + " eq '" + Constants.MAXEXPALWM + "' ";
        } else {
            qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SS + "' and " + Constants.Types + " eq '" + Constants.MAXEXPALWD + "' ";
//            qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq 'SF' and " + Constants.Types + " eq '" + Constants.MAXEXPALWD + "' ";
        }
        try {
            String mStrDays = OfflineManager.getConfigValue(qryStr);
            maxDays = Integer.parseInt(mStrDays);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxDays;
    }
    private int checkValidations() {
        int validationCode = 0;
        if (TextUtils.isEmpty(mStrSeleExpenseTypeId)) {
            validationCode = 1;
            if (expenseView != null) {
                expenseView.errorExpenseType("Select Expense Type");
            }
//            spExpenseType.setBackgroundResource(R.drawable.error_spinner);
        }
        if (mStrSeleExpenseTypeId.equalsIgnoreCase("000010")) {
            if (TextUtils.isEmpty(stBeatId)) {
                validationCode = 1;
                if (expenseView != null) {
                    expenseView.errorBeatName("Select Beat Name");
                }
//                spBeatName.setBackgroundResource(R.drawable.error_spinner);
            }
        } else {
            if (TextUtils.isEmpty(stNonBeatType)) {
                validationCode = 1;
                if (expenseView != null) {
                    expenseView.errorBeatName("Select Non Beat Work Type");
                }
//                spNonBeatType.setBackgroundResource(R.drawable.error_spinner);
            }

        }
        if (TextUtils.isEmpty(stLocation)) {
            validationCode = 1;
            if (expenseView != null) {

                if (mStrSeleExpenseTypeId.equalsIgnoreCase("000010"))
                    expenseView.errorBeatWork("Select Beat Work At");
                else
                    expenseView.errorBeatWork("Select Non Beat Work At");
            }
//            spBeatWorkAt.setBackgroundResource(R.drawable.error_spinner);
        }
        if (TextUtils.isEmpty(stMode)) {
            validationCode = 1;
            if (expenseView != null) {
                expenseView.errorMode("Select Mode of Conveyance");
            }
//            spModeConve.setBackgroundResource(R.drawable.error_spinner);
        }
        if (!TextUtils.isEmpty(stMode)) {
            if (stMode.equalsIgnoreCase(Constants.Conv_Mode_Type_Other)) {
                if (TextUtils.isEmpty(stOtherConv) || stOtherConv.trim().equalsIgnoreCase("")) {
                    validationCode = 1;
                    if (expenseView != null) {
                        expenseView.errorConvMode("Enter Conveyance Mode");
                    }
//                    et_other_conv.setBackgroundResource(R.drawable.edittext_border);
                }
            }
        }


        if (TextUtils.isEmpty(stDistanceValue)) {
            validationCode = 1;
            if (expenseView != null) {
                expenseView.errorDistance("Enter Distance");
            }
//            etDistance.setBackgroundResource(R.drawable.edittext_border);
        } else if (stDistanceValue.equals(".") || stDistanceValue.equals("0")) {
            validationCode = 1;
            if (expenseView != null) {
                expenseView.errorDistance("Enter Distance");
            }
//            etDistance.setBackgroundResource(R.drawable.edittext_border);
        } else {

            try {
                BigDecimal bDistance = null;
                try {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    decimalFormat.setParseBigDecimal(true);
                    bDistance = (BigDecimal) decimalFormat.parse(stDistanceValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (bDistance == null || bDistance.compareTo(new BigDecimal("0.0")) != 1) {
                    validationCode = 1;
                    if (expenseView != null) {
                        expenseView.errorDistance("Enter Valid Distance");
                    }
//                    etDistance.setBackgroundResource(R.drawable.edittext_border);
                }
            } catch (Exception e) {
                e.printStackTrace();
                validationCode = 1;
                if (expenseView != null) {
                    expenseView.errorDistance("Enter Valid Distance");
                }

//                etDistance.setBackgroundResource(R.drawable.edittext_border);
            }
        }
        if (stAmountCatType.equalsIgnoreCase("000020")) {
            if (TextUtils.isEmpty(stDailyTypeAllowance)) {
                validationCode = 1;
                if (expenseView != null) {
                    expenseView.errorDailyAllonce("Enter Daily Allowance");
                }
//                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            } else {
                try {
                    double doubQty = Double.parseDouble(stDailyTypeAllowance);
                    double maxValuePer = Double.parseDouble(stMaxAllowancePer);
                    double finalAmount = (doubQty / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
                        validationCode = 4;
                        if (expenseView != null) {
                            expenseView.errorDailyAllonce("Enter Valid Daily Allowance");
                        }
//                        etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                    } else {
                        expenseBeanList.get(1).setAmount(finalAmount + "");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    validationCode = 3;
                    expenseBeanList.get(1).setAmount("0.0");
                    if (expenseView != null) {
                        expenseView.errorDailyAllonce("Enter Valid Daily Allowance");
                    }
//                    etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                }
            }

        } else if (stAmountCatType.equalsIgnoreCase("000010")) {
            if (TextUtils.isEmpty(stDailyTypeAllowance)) {
                validationCode = 1;
                if (expenseView != null) {
                    expenseView.errorDailyAllonce("Enter Daily Allowance");
                }
//                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            }
        }

        return validationCode;
    }
}
