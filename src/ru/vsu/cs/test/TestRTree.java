import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.vsu.cs.course1.tree.RTree;
public class TestRTree extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestRTree( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(TestRTree.class);
        suite.addTest(TestDelete.suite());
        suite.addTest(TestInsertion.suite());
        suite.addTest(TestSearch.suite());
        return suite;
    }

    /**
     * Rigourous Test :-)
     */
    public void testTree01() {
        try {
            RTree<Integer> tree = new RTree<>(2, 3);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Max entries smaller than 4");
        }
    }

    public void testTree02() {
        try {
            RTree<Integer> tree = new RTree<>(4, 6);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Min entries bigger than half of max entries");
        }
    }
}
