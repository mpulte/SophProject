package com.discordbot.gui;

import com.discordbot.model.ProfanityFilter;
import com.discordbot.util.ProfanityFilterListener;
import com.discordbot.util.SettingHandler;
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

/**
 * A {@link FXMLController} implementation for controlling ProfanityFilterPane.fxml.
 *
 * @see FXMLController
 */
public class ProfanityFilterController implements FXMLController {

    @FXML
    private TextField addField;
    @FXML
    private Button addButton;
    @FXML
    private CheckComboBox<String> removeComboBox;
    @FXML
    private Button removeButton;
    @FXML
    private CheckBox replyGuildCheckBox;
    @FXML
    private CheckBox replyPrivateCheckBox;

    private ProfanityFilter filter;

    /**
     * @param filter The {@link ProfanityFilter} to use for filtering.
     */
    public ProfanityFilterController(ProfanityFilter filter) {
        this.filter = filter;
    }

    /**
     * Initializes the ProfanityFilterController. Sets up the {@link javafx.event.EventHandler}s for the JavaFX {@link
     * javafx.scene.control.Control}s and calls {@link #updateRemoveComboBox()}.
     *
     * @param location  The location used to resolve relative paths for the root object, or <tt>null</tt> if the
     *                  location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if the root object was not
     *                  localized.
     * @see javafx.fxml.Initializable#initialize(URL, ResourceBundle)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addField.setOnAction(e -> handleAddAction());
        addButton.setOnAction(e -> handleAddAction());
        removeButton.setOnAction(e -> handleRemoveAction());
        replyGuildCheckBox.setOnAction(e -> handleGuildCheckBox());
        replyPrivateCheckBox.setOnAction(e -> handlePrivateCheckBox());

        try {
            replyGuildCheckBox.setSelected(SettingHandler.getBoolean(ProfanityFilterListener.SETTING_REPLY_GUILD));
        } catch (InvalidKeyException e) {
            SettingHandler.setBoolean(ProfanityFilterListener.SETTING_REPLY_GUILD, false);
        }
        try {
            replyPrivateCheckBox.setSelected(SettingHandler.getBoolean(ProfanityFilterListener.SETTING_REPLY_PRIVATE));
        } catch (InvalidKeyException e) {
            SettingHandler.setBoolean(ProfanityFilterListener.SETTING_REPLY_PRIVATE, false);
        }

        updateRemoveComboBox();
    }

    /**
     * Not implemented
     */
    @Override
    public void stop() {
    }

    /**
     * Adds the word in {@link #addField} to the {@link ProfanityFilter}.
     */
    public void handleAddAction() {
        String word = addField.getText();
        if (!word.isEmpty()) {
            filter.add(word);
            addField.setText("");
            updateRemoveComboBox();
        }
    }

    /**
     * Adds the words selected in {@link #removeComboBox} from the {@link ProfanityFilter}.
     */
    public void handleRemoveAction() {
        Collection<String> words = removeComboBox.getCheckModel().getCheckedItems();
        if (!words.isEmpty()) {
            filter.remove(words.toArray(new String[words.size()]));
            updateRemoveComboBox();
        }
    }

    /**
     * Updates {@link #removeComboBox} with the list from the {@link ProfanityFilter}.
     */
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

    /**
     * Updates the reply guild setting used by {@link ProfanityFilterListener}.
     */
    private void handleGuildCheckBox() {
        SettingHandler.setBoolean(ProfanityFilterListener.SETTING_REPLY_GUILD, replyGuildCheckBox.isSelected());
    }

    /**
     * Updates the reply private setting used by {@link ProfanityFilterListener}.
     */
    private void handlePrivateCheckBox() {
        SettingHandler.setBoolean(ProfanityFilterListener.SETTING_REPLY_PRIVATE, replyPrivateCheckBox.isSelected());
    }

    /**
     * Not implemented
     */
    @Override
    public void setResizeListener(ResizeListener listener) {
    }

}
