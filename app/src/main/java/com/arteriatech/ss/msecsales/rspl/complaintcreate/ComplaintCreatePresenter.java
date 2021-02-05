package com.arteriatech.ss.msecsales.rspl.complaintcreate;

public interface ComplaintCreatePresenter {

    void onStart(String mStrBundleRetID, String RetailerName, String mStrBundleCPGUID32, String mStrBundleCPGUID);

    void onDestroy();

    boolean validateFields(ComplaintCreateBean complaintCreateBean);

    void onAsignData(String save, ComplaintCreateBean collectionBean);

    void onSaveCustComp(ComplaintCreateBean complaintCreateBean);
    void onSaveData();
    void getLocation();
    void locationPerGranted();
    void finalSaveCondition();
    void onSave();
    void requestCompliantOrderMaterialType();
    void requestComplaintCategoryType();
    void requestComplaintType(ComplaintCreateBean complaintCreateBean);
    void requestItemDescription(ComplaintCreateBean collectionBean);
    void requestUOM(ComplaintCreateBean collectionBean);

    void onImageStart();

}
