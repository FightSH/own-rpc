package com.hao.registry;

public class RpcServiceProperties {

    /**
     * service version
     */
    private String version;
    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group;
    private String serviceName;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public RpcServiceProperties() {
    }

    public RpcServiceProperties(String version, String group, String serviceName) {
        this.version = version;
        this.group = group;
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "RpcServiceProperties{" +
                "version='" + version + '\'' +
                ", group='" + group + '\'' +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }

    public String toRpcServiceInfo() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }


}
