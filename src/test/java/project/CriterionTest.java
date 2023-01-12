package project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CriterionTest {
    @Test
    public void weightVectorTest(){
        Criterion c1 = new Criterion("test");
        c1.createMatrix(3);
        c1.setComparison(1, 0, 7);
        c1.setComparison(2, 0, 5);
        c1.setComparison(2, 1, 1.0/3);

        Double[] expectedVector = {0.072, 0.649, 0.279};

        double[] actualVector = c1.weightVector().getDDRM().getData();

        for (int i = 0; i<3; i++) {
            assertEquals(expectedVector[i], actualVector[i], 0.001);
        }
    }

    @Test
    public void inconsistencyTest(){
        Criterion c = new Criterion("test");

        c.createMatrix(5);

        c.setComparison(1, 0, 74.0/69);
        c.setComparison(2, 0, 31.0/10);
        c.setComparison(3, 0, 22.0/7);
        c.setComparison(4, 0, 7.0/19);

        c.setComparison(2, 1, 43.0/27);
        c.setComparison(3, 1, 3.0/2);
        c.setComparison(4, 1, 13.0/34);

        c.setComparison(3, 2, 38.0/47);
        c.setComparison(4, 2, 7.0/73);

        c.setComparison(4, 3, 1.0/7);

        double expectedInconsistency = 0.60059;
        double actualInconsistency = c.inconsistencyIndex();

        assertEquals(expectedInconsistency, actualInconsistency, 0.00001);
    }
}
