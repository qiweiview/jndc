package jndc_client.gui_support.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * not thread safe
 */
public class DialogClose {

    public static volatile EventHandler<ActionEvent> tConsumer;

    public static void close() {
        if (tConsumer != null) {
            tConsumer.handle(null);
        }
    }
}
