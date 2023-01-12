package project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Criterion[][] criterions;
    private Criterion[] criterionWeights;
    private String[] alternatives;
    private String[] criterionsStrings;
    private int numExperts;
    private boolean gmmMethod;

    private VBox mainBox= new VBox();

    private ArrayList[][] comparisons;
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
            criterionsStrings = criterionField.getText().split(", |,");
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
            getNumExperts();
        }));
    }

    private void getNumExperts(){
        TextField numField =new TextField();
        numField.setFont(Font.font("Verdana",20));
        Label numLabel = new Label("Enter number of experts:");
        numLabel.setFont(Font.font("Verdana",20));

        Label methodLabel = new Label("Choose weight vector calculation method:");
        methodLabel.setFont(Font.font("Verdana",20));
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "EVM",
                        "GMM"
                );
        ComboBox method = new ComboBox(options);

        Button nextButton = new Button("Next");

        mainBox.getChildren().clear();
        mainBox.getChildren().addAll(numLabel, numField, methodLabel, method, nextButton);
        nextButton.setOnAction((event1 -> {
            if (method.getValue().toString()=="EVM")
                gmmMethod = false;
            else
                gmmMethod = true;

            numExperts = Integer.parseInt(numField.getText());

            criterions = new Criterion[numExperts][criterionsStrings.length];
            for (int i = 0; i<numExperts; i++)
                for (int j = 0; j<criterions[0].length; j++) {
                    criterions[i][j] = new Criterion(criterionsStrings[j]);
                    criterions[i][j].createMatrix(alternatives.length);
                }

            criterionWeights = new Criterion[numExperts];
            for (int i = 0; i<numExperts; i++) {
                criterionWeights[i] = new Criterion("CRITERIONS");
                criterionWeights[i].createMatrix(criterions[0].length);
            }

            compare();
        }));
    }

    private void compare(){
        Button nextButton = new Button("Next");
        Text label = new Text();
        TextField comparison =new TextField();
        mainBox.getChildren().clear();
        mainBox.getChildren().addAll(label, comparison, nextButton);

        comparisons = new ArrayList[numExperts][criterions[0].length];
        for (int e = 0; e<numExperts; e++) {
            for (int i = 0; i < criterions[0].length; i++) {
                comparisons[e][i] = new ArrayList();
                for (int j = 0; j < alternatives.length; j++)
                    for (int k = j + 1; k < alternatives.length; k++)
                        comparisons[e][i].add(new Tuple(j, k));
            }
        }

        int randomIndex = rand.nextInt(comparisons[0][0].size());
        Tuple toCompare = (Tuple) comparisons[0][0].get(randomIndex);
        comparisons[0][0].remove(randomIndex);
        nextComparison(label, comparison, nextButton, toCompare.getData1(), toCompare.getData2(), 0, 0);
    }

    private void nextComparison(Text label, TextField field, Button nextButton, int i, int j, int crit, int expert){
        label.setText("Expert "+(expert+1)+": Compare "+alternatives[i]+" with "+alternatives[j]+
                " by criterion "+criterions[expert][crit].getCriterionName()+"\n (value 1 to 10 if first alternative is better,\n" +
                "0 to 1 if second alternative is better)");

        label.setFont(Font.font("Verdana",20));
        int next_crit = crit;

        if (comparisons[expert][crit].size()==0)
            next_crit+=1;

        if (next_crit >= criterions[0].length){
            nextButton.setOnAction((event2 -> {
                criterions[expert][crit].setComparison(i, j, Double.parseDouble(field.getText()));
                compareCriterions(label, field, nextButton, 0, 1, expert);
            }));
        }
        else {
            int randomIndex = rand.nextInt(comparisons[expert][next_crit].size());
            Tuple toCompare = (Tuple) comparisons[expert][next_crit].get(randomIndex);
            comparisons[expert][next_crit].remove(randomIndex);

            int finalNext_crit = next_crit;
            nextButton.setOnAction((event2 -> {
                criterions[expert][crit].setComparison(i, j, Double.parseDouble(field.getText()));
                nextComparison(label, field, nextButton, toCompare.getData1(), toCompare.getData2(), finalNext_crit, expert);
            }));
        }
    }

    private void compareCriterions(Text label, TextField field, Button nextButton, int i,int j, int expert){
        if (criterions[0].length==1) {
            if (numExperts == 1 || expert + 1 >= numExperts)
                end();
            else {
                int randomIndex = rand.nextInt(comparisons[expert + 1][0].size());
                Tuple toCompare = (Tuple) comparisons[expert + 1][0].get(randomIndex);
                comparisons[expert + 1][0].remove(randomIndex);
                nextComparison(label, field, nextButton, toCompare.getData1(), toCompare.getData2(), 0, expert + 1);
            }
        }

        label.setText("Expert "+(expert+1)+": Compare "+criterions[expert][i].getCriterionName()+" with "+criterions[expert][j].getCriterionName());
        label.setFont(Font.font("Verdana",20));
        int next_i = i;
        int next_j = j+1;

        if (next_j>= criterions[0].length){
            next_i = next_i+1;
            next_j = next_i+1;
        }

        if (next_i>= criterions[0].length-1){
            if (expert+1<numExperts){
                int randomIndex = rand.nextInt(comparisons[expert+1][0].size());
                Tuple toCompare = (Tuple) comparisons[expert+1][0].get(randomIndex);
                comparisons[expert+1][0].remove(randomIndex);
                nextButton.setOnAction((event2 -> {
                    criterionWeights[expert].setComparison(i, j, Double.parseDouble(field.getText()));
                    nextComparison(label, field, nextButton, toCompare.getData1(), toCompare.getData2(), 0, expert + 1);
                }));
            }
            else {
                nextButton.setOnAction((event2 -> {
                    criterionWeights[expert].setComparison(i, j, Double.parseDouble(field.getText()));
                    end();
                }));
            }
        }
        else {
            int finalNext_i = next_i;
            int finalNext_j = next_j;
            nextButton.setOnAction((event2 -> {
                criterionWeights[expert].setComparison(i, j, Double.parseDouble(field.getText()));
                compareCriterions(label, field, nextButton, finalNext_i, finalNext_j, expert);
            }));
        }
    }

    private void end(){
        mainBox.getChildren().clear();

        FinalWeightVectorCalculator[] calc = new FinalWeightVectorCalculator[numExperts];
        SimpleMatrix currWeightVector;
        SimpleMatrix weightVector = new SimpleMatrix(alternatives.length, 1);
        for (int i=0; i< alternatives.length; i++)
            weightVector.set(i, 0.0);

        for (int i=0; i<numExperts; i++) {
            calc[i] = new FinalWeightVectorCalculator();
            for (Criterion crit : criterions[i]) {
                if (gmmMethod)
                    calc[i].addWeightVector(crit.gmmWeightVector());
                else
                    calc[i].addWeightVector(crit.weightVector());
            }
            if (gmmMethod)
                calc[i].setCriterionWeightVectors(criterionWeights[i].gmmWeightVector());
            else
                calc[i].setCriterionWeightVectors(criterionWeights[i].weightVector());

            currWeightVector=calc[i].calculate();
            for (int j=0;j<currWeightVector.getNumElements();j++)
                weightVector.set(j, weightVector.get(j)+currWeightVector.get(j));
        }

        for (int i=0; i< alternatives.length; i++)
            weightVector.set(i, weightVector.get(i)/numExperts);

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

        Text inconsistencyLabels = new Text();
        StringBuilder inconsistency = new StringBuilder();
        for (int e=0; e<numExperts; e++)
            for (int i=0; i<criterions[0].length; i++)
                inconsistency.append("Expert "+(e+1)+": Criterion \""+criterions[e][i].getCriterionName()+"\" inconsistency: "
                        +criterions[e][i].inconsistencyIndex()+"\n");

        inconsistencyLabels.setText(inconsistency.toString());
        mainBox.getChildren().addAll(ranking, inconsistencyLabels);

    }

    public VBox getMainBox(){ return mainBox; }
}
