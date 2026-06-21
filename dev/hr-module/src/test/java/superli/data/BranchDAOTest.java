package superli.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import superli.domain.StoreBranch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BranchDAOTest {

    private BranchDAO branchDAO;

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
        branchDAO = new BranchDAO();
    }

    @Test
    public void saveAndFindBranchSuccessfully() {
        StoreBranch branch =
                new StoreBranch(99, "Test Branch", "Beer Sheva");

        branchDAO.save(branch);

        StoreBranch loadedBranch = branchDAO.findById(99);

        assertNotNull(loadedBranch);
        assertEquals(99, loadedBranch.getBranchId());
        assertEquals("Test Branch", loadedBranch.getName());
        assertEquals("Beer Sheva", loadedBranch.getAddress());
    }
}