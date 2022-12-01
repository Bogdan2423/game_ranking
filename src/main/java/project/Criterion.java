package project;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.Matrix;
import org.ejml.dense.row.NormOps_DDRM;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

public class Criterion {
    String criterionName;
    SimpleMatrix matrix;

    public Criterion(String name, SimpleMatrix matrix){
        this.criterionName = name;
        this.matrix = matrix;
    }

    public SimpleMatrix weightVector(){
        SimpleEVD<SimpleMatrix> evdCalculator = new SimpleEVD<>(matrix.getMatrix());

        SimpleMatrix eigenVector = evdCalculator.getEigenVector(evdCalculator.getIndexMax());

        double norm = NormOps_DDRM.normF(new DMatrixRMaj(eigenVector.getMatrix()))*(-1);

        for (int i = 0; i<eigenVector.getNumElements(); i++)
            eigenVector.set(i, eigenVector.get(i)/norm);

        double sum = eigenVector.elementSum();

        for (int i = 0; i<eigenVector.getNumElements(); i++)
            eigenVector.set(i, eigenVector.get(i)/sum);

        return eigenVector;
    }
}
