package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class Tag extends Label {
    public Tag() {
        super();
        initTag();
    }

    private Word word;
    private OnClickItemWord mListener;

    public interface OnClickItemWord {
        void OnClickWord(Word word);
    }

    private Tag(String arg0, Node arg1) {
        super(arg0, arg1);
    }

    public Tag(Word word, OnClickItemWord listener) {
        super(word.getName());
        initTag();
        this.word = word;
        this.mListener = listener;
    }

    private void initTag() {
        setPadding(new Insets(5, 10, 5, 10));
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
        Tag.this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (mListener != null && word != null) {
                    if (word.isSelected()){
                        setStyle("-fx-background-color: white; "
                                + "-fx-border-radius: 3;"
                                + "-fx-border-color: black;");
                        word.setSelected(!word.isSelected());
                    }
                    else {
                        setStyle("-fx-background-color: gray; "
                                + "-fx-border-radius: 3;"
                                + "-fx-border-color: black;");
                        word.setSelected(!word.isSelected());
                    }
                    mListener.OnClickWord(word);
                }
            }
        });
    }

}
