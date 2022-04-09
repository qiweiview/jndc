package jndc_client.gui_support;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class GuiStart extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        URL resource = GuiStart.class.getResource("index.fxml");

        Parent parent = FXMLLoader.load(resource);
        primaryStage.setTitle("jndc_client_v1.0");
        primaryStage.setResizable(false);
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();

        //close jvm on close window
        primaryStage.setOnCloseRequest(e->{
            System.exit(1);
        });
    }

    public void start(){
        launch(new String[]{});
    }

}
