
package com.vehicleservicecenter.dao;

import com.vehicleservicecenter.model.Vehicle;
import com.vehicleservicecenter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // Register a new vehicle
    public Vehicle registerVehicle(
            int customerId,
            String vehicleNumber,
            String brand,
            String model) throws SQLException {

        String sql = """
                INSERT INTO vehicles
                (customer_id, vehicle_number, brand, model)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, customerId);
            ps.setString(2, vehicleNumber);
            ps.setString(3, brand);
            ps.setString(4, model);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("Vehicle registration failed.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    int vehicleId = rs.getInt(1);

                    return new Vehicle(
                            vehicleId,
                            customerId,
                            vehicleNumber,
                            brand,
                            model
                    );
                }
            }

            throw new SQLException("Generated vehicle ID not available.");
        }
    }

    // Check duplicate registration number
    public boolean vehicleNumberExists(String vehicleNumber)
            throws SQLException {

        String sql = """
                SELECT 1 FROM vehicles
                WHERE vehicle_number = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Get all vehicles belonging to a customer
    public List<Vehicle> getVehiclesByCustomerId(int customerId)
            throws SQLException {

        List<Vehicle> vehicles = new ArrayList<>();

        String sql = """
                SELECT vehicle_id, customer_id,
                       vehicle_number, brand, model
                FROM vehicles
                WHERE customer_id = ?
                ORDER BY vehicle_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Vehicle vehicle = new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getInt("customer_id"),
                            rs.getString("vehicle_number"),
                            rs.getString("brand"),
                            rs.getString("model")
                    );

                    vehicles.add(vehicle);
                }
            }
        }

        return vehicles;
    }

    public Vehicle getVehicleById(int vehicleId)
            throws SQLException {

        String sql = """
            SELECT vehicle_id, customer_id,
                   vehicle_number, brand, model
            FROM vehicles
            WHERE vehicle_id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getInt("customer_id"),
                            rs.getString("vehicle_number"),
                            rs.getString("brand"),
                            rs.getString("model")
                    );
                }

                return null;
            }
        }
    }

}
