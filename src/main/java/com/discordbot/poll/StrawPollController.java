package com.discordbot.poll;

import com.discordbot.gui.FXMLController;
import com.discordbot.gui.TimeTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.UnaryOperator;

public class StrawPollController implements FXMLController {

    private final String SECONDS = "Seconds";
    private final String MINUTES = "Minutes";
    private final String HOURS = "Hours";

    @FXML
    private Button startStopButton;

    @FXML
    private TimeTextField timeField;

    @FXML
    private ComboBox<String> unitsComboBox;

    @FXML
    private TextField tagField;

    @FXML
    private TextField promptField;

    private Timer timer = null;

    @FXML
    private synchronized void handleStartStopButton(ActionEvent event) {
        if (timer == null) {
            startTimer();
        } else {
            stopTimer();
        }
    } // method handleStartStopButton

    @FXML
    private void handleAddOptionButton(ActionEvent event) {

    } // method handleAddOptionButton

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();

            timer.schedule(new TimerTask() {
                long lastChanged = System.currentTimeMillis();

                @Override
                public void run() {
                    long timeRemaining = Long.parseLong(timeField.getText());
                    long difference = (System.currentTimeMillis() - lastChanged) / 1000;
                    switch (unitsComboBox.getValue()) {
                        case SECONDS:
                            break;
                        case MINUTES:
                            difference /= 60;
                            break;
                        case HOURS:
                            difference /= 3600;
                            break;
                    }

                    if (difference > 0) {
                        timeRemaining -= difference;
                        lastChanged = System.currentTimeMillis();

                        if (timeRemaining <= 0) {
                            timeField.setText("0");
                            Platform.runLater(() -> stopTimer());
                            return;
                        }

                        timeField.setText(Long.toString(timeRemaining));
                    }
                }
            }, 0, 1000);

            startStopButton.setText("Stop");
            timeField.setEditable(false);
            unitsComboBox.setDisable(true);
        }
    } // method startTime

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            startStopButton.setText("Start");
            timeField.setEditable(true);
            unitsComboBox.setDisable(false);
        }
    } // method stopTimer

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        // set up time text view
//        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
//            String input = change.getText();
//            if (input.matches("[0-9]*")) {
//                return change;
//            }
//            return null;
//        };
//        timeField.setTextFormatter(new TextFormatter<String>(integerFilter));

        // set up units combo box
        ObservableList<String> options = FXCollections.observableArrayList(SECONDS, MINUTES, HOURS);
        unitsComboBox.getItems().addAll(options);
        unitsComboBox.setValue("Seconds");
    } // method initialize

    @Override
    public void stop() {
        stopTimer();
    } // method stop

} // class StrawPollController