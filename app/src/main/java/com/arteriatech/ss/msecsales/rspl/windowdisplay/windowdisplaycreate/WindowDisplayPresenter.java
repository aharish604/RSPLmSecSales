package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

public interface WindowDisplayPresenter {
    void getWindowDispType();
    void getDistributorsValues();
    void getRegistrationData();
    void getImageFromDb();
    void onSave(WindowDisplayCreateBean windowDisplayCreateBean);
    boolean validateField(WindowDisplayCreateBean windowDisplayCreateBean);


}
