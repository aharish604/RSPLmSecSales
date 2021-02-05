package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.sampledisbursement.SampleDisbursementActivity;
import com.arteriatech.ss.msecsales.rspl.sampledisbursement.SampleDisbursementPresenter;
import com.arteriatech.ss.msecsales.rspl.sampledisbursement.SampleDisbursementView;

import java.util.ArrayList;

import static com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement.AddSampleDisbursementActivity.SD_RESULT_ID;

public class AddSampleDisbursementPresenterImple implements AddSampleDisbursementPresenter {
    Context context;
    Activity activity;
    AddSampleDisbursementView  sampleDisbursementView;
    private ArrayList<RetailerStockBean> retailerSearchStockBeanArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanTotalArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> finalRetailerStockBean = new ArrayList<>();
    DMSDivisionBean dmsDivisionBean;
        String mStrSelBrandId = "";
        String mStrSelCategoryId = "";
        String mStrSelOrderMaterialID = "";
    public AddSampleDisbursementPresenterImple(Context context, ArrayList<RetailerStockBean> retailerStockBeanTotalArrayList,Activity activity) {

    this.context=context;
    this.retailerStockBeanTotalArrayList=retailerStockBeanTotalArrayList;
    this.activity=activity;
    if(context instanceof AddSampleDisbursementView)
    {
        sampleDisbursementView=(AddSampleDisbursementView)context;
    }
    }

    @Override
    public void start() {

    }

    @Override
    public void filter(final String text, final ArrayList<RetailerStockBean> filterStockBeanPopupArrayLis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sampleDisbursementView != null) {
                            sampleDisbursementView.showProgress();
                        }

                    }
                });
                retailerSearchStockBeanArrayList.clear();
                if (TextUtils.isEmpty(text)) {
                    retailerSearchStockBeanArrayList.addAll(filterStockBeanPopupArrayLis);
                } else {
                    for (RetailerStockBean item : filterStockBeanPopupArrayLis) {

                        if (item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())) {
                            retailerSearchStockBeanArrayList.add(item);
                        }
                    }
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sampleDisbursementView != null) {
                            sampleDisbursementView.hideProgress();
                            sampleDisbursementView.displayList(retailerSearchStockBeanArrayList);
                        }

                    }
                });

            }
        }).start();

    }

    @Override
    public void sendResultToOtherActivity(final ArrayList<RetailerStockBean> list) {

        finalRetailerStockBean.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (RetailerStockBean retailerStockBean : list) {
                    if (retailerStockBean.getSelected()) {
                        finalRetailerStockBean.add(retailerStockBean);
                    }
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalRetailerStockBean.size()>0) {
                            Intent intent = new Intent(context, SampleDisbursementActivity.class);
                            intent.putExtra(ConstantsUtils.EXTRA_ARRAY_LIST, finalRetailerStockBean);
                            activity.setResult(SD_RESULT_ID, intent);
                            activity.finish();
                        }else{
                            UtilConstants.showAlert(activity.getString(R.string.validation_sel_atlest_one_material), activity);
                        }

                    }
                });
            }
        }).start();

    }

    @Override
    public void onFragmentInteraction(DMSDivisionBean dmsDivisionBean, String distributor, String divisionName, String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {


        filterType(divisionName, brandName, categoryName, creskuGrpName);
    }
    private void filterType(String divisionName, String brandName, String categoryName, String creskuGrpName) {
        try {
            String filteredResult = "";
            if (!TextUtils.isEmpty(divisionName)) {
                filteredResult = ", " + divisionName;
            }
            if (!TextUtils.isEmpty(brandName) && !brandName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + brandName;
            }
            if (!TextUtils.isEmpty(categoryName) && !categoryName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + categoryName;
            }
            if (!TextUtils.isEmpty(creskuGrpName) && !creskuGrpName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + creskuGrpName;
            }
            if (sampleDisbursementView != null) {
                sampleDisbursementView.setFilterDate(filteredResult);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
