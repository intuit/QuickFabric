package com.intuit.quickfabric.commons.vo;

public class AwsAccountProfile {

    private String accountId;

    private String accountEnv;
    
    private String accountOwner;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountEnv() {
		return accountEnv;
	}

	public void setAccountEnv(String accountEnv) {
		this.accountEnv = accountEnv;
	}

	public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }
}
