package project;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.concurrent.atomic.AtomicInteger;

public class Controller {
    private Criterion[] criterions;
    private Criterion criterionWeights;
    private String[] alternatives;

    private VBox mainBox= new VBox();

    public void start(){
        TextField criterionField =new TextField();
        Label criterionLabel = new Label("Enter criterions seperated by commas:");
        Button nextButton = new Button("Next");

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
    }

    private void enterAlternatives(){
        TextField alternativeField =new TextField();
        Label alterativeLabel = new Label("Enter alternatives seperated by commas:");
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
        Label label = new Label();
        TextField comparison =new TextField();
        mainBox.getChildren().clear();
        mainBox.getChildren().addAll(label, comparison, nextButton);

        nextComparison(label, comparison, nextButton, 0, 1, 0);
    }

    private void nextComparison(Label label, TextField field, Button nextButton, int i,int j, int crit){
        label.setText("Compare "+alternatives[i]+" with "+alternatives[j]+
                " by criterion "+criterions[crit].getCriterionName());

        int next_i = i;
        int next_j = j+1;
        int next_crit = crit;

        if (next_j>= alternatives.length){
            next_i = next_i+1;
            next_j = next_i+1;
        }

        if (next_i>= alternatives.length-1){
            next_crit = next_crit + 1;
            next_i = 0;
            next_j = 1;
        }

        if (next_crit >= criterions.length){
            nextButton.setOnAction((event2 -> {
                criterions[crit].setComparison(i, j, Double.parseDouble(field.getText()));
                compareCriterions(label, field, nextButton, 0, 1);
            }));
        }
        else {
            int finalNext_i = next_i;
            int finalNext_j = next_j;
            int finalNext_crit = next_crit;
            nextButton.setOnAction((event2 -> {
                criterions[crit].setComparison(i, j, Double.parseDouble(field.getText()));
                nextComparison(label, field, nextButton, finalNext_i, finalNext_j, finalNext_crit);
            }));
        }
    }

    private void compareCriterions(Label label, TextField field, Button nextButton, int i,int j){
        label.setText("Compare "+criterions[i].getCriterionName()+" with "+criterions[j].getCriterionName());

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
        vector.setText(calc.calculate().toString());
        mainBox.getChildren().addAll(vector);
    }

    public VBox getMainBox(){ return mainBox; }
}
