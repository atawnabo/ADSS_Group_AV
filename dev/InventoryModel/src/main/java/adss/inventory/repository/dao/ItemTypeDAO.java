package adss.inventory.repository.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import adss.inventory.dto.ItemTypeDTO;
import adss.inventory.repository.DatabaseManager;

public class ItemTypeDAO {

    public void save(ItemTypeDTO itemType) {
        String sql = """
                INSERT OR REPLACE INTO item_types
                (id, name, shelf_num, aisle_num, shelf_quantity, warehouse_quantity,
                 min_quantity, cost_price, selling_price, category_id, manufacturer)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, itemType.getId());
            statement.setString(2, itemType.getName());

            if (itemType.getShelfNum() == null) statement.setNull(3, Types.INTEGER);
            else statement.setInt(3, itemType.getShelfNum());

            if (itemType.getAisleNum() == null) statement.setNull(4, Types.INTEGER);
            else statement.setInt(4, itemType.getAisleNum());

            statement.setInt(5, itemType.getShelfQuantity());
            statement.setInt(6, itemType.getWarehouseQuantity());
            statement.setInt(7, itemType.getMinQuantity());
            statement.setInt(8, itemType.getCostPrice());
            statement.setInt(9, itemType.getSellingPrice());
            statement.setInt(10, itemType.getCategoryId());
            statement.setString(11, itemType.getManufacturer());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save item type", e);
        }
    }

    public ItemTypeDTO findById(int id) {
        String sql = "SELECT * FROM item_types WHERE id = ?";

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
            throw new RuntimeException("Failed to find item type by id", e);
        }
    }

    public List<ItemTypeDTO> findAll() {
        String sql = "SELECT * FROM item_types";
        List<ItemTypeDTO> itemTypes = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                itemTypes.add(mapResultSetToDTO(rs));
            }

            return itemTypes;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load item types", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM item_types WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete item type", e);
        }
    }

    private ItemTypeDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        Integer shelfNum = rs.getObject("shelf_num") == null ? null : rs.getInt("shelf_num");
        Integer aisleNum = rs.getObject("aisle_num") == null ? null : rs.getInt("aisle_num");

        return new ItemTypeDTO(
                rs.getInt("id"),
                rs.getString("name"),
                shelfNum,
                aisleNum,
                rs.getInt("shelf_quantity"),
                rs.getInt("warehouse_quantity"),
                rs.getInt("min_quantity"),
                rs.getInt("cost_price"),
                rs.getInt("selling_price"),
                rs.getInt("category_id"),
                rs.getString("manufacturer")
        );
    }
}