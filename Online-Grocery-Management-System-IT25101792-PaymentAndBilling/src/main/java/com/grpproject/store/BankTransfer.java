package com.grpproject.store;

public class BankTransfer extends Payment {

    private String bankName;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

}
