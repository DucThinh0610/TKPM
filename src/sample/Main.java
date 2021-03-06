package sample;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.collections.ObservableList;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javafx.collections.FXCollections;

public class Main extends Application implements XCell.OnClickSaveString, Tag.OnClickItemWord {
    private MediaPlayer mp;
    private MediaView mediaView;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Button btnPlay;
    private Button btnAddVideo;
    private Button btnAddSubtitle;
    private TextField searchField;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private Duration duration;
    private boolean repeat = false;
    private HBox mediaBar;
    private FlowPane flowPaneWord;
    private ListView<String> list_video;
    private ListView<String> list_subtitle;
    private ListView<String> list_history;
    private String name;
    private Media m;
    private List<Word> mListWordSelected = new ArrayList<>();
    private ImageView imvSaveWord;
    private String stringSelected;
    private List<String> rootsub = new ArrayList<String>() ;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        mediaView = (MediaView) root.lookup("#media_view");
        mediaBar = (HBox) root.lookup("#media_bar");
        btnAddVideo = (Button) root.lookup("#btn_addVideo");
        btnAddSubtitle = (Button) root.lookup("#btn_addSubtitle");
        flowPaneWord = (FlowPane) root.lookup("#flow_panel");
        flowPaneWord.setHgap(10);
        flowPaneWord.setVgap(15);
        list_video = (ListView<String>) root.lookup("#lv_video");
        list_subtitle = (ListView<String>) root.lookup("#list_subtitle");
        list_history= (ListView<String>) root.lookup("#lv_history");
        imvSaveWord = (ImageView) root.lookup("#imv_save_word");
        searchField = (TextField) root.lookup("#text_search");
        imvSaveWord.setVisible(false);
        javafx.scene.image.Image image = new Image(getClass().getResourceAsStream("star.png"));
        imvSaveWord.setImage(image);
        //Hiển thị danh sách video
        imvSaveWord.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (stringSelected == null)
                    return;
                if (mListWordSelected.size() == 0) {
                    System.out.println("Luu nguyen 1 cau: " + stringSelected);
                } else {
                    System.out.println("Luu cac tu trong cau: " + mListWordSelected.toString());
                }
            }
        });
        btnAddVideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("mp4", "*.mp4")
                );
                File selectedDirectory = fileChooser.showOpenDialog(primaryStage);
                if (selectedDirectory != null) {

                    System.out.println(selectedDirectory.getAbsolutePath());
                    CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES
                    };
                    System.out.println(selectedDirectory.getName());
                    name = selectedDirectory.getName();
                    String pathTemp=getClass().getResource("/data/videos").toString().substring(6);
                    pathTemp=pathTemp.replaceAll("%20"," ");

                    Path to = Paths.get(pathTemp, selectedDirectory.getName());
                    System.out.println("luu video " + to);


                    try {
                        Files.copy(selectedDirectory.toPath(), to, options);
                        list_video.getItems().add(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    Files.copy(from, to, options);
                }

            }
        });
        btnAddSubtitle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("txt", "*.txt")
                );
                File selectedDirectory = fileChooser.showOpenDialog(primaryStage);
                if (selectedDirectory != null) {

                    System.out.println(selectedDirectory.getAbsolutePath());
                    CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES
                    };
                    System.out.println(selectedDirectory.getName());
                    String pathTemp=getClass().getResource("/data/subtitle").toString().substring(6);
                    pathTemp=pathTemp.replaceAll("%20"," ");

                    Path to = Paths.get(pathTemp, name + ".txt");
                    System.out.println(to);
                    try {
                        Files.copy(selectedDirectory.toPath(), to, options);
                        readFile(name);
                        System.out.println(name);
                        //readFile(newValue.substring(0,newValue.length()-4));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Files.copy(from, to, options);
                }
            }
        });
        list_video.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("chọn item" + newValue);
                stopRequested = false;
                atEndOfMedia = false;
                repeat = false;
                mp.stop();
                btnPlay.setText(">");
                //m = new Media("file:///F:/"+ newValue +".mp4");
                m = new Media(getClass().getResource("/data/videos/" + newValue).toExternalForm());
                System.out.println("lấy video " + getClass().getResource("/data/videos/" + newValue).getPath());
                mp = new MediaPlayer(m);

                mediaView.setMediaPlayer(mp);
                mediaView.setPreserveRatio(true);
                replay();
                name = newValue.substring(0, newValue.length() - 4);
                readFile(name);
                readFile2(name);
            }
        });
        list_subtitle.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("chọn item " + newValue.substring(0, 5));

                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                stringSelected = newValue;
                Date date = null;
                try {
                    date = sdf.parse(newValue.substring(0, 5));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mp.seek(Duration.millis(date.getTime()));
                String s = newValue.substring(6, newValue.length() - 1);
                String[] arr = s.split(" ");
                mListWordSelected.clear();
                flowPaneWord.getChildren().clear();
                imvSaveWord.setVisible(true);
                for (int i = 0; i < arr.length; i++) {
                    String data = arr[i];
                    System.out.println(data);
                    flowPaneWord.getChildren().add(new Tag(new Word(data, i), Main.this));
                }
                flowPaneWord.setPadding(new Insets(10, 10, 10, 10));
            }
        });
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {



                String value = newValue.toUpperCase();
                List<String> liststring = new ArrayList<String>();
                //  System.out.println(" OK"+list_subtitle.getItems());
                for (Object entry : rootsub) {
                    // System.out.println(" OK"+entry);
                    boolean match = true;
                    String entryText = (String)entry;
                    if (!entryText.toUpperCase().contains(value)) {
                        match = false;
                        // break;
                    }
                    if (match) {
                        liststring.add((String)entry);
                    }
                }
                list_subtitle.setItems(FXCollections.observableArrayList(liststring));


            }
        });
        //Chạy Video
        getFileName(getClass().getResource("/data/videos/").getPath());
        System.out.println(list_video.getItems().get(0));

        name = list_video.getItems().get(0).substring(0, list_video.getItems().get(0).length() - 4);
        System.out.println(name);
        readFile(name);
        readFile2(name);
        m = new Media(getClass().getResource("/data/videos/" + name + ".mp4").toExternalForm());
        mp = new MediaPlayer(m);
        mediaView.setMediaPlayer(mp);
        mediaView.setPreserveRatio(true);

        // Add button play
        btnPlay = new Button(">");
        mediaBar.getChildren().add(btnPlay);
        // Add spacer
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    mp.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });
        mediaBar.getChildren().add(volumeSlider);
        //********

        btnPlay.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                MediaPlayer.Status status = mp.getStatus();


                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    mp.play();
                } else {
                    mp.pause();
                }
            }
        });
        replay();
        primaryStage.setTitle("Học tiếng anh");
        primaryStage.setScene(new Scene(root, 1400, 700));
        primaryStage.show();
    }
    public void getFileName(String directoryName) {
        directoryName=directoryName.replaceAll("%20"," ");
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    System.out.println("đọc tên file:" +file.getName());
                    list_video.getItems().add(file.getName());
                }
            }
        }
    }
    public void readFile2(String nameFile) {
        // array_subtitle= new ArrayList<>();
        list_history.getItems().clear();
        String pathTemp=getClass().getResource("/data/history/").getPath()+nameFile+".txt";
        pathTemp=pathTemp.replaceAll("%20"," ");
        File temp=(new File(pathTemp));
        try (BufferedReader reader = new BufferedReader(new FileReader(temp))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list_history.getItems().add(line);
                //array_subtitle.add(line);
                //System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Không tìm thấy file subtitle");
            list_history.getItems().add("Không tìm thấy file history");
            // e.printStackTrace();
        }
        list_history.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new XCell(Main.this);
            }
        });
    }
    public void readFile(String nameFile) {
        // array_subtitle= new ArrayList<>();
        list_subtitle.getItems().clear();
        String pathTemp=getClass().getResource("/data/subtitle/").getPath()+nameFile+".txt";
        pathTemp=pathTemp.replaceAll("%20"," ");
        File temp=(new File(pathTemp));
        try (BufferedReader reader = new BufferedReader(new FileReader(temp))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list_subtitle.getItems().add(line);
                //array_subtitle.add(line);
                //System.out.println(line);
                rootsub.add(line);
            }
        } catch (IOException e) {
            System.out.println("Không tìm thấy file subtitle");
            list_subtitle.getItems().add("Không tìm thấy file subtitle");
            // e.printStackTrace();
        }
        list_subtitle.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new XCell(Main.this);
            }
        });
    }

    public void replay() {
        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });
        mp.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mp.pause();
                    stopRequested = false;
                } else {
                    btnPlay.setText("||");
                }
            }
        });

        mp.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                btnPlay.setText(">");
            }
        });
        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                updateValues();
            }
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    btnPlay.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mp.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mp.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    // Lưu vào lịch sử

    @Override
    public void saveString(String s) {
        System.out.println(s);
            String pathTemp1=getClass().getResource("/data/history").toString().substring(6);
            pathTemp1=pathTemp1.replaceAll("%20"," ");
            Path path =Paths.get(pathTemp1, name + ".txt");
            System.out.println("path:" +path);
            if (!Files.exists(path))
            {
                try {
                  //  Files.createDirectories(path);
                    Files.createFile(path);

                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            try{
                String pathTemp=getClass().getResource("/data/history/"+name+".txt").getPath();
                pathTemp=pathTemp.replaceAll("%20"," ");
                File file=(new File(pathTemp));

                FileWriter fw = new FileWriter(file,true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                pw.println(s);
                pw.close();
                list_history.getItems().add(s);
                System.out.println("Data successfully appended at the end of file");
            }
            catch (IOException e)
            {
                System.out.println("Exception occurred:");
                e.printStackTrace();
            }
    }

    @Override
    public void OnClickWord(Word word) {
        if (word.isSelected()) {
            mListWordSelected.add(word);
        } else {
            mListWordSelected.remove(word);
        }
        Collections.sort(mListWordSelected, new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });
        for (Word temp : mListWordSelected) {
            System.out.println(temp.getId() + "----" + temp.getName());
        }
    }
}
