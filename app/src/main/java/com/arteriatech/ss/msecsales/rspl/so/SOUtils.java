package com.arteriatech.ss.msecsales.rspl.so;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.interfaces.CustomDialogCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.mbo.CustomerPartnerFunctionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterModelImpl;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by e10769 on 08-05-2017.
 */

public class SOUtils {

    public static String getStartDate(Context mContext, String type) {
        Calendar currentDate = Calendar.getInstance();
        if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 30);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 1);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_current_mont))) {
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_next_mont))) {
            currentDate.add(Calendar.MONTH, 1);
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }else {//if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days)))
            currentDate.set(Calendar.DAY_OF_MONTH, (currentDate.get(Calendar.DAY_OF_MONTH)) - 7);
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }
    }

    public static String getEndDate(Context mContext, String type) {
        Calendar currentDate = Calendar.getInstance();
//        currentDate.set(Calendar.DAY_OF_MONTH,-30);
        if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_one_month))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_today_and_yesterday))) {
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_current_mont))) {
            currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_next_mont))) {
            currentDate.add(Calendar.MONTH, 1);
            currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        } else {//if (type.equalsIgnoreCase(mContext.getString(R.string.so_filter_last_seven_days)))
            return setFromDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }
    }

    private static String setFromDate(int mYear, int mMonth, int mDay) {
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        return mYear + "-" + mon + "-" + day + "T00:00:00";
    }

    public static boolean isStartDateIsSmall(String startDate, String endDate) {
        try {
            String[] arrD1 = startDate.split("T0");
            String[] arrD2 = endDate.split("T0");
            String d1 = arrD1[0];
            String d2 = arrD2[0];
            SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
            boolean b = false;
            try {
                if (dfDate.parse(d1).before(dfDate.parse(d2))) {
                    b = true;//If start date is before end date
                } else if (dfDate.parse(d1).equals(dfDate.parse(d2))) {
                    b = true;//If two dates are equal
                } else {
                    b = false; //If start date is after the end date
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static Drawable displayStatusIcon(String status, Context mContext) {
        Drawable img = null;
        switch (status) {
            case "A"://OK
//                img = VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_assignment_black_24dp, null);
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp).mutate();
                break;
            case "B"://Pending for approval
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "C"://Approved
                img =ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "D"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            case InvoiceFilterModelImpl.STATUS_POSTED:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_receipt_black_24dp).mutate().mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusRed), PorterDuff.Mode.SRC_IN);
                break;
            case InvoiceFilterModelImpl.STATUS_CONFIRMED:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_receipt_black_24dp).mutate().mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusOrange), PorterDuff.Mode.SRC_IN);
                break;
            case InvoiceFilterModelImpl.STATUS_PARTIALLY_PAID:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_account_balance_wallet_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusOrange), PorterDuff.Mode.SRC_IN);
                break;
            case InvoiceFilterModelImpl.STATUS_COMPLETELY_PAID:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_account_balance_wallet_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusGreen), PorterDuff.Mode.SRC_IN);
                break;
            case InvoiceFilterModelImpl.STATUS_COMPLETELY_RETURN:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusGreen), PorterDuff.Mode.SRC_IN);
                break;
            case InvoiceFilterModelImpl.STATUS_PARTIALLY_RETURN:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.InvStatusOrange), PorterDuff.Mode.SRC_IN);
                break;
            case InvoiceFilterModelImpl.STATUS_CANCELED:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_cancel_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
//                mDrawable.setColorFilter(new
//                        PorterDuffColorFilter(0xff2196F3,PorterDuff.Mode.SRC_IN));
//                img = VectorDrawableCompat.create(mContext.getResources(), R.drawable.ic_transparent, null);
                break;
        }
        return img;
    }
    public static Drawable displayDelvStatusIcon(String delvStatus, Context mContext) {
        Drawable img=null;
        switch (delvStatus) {
            case "A"://Open
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp);
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.OpenColor), PorterDuff.Mode.SRC_IN);
                break;
            case "B"://Partially processed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp);
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PartialyClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "C"://Closed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp);
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.ClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "D"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp);
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "F"://Not relevant
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_forever_black_24dp);
                break;
            default:
                img = ContextCompat.getDrawable(mContext,R.drawable.ic_transparent);
                break;
        }
        return img;
    }
    public static Drawable displayStatusImage(String status, String delvStatus, Context mContext) {
        Drawable img = null;
        switch (status) {
            case "000001"://Open
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                if (img != null)
                    img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "000002"://Partially Processed
                int resImage = R.drawable.ic_local_shipping_black_24dp;
                int resColor = R.color.PartialyClosedColor;
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "000003"://Bill Generated
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "000004"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }

    public static Drawable returnGrStatus(String delvStatus, Context mContext) {
        Drawable img = null;
        switch (delvStatus) {
            case "A"://Open
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.OpenColor), PorterDuff.Mode.SRC_IN);
                break;
            case "20"://Partially processed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PartialyClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "30"://Closed
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ClosedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "10"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.OpenColor), PorterDuff.Mode.SRC_IN);
                break;
            case "F"://Not relevant
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_forever_black_24dp).mutate();
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }

    public static Drawable displayReturnStatus(String status, Context mContext) {
        Drawable img = null;
        switch (status) {
            case "20"://OK
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "10"://Pending for approval
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.PendingApprovalColor), PorterDuff.Mode.SRC_IN);
                break;
            case "30"://Approved
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "40"://Credit note
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_receipt_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "50"://Replacement
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_add_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.ApprovedColor), PorterDuff.Mode.SRC_IN);
                break;
            case "60"://Rejected
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_remove_shopping_cart_black_24dp).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
                break;
        }
        return img;
    }

    /*get address from odata*/
    public static String getAddressValue(ODataPropMap properties) {
        String address = "";
        ODataProperty property;
        property = properties.get(Constants.Address1);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                address = ad1;
            }
        }
        property = properties.get(Constants.Address2);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.Address3);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        property = properties.get(Constants.Address4);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.District);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.City);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.RegionDesc);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.CountryDesc);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.PostalCode);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + " - " + ad1;
                else
                    address = ad1;
            }
        }
        return address;
    }
    /*get address from array list*/
    public static String getAddressValue(CustomerPartnerFunctionBean customerPartnerFunctionBean) {
        String address = "";
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress1())) {
            String ad1 = customerPartnerFunctionBean.getAddress1();
            if (!TextUtils.isEmpty(ad1)) {
                address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress2())) {
            String ad1 = customerPartnerFunctionBean.getAddress2();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress3())) {
            String ad1 = customerPartnerFunctionBean.getAddress3();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getAddress4())) {
            String ad1 = customerPartnerFunctionBean.getAddress4();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getDistrict())) {
            String ad1 = customerPartnerFunctionBean.getDistrict();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getCityID())) {
            String ad1 = customerPartnerFunctionBean.getCityID();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getRegionDesc())) {
            String ad1 = customerPartnerFunctionBean.getRegionDesc();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getCountryDesc())) {
            String ad1 = customerPartnerFunctionBean.getCountryDesc();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        if (!TextUtils.isEmpty(customerPartnerFunctionBean.getPostalCode())) {
            String ad1 = customerPartnerFunctionBean.getPostalCode();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + " - " + ad1;
                else
                    address = ad1;
            }
        }
        return address;
    }

    public static Drawable getSODefaultDrawable(Context mContext) {
        Drawable img = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart_black_24dp).mutate();
        if (img != null)
            img.setColorFilter(ContextCompat.getColor(mContext, R.color.secondaryColor), PorterDuff.Mode.SRC_IN);
        return img;
    }

    public static Drawable displayInvoiceStatusImage(String status, String delvStatus, Context mContext) {
        Drawable img = null;
        int resImage;
        int resColor;
        if (delvStatus.equalsIgnoreCase("02")){//cancel
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_cancel_black_24dp).mutate();
            img.setColorFilter(ContextCompat.getColor(mContext, R.color.RejectedColor), PorterDuff.Mode.SRC_IN);
            return img;
        }
        switch (status) {
            case "01"://open
                resImage = R.drawable.ic_receipt_black_24dp;
                resColor = R.color.StatusDefaultColor;
                if (delvStatus.equalsIgnoreCase("01")) {
                    resImage = R.drawable.ic_receipt_black_24dp;
                    resColor = R.color.InvStatusRed;
                }else if (delvStatus.equalsIgnoreCase("03")) {
                    resImage = R.drawable.ic_receipt_black_24dp;
                    resColor = R.color.InvStatusOrange;
                }
                else if (delvStatus.equalsIgnoreCase("04")) {
                    resImage = R.drawable.ic_remove_shopping_cart_black_24dp;
                    resColor = R.color.InvStatusOrange;
                }
                else if (delvStatus.equalsIgnoreCase("05")) {
                    resImage = R.drawable.ic_remove_shopping_cart_black_24dp;
                    resColor = R.color.InvStatusGreen;
                }
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "02"://Partially processed
                /*resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                resColor = R.color.StatusDefaultColor;
                if (delvStatus.equalsIgnoreCase("C")) {//over Due
                    resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                    resColor = R.color.InvStatusRed;
                } else if (delvStatus.equalsIgnoreCase("B")) {//not due
                    resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                    resColor = R.color.InvStatusGreyColor;
                } else if (delvStatus.equalsIgnoreCase("A")) {//near due
                    resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                    resColor = R.color.InvStatusOrange;
                }*/
                resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                resColor = R.color.PartialyClosedColor;
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            case "03"://closed
                resImage = R.drawable.ic_account_balance_wallet_black_24dp;
                resColor = R.color.InvStatusGreen;
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
                break;
            default:
                img = ContextCompat.getDrawable(mContext, R.drawable.ic_transparent).mutate();
                break;
        }
        return img;
    }
    public static Drawable displayDueStatusInvoiceFilter(String delvStatus, Context mContext) {
        Drawable img = null;
        int resImage=0;
        int resColor=0;
//        resImage = R.drawable.ic_stop_black_24dp;
//        resColor = R.color.StatusDefaultColor;
        if (delvStatus.equalsIgnoreCase("01")) {//over Due
            resImage = R.drawable.ic_stop_black_24dp;
            resColor = R.color.InvStatusRed;
        } else if (delvStatus.equalsIgnoreCase("02")) {//not due
            resImage = R.drawable.ic_stop_black_24dp;
            resColor = R.color.InvStatusGreyColor;
        } else if (delvStatus.equalsIgnoreCase("03")) {//near due
            resImage = R.drawable.ic_stop_black_24dp;
            resColor = R.color.InvStatusOrange;
        }
        if (resImage!=0) {
            img = ContextCompat.getDrawable(mContext, resImage).mutate();
            img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
        }
        return img;
    }
    public static String getCustomerDetailsAddressValue(ODataPropMap properties) {
        String address = "";
        ODataProperty property;
        property = properties.get(Constants.Address1);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                address = ad1;
            }
        }
        property = properties.get(Constants.Address2);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.Address3);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }

        property = properties.get(Constants.Address4);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.DistrictDesc);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }

       /* property = properties.get(Constants.Landmark);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }*/
        property = properties.get(Constants.CityDesc);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        /*property = properties.get(Constants.RegionDesc);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }*/

        property = properties.get(Constants.PostalCode);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "-" + ad1;
                else
                    address = ad1;
            }
        }

        property = properties.get(Constants.MobileNo);
        if (property != null) {
            String ad1 = (String) property.getValue()!=null?(String) property.getValue():"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        /*property = properties.get(Constants.Tax1);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }
        property = properties.get(Constants.TaxRegStatusDesc);
        if (property != null) {
            String ad1 = property.getValue().toString();
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }*/
        return address;
    }
    public static int getPoss(String[][] arraData, String id, int takePos) {
        if (arraData != null) {
            if (arraData[takePos].length > 0) {
                for (int i = 0; i < arraData[takePos].length; i++) {
                    if (arraData[takePos][i].equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public static int getDivisionPos(ArrayList<DMSDivisionBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getDistributorId().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public static int getBrandPos(ArrayList<BrandBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getBrandID().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public static int getOrderMatPos(ArrayList<SKUGroupBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getOrderMaterialGroupID().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public static int getConfigTypeset(List<ConfigTypesetTypesBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getTypes().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    public static int getValueHelpset(List<ValueHelpBean> arraData, String id) {
        if (arraData != null) {
            if (arraData.size() > 0) {
                for (int i = 0; i < arraData.size(); i++) {
                    if (arraData.get(i).getID().equalsIgnoreCase(id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static Drawable displayCollectionStatusImage(String status, Context mContext) {
        Drawable img = null;
        Double amountVal = null;
        try {
            amountVal = Double.parseDouble(status);
        } catch (NumberFormatException e) {
            amountVal = 0.0;
            e.printStackTrace();
        }
        if(amountVal>0){
            int resImage = R.drawable.ic_partial_coll_black_24dp;
            int resColor = R.color.PartialyClosedColor;
            img = ContextCompat.getDrawable(mContext, resImage).mutate();
            img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
        }else{
            int resImage = R.drawable.ic_done_black_24dp;
            int resColor = R.color.ClosedColor;
            img = ContextCompat.getDrawable(mContext, resImage).mutate();
            img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
        }
        return img;
    }

    public static int displayCollectionStatusColor(String status, Context mContext) {
        Double amountVal = null;
        try {
            amountVal = Double.parseDouble(status);
        } catch (NumberFormatException e) {
            amountVal = 0.0;
            e.printStackTrace();
        }
        if(amountVal>0){
            return mContext.getResources().getColor(R.color.InvStatusOrange);
        }else{
            return mContext.getResources().getColor(R.color.InvStatusGreen);
        }
    }

    public static Drawable displayTaxRegStatusImage(double lat,double lang, Context mContext) {
        Drawable img = null;
        try {
            if(lat==0.0 || lang==0.0){
                int resImage = R.drawable.ic_add_location_black_24dp;
                int resColor = R.color.secondaryColor;
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
            }else{
                int resImage = R.drawable.ic_place_black_24dp;
                int resColor = R.color.secondaryColor;
                img = ContextCompat.getDrawable(mContext, resImage).mutate();
                img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
            }
        } catch (Exception e) {
            int resImage = R.drawable.ic_add_location_black_24dp;
            int resColor = R.color.secondaryColor;
            img = ContextCompat.getDrawable(mContext, resImage).mutate();
            img.setColorFilter(ContextCompat.getColor(mContext, resColor), PorterDuff.Mode.SRC_IN);
            e.printStackTrace();
        }
        return img;
    }
    public static void showCommentsDialog(Activity activity, final CustomDialogCallBack customDialogCallBack, String title) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.so_approval_dialog);

        final EditText etComments = (EditText) dialog.findViewById(R.id.etComments);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        Button okButton = (Button) dialog.findViewById(R.id.btYes);
        Button cancleButton = (Button) dialog.findViewById(R.id.btNo);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.cancelDialogCallBack(true, "", etComments.getText().toString());
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (customDialogCallBack != null) {
                    customDialogCallBack.cancelDialogCallBack(false, "", "");
                }
            }
        });

        dialog.show();

    }

    public static Drawable displaySchemeSlabStatusImage(String slabRuleID, Context mContext) {
        Drawable img = null;

        if (slabRuleID.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_monetization_on_black_24dp).mutate();
//            if (img != null)
//                img.setColorFilter(ContextCompat.getColor(mContext, R.color.secondaryColor), PorterDuff.Mode.SRC_IN);
        } else if (slabRuleID.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_loyalty_black_24dp).mutate();
//            if (img != null)
//                img.setColorFilter(ContextCompat.getColor(mContext, R.color.secondaryColor), PorterDuff.Mode.SRC_IN);
        } else {
            img = ContextCompat.getDrawable(mContext, R.drawable.ic_local_shipping_black_24dp).mutate();
//            if (img != null)
//                img.setColorFilter(ContextCompat.getColor(mContext, R.color.secondaryColor), PorterDuff.Mode.SRC_IN);
        }

        return img;
    }

    public static String getCustomerDetailsAddressValue(RetailerCreateBean createRetailerBean) {
        String address = "";
            if (!TextUtils.isEmpty(createRetailerBean.getAddress1())) {
                address = createRetailerBean.getAddress1();
            }
            if (!TextUtils.isEmpty(createRetailerBean.getAddress2())) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + createRetailerBean.getAddress2();
                else
                    address = createRetailerBean.getAddress2();
            }
            if (!TextUtils.isEmpty(createRetailerBean.getAddress3())) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + createRetailerBean.getAddress3();
                else
                    address = createRetailerBean.getAddress3();
            }

            if (!TextUtils.isEmpty(createRetailerBean.getAddress4())) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + createRetailerBean.getAddress4();
                else
                    address = createRetailerBean.getAddress4();
            }
            if (!TextUtils.isEmpty(createRetailerBean.getDistrictDesc())) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + createRetailerBean.getDistrictDesc();
                else
                    address = createRetailerBean.getDistrictDesc();
            }


            if (!TextUtils.isEmpty(createRetailerBean.getCityDesc())) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + createRetailerBean.getCityDesc();
                else
                    address = createRetailerBean.getCityDesc();
            }

            if (!TextUtils.isEmpty(createRetailerBean.getPostalCode())) {
                if (!TextUtils.isEmpty(address))
                    address = address + "-" + createRetailerBean.getPostalCode();
                else
                    address = createRetailerBean.getPostalCode();
            }

        return address;
    }
    public static String getCustomerDetailsAddressValue(JSONObject jsonObject) {
        String address = "";
        if (jsonObject.optString(Constants.Address1)!=null) {
            String ad1 = jsonObject.optString(Constants.Address1)!=null?jsonObject.optString(Constants.Address1):"";
            if (!TextUtils.isEmpty(ad1)) {
                address = ad1;
            }
        }
        if (jsonObject.optString(Constants.Address2)!=null) {
            String ad1 = jsonObject.optString(Constants.Address2)!=null?jsonObject.optString(Constants.Address2):"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }
        if (jsonObject.optString(Constants.Address3)!=null) {
            String ad1 = jsonObject.optString(Constants.Address3)!=null?jsonObject.optString(Constants.Address3):"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }

        if (jsonObject.optString(Constants.Address4)!=null) {
            String ad1 = jsonObject.optString(Constants.Address4)!=null?jsonObject.optString(Constants.Address4):"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }
        if (jsonObject.optString(Constants.DistrictDesc)!=null) {
            String ad1 = jsonObject.optString(Constants.DistrictDesc)!=null?jsonObject.optString(Constants.DistrictDesc):"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "," + ad1;
                else
                    address = ad1;
            }
        }

        if (jsonObject.optString(Constants.CityDesc)!=null) {
            String ad1 = jsonObject.optString(Constants.CityDesc)!=null?jsonObject.optString(Constants.CityDesc):"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        if (jsonObject.optString(Constants.PostalCode)!=null) {
            String ad1 = jsonObject.optString(Constants.PostalCode)!=null?jsonObject.optString(Constants.PostalCode):"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "-" + ad1;
                else
                    address = ad1;
            }
        }

        if (jsonObject.optString(Constants.MobileNo)!=null) {
            String ad1 = jsonObject.optString(Constants.MobileNo)!=null?jsonObject.optString(Constants.MobileNo):"";
            if (!TextUtils.isEmpty(ad1)) {
                if (!TextUtils.isEmpty(address))
                    address = address + "\n" + ad1;
                else
                    address = ad1;
            }
        }

        return address;
    }


}
