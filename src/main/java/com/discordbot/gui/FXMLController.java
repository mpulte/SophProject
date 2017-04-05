package com.discordbot.gui;

import javafx.fxml.Initializable;

/**
 * Controller initialization, resizing, and stopping interface.
 *
 * @see Initializable
 */
public interface FXMLController extends Initializable {

    /**
     * Called to stop the controller. This should free up any memory in use and save any unsaved data.
     */
    void stop();

    /**
     * Mutator for the {@link ResizeListener ResizeListener}. This should be implemented on on any GUI that has a
     * dynamic size.
     *
     * @param listener the {@link ResizeListener ResizeListener} to set.
     */
    void setResizeListener(ResizeListener listener);

    /**
     * A listener used to handle changes in the corresponding GUI changes in preferred size.
     */
    interface ResizeListener {
        /**
         * Called whenever the corresponding GUI changes in preferred size.
         */
        void onResize();
    } // interface ResizeListener

} // interface FXMLController
