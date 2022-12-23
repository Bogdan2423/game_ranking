package project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

public class Controller {
    private Criterion[] criterions;
    private Criterion criterionWeights;
    private String[] alternatives;

    private VBox mainBox= new VBox();

    private ArrayList[] comparisons;
    private Random rand = new Random();

    public void start(){
        TextField criterionField =new TextField();
        criterionField.setFont(Font.font("Verdana",20));
        Label criterionLabel = new Label("Enter criterions seperated by commas (at least one):");
        criterionLabel.setFont(Font.font("Verdana",20));
        Button nextButton = new Button("Next");

        mainBox.setAlignment(Pos.BASELINE_CENTER);
        mainBox.setPadding(new Insets(100));
        mainBox.setSpacing(50);

        mainBox.getChildren().clear();
        mainBox.getChildren().addAll(criterionLabel, criterionField, nextButton);

        nextButton.setOnAction((event0 -> {
            String[] criterionsStrings = criterionField.getText().split(", |,");
            criterions = new Criterion[criterionsStrings.length];
            for (int i = 0; i<criterions.length; i++) {
                criterions[i] = new Criterion(criterionsStrings[i]);
            }

            criterionWeights = new Criterion("CRITERIONS");
            criterionWeights.createMatrix(criterions.length);

            enterAlternatives();
        }));
        nextButton.setFont(Font.font("Verdana",20));

    }

    private void enterAlternatives(){
        TextField alternativeField =new TextField();
        alternativeField.setFont(Font.font("Verdana",20));
        Label alterativeLabel = new Label("Enter alternatives seperated by commas (at least two):");
        alterativeLabel.setFont(Font.font("Verdana",20));

        Button nextButton = new Button("Next");

        mainBox.getChildren().clear();
        mainBox.getChildren().addAll(alterativeLabel, alternativeField, nextButton);
        nextButton.setOnAction((event1 -> {
            alternatives = alternativeField.getText().split(", |,");
            compare();
        }));
    }

    private void compare(){
        for (Criterion crit: criterions){
            crit.createMatrix(alternatives.length);
        }
        Button nextButton = new Button("Next");
        Text label = new Text();
        TextField comparison =new TextField();
        mainBox.getChildren().clear();
        mainBox.getChildren().addAll(label, comparison, nextButton);

        comparisons = new ArrayList[criterions.length];
        for (int i = 0; i<criterions.length; i++) {
            comparisons[i] = new ArrayList();
            for (int j = 0; j < alternatives.length; j++)
                for (int k = j + 1; k < alternatives.length; k++)
                    comparisons[i].add(new Tuple(j, k));
        }

        int randomIndex = rand.nextInt(comparisons[0].size());
        Tuple toCompare = (Tuple) comparisons[0].get(randomIndex);
        comparisons[0].remove(randomIndex);
        nextComparison(label, comparison, nextButton, toCompare.getData1(), toCompare.getData2(), 0);
    }

    private void nextComparison(Text label, TextField field, Button nextButton, int i, int j, int crit){
        label.setText("Compare "+alternatives[i]+" with "+alternatives[j]+
                " by criterion "+criterions[crit].getCriterionName()+"\n (value 1 to 10 if first alternative is better,\n" +
                "0 to 1 if second alternative is better)");

        label.setFont(Font.font("Verdana",20));
        int next_crit = crit;

        if (comparisons[crit].size()==0)
            next_crit+=1;

        if (next_crit >= criterions.length){
            nextButton.setOnAction((event2 -> {
                criterions[crit].setComparison(i, j, Double.parseDouble(field.getText()));
                compareCriterions(label, field, nextButton, 0, 1);
            }));
        }
        else {
            int randomIndex = rand.nextInt(comparisons[next_crit].size());
            Tuple toCompare = (Tuple) comparisons[next_crit].get(randomIndex);
            comparisons[next_crit].remove(randomIndex);

            int finalNext_crit = next_crit;
            nextButton.setOnAction((event2 -> {
                criterions[crit].setComparison(i, j, Double.parseDouble(field.getText()));
                nextComparison(label, field, nextButton, toCompare.getData1(), toCompare.getData2(), finalNext_crit);
            }));
        }
    }

    private void compareCriterions(Text label, TextField field, Button nextButton, int i,int j){
        if (criterions.length==1)
            end();

        label.setText("Compare "+criterions[i].getCriterionName()+" with "+criterions[j].getCriterionName());
        label.setFont(Font.font("Verdana",20));
        int next_i = i;
        int next_j = j+1;

        if (next_j>= criterions.length){
            next_i = next_i+1;
            next_j = next_i+1;
        }

        if (next_i>= criterions.length-1){
            nextButton.setOnAction((event2 -> {
                criterionWeights.setComparison(i, j, Double.parseDouble(field.getText()));
                end();
            }));
        }
        else {
            int finalNext_i = next_i;
            int finalNext_j = next_j;
            nextButton.setOnAction((event2 -> {
                criterionWeights.setComparison(i, j, Double.parseDouble(field.getText()));
                compareCriterions(label, field, nextButton, finalNext_i, finalNext_j);
            }));
        }
    }

    private void end(){
        mainBox.getChildren().clear();

        FinalWeightVectorCalculator calc = new FinalWeightVectorCalculator();
        for (Criterion crit: criterions)
            calc.addWeightVector(crit.weightVector());

        calc.setCriterionWeightVectors(criterionWeights.weightVector());

        Label vector = new Label();
        SimpleMatrix weightVector = calc.calculate();
        vector.setText("Weight vector: "+ Arrays.toString(weightVector.getDDRM().getData()));

        int[] sortedIndices = IntStream.range(0, weightVector.getNumElements())
                .boxed().sorted(Comparator.comparingDouble(weightVector::get))
                .mapToInt(ele -> ele).toArray();


        Text ranking = new Text();
        StringBuilder text = new StringBuilder();
        for (int i= sortedIndices.length-1; i>=0; i--){
            text.append(sortedIndices.length - i).append(". ").append(alternatives[sortedIndices[i]]).append("\n");
        }
        ranking.setText(text.toString());
        ranking.setFont(Font.font("Verdana",20));

        Label[] inconsistencyLabels = new Label[criterions.length];
        for (int i=0; i<criterions.length; i++)
            inconsistencyLabels[i] = new Label("Criterion \""+criterions[i].getCriterionName()+"\" inconsistency: "+criterions[i].inconsistencyIndex());

        mainBox.getChildren().addAll(ranking, vector);
        for (Label label: inconsistencyLabels)
            mainBox.getChildren().add(label);
    }

    public VBox getMainBox(){ return mainBox; }
}
