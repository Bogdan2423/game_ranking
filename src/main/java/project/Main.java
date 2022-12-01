package project;

import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;

public class Main {

    public static void main(String args[]) {
        SimpleMatrix matrix1 = new SimpleMatrix(
                new double[][]{
                        new double[]{1d, 2d, 3d},
                        new double[]{0.5d, 1d, 4d},
                        new double[]{0.33d, 0.25d, 1d}
                }
        );
        Criterion crit1 = new Criterion("Graphics",  matrix1);

        System.out.println(crit1.weightVector().get(0));
    }
}
