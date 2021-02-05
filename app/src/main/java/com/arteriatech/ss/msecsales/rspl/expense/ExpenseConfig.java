package com.arteriatech.ss.msecsales.rspl.expense;

public class ExpenseConfig {

    private String ExpenseType = "";
    private String ExpenseTypeDesc = "";

    public String getExpenseType() {
        return ExpenseType;
    }

    public void setExpenseType(String expenseType) {
        ExpenseType = expenseType;
    }

    public String getExpenseTypeDesc() {
        return ExpenseTypeDesc;
    }

    public void setExpenseTypeDesc(String expenseTypeDesc) {
        ExpenseTypeDesc = expenseTypeDesc;
    }

    @Override
    public String toString() {
        return ExpenseTypeDesc.toString();
    }
}
