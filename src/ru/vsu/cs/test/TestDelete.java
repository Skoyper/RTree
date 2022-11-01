import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.vsu.cs.course1.tree.RTree;

public class TestDelete extends TestCase {

    public TestDelete(String name) {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(TestDelete.class);
    }

    //    @ParameterizedTest
//    @ValueSource(ints = {1, 4, 5, 10})
    public void testDelete01() {
        try {
            RTree<Integer> tree = new RTree<>(2, 4);
            tree.insert(new float[]{1, 2, 2, 2}, 0);
            tree.insert(new float[]{1, 2, 2, 2}, 1);
            tree.insert(new float[]{1, 4, 2, 4}, 2);
            tree.insert(new float[]{1, 7, 2, 7}, 3);
            tree.insert(new float[]{1, 3, 2, 3}, 1);
            tree.insert(new float[]{1, 3, 2, 3}, 4);
            tree.insert(new float[]{1, 3, 2, 3}, 5);
            tree.insert(new float[]{1, 3, 2, 3}, 10);
            assertTrue(tree.delete(new float[]{1, 3, 2, 3}, 4));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //    @ParameterizedTest
//    @ValueSource(ints = {1, 9, 5, 12})
    public void testDelete02() {
        try {
            RTree<Integer> tree = new RTree<>(2, 4);
            tree.insert(new float[]{1, 2, 2, 2}, 0);
            tree.insert(new float[]{1, 2, 1.5f, 2}, 1);
            tree.insert(new float[]{1, 4, 2, 4}, 2);
            tree.insert(new float[]{1, 7, 2, 7}, 3);
            tree.insert(new float[]{1, 3, 2, 3}, 4);
            tree.delete(new float[]{1, 3, 2, 3}, 12);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "wrong object to delete");
        }
    }
}
