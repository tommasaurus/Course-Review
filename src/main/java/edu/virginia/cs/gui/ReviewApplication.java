package edu.virginia.cs.gui;
import edu.virginia.cs.courseReview.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.io.IOException;
public class ReviewApplication extends Application {
    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager manager = new DatabaseManager();
        manager.connect();
        try
        {
            manager.createTables();
            manager.setup();
        }
        catch (Exception e)
        {
        }
//        manager.deleteTables();
//        manager.createTables();
        stg = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ReviewApplication.class.getResource("courseReview-view.fxml"));
        ReviewController con = new ReviewController();
        fxmlLoader.setController(con);
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        stage.setTitle("Course Review");
        stage.setScene(scene);
        stage.show();
    }

    public void changeScene(String fxml) throws IOException
    {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(pane);

    }

}