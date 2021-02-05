package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;

import java.util.ArrayList;

public interface AddSampleDisbursementDialogFragmentView {

    void DisplayBrandList(String [][] brandList);
    void DisplayBrandDataList(ArrayList<BrandBean> brandList);
    void DisplaygetOrderedMaterialGroupsTemp(String [][] orderedmaterialList);

}
