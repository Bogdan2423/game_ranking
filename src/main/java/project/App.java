package project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application{
    private double sceneWidth=800;
    private double sceneHeight=1000;

    public void start(Stage primaryStage) throws Exception {
        Controller controller = new Controller();
        VBox mainBox=new VBox(controller.getMainBox());
        Scene scene = new Scene(mainBox,sceneWidth,sceneHeight);
        primaryStage.setScene(scene);
        primaryStage.show();

        controller.start();
    }
}
