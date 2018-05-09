package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class XCell extends ListCell<String> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    Image image = new Image(getClass().getResourceAsStream("star.png"));
    Button button = new Button();
    String lastItem;
    private OnClickSaveString mListener;

    public interface OnClickSaveString {
        void saveString(String s);
    }

    public XCell(OnClickSaveString listener) {
        super();
        this.mListener = listener;
        ImageView imv=new ImageView(image);
        imv.setFitHeight(15);
        imv.setFitWidth(15);
        button.setGraphic(imv);
        hbox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (mListener != null)
                    mListener.saveString(label.getText());
            }
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
            lastItem = item;
            label.setText(item != null ? item : "<null>");
            setGraphic(hbox);
        }
    }
}
