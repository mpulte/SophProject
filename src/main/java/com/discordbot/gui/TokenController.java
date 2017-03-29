package com.discordbot.gui;

import com.discordbot.model.Token;
import com.discordbot.sql.TokenDB;
import com.discordbot.util.SettingHandler;
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

/**
 * A {@link FXMLController} implementation for controlling TokenPane.fxml.
 *
 * @see FXMLController
 */
public class TokenController implements FXMLController {

    /**
     * The key to use for the token setting
     */
    public static final String TOKEN_SETTING = "token";

    @FXML
    private GridPane gridPane;
    @FXML
    private Button addButton;

    private TokenDB database = new TokenDB();
    private ResizeListener resizeListener = null;

    private List<TextField> tokenFields = new LinkedList<>();
    private List<TextField> nameFields = new LinkedList<>();
    private ToggleGroup toggleGroup = new ToggleGroup();

    private int selectedRow = -1;

    /**
     * Initializes the TokenController. Calls {@link #loadTokens()}.
     *
     * @param location  The location used to resolve relative paths for the root object, or <tt>null</tt> if the
     *                  location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if the root object was not
     *                  localized.
     * @see javafx.fxml.Initializable#initialize(URL, ResourceBundle)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTokens();
    }

    /**
     * Not implemented
     */
    @Override
    public void stop() {
    }

    /**
     * Handles save button clicks. Calls {@link #saveTokens()}.
     */
    @FXML
    public void handleSaveButton() {
        saveTokens();
    }

    /**
     * Handles save button clicks. Calls {@link #loadTokens()}.
     */
    @FXML
    public void handleRevertButton() {
        loadTokens();
    }

    /**
     * Handles add button clicks. Calls {@link #addToken()}.
     */
    @FXML
    public void handleAddButton() {
        addToken();
    }

    /**
     * Adds a new blank row for setting a token.
     */
    private void addToken() {
        addToken(new Token("", ""), false);
    }

    /**
     * Adds a new row for a token.
     *
     * @param token    The {@link Token} to use to set the value of the name field and token field added.
     * @param selected The truth value for the selection of the check box added.
     */
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
        tokenField.setPrefWidth(450);
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

    /**
     * Removes a row for a token.
     *
     * @param row The row to remove.
     */
    private void removeToken(int row) {
        List<Node> toRemove = new ArrayList<>();
        List<Node> toMove = new ArrayList<>();

        // don't remove if it's the last one
        if (GridPane.getRowIndex(addButton) <= 1) {
            return;
        }

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

    /**
     * Accessor for the list of tokens. The list excludes rows with empty token values and includes rows with empty name
     * values.
     *
     * @return the list of {@link Token}s.
     */
    private List<Token> getTokens() {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < tokenFields.size(); ++i) {
            String token = tokenFields.get(i).getText();
            String name = nameFields.get(i).getText();

            // don't add rows with empty tokens
            if (!token.isEmpty()) {
                tokens.add(new Token(token, name));
            }
        }
        return tokens;
    }

    /**
     * Loads the saved {@link Token}s from {@link TokenDB} and the token setting from {@link SettingHandler}.
     */
    private void loadTokens() {
        // load selected token setting
        String setting;
        try {
            setting = SettingHandler.getString(TOKEN_SETTING);
        } catch (InvalidKeyException e) {
            setting = "";
        }
        final String savedToken = setting;

        // load tokens
        List<Token> tokens = database.selectAll();
        for (int i = 0; i < tokens.size(); ++i) {
            if (i < tokenFields.size()) {
                tokenFields.get(i).setText(tokens.get(i).getToken());
                nameFields.get(i).setText(tokens.get(i).getName());
            } else {
                addToken(tokens.get(i), tokens.get(i).getToken().equals(savedToken));
            }
        }

        // remove any extra rows
        while (tokenFields.size() > tokens.size()) {
            removeToken(tokens.size());
        }

        // if there are no saved tokens, create a first row
        if (tokenFields.isEmpty()) {
            addToken(new Token("", ""), false);
        }
    }

    /**
     * Saves the {@link Token}s to {@link TokenDB} and the token setting to {@link SettingHandler}.
     */
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
        String token = (selectedRow != -1 && tokenFields.size() > selectedRow)
                ? tokenFields.get(selectedRow).getText() : "";
        SettingHandler.setString(TOKEN_SETTING, token);
    }

    /**
     * Sets the {@link ResizeListener}.
     *
     * @param listener The {@link ResizeListener} to set.
     */
    @Override
    public void setResizeListener(ResizeListener listener) {
        resizeListener = listener;
    }
}
