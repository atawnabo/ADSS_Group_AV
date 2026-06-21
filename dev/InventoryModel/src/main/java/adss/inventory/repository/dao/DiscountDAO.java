package adss.inventory.repository.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adss.inventory.repository.DatabaseManager;
import adss.inventory.repository.dto.DiscountDTO;

public class DiscountDAO {

    public void save(DiscountDTO discount) {
        String sql = """
                INSERT OR REPLACE INTO discounts
                (id, discount_type, percentage, start_date, end_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, discount.getId());
            statement.setString(2, discount.getDiscountType());
            statement.setDouble(3, discount.getPercentage());
            statement.setString(4, discount.getStartDate());
            statement.setString(5, discount.getEndDate());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save discount", e);
        }
    }

    public void saveItemTarget(int discountId, int itemTypeId) {
        String sql = """
                INSERT OR REPLACE INTO item_discount_targets
                (discount_id, item_type_id)
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, discountId);
            statement.setInt(2, itemTypeId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save item discount target", e);
        }
    }

    public void saveCategoryTarget(int discountId, int categoryId) {
        String sql = """
                INSERT OR REPLACE INTO category_discount_targets
                (discount_id, category_id)
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, discountId);
            statement.setInt(2, categoryId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save category discount target", e);
        }
    }

    public DiscountDTO findById(int id) {
        String sql = "SELECT * FROM discounts WHERE id = ?";

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
            throw new RuntimeException("Failed to find discount by id", e);
        }
    }

    public List<DiscountDTO> findAll() {
        String sql = "SELECT * FROM discounts";
        List<DiscountDTO> discounts = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapResultSetToDTO(rs));
            }

            return discounts;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load discounts", e);
        }
    }

    public List<Integer> findItemTargets(int discountId) {
        String sql = "SELECT item_type_id FROM item_discount_targets WHERE discount_id = ?";
        List<Integer> ids = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, discountId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("item_type_id"));
                }
            }

            return ids;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load item discount targets", e);
        }
    }

    public List<Integer> findCategoryTargets(int discountId) {
        String sql = "SELECT category_id FROM category_discount_targets WHERE discount_id = ?";
        List<Integer> ids = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, discountId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("category_id"));
                }
            }

            return ids;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load category discount targets", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM discounts WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete discount", e);
        }
    }

    private DiscountDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new DiscountDTO(
                rs.getInt("id"),
                rs.getString("discount_type"),
                rs.getDouble("percentage"),
                rs.getString("start_date"),
                rs.getString("end_date")
        );
    }
}