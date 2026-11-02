
package com.vehicleservicecenter;

import com.vehicleservicecenter.dao.CustomerDAO;
import com.vehicleservicecenter.dao.VehicleDAO;
import com.vehicleservicecenter.dao.ServiceRecordDAO;

import com.vehicleservicecenter.model.Customer;
import com.vehicleservicecenter.model.Vehicle;
import com.vehicleservicecenter.model.ServiceRecord;
import com.vehicleservicecenter.model.ServiceHistory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class VehicleServiceCenterManagement {

    public static void main(String[] args) {

        CustomerDAO customerDAO = new CustomerDAO();
        VehicleDAO vehicleDAO = new VehicleDAO();
        ServiceRecordDAO serviceRecordDAO = new ServiceRecordDAO();

        try (Scanner scanner = new Scanner(System.in)) {

            boolean running = true;

            while (running) {

                System.out.println("\n===== VEHICLE SERVICE CENTER =====");
                System.out.println("1. Register Customer");
                System.out.println("2. View Customer");
                System.out.println("3. Register Vehicle");
                System.out.println("4. View Customer Vehicles");
                System.out.println("5. Book Service");
                System.out.println("6. Complete Service");
                System.out.println("7. View Service History");
                System.out.println("0. Exit");

                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {

                    case "1":
                        handleCustomerRegistration(scanner, customerDAO);
                        break;

                    case "2":
                        handleViewCustomer(scanner, customerDAO);
                        break;

                    case "3":
                        handleVehicleRegistration(
                                scanner, customerDAO, vehicleDAO);
                        break;

                    case "4":
                        handleViewCustomerVehicles(
                                scanner, customerDAO, vehicleDAO);
                        break;

                    case "5":
                        handleServiceBooking(
                                scanner, vehicleDAO, serviceRecordDAO);
                        break;

                    case "6":
                        handleCompleteService(scanner, serviceRecordDAO);
                        break;

                    case "7":
                        handleViewServiceHistory(
                                scanner, vehicleDAO, serviceRecordDAO);
                        break;

                    case "0":
                        running = false;
                        System.out.println("Application closed.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }

    // STEP 3: Register customer
    private static void handleCustomerRegistration(
            Scanner scanner, CustomerDAO customerDAO) {

        System.out.print("Enter customer name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty() || name.length() > 100) {
            System.out.println("Invalid customer name.");
            return;
        }

        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine().trim();

        if (!phone.matches("\\d{10}")) {
            System.out.println("Phone number must contain 10 digits.");
            return;
        }

        try {
            Customer customer =
                    customerDAO.registerCustomer(name, phone);

            System.out.println("Customer registered successfully!");
            System.out.println("Customer ID: "
                    + customer.getCustomerId());

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // STEP 3: View customer
    private static void handleViewCustomer(
            Scanner scanner, CustomerDAO customerDAO) {

        Integer customerId = readCustomerId(scanner);

        if (customerId == null) {
            return;
        }

        try {
            Customer customer =
                    customerDAO.getCustomerById(customerId);

            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            System.out.println("\n----- CUSTOMER DETAILS -----");
            System.out.println("ID: " + customer.getCustomerId());
            System.out.println("Name: " + customer.getName());
            System.out.println("Phone: " + customer.getPhone());

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    // STEP 4: Register vehicle
    private static void handleVehicleRegistration(
            Scanner scanner,
            CustomerDAO customerDAO,
            VehicleDAO vehicleDAO) {

        Integer customerId = readCustomerId(scanner);

        if (customerId == null) {
            return;
        }

        try {
            Customer customer =
                    customerDAO.getCustomerById(customerId);

            if (customer == null) {
                System.out.println("Customer not found.");
                System.out.println("Register the customer first.");
                return;
            }

            System.out.print("Enter vehicle number: ");
            String vehicleNumber = scanner.nextLine()
                    .trim().toUpperCase(Locale.ROOT);

            if (!vehicleNumber.matches("[A-Z0-9-]{5,20}")) {
                System.out.println("Invalid vehicle number.");
                return;
            }

            if (vehicleDAO.vehicleNumberExists(vehicleNumber)) {
                System.out.println("Vehicle number already registered.");
                return;
            }

            System.out.print("Enter vehicle brand: ");
            String brand = scanner.nextLine().trim();

            if (brand.isEmpty() || brand.length() > 50) {
                System.out.println("Invalid vehicle brand.");
                return;
            }

            System.out.print("Enter vehicle model: ");
            String model = scanner.nextLine().trim();

            if (model.isEmpty() || model.length() > 50) {
                System.out.println("Invalid vehicle model.");
                return;
            }

            Vehicle vehicle = vehicleDAO.registerVehicle(
                    customerId, vehicleNumber, brand, model);

            System.out.println("Vehicle registered successfully!");
            System.out.println("Vehicle ID: " + vehicle.getVehicleId());
            System.out.println("Owner: " + customer.getName());

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                System.out.println("Vehicle number already registered.");
            } else if (e.getErrorCode() == 1452) {
                System.out.println("Customer no longer exists.");
            } else {
                System.out.println("Registration failed: "
                        + e.getMessage());
            }
        }
    }

    // STEP 4: View customer vehicles
    private static void handleViewCustomerVehicles(
            Scanner scanner,
            CustomerDAO customerDAO,
            VehicleDAO vehicleDAO) {

        Integer customerId = readCustomerId(scanner);

        if (customerId == null) {
            return;
        }

        try {
            Customer customer =
                    customerDAO.getCustomerById(customerId);

            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            List<Vehicle> vehicles =
                    vehicleDAO.getVehiclesByCustomerId(customerId);

            if (vehicles.isEmpty()) {
                System.out.println("No vehicles registered.");
                return;
            }

            System.out.println("\nVehicles of " + customer.getName());

            for (Vehicle vehicle : vehicles) {

                System.out.println("--------------------------");
                System.out.println("Vehicle ID: "
                        + vehicle.getVehicleId());
                System.out.println("Number: "
                        + vehicle.getVehicleNumber());
                System.out.println("Brand: "
                        + vehicle.getBrand());
                System.out.println("Model: "
                        + vehicle.getModel());
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    // STEP 5: Book service
    private static void handleServiceBooking(
            Scanner scanner,
            VehicleDAO vehicleDAO,
            ServiceRecordDAO serviceRecordDAO) {

        Integer vehicleId = readVehicleId(scanner);

        if (vehicleId == null) {
            return;
        }

        try {
            Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);

            if (vehicle == null) {
                System.out.println("Vehicle not found.");
                System.out.println("Register the vehicle first.");
                return;
            }

            System.out.println("Vehicle: " + vehicle.getVehicleNumber());

            System.out.print("Enter service type: ");
            String serviceType = scanner.nextLine().trim();

            if (serviceType.isEmpty() || serviceType.length() > 100) {
                System.out.println("Invalid service type.");
                return;
            }

            System.out.print("Enter service date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine().trim();

            LocalDate serviceDate;

            try {
                serviceDate = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use yyyy-MM-dd.");
                return;
            }

            if (serviceDate.isBefore(LocalDate.now())) {
                System.out.println("Cannot book a service for a past date.");
                return;
            }

            ServiceRecord service =
                    serviceRecordDAO.bookService(
                            vehicleId, serviceType, serviceDate);

            System.out.println("\nService booked successfully!");
            System.out.println("Service ID: " + service.getServiceId());
            System.out.println("Vehicle: " + vehicle.getVehicleNumber());
            System.out.println("Service Type: " + service.getServiceType());
            System.out.println("Service Date: " + service.getServiceDate());
            System.out.println("Status: " + service.getStatus());

        } catch (SQLException e) {

            if (e.getErrorCode() == 1452) {
                System.out.println("Vehicle no longer exists.");
            } else {
                System.out.println("Service booking failed: "
                        + e.getMessage());
            }
        }
    }

    // STEP 6: Complete service
    private static void handleCompleteService(
            Scanner scanner,
            ServiceRecordDAO serviceRecordDAO) {

        Integer serviceId = readServiceId(scanner);

        if (serviceId == null) {
            return;
        }

        try {
            ServiceRecord service =
                    serviceRecordDAO.getServiceById(serviceId);

            if (service == null) {
                System.out.println("Service not found.");
                return;
            }

            if ("COMPLETED".equals(service.getStatus())) {
                System.out.println("Service is already completed.");
                return;
            }

            if (!"BOOKED".equals(service.getStatus())) {
                System.out.println("Service cannot be completed.");
                return;
            }

            boolean updated =
                    serviceRecordDAO.completeService(serviceId);

            if (updated) {
                System.out.println("\nService completed successfully!");
                System.out.println("Service ID: " + serviceId);
                System.out.println("Service Type: "
                        + service.getServiceType());
                System.out.println("Status: COMPLETED");
            } else {
                System.out.println(
                        "Service could not be completed. Status may have changed.");
            }

        } catch (SQLException e) {
            System.out.println("Status update failed: " + e.getMessage());
        }
    }

    // STEP 7: View service history by vehicle number
    private static void handleViewServiceHistory(
            Scanner scanner,
            VehicleDAO vehicleDAO,
            ServiceRecordDAO serviceRecordDAO) {

        System.out.print("Enter vehicle number: ");
        String vehicleNumber = scanner.nextLine()
                .trim().toUpperCase(Locale.ROOT);

        if (!vehicleNumber.matches("[A-Z0-9-]{5,20}")) {
            System.out.println("Invalid vehicle number.");
            return;
        }

        try {
            if (!vehicleDAO.vehicleNumberExists(vehicleNumber)) {
                System.out.println("Vehicle not found.");
                return;
            }

            List<ServiceHistory> history =
                    serviceRecordDAO.getServiceHistoryByVehicleNumber(
                            vehicleNumber);

            if (history.isEmpty()) {
                System.out.println(
                        "No service records found for this vehicle.");
                return;
            }

            ServiceHistory first = history.get(0);

            System.out.println("\n========== SERVICE HISTORY ==========");
            System.out.println("Customer Name : "
                    + first.getCustomerName());
            System.out.println("Vehicle       : "
                    + first.getVehicleNumber());
            System.out.println("Brand         : "
                    + first.getBrand());
            System.out.println("Model         : "
                    + first.getModel());

            for (ServiceHistory record : history) {

                System.out.println("\n-------------------------------------");
                System.out.println("Service ID    : "
                        + record.getServiceId());
                System.out.println("Service Type  : "
                        + record.getServiceType());
                System.out.println("Service Date  : "
                        + record.getServiceDate());
                System.out.println("Status        : "
                        + record.getStatus());
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    // Shared customer ID validation
    private static Integer readCustomerId(Scanner scanner) {

        System.out.print("Enter customer ID: ");
        String input = scanner.nextLine().trim();

        try {
            int customerId = Integer.parseInt(input);

            if (customerId <= 0) {
                System.out.println("Customer ID must be positive.");
                return null;
            }

            return customerId;

        } catch (NumberFormatException e) {
            System.out.println("Invalid customer ID.");
            return null;
        }
    }

    // Shared vehicle ID validation
    private static Integer readVehicleId(Scanner scanner) {

        System.out.print("Enter vehicle ID: ");
        String input = scanner.nextLine().trim();

        try {
            int vehicleId = Integer.parseInt(input);

            if (vehicleId <= 0) {
                System.out.println("Vehicle ID must be positive.");
                return null;
            }

            return vehicleId;

        } catch (NumberFormatException e) {
            System.out.println("Invalid vehicle ID.");
            return null;
        }
    }

    // Shared service ID validation
    private static Integer readServiceId(Scanner scanner) {

        System.out.print("Enter service ID: ");
        String input = scanner.nextLine().trim();

        try {
            int serviceId = Integer.parseInt(input);

            if (serviceId <= 0) {
                System.out.println("Service ID must be positive.");
                return null;
            }

            return serviceId;

        } catch (NumberFormatException e) {
            System.out.println("Invalid service ID.");
            return null;
        }
    }
}
