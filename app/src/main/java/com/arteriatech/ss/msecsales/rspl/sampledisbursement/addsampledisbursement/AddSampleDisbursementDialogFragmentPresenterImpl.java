package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

import androidx.fragment.app.Fragment;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;

public class AddSampleDisbursementDialogFragmentPresenterImpl implements AddSampleDisbursementDialogFragmentPresenter {
    Fragment context;
    private String[][] mArrayBrandTypeVal = null;
    private String[][] mArrayOrderedGroup = null;
    AddSampleDisbursementDialogFragmentView addSampleDisbursementDialogFragmentView;
    ArrayList<BrandBean>brandBeansList;

    String previousBrandId="";
    public AddSampleDisbursementDialogFragmentPresenterImpl(Fragment context)
    {
        this.context=context;
        if(context instanceof AddSampleDisbursementDialogFragmentView)
        {
            addSampleDisbursementDialogFragmentView  =(AddSampleDisbursementDialogFragmentView)context;
        }
    }

    @Override
    public void getBrandList(String divisionID) {
        try {
            if(!TextUtils.isEmpty(divisionID)) {
                String mStrConfigQry = Constants.Brands + "?$filter="+Constants.DMSDivision+" eq '"+divisionID+"' &$orderby=" + Constants.BrandDesc;
                //mArrayBrandTypeVal = OfflineManager.getBrandListValuesSampleDisbursement(mStrConfigQry);
                brandBeansList = OfflineManager.getBrandListValues(mStrConfigQry);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayBrandTypeVal == null) {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0] = "";
            mArrayBrandTypeVal[1][0] = "";
        }
        if (addSampleDisbursementDialogFragmentView != null){
          //  addSampleDisbursementDialogFragmentView.DisplayBrandList(mArrayBrandTypeVal);

            addSampleDisbursementDialogFragmentView.DisplayBrandDataList(brandBeansList);
        }

    }
    @Override
    public void getOrderMaterialGroup() {

    }
    @Override
    public void updateMaterialGroup(AddSampleDisbursementModel addSampleDisbursementModel) {

        String orderMatGrpQuery = "";
        previousBrandId=addSampleDisbursementModel.getBrandId();

        if (!TextUtils.isEmpty(previousBrandId) && !previousBrandId.equalsIgnoreCase(Constants.None)) {
            orderMatGrpQuery = Constants.BrandID + " eq '" + previousBrandId + "'";
        }
        try {
            if (!TextUtils.isEmpty(orderMatGrpQuery)) {
                orderMatGrpQuery = Constants.OrderMaterialGroups + "?$orderby="+Constants.OrderMaterialGroupDesc+" &$filter=" + orderMatGrpQuery;
            } else {
                orderMatGrpQuery = Constants.OrderMaterialGroups+"?$orderby="+Constants.OrderMaterialGroupDesc;
            }
            mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroupsSampleDisbursement(orderMatGrpQuery);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayOrderedGroup == null) {
            mArrayOrderedGroup = new String[2][0];
            mArrayOrderedGroup[0][0] = "";
            mArrayOrderedGroup[1][0] = "";
        }
        if (addSampleDisbursementDialogFragmentView != null)
            addSampleDisbursementDialogFragmentView.DisplaygetOrderedMaterialGroupsTemp(mArrayOrderedGroup);

    }

    @Override
    public void start() {

    }


}
