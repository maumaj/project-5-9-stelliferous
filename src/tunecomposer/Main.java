/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author wangj2
 */
public class Main extends Application {

    /**
     * Construct the scene and start the application.
     * Loads GUI/layout from the TuneComposer.fxml into a scene, which
     * is placed inside the primary Stage. Program terminates when the user
     * hits the close button. Stage is shown.
     * @param primaryStage the stage for the main window
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        //loads fxml file, places in a new scene, which is placed in the stage    
        Parent root = FXMLLoader.load(getClass().getResource("/view/TuneComposer.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Tune Composer");
        primaryStage.setScene(scene);
        
        //closes the program when the window is closed
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });        
        
        //displays the stage
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
