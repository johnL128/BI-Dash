package appdscw2sub;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppDsCw2 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("chartVis.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(AppDsCw2.class.getResource("Custom.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Car Sales");
        primaryStage.show();
    }   //splash screen on startup, progress loading data in dashboard

    public static void main(String[] args) {
        LauncherImpl.launchApplication(AppDsCw2.class, args);
    }
}
