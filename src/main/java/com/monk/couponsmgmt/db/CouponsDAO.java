package com.monk.couponsmgmt.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.couponsmgmt.dto.CouponDTO;
import com.monk.couponsmgmt.exceptions.GlobalExceptionHandler.CouponNotFoundException;
import com.monk.couponsmgmt.exceptions.GlobalExceptionHandler.CouponStructureInvalidException;
import com.monk.couponsmgmt.exceptions.GlobalExceptionHandler.NoCouponsFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.monk.couponsmgmt.consts.UniversalConstants.*;

public class CouponsDAO {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String QUERY_CREATE_COUPON_TABLE = "CREATE TABLE IF NOT EXISTS coupon (id INT AUTO_INCREMENT PRIMARY KEY, type VARCHAR(30) NOT NULL, details JSON  );";
    private final String QUERY_INSERT_COUPON = "INSERT INTO coupon(type, details) values (?, ?);";
    private final String QUERY_SELECT_ALL_COUPON = "SELECT * FROM coupon;";
    private final String QUERY_SELECT_COUPON = "SELECT * FROM coupon where id=?;";
    private final String QUERY_DELETE_COUPON = "DELETE FROM coupon WHERE id = ?;";
    private final String QUERY_UPDATE_COUPON = "UPDATE coupon SET type = ?, details = ? WHERE id = ?;";

    public void init(Connection connection) {
        try {
            createTables(connection);
            System.out.println("Coupon table created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating coupon table: " + e.getMessage());
        }
    }

    public void createTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(QUERY_CREATE_COUPON_TABLE);
        }
    }

    public CouponDTO insertCoupon(CouponDTO coupon, Connection connection) {
        Integer id;
        try (PreparedStatement ps = connection.prepareStatement(QUERY_INSERT_COUPON, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, coupon.getType());
            ps.setString(2, objectMapper.writeValueAsString(coupon.getDetails()));
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new CouponStructureInvalidException("Coupon Structure is Invalid !");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                } else {
                    throw new CouponStructureInvalidException("Coupon Structure is Invalid !");
                }
            }
        } catch (JsonProcessingException e) {
            throw new CouponStructureInvalidException("Coupon Structure is Invalid !");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        coupon.setId(id);
        return coupon;
    }

    public List<CouponDTO> getAllCoupons(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(QUERY_SELECT_ALL_COUPON);

            List<CouponDTO> coupons = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");

                CouponDTO.Details details;
                switch (type) {
                    case TYPE_BXGY:
                        details = objectMapper.readValue(objectMapper.readTree(rs.getString("details")).asText(), CouponDTO.BxGyDetails.class);
                        break;
                    case TYPE_CARTWISE:
                        details = objectMapper.readValue(objectMapper.readTree(rs.getString("details")).asText(), CouponDTO.CartWiseDetails.class);
                        break;
                    case TYPE_PRODUCTWISE:
                        details = objectMapper.readValue(objectMapper.readTree(rs.getString("details")).asText(), CouponDTO.ProductWiseDetails.class);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown type: " + type);
                }

                CouponDTO coupon = new CouponDTO();
                coupon.setId(id);
                coupon.setType(type);
                coupon.setDetails(details);

                coupons.add(coupon);
            }
            if (coupons.size() == 0) {
                throw new NoCouponsFoundException("No Coupons in Database!");
            }
            return coupons;
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public CouponDTO getCoupon(Integer id, Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(QUERY_SELECT_COUPON, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return getCouponFromResultSet(rs);
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    private CouponDTO getCouponFromResultSet(ResultSet rs) throws SQLException, JsonProcessingException {
        if (getResultSetSize(rs) == 0) {
            throw new CouponNotFoundException();
        }

        while (rs.next()) {
            int couponId = rs.getInt("id");
            String type = rs.getString("type");

            CouponDTO.Details details;
            switch (type) {
                case TYPE_BXGY:
                    details = objectMapper.readValue(objectMapper.readTree(rs.getString("details")).asText(), CouponDTO.BxGyDetails.class);
                    break;
                case TYPE_CARTWISE:
                    details = objectMapper.readValue(objectMapper.readTree(rs.getString("details")).asText(), CouponDTO.CartWiseDetails.class);
                    break;
                case TYPE_PRODUCTWISE:
                    details = objectMapper.readValue(objectMapper.readTree(rs.getString("details")).asText(), CouponDTO.ProductWiseDetails.class);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type: " + type);
            }

            CouponDTO coupon = new CouponDTO();
            coupon.setId(couponId);
            coupon.setType(type);
            coupon.setDetails(details);

            return coupon;
        }
        return null;
    }

    public static int getResultSetSize(ResultSet rs) throws SQLException {
        rs.last();
        int size = rs.getRow();
        rs.beforeFirst();
        return size;
    }

    public boolean deleteCoupon(Integer id, Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(QUERY_DELETE_COUPON)) {
            getCoupon(id, connection);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CouponDTO updateCoupon(Integer id, CouponDTO coupon, Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(QUERY_UPDATE_COUPON, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            getCoupon(id, connection);
            ps.setString(1, coupon.getType());
            ps.setString(2, objectMapper.writeValueAsString(coupon.getDetails()));
            ps.setInt(3, id);
            ps.executeUpdate();
            return getCoupon(id, connection);
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}
