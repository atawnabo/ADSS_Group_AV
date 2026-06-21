package adss.inventory.repository.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adss.inventory.repository.DatabaseManager;
import adss.inventory.repository.dto.ItemDTO;

public class ItemDAO {

    public void save(ItemDTO item) {
        String sql = """
                INSERT OR REPLACE INTO items
                (id, item_type_id, sell_discount, buy_discount, item_price,
                 item_sell_price, expiration_date, damaged, in_warehouse)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, item.getId());
            statement.setInt(2, item.getItemTypeId());
            statement.setInt(3, item.getSellDiscount());
            statement.setInt(4, item.getBuyDiscount());
            statement.setInt(5, item.getItemPrice());
            statement.setInt(6, item.getItemSellPrice());
            statement.setString(7, item.getExpirationDate());
            statement.setInt(8, item.isDamaged() ? 1 : 0);
            statement.setInt(9, item.isInWarehouse() ? 1 : 0);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save item", e);
        }
    }

    public ItemDTO findById(int id) {
        String sql = "SELECT * FROM items WHERE id = ?";

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
            throw new RuntimeException("Failed to find item by id", e);
        }
    }

    public List<ItemDTO> findAll() {
        String sql = "SELECT * FROM items";
        List<ItemDTO> items = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                items.add(mapResultSetToDTO(rs));
            }

            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load items", e);
        }
    }

    public List<ItemDTO> findByItemTypeId(int itemTypeId) {
        String sql = "SELECT * FROM items WHERE item_type_id = ?";
        List<ItemDTO> items = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, itemTypeId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToDTO(rs));
                }
            }

            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load items by item type id", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete item", e);
        }
    }

    private ItemDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new ItemDTO(
                rs.getInt("id"),
                rs.getInt("item_type_id"),
                rs.getInt("sell_discount"),
                rs.getInt("buy_discount"),
                rs.getInt("item_price"),
                rs.getInt("item_sell_price"),
                rs.getString("expiration_date"),
                rs.getInt("damaged") == 1,
                rs.getInt("in_warehouse") == 1
        );
    }
}