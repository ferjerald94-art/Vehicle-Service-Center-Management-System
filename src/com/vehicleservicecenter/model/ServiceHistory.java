
package com.vehicleservicecenter.model;

import java.time.LocalDate;

public class ServiceHistory {

    private final String customerName;
    private final String vehicleNumber;
    private final String brand;
    private final String model;

    private final int serviceId;
    private final String serviceType;
    private final LocalDate serviceDate;
    private final String status;

    public ServiceHistory(
            String customerName,
            String vehicleNumber,
            String brand,
            String model,
            int serviceId,
            String serviceType,
            LocalDate serviceDate,
            String status) {

        this.customerName = customerName;
        this.vehicleNumber = vehicleNumber;
        this.brand = brand;
        this.model = model;
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getServiceId() {
        return serviceId;
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
