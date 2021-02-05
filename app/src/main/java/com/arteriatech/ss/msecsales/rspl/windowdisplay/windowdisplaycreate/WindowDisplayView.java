package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

import java.util.ArrayList;

public interface WindowDisplayView {
    void setWindowDisplayType(String [][] data);
    void setWindowSizeType(String [][] data);
    void setDistributorValues(String [][] data);
    void setgetRegistrationData(String [][] data);
    void setContractImageList(ArrayList<ExpenseImageBean> data);
    void setSelfImageBeanList(ArrayList<ExpenseImageBean> data);
    void errorDisplayType(String s);
    void errorSizeType(String s);
    void errorLenght(String s);
    void errorwidht(String s);
    void errorheight(String s);
    void errorRemarks(String s);
    void navigateToRetDetailsActivity();
}
