import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.vsu.cs.course1.tree.RTree;

import java.util.ArrayList;
import java.util.List;

public class TestSearch extends TestCase {

    public TestSearch(String name) {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(TestSearch.class);
    }

    //    @ParameterizedTest
//    @ValueSource(ints = {1, 4, 5, 10})
    public void testSearch01() {
        try {
            RTree<Integer> tree = new RTree<>(2, 4);
            tree.insert(new float[]{1, 2, 2, 2}, 0);
            tree.insert(new float[]{1, 2, 2, 2}, 1);
            tree.insert(new float[]{1, 4, 2, 4}, 2);
            tree.insert(new float[]{5, 7, 4, 8}, 3);
            List<Integer> list = new ArrayList<>();
            list.add(0);
            list.add(1);
            list.add(2);
            assertEquals(list, tree.search(new float[]{1, 2, 2, 4}));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testSearch02() {
        try {
            RTree<Integer> tree = new RTree<>(2, 4);
            tree.insert(new float[]{1, 2, 2, 2}, 0);
            tree.insert(new float[]{1, 2, 1.5f, 2}, 1);
            tree.insert(new float[]{1, 4, 2, 4}, 2);
            tree.insert(new float[]{1, 7, 2, 7}, 3);
            tree.insert(new float[]{1, 3, 2, 3}, 4);
            tree.delete(new float[]{1, 3, 2, 3}, 12);
            List<Integer> list = new ArrayList<>();
            assertEquals(list, tree.search(new float[]{12, 24, 16, 55}));
        } catch (Exception e) {
            assertEquals(e.getMessage(), "wrong object to delete");
        }
    }
}
