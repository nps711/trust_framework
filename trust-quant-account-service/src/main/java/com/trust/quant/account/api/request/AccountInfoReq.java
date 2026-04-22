package com.trust.quant.account.api.request;

import jakarta.validation.constraints.NotBlank;

public class AccountInfoReq {

    @NotBlank
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
