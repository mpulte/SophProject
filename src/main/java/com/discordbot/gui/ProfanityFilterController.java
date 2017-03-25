package com.discordbot.gui;

import com.discordbot.model.ProfanityFilter;
import com.discordbot.util.ProfanityFilterListener;
import com.discordbot.util.SettingsManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.security.InvalidKeyException;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProfanityFilterController implements FXMLController {

    @FXML
    public TextField addField;
    @FXML
    public Button addButton;
    @FXML
    public CheckComboBox<String> removeComboBox;
    @FXML
    public Button removeButton;
    @FXML
    public CheckBox replyGuildCheckBox;
    @FXML
    public CheckBox replyPrivateCheckBox;

    private ProfanityFilter filter;

    public ProfanityFilterController(ProfanityFilter filter) {
        this.filter = filter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addField.setOnAction(e -> handleAddAction());
        addButton.setOnAction(e -> handleAddAction());
        removeButton.setOnAction(e -> handleRemoveAction());
        replyGuildCheckBox.setOnAction(e -> handleGuildCheckBox());
        replyPrivateCheckBox.setOnAction(e -> handlePrivateCheckBox());

        try {
            replyGuildCheckBox.setSelected(SettingsManager.getBoolean(ProfanityFilterListener.SETTING_REPLY_GUILD));
        } catch (InvalidKeyException e) {
            SettingsManager.setBoolean(ProfanityFilterListener.SETTING_REPLY_GUILD, false);
        }
        try {
            replyPrivateCheckBox.setSelected(SettingsManager.getBoolean(ProfanityFilterListener.SETTING_REPLY_PRIVATE));
        } catch (InvalidKeyException e) {
            SettingsManager.setBoolean(ProfanityFilterListener.SETTING_REPLY_PRIVATE, false);
        }

        updateRemoveComboBox();
    }

    @Override
    public void stop() {
    }

    public void handleAddAction() {
        String word = addField.getText();
        if (!word.isEmpty()) {
            filter.add(word);
            addField.setText("");
            updateRemoveComboBox();
        }
    }

    public void handleRemoveAction() {
        Collection<String> words = removeComboBox.getCheckModel().getCheckedItems();
        if (!words.isEmpty()) {
            filter.remove(words.toArray(new String[words.size()]));
            updateRemoveComboBox();
        }
    }

    private void updateRemoveComboBox() {
        ObservableList<String> comboBoxList = removeComboBox.getItems();
        List<String> filterList = filter.asList();

        // determine which words to add or remove
        Collection<String> toRemove =
                comboBoxList.stream()
                        .filter(w -> filterList.stream().noneMatch(w::equalsIgnoreCase))
                        .collect(Collectors.toList());
        Collection<String> toAdd =
                filterList.stream()
                        .filter(w -> comboBoxList.stream().noneMatch(w::equalsIgnoreCase))
                        .collect(Collectors.toList());

        // remove check from items
        removeComboBox.getCheckModel().clearChecks();

        // add or remove words if necessary
        comboBoxList.addAll(toAdd);
        comboBoxList.removeAll(toRemove);
        comboBoxList.sort(String::compareTo);
    }

    private void handleGuildCheckBox() {
        SettingsManager.setBoolean(ProfanityFilterListener.SETTING_REPLY_GUILD, replyGuildCheckBox.isSelected());
    }

    private void handlePrivateCheckBox() {
        SettingsManager.setBoolean(ProfanityFilterListener.SETTING_REPLY_PRIVATE, replyPrivateCheckBox.isSelected());
    }

    @Override
    public void setResizeListener(ResizeListener listener) {
    }

}
