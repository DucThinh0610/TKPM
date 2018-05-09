package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

public class Tag extends Label {
    public Tag() {
        super();
        initTag();
    }

    private Tag(String arg0, Node arg1) {
        super(arg0, arg1);
    }

    public Tag(String arg0) {
        super(arg0);
        initTag();
    }

    private void initTag(){
        setPadding(new Insets(5,10,5,10));
        setContentDisplay(ContentDisplay.RIGHT);
        setGraphicTextGap(8);
        graphicProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> paramObservableValue,
                                Node paramT1, Node paramT2) {

            }
        });
        setStyle("-fx-background-color: white; "
                + "-fx-border-radius: 3;"
                + "-fx-border-color: black;");
    }
}
