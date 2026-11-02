
package com.vehicleservicecenter.model;

public class Vehicle {

    private final int vehicleId;
    private final int customerId;
    private final String vehicleNumber;
    private final String brand;
    private final String model;

    public Vehicle(int vehicleId, int customerId,
                   String vehicleNumber, String brand, String model) {

        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.vehicleNumber = vehicleNumber;
        this.brand = brand;
        this.model = model;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public int getCustomerId() {
        return customerId;
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
}
