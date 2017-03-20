package com.discordbot.gui;

import javafx.fxml.Initializable;

public interface FXMLController extends Initializable {

    void stop();
    void setResizeListener(ResizeListener listener);

    interface ResizeListener {
        void onResize();
    } // interface ResizeListener

} // interface FXMLController
