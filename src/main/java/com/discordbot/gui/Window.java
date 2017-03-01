package com.discordbot.gui;

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;

public abstract class Window extends JFrame {

    public Window(WindowAdapter adapter) {
        addWindowListener(adapter);
    } // constructor

} // class TestWindow
