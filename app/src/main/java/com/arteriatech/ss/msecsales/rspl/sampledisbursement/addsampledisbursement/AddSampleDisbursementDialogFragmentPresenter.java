package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

public interface AddSampleDisbursementDialogFragmentPresenter {
    void getBrandList(String divisionID);
    void getOrderMaterialGroup();
    void updateMaterialGroup(AddSampleDisbursementModel addSampleDisbursementModel);
    void start();

}
