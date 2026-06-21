package adss.inventory.repository.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import adss.inventory.repository.DatabaseManager;
import adss.inventory.repository.dto.CategoryDTO;

public class CategoryDAO {

    public void save(CategoryDTO category) {
        String sql = """
                INSERT OR REPLACE INTO categories (id, name, parent_id)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, category.getId());
            statement.setString(2, category.getName());

            if (category.getParentId() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, category.getParentId());
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save category", e);
        }
    }

    public CategoryDTO findById(int id) {
        String sql = """
                SELECT id, name, parent_id
                FROM categories
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Integer parentId = resultSet.getObject("parent_id") == null
                            ? null
                            : resultSet.getInt("parent_id");

                    return new CategoryDTO(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            parentId
                    );
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category by id", e);
        }
    }

    public List<CategoryDTO> findAll() {
        String sql = """
                SELECT id, name, parent_id
                FROM categories
                """;

        List<CategoryDTO> categories = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer parentId = resultSet.getObject("parent_id") == null
                        ? null
                        : resultSet.getInt("parent_id");

                categories.add(new CategoryDTO(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        parentId
                ));
            }

            return categories;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load categories", e);
        }
    }

    public void delete(int id) {
        String sql = """
                DELETE FROM categories
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete category", e);
        }
    }
}