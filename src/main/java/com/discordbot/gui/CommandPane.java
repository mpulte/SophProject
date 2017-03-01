package com.discordbot.gui;

import com.discordbot.command.CommandHandler;
import com.discordbot.command.CommandSetting;
import com.discordbot.sql.CommandDB;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import javax.swing.JOptionPane;
import java.util.List;

public class CommandPane extends GridPane {

    private final Insets PADDING = new Insets(5, 5, 5, 5);

    private final double H_GAP = 20d;
    private final double V_GAP = 5d;

    private CommandHandler commandHandler;
    private CommandDB commandDB;

    public CommandPane(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        commandDB = new CommandDB();

        // format the pane
        setStyle("-fx-background-color:transparent");
        setHgap(H_GAP);
        setVgap(V_GAP);
        setPadding(PADDING);

        // set column widths
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(40);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(40);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(20);
        getColumnConstraints().addAll(column1, column2, column3);

        // add each setting to the layout
        List<CommandSetting> settings = commandDB.selectAll();
        for (int i = 0; i < settings.size(); i++) {
            addSettingRow(i, settings.get(i));
        }
    } // constructor

    private void addSettingRow(int row, CommandSetting setting) {
        // label
        String className = setting.getCls().getName().substring(setting.getCls().getName().lastIndexOf('.') + 1);
        Label label = new Label(className);
        add(label, 0, row);

        // text field
        TextField textField = new TextField(setting.getTag());
        add(textField, 1, row);

        // check box
        CheckBox checkBox = new CheckBox(setting.isEnabled() ? "on" : "off");
        checkBox.setSelected(setting.isEnabled());
        add(checkBox, 2, row);

        // action listeners
        textField.setOnAction(e -> update(setting, label, textField, checkBox));
        checkBox.setOnAction(e -> update(setting, label, textField, checkBox));
    } // method addSettingRow

    private void update(CommandSetting setting, Label label, TextField textField, CheckBox checkBox) {
        // check if the text has changed
        String text = textField.getText().trim();
        if (!setting.getTag().equals(text)) {
            if (text.contains(" ")) {
                JOptionPane.showMessageDialog(null,
                        "Command tags cannot contain spaces.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                textField.setText(setting.getTag());
            } else if (text.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Command tags cannot be blank.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                textField.setText(setting.getTag());
            } else {
                // we want both the setting and checkbox to match before making changes
                // if not, the listener will be added or removed when the enabled setting is updated
                String oldText = setting.getTag();
                setting.setTag(text);
                if (setting.isEnabled() && checkBox.isSelected()) {
                    commandHandler.removeCommandListener(oldText);
                }
                commandDB.update(setting);
            }
        }

        // check if the check box has changed
        if (checkBox.isSelected() != setting.isEnabled()) {
            setting.setEnabled(checkBox.isSelected());
        }

        // update the command
        commandHandler.setCommandListener(setting);

        // check that update was successful
        if (!setting.isEnabled() && checkBox.isSelected()) {
            checkBox.setSelected(false);
            JOptionPane.showMessageDialog(null,
                    "You cannot have two commands with the same tag.\n" +
                            label.getText() + " has been disabled.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        // update the database
        commandDB.update(setting);

        // update check box text
        checkBox.setText(checkBox.isSelected() ? "on" : "off");
    } // method update

} // class CommandPane