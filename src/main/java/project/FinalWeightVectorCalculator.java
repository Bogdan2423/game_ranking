package project;

import org.ejml.simple.SimpleMatrix;

public class FinalWeightVectorCalculator {
    SimpleMatrix weightVectors;
    SimpleMatrix criterionWeightVectors;

    public void addWeightVector(SimpleMatrix vector){
        if (weightVectors==null)
            weightVectors = vector;
        else
            weightVectors = weightVectors.combine(0, SimpleMatrix.END, vector);
    }

    public void setCriterionWeightVectors(SimpleMatrix vector){
        criterionWeightVectors = vector;
    }

    public SimpleMatrix calculate(){
        return weightVectors.mult(criterionWeightVectors);
    }
}
