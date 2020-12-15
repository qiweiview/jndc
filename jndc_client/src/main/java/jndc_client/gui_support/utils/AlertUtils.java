package jndc_client.gui_support.utils;

import javafx.scene.control.Alert;

public class AlertUtils {

    public static void error(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.show();
    }

    public static void info(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.show();
    }
}
