package com.intuit.quickfabric.commons.vo;

public class BootstrapParametersRequest{

    private String dnsName;
    private String headlessUser;

    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    public String getHeadlessUser() {
        return headlessUser;
    }

    public void setHeadlessUser(String headlessUser) {
        this.headlessUser = headlessUser;
    }
}
