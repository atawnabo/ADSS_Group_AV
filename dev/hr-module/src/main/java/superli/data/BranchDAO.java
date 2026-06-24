package superli.data;

import superli.domain.StoreBranch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BranchDAO {

    public void save(StoreBranch branch) {
        String sql =
                "INSERT INTO branches(branch_id, name, address) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT(branch_id) DO UPDATE SET " +
                "name = excluded.name, " +
                "address = excluded.address";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, branch.getBranchId());
            statement.setString(2, branch.getName());
            statement.setString(3, branch.getAddress());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Could not save branch.", e);
        }
    }

    public StoreBranch findById(int branchId) {
        String sql =
                "SELECT branch_id, name, address " +
                "FROM branches " +
                "WHERE branch_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, branchId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new StoreBranch(
                        resultSet.getInt("branch_id"),
                        resultSet.getString("name"),
                        resultSet.getString("address")
                );
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not load branch.", e);
        }
    }
}