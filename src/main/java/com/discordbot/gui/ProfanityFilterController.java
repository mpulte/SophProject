package com.discordbot.gui;

import com.discordbot.model.ProfanityFilter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProfanityFilterController implements FXMLController {

    private static SimpleLog LOG = SimpleLog.getLog("ProfanityFilterController");

    @FXML
    public TextField addField;
    @FXML
    public Button addButton;
    @FXML
    public CheckComboBox<String> removeComboBox;
    @FXML
    public Button removeButton;

    private ProfanityFilter filter;
    private ResizeListener resizeListener = null;

    public ProfanityFilterController(ProfanityFilter filter) {
        this.filter = filter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addField.setOnAction(e -> handleAddAction());
        addButton.setOnAction(e -> handleAddAction());
        removeButton.setOnAction(e -> handleRemoveAction());
        removeComboBox.prefWidthProperty().addListener((observable, oldValue, newValue) -> resizeListener.onResize());
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

        // resize the window
        if (resizeListener != null) {
            resizeListener.onResize();
        }
    }

    @Override
    public void setResizeListener(ResizeListener listener) {
        this.resizeListener = listener;
    }

}
