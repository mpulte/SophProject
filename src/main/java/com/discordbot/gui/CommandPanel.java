package com.discordbot.gui;

import com.discordbot.command.CommandHandler;
import com.discordbot.command.CommandSetting;
import com.discordbot.sql.CommandDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

public class CommandPanel extends JPanel {

    private CommandHandler commandHandler;
    private CommandDB commandDB;

    public CommandPanel(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        commandDB = new CommandDB();

        // create a boarder
        setBorder(BorderFactory.createTitledBorder("Commands"));

        // get the settings stored in the database
        List<CommandSetting> settings = commandDB.selectAll();

        // set the layout to a box layout with vertical orientation
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // add each row to the layout
        for (CommandSetting setting : settings) {
            add(new SettingPanel(setting));
        }
    } // constructor

    private class SettingPanel extends JPanel {

        private final CommandSetting setting;
        private JLabel label = null;
        private JTextField textField = null;
        private JCheckBox checkBox = null;

        private SettingPanel(CommandSetting setting) {
            this.setting = setting;

            // set the layout to a GridLayout with 1 row and 3 cols
            setLayout(new SettingLayout());

            // add the components
            addLabel();
            addTextBox();
            addCheckBox();
        } // constructor

        private void addLabel() {
            String className = setting.getCls().getName().substring(setting.getCls().getName().lastIndexOf('.') + 1);
            label = new JLabel(className);
            add(label);
        } // method addLabel

        private void addTextBox() {
            textField = new JTextField(5);
            textField.setText(setting.getTag());
            textField.addActionListener(e -> update());
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {}

                @Override
                public void focusLost(FocusEvent e) {
                    update();
                }
            });
            add(textField);
        } // method addTextBox

        private void addCheckBox() {
            checkBox = new JCheckBox(setting.isEnabled() ? "on" : "off", setting.isEnabled());
            checkBox.addActionListener(e -> update());
            add(checkBox);
        } // method addCheckBox

        private void update() {
            // check if the text has changed
            String text = textField.getText().trim();
            if (!setting.getTag().equals(text)) {
                if (text.contains(" ")) {
                    JOptionPane.showMessageDialog(null,
                            "Command tags cannot contain spaces.",
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

    } // class SettingPanel

    private class SettingLayout implements LayoutManager {

        private final int GAP = 5;
        private int minWidth = 0;
        private int minHeight = 0;
        private int minComponentWidth = 0;
        private int minComponentHeight = 0;
        private boolean dimensionsSet = false;

        @Override
        public void addLayoutComponent(String name, Component comp) {
        } // method addLayoutComponent

        @Override
        public void removeLayoutComponent(Component comp) {
        } //method removeLayoutComponent

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return minimumLayoutSize(parent);
        } // method preferredLayoutSize

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            Dimension dim = new Dimension(0, 0);

            setMinimumSize(parent);

            Insets insets = parent.getInsets();
            dim.width = minWidth + insets.left + insets.right;
            dim.height = minHeight + insets.top + insets.bottom;

            return dim;
        } // method minimumLayoutSize

        private void setMinimumSize(Container parent) {
            minWidth = 0;
            minHeight = 0;
            minComponentWidth = 0;
            minComponentHeight = 0;

            // determines minimum component dimensions
            for (int i = 0; i < parent.getComponentCount() && i < 3; i++) {
                minComponentWidth = Math.max(minComponentWidth, parent.getComponent(i).getMinimumSize().width
                        * (i == 2 ? 2 : 1));
                minComponentHeight = Math.max(minComponentHeight, parent.getComponent(i).getMinimumSize().height);
            }

            minWidth += Math.ceil(minComponentWidth * 2.5 + GAP * 2);
            minHeight = minComponentHeight;

            dimensionsSet = true;
        } // end setMinimumSize method

        @Override
        public void layoutContainer(Container parent) {
            Insets insets = parent.getInsets();
            int maxWidth = parent.getWidth() - insets.left - insets.right;
            int maxHeight = parent.getHeight() - insets.top - insets.bottom;

            // set the minimum and preferred dimensions
            if (!dimensionsSet) {
                setMinimumSize(parent);
            }

            // determine the size of components
            int componentWidth = minComponentWidth + (int)Math.max(0, (maxWidth - minWidth) / 2.5);
            int componentHeight = minComponentHeight + (int)Math.max(0, (maxHeight - minHeight) / 2.5);

            // set the bounds for each component
            for (int i = 0; i < parent.getComponentCount() && i < 3; i++) {
                parent.getComponent(i).setBounds((componentWidth + GAP) * i, 0,
                        componentWidth / (i == 2 ? 2 : 1), componentHeight );
            }

        } // method layoutContainer

    } // class SettingLayout

} // class CommandPanel
