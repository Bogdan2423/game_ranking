package project;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.NormOps_DDRM;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

public class Criterion {
    String criterionName;
    SimpleMatrix matrix;
    private int size;

    public Criterion(String name){
        this.criterionName = name;
    }

    public void createMatrix(int size){
        this.size = size;
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

    public SimpleMatrix gmmWeightVector(){
        SimpleMatrix weightVector = new SimpleMatrix(size, 1);
        double currProduct;
        for (int i = 0; i<size; i++){
            currProduct = 1;
            for (int j = 0; j<size; j++)
                currProduct*=matrix.get(i, j);
            weightVector.set(i, Math.pow(currProduct, 1.0/size));
        }
        double sum = weightVector.elementSum();
        for (int i = 0; i<size; i++)
            weightVector.set(i, weightVector.get(i)/sum);
        return weightVector;
    }

    public SimpleMatrix sscsmWeightVector() {
        SimpleMatrix weightVector = new SimpleMatrix(size, 1);
        double[] columnSums = new double[size];
        for (int j = 0; j < size; j++) {
            columnSums[j] = 0;
            for (int k = 0; k < size; k++) {
                columnSums[j] += matrix.get(k, j);
            }
        }

        for (int i = 0; i < size; i++) {
            weightVector.set(i, 0);
            for (int j = 0; j < size; j++) {
                weightVector.set(i, weightVector.get(i) + (matrix.get(i, j) / columnSums[j]));
            }
        }

        return weightVector;
    }

    public double inconsistencyIndex(){
        double maxIndex=0;
        double currIndex;
        for (int i =0; i<matrix.numRows(); i++){
            for (int j =i+1; j<matrix.numRows(); j++){
                for (int k =j+1; k<matrix.numRows(); k++){
                    currIndex = Math.min(Math.abs(1-((matrix.get(i,k)*matrix.get(k,j))/matrix.get(i,j))),
                            Math.abs(1-(matrix.get(i,j)/(matrix.get(i,k)*matrix.get(k,j)))));
                    maxIndex = Math.max(maxIndex, currIndex);
                }
            }
        }
        return maxIndex;
    }

    public String getCriterionName() {return criterionName;}
}
