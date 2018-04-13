package controller;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaLogHandler extends StreamHandler {


    TextArea textArea = null;

    public TextAreaLogHandler(TextArea textArea) {
        
        this.textArea = textArea;
    }

    @Override
    public void publish(LogRecord record) {
        
        super.publish(record);
        flush();
        Platform.runLater(() -> textArea.appendText(getFormatter().format(record)));
    }
}