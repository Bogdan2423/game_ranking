package project;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.Matrix;
import org.ejml.dense.row.NormOps_DDRM;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

public class Criterion {
    String criterionName;
    SimpleMatrix matrix;

    public Criterion(String name){
        this.criterionName = name;
    }

    public void createMatrix(int size){
        matrix = new SimpleMatrix(size, size);
        for (int i = 0; i<size; i++)
            matrix.set(i, i, 1);
    }

    public void setComparison(int i, int j, double val){
        matrix.set(i, j, val);
        matrix.set(j, i, 1.0/val);
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

    public String getCriterionName() {return criterionName;}
}
