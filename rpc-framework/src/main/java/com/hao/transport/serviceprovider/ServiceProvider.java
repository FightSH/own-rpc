package com.hao.transport.serviceprovider;

public interface ServiceProvider {

    /**
     *
     * @param service
     * @param serviceClass
     * @param serviceName
     */
    void addService(Object service, Class<?> serviceClass,  String serviceName);

    /**
     *
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);

    /**
     *
     * @param service
     * @param serviceName
     */
    void publishService(Object service, String serviceName);


}
