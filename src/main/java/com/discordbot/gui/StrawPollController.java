package com.discordbot.gui;

import com.discordbot.DiscordBot;
import com.discordbot.command.CommandHandler;
import com.discordbot.command.CommandReceivedEvent;
import com.discordbot.command.StrawPollCommand;
import com.discordbot.model.StrawPoll;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StrawPollController implements FXMLController {

    @FXML
    private Button startStopButton;
    @FXML
    private Button addOptionButton;
    @FXML
    private ComboBox<ComboBoxEntry> guildComboBox;
    @FXML
    private ComboBox<ComboBoxEntry> channelComboBox;
    @FXML
    private TimeTextField timeField;
    @FXML
    private TextField tagField;
    @FXML
    private TextField promptField;

    private Timer timer = null;

    private List<TextField> optionFields = new ArrayList<>();
    private List<Button> removeButtons = new ArrayList<>();

    private ResizeListener resizeListener = null;
    private StatusListener statusListener = null;
    private StrawPoll poll = null;

    @FXML
    private synchronized void handleStartStopButton() {
        if (timer == null) {
            startListening();
        } else {
            stopListening();
        }
    } // method handleStartStopButton

    @FXML
    private void handleResultsButton() {
        if (DiscordBot.getInstance().isRunning() && filledOut(false) && poll != null) {
            DiscordBot.getInstance().getJDA().getTextChannelById(channelComboBox.getValue().getId())
                    .sendMessage(poll.resultsAsString()).queue();
        }
    } // method handleResultsButton

    @FXML
    private void handleAddOptionButton() {
        addOption();
    } // method handleAddOptionButton

    private void addOption() {
        final int row = GridPane.getRowIndex(addOptionButton);

        // set up text field
        TextField textField = new TextField();
        textField.setPrefWidth(300);
        optionFields.add(textField);

        // set up button
        Button button = new Button("Remove");
        button.setOnAction(e -> removeOption(row));
        removeButtons.add(button);

        // add text field and button to grid pane and array lists
        GridPane gridPane = (GridPane) addOptionButton.getParent();
        gridPane.add(textField, 1, row, 2, 1);
        gridPane.add(button, 3, row);
        gridPane.getChildren().remove(addOptionButton);
        gridPane.add(addOptionButton, 1, row + 1, 3, 1);

        if (resizeListener != null) {
            resizeListener.onResize();
        }
    } // method addOption

    private void removeOption(int row) {
        // don't allow less than 2 rows
        if (optionFields.size() <= 2) {
            return;
        }

        GridPane gridPane = (GridPane) addOptionButton.getParent();
        List<Node> toRemove = new ArrayList<>();
        List<Node> toMove = new ArrayList<>();

        // determine which nodes to remove or move
        for (Node node : gridPane.getChildren()) {
            int rowIndex = GridPane.getRowIndex(node);
            if (node instanceof TextField) {
                if (rowIndex == row) {
                    // remove text field from row to remove
                    toRemove.add(node);
                    optionFields.remove(node);
                } else if (rowIndex > row){
                    // move text fields below the row to remove
                    toMove.add(node);
                }
            } else if (node == addOptionButton) {
                // move add options button
                toMove.add(node);
            } else if (node instanceof Button && rowIndex == GridPane.getRowIndex(addOptionButton) - 1) {
                // remove last remove button only
                toRemove.add(node);
                removeButtons.remove(node);
            }
        }

        // remove nodes to remove
        gridPane.getChildren().removeAll(toRemove);

        // move nodes to move
        for (Node node : toMove) {
            int rowIndex = GridPane.getRowIndex(node);
            int colIndex = GridPane.getColumnIndex(node);

            gridPane.getChildren().remove(node);
            if (node == addOptionButton) {
                gridPane.getChildren().remove(node);
                gridPane.add(node, colIndex, rowIndex - 1, 3, 1);
            } else if (node instanceof TextField) {
                gridPane.getChildren().remove(node);
                gridPane.add(node, colIndex, rowIndex - 1, 2, 1);
            } else if (node instanceof Button){
                gridPane.getChildren().remove(node);
                gridPane.add(node, colIndex, rowIndex - 1);
            }
        }

        if (resizeListener != null) {
            resizeListener.onResize();
        }
    } // method removeOption

    private synchronized void startListening() {
        CommandHandler commandHandler = DiscordBot.getInstance().getCommandHandler();
        if (timer == null && filledOut(true) && !commandHandler.isTag(tagField.getText())) {
            // add the command listener
            List<String> options = optionFields.stream().map(TextInputControl::getText).collect(Collectors.toList());
            poll = new StrawPoll(promptField.getText(), options.toArray(new String[options.size()]));
            commandHandler.addCommandListener(tagField.getText(),
                    new StrawPollCommand(commandHandler, poll, channelComboBox.getValue().getId()));

            // post the poll
            if (DiscordBot.getInstance().isRunning()) {
                DiscordBot.getInstance().getJDA().getTextChannelById(channelComboBox.getValue().getId())
                        .sendMessage("Poll starting (" + CommandReceivedEvent.PREFIX + tagField.getText() + "):\n"
                                + poll.toString() + "\nYou have " + timeField.getText() + " to respond.").queue();
            }

            // start the timer
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                int hours = timeField.getHours();
                                int minutes = timeField.getMinutes();
                                int seconds = timeField.getSeconds();

                                // decrement time 1 second
                                if (seconds > 0) {
                                    --seconds;
                                } else if (minutes > 0) {
                                    --minutes;
                                    seconds = 59;
                                } else if (hours > 0) {
                                    --hours;
                                    minutes = 59;
                                    seconds = 59;
                                } else {
                                    //  stop the timer
                                    stopListening();
                                    hours = minutes = seconds = 0;
                                }

                                // update time field
                                DecimalFormat format = new DecimalFormat("00");
                                timeField.setText(format.format(hours)
                                        + ":" + format.format(minutes)
                                        + ":" + format.format(seconds));
                            });
                        }
                    }, 0, 1000);

            startStopButton.setText("Stop");
            setEditable(false);
        }
    } // method startListening

    private synchronized void stopListening() {
        // remove the command listener
        DiscordBot.getInstance().getCommandHandler().removeCommandListener(tagField.getText());

        // stop the timer
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;

            startStopButton.setText("Start");
            setEditable(true);

            // post the poll
            if (DiscordBot.getInstance().isRunning()) {
                DiscordBot.getInstance().getJDA().getTextChannelById(channelComboBox.getValue().getId())
                        .sendMessage("Poll ended (" + CommandReceivedEvent.PREFIX + tagField.getText() + "):\n"
                                + poll.resultsAsString()).queue();
            }
        }
    } // method stopListening

    private boolean filledOut(boolean checkTimer) {
        if (checkTimer && timeField.getHours() == 0 && timeField.getMinutes() == 0 && timeField.getSeconds() == 0) {
            timeField.requestFocus();
            return false;
        } else if (tagField.getText().equals("")){
            tagField.requestFocus();
            return false;
        } else if (guildComboBox.getSelectionModel().getSelectedItem() == null) {
            guildComboBox.requestFocus();
            return false;
        } else if (channelComboBox.getSelectionModel().getSelectedItem() == null) {
            channelComboBox.requestFocus();
            return false;
        } else if (promptField.getText().equals("")){
            promptField.requestFocus();
            return false;
        }
        for (TextField field : optionFields) {
            if (field.getText().equals("")){
                field.requestFocus();
                return false;
            }
        }
        return true;
    } // method filledOut

    private void setEditable(boolean enabled) {
        timeField.setEditable(enabled);
        tagField.setEditable(enabled);
        promptField.setEditable(enabled);
        guildComboBox.setDisable(!enabled);
        channelComboBox.setDisable(!enabled);
        addOptionButton.setDisable(!enabled);
        for (Button button : removeButtons) {
            button.setDisable(!enabled);
        }
    } // method setEditable

    private void updateGuildComboBox() {
        ComboBoxEntry oldEntry = guildComboBox.getValue();

        if (DiscordBot.getInstance().isRunning()) {
            for (Guild guild : DiscordBot.getInstance().getJDA().getGuilds()) {
                ComboBoxEntry entry = new ComboBoxEntry(guild.getId(), guild.getName());
                guildComboBox.getItems().add(entry);
                if (oldEntry != null && guild.getId().equals(oldEntry.getId())) {
                    guildComboBox.setValue(entry);
                }
            }
        }
    } // method updateGuildComboBox

    private void updateChannelComboBox() {
        ComboBoxEntry oldEntry = channelComboBox.getValue();

        channelComboBox.setItems(FXCollections.observableArrayList());
        if (DiscordBot.getInstance().isRunning() && guildComboBox.getValue() != null) {
            Guild guild = DiscordBot.getInstance().getJDA().getGuildById(guildComboBox.getValue().getId());
            for (Channel channel : guild.getTextChannels()) {
                ComboBoxEntry entry = new ComboBoxEntry(channel.getId(), channel.getName());
                channelComboBox.getItems().add(entry);
                if (oldEntry != null && channel.getId().equals(oldEntry.getId())) {
                    channelComboBox.setValue(entry);
                }
            }
        }
    } // method updateChannelComboBox

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // add two options (the minimum);
        addOption();
        addOption();

        updateGuildComboBox();
        updateChannelComboBox();
        guildComboBox.setOnAction(e -> updateChannelComboBox());

        statusListener = new StatusListener();
        DiscordBot.getInstance().addEventListener(statusListener);
    } // method initialize

    @Override
    public void stop() {
        DiscordBot.getInstance().removeEventListener(statusListener);
        stopListening();
    } // method stop

    @Override
    public void setResizeListener(ResizeListener listener) {
        resizeListener = listener;
    }

    private class ComboBoxEntry {

        private String id;
        private String name;

        ComboBoxEntry(String id, String name) {
            this.id = id;
            this.name = name;
        } // constructor

        String getId() {
            return id;
        } // method getId

        @Override
        public String toString() {
            return name;
        }
    } // class ComboBoxEntry

    private class StatusListener extends ListenerAdapter {

        @Override
        public void onReady(ReadyEvent event) {
            callUpdates();
        } // method onReady

        @Override
        public void onResume(ResumedEvent event) {
            callUpdates();
        } // method onResume

        private void callUpdates() {
            Platform.runLater(() -> {
                updateGuildComboBox();
                updateChannelComboBox();
            });
        } // method callUpdates

    } // class StatusListener

} // class StrawPollController