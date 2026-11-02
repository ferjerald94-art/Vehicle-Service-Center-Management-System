
package com.vehicleservicecenter.dao;

import com.vehicleservicecenter.model.ServiceRecord;
import com.vehicleservicecenter.model.ServiceHistory;
import com.vehicleservicecenter.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecordDAO {

    // STEP 5: Book service
    public ServiceRecord bookService(
            int vehicleId,
            String serviceType,
            LocalDate serviceDate) throws SQLException {

        String sql = """
                INSERT INTO service_records
                (vehicle_id, service_type, service_date)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, vehicleId);
            ps.setString(2, serviceType);
            ps.setDate(3, Date.valueOf(serviceDate));

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("Service booking failed.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    int serviceId = rs.getInt(1);

                    return new ServiceRecord(
                            serviceId,
                            vehicleId,
                            serviceType,
                            serviceDate,
                            "BOOKED"
                    );
                }
            }

            throw new SQLException("Generated service ID not available.");
        }
    }

    // STEP 6: Find service by ID
    public ServiceRecord getServiceById(int serviceId)
            throws SQLException {

        String sql = """
                SELECT service_id, vehicle_id, service_type,
                       service_date, status
                FROM service_records
                WHERE service_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new ServiceRecord(
                            rs.getInt("service_id"),
                            rs.getInt("vehicle_id"),
                            rs.getString("service_type"),
                            rs.getDate("service_date").toLocalDate(),
                            rs.getString("status")
                    );
                }

                return null;
            }
        }
    }

    // STEP 6: Mark service completed
    public boolean completeService(int serviceId)
            throws SQLException {

        String sql = """
                UPDATE service_records
                SET status = 'COMPLETED'
                WHERE service_id = ?
                  AND status = 'BOOKED'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            return ps.executeUpdate() == 1;
        }
    }

    // STEP 7: Retrieve complete service history
    public List<ServiceHistory> getServiceHistoryByVehicleNumber(
            String vehicleNumber) throws SQLException {

        List<ServiceHistory> history = new ArrayList<>();

        String sql = """
                SELECT
                    c.name AS customer_name,
                    v.vehicle_number,
                    v.brand,
                    v.model,
                    sr.service_id,
                    sr.service_type,
                    sr.service_date,
                    sr.status
                FROM customers c
                INNER JOIN vehicles v
                    ON c.customer_id = v.customer_id
                INNER JOIN service_records sr
                    ON v.vehicle_id = sr.vehicle_id
                WHERE v.vehicle_number = ?
                ORDER BY sr.service_date DESC, sr.service_id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ServiceHistory record = new ServiceHistory(
                            rs.getString("customer_name"),
                            rs.getString("vehicle_number"),
                            rs.getString("brand"),
                            rs.getString("model"),
                            rs.getInt("service_id"),
                            rs.getString("service_type"),
                            rs.getDate("service_date").toLocalDate(),
                            rs.getString("status")
                    );

                    history.add(record);
                }
            }
        }

        return history;
    }
}
