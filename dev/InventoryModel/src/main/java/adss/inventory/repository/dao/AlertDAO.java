package adss.inventory.repository.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adss.inventory.repository.DatabaseManager;
import adss.inventory.repository.dto.AlertDTO;

public class AlertDAO {

    public void save(AlertDTO alert) {
        String sql = """
                INSERT OR REPLACE INTO alerts
                (id, description, item_type_id)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, alert.getId());
            statement.setString(2, alert.getDescription());
            statement.setInt(3, alert.getItemTypeId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save alert", e);
        }
    }

    public AlertDTO findById(int id) {
        String sql = "SELECT * FROM alerts WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find alert by id", e);
        }
    }

    public List<AlertDTO> findAll() {
        String sql = "SELECT * FROM alerts";
        List<AlertDTO> alerts = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapResultSetToDTO(rs));
            }

            return alerts;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load alerts", e);
        }
    }

    public List<AlertDTO> findByItemTypeId(int itemTypeId) {
        String sql = "SELECT * FROM alerts WHERE item_type_id = ?";
        List<AlertDTO> alerts = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, itemTypeId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapResultSetToDTO(rs));
                }
            }

            return alerts;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load alerts by item type id", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM alerts WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete alert", e);
        }
    }

    private AlertDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new AlertDTO(
                rs.getInt("id"),
                rs.getString("description"),
                rs.getInt("item_type_id")
        );
    }
}