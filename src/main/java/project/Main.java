package project;

import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;

public class Main {

    public static void main(String[] args) {
        SimpleMatrix matrix1 = new SimpleMatrix(
                new double[][]{
                        new double[]{1d, 2d, 3d},
                        new double[]{0.5d, 1d, 4d},
                        new double[]{0.33d, 0.25d, 1d}
                }
        );
        Criterion crit1 = new Criterion("Graphics",  matrix1);

        FinalWeightVectorCalculator calc = new FinalWeightVectorCalculator();
        calc.setCriterionWeightVectors(crit1.weightVector());

        SimpleMatrix matrix2 = new SimpleMatrix(
                new double[][]{
                        new double[]{0.072d},
                        new double[]{0.694d},
                        new double[]{0.279d}
                }
        );

        calc.addWeightVector(matrix2);

        SimpleMatrix matrix3 = new SimpleMatrix(
                new double[][]{
                        new double[]{0.743d},
                        new double[]{0.194d},
                        new double[]{0.063d}
                }
        );

        calc.addWeightVector(matrix3);

        SimpleMatrix matrix4 = new SimpleMatrix(
                new double[][]{
                        new double[]{0.194d},
                        new double[]{0.063d},
                        new double[]{0.743d}
                }
        );

        calc.addWeightVector(matrix4);

        System.out.println(calc.calculate());
    }
}
