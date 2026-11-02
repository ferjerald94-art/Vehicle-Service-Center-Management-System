
package com.vehicleservicecenter.model;

import java.time.LocalDate;

public class ServiceRecord {

    private final int serviceId;
    private final int vehicleId;
    private final String serviceType;
    private final LocalDate serviceDate;
    private final String status;

    public ServiceRecord(int serviceId, int vehicleId,
                         String serviceType, LocalDate serviceDate,
                         String status) {

        this.serviceId = serviceId;
        this.vehicleId = vehicleId;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.status = status;
    }

    public int getServiceId() {
        return serviceId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public String getStatus() {
        return status;
    }
}
