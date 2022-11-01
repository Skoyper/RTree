import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.vsu.cs.course1.tree.RTree;

public class TestInsertion extends TestCase {

    public TestInsertion(String name) {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(TestInsertion.class);
    }

    public void testInsertion01() {
        try {
            RTree<Integer> tree = new RTree<>(2, 4);
            tree.insert(new float[]{1, 2, 2, 2}, 0);
            tree.insert(new float[]{1, 2, 2, 2}, 1);
            tree.insert(new float[]{1, 4, 2, 4}, 2);
            tree.insert(new float[]{1, 7, 2, 7}, 3);
            tree.insert(new float[]{1, 3, 2, 3}, 1);
            tree.insert(new float[]{1, 3, 2, 3}, 4);
            tree.insert(new float[]{1, 3, 2, 3}, 5);
            tree.insert(new float[]{1, 3, 4, 2, 3, 7}, 10);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Wrong coordinates for 2D");
        }
    }

}
