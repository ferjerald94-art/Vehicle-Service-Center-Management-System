package com.vehicleservicecenter.dao;

import com.vehicleservicecenter.model.Customer;
import com.vehicleservicecenter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerDAO {

    public Customer registerCustomer(String name, String phone)
            throws SQLException {

        String sql = "INSERT INTO customers (name, phone) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, phone);

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("Customer registration failed.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    int customerId = rs.getInt(1);

                    return new Customer(customerId, name, phone);
                }
            }

            throw new SQLException("Generated customer ID not available.");
        }
    }

    public Customer getCustomerById(int customerId)
            throws SQLException {

        String sql = """
                SELECT customer_id, name, phone
                FROM customers
                WHERE customer_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("phone")
                    );
                }

                return null;
            }
        }
    }
}
