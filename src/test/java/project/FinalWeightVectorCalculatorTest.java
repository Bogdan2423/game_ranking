package project;

import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FinalWeightVectorCalculatorTest {
    @Test
    public void calculatorTest(){
        SimpleMatrix criterionWeightVector = new SimpleMatrix(new double[][]{{0.345}, {0.175}, {0.062}, {0.103}, {0.019}, {0.034}, {0.041}, {0.22}});
        SimpleMatrix w1 = new SimpleMatrix(new double[][]{{0.072}, {0.649}, {0.279}});
        SimpleMatrix w2 = new SimpleMatrix(new double[][]{{0.743}, {0.194}, {0.063}});
        SimpleMatrix w3 = new SimpleMatrix(new double[][]{{0.194}, {0.063}, {0.743}});
        SimpleMatrix w4 = new SimpleMatrix(new double[][]{{0.717}, {0.066}, {0.217}});
        SimpleMatrix w5 = new SimpleMatrix(new double[][]{{0.333}, {0.333}, {0.333}});
        SimpleMatrix w6 = new SimpleMatrix(new double[][]{{0.691}, {0.091}, {0.218}});
        SimpleMatrix w7 = new SimpleMatrix(new double[][]{{0.77}, {0.068}, {0.162}});
        SimpleMatrix w8 = new SimpleMatrix(new double[][]{{0.2}, {0.4}, {0.4}});

        FinalWeightVectorCalculator calc = new FinalWeightVectorCalculator();
        calc.setCriterionWeightVectors(criterionWeightVector);
        calc.addWeightVector(w1);
        calc.addWeightVector(w2);
        calc.addWeightVector(w3);
        calc.addWeightVector(w4);
        calc.addWeightVector(w5);
        calc.addWeightVector(w6);
        calc.addWeightVector(w7);
        calc.addWeightVector(w8);

        Double[] testVector = {0.346, 0.369, 0.284};

        double[] actualVector = calc.calculate().getDDRM().getData();

        for (int i = 0; i<3; i++) {
            assertEquals(testVector[i], actualVector[i], 0.001);
        }
    }
}
