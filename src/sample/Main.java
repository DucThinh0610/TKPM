package sample;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Media m = new Media("file:///C:/Users/ducth/IdeaProjects/video3.mp4");
        final MediaPlayer mp = new MediaPlayer(m);
        final MediaView mv = (MediaView) root.lookup("#media_view");
        mv.setMediaPlayer(mp);
        mv.setPreserveRatio(true);
        mp.play();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1400, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
