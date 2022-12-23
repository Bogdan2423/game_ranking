package project;

import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CriterionTest {
    @Test
    public void weightVectortest(){
        Criterion c1 = new Criterion("test");
        c1.createMatrix(3);
        c1.setComparison(1, 0, 7);
        c1.setComparison(2, 0, 5);
        c1.setComparison(2, 1, 1.0/3);

        System.out.println(c1.matrix);
        Double[] testVector = {0.072, 0.649, 0.279};

        for (int i = 0; i<3; i++) {
            assertEquals(testVector[i], c1.weightVector().getDDRM().getData()[i], 0.001);
        }
    }
}
