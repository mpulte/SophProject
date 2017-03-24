package com.discordbot.gui;

import com.discordbot.model.Setting;
import com.discordbot.model.Token;
import com.discordbot.sql.SettingDB;
import com.discordbot.sql.TokenDB;
import com.discordbot.util.SettingsManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class TokenController implements FXMLController {

    private static final String TOKEN_SETTING = "token";

    @FXML
    public GridPane gridPane;
    @FXML
    public Button addButton;

    private TokenDB database = new TokenDB();
    private ResizeListener resizeListener = null;

    private List<TextField> tokenFields = new LinkedList<>();
    private List<TextField> nameFields = new LinkedList<>();
    private ToggleGroup toggleGroup = new ToggleGroup();

    private int selectedRow = -1;

    @FXML
    public void handleSaveButton() {
        saveTokens();
    }

    @FXML
    public void handleRevertButton() {
        loadTokens();
    }

    @FXML
    public void handleAddButton() {
        addToken();
    }

    private void addToken() {
        addToken(new Token("", ""), false);
    }

    private void addToken(Token token, boolean selected) {
        final int row = GridPane.getRowIndex(addButton);

        // set selected row if necessary
        if (selected) {
            selectedRow = row;
        }

        // set up radio button
        RadioButton radioButton = new RadioButton();
        radioButton.setToggleGroup(toggleGroup);
        radioButton.setOnAction(event -> selectedRow = row);
        radioButton.setSelected(selected);

        // set up text fields
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setText(token.getName());
        nameFields.add(nameField);

        TextField tokenField = new TextField();
        tokenField.setPromptText("Token");
        tokenField.setText(token.getToken());
        tokenField.setPrefWidth(400);
        tokenFields.add(tokenField);

        // set up button
        Button button = new Button("Remove");
        button.setOnAction(e -> removeToken(row));

        // add text fields and button to grid pane and array lists
        gridPane.add(radioButton, 0, row);
        gridPane.add(nameField, 1, row);
        gridPane.add(tokenField, 2, row);
        gridPane.add(button, 3, row);
        gridPane.getChildren().remove(addButton);
        gridPane.add(addButton, 0, row + 1, 4, 1);

        if (resizeListener != null) {
            resizeListener.onResize();
        }
    }

    private void removeToken(int row) {
        List<Node> toRemove = new ArrayList<>();
        List<Node> toMove = new ArrayList<>();

        // determine which nodes to remove or move
        for (Node node : gridPane.getChildren()) {
            int rowIndex = GridPane.getRowIndex(node);
            if (node instanceof TextField) {
                if (rowIndex == row) {
                    // remove text fields from row to remove
                    toRemove.add(node);
                    if (tokenFields.stream().anyMatch(n -> node == n)) {
                        tokenFields.remove(node);
                    } else {
                        nameFields.remove(node);
                    }
                } else if (rowIndex > row) {
                    // move text fields below the row to remove
                    toMove.add(node);
                }
            } else if (node == addButton) {
                // move add button
                toMove.add(node);
            } else if (rowIndex == GridPane.getRowIndex(addButton) - 1) {
                // remove last remove radio button and remove button only
                toRemove.add(node);
                if (node instanceof RadioButton) {
                    toggleGroup.getToggles().remove(node);
                }
            }
        }

        // remove nodes to remove
        gridPane.getChildren().removeAll(toRemove);

        // move nodes to move
        for (Node node : toMove) {
            int rowIndex = GridPane.getRowIndex(node);
            int colIndex = GridPane.getColumnIndex(node);

            gridPane.getChildren().remove(node);
            if (node == addButton) {
                gridPane.add(node, colIndex, rowIndex - 1, 4, 1);
            } else {
                gridPane.add(node, colIndex, rowIndex - 1);
            }
        }

        // if row is selected row, set flag to no row selected
        if (row == selectedRow) {
            selectedRow = -1;
        }

        if (resizeListener != null) {
            resizeListener.onResize();
        }
    }

    private List<Token> getTokens() {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < tokenFields.size(); ++i) {
            tokens.add(new Token(tokenFields.get(i).getText(), nameFields.get(i).getText()));
        }
        return tokens;
    }

    private void loadTokens() {
        // load selected token setting
        String setting;
        try {
            setting = SettingsManager.getString(TOKEN_SETTING);
        } catch (InvalidKeyException e) {
            setting = "";
        }
        final String savedToken = setting;

        // load tokens
        database.selectAll().forEach(token -> addToken(token, token.getToken().equals(savedToken)));
    }

    private void saveTokens() {
        List<Token> tokens = getTokens();

        // delete removed tokens
        database.selectAll()
                .stream()
                .filter(t1 -> tokens.stream().noneMatch(t2 -> t2.equals(t1)))
                .forEach(t -> database.delete(t.getToken()));

        // insert or update remaining tokens
        tokens.forEach(token -> {
            if (database.exists(token.getToken())) {
                database.update(token);
            } else {
                database.insert(token);
            }
        });

        // save selected token setting
        SettingDB settingDB = new SettingDB();
        String token = (selectedRow != -1 && tokenFields.size() > selectedRow)
                ? tokenFields.get(selectedRow).getText() : "";
        if (settingDB.exists(TOKEN_SETTING)) {
            settingDB.update(new Setting(TOKEN_SETTING, token));
        } else {
            settingDB.insert(new Setting(TOKEN_SETTING, token));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTokens();
    }

    @Override
    public void stop() {
        saveTokens();
    }

    @Override
    public void setResizeListener(ResizeListener listener) {
        resizeListener = listener;
    }
}
