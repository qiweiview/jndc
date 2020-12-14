package jndc_client.gui_support.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.util.Callback;

import java.util.stream.Stream;

public class ContextMenuBuilder {


    public static Callback multipleButton(InnerButtonDescription... innerButtonDescription) {
        Callback<ListView<RowPackaging>, ListCell<RowPackaging>> value = param -> {
            ListCell<RowPackaging> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            ObservableList<MenuItem> items = contextMenu.getItems();

            Stream.of(innerButtonDescription).forEach(z -> {
                MenuItem editItem = new MenuItem();
                editItem.textProperty().bind(Bindings.format(z.getButtonName()));
                editItem.setOnAction(event -> {
                    RowPackaging item = cell.getItem();

                    z.getInnerStringCallBack().run(item);
                });
                items.add(editItem);
            });
            ObjectProperty<RowPackaging> listViewRowObjectProperty = cell.itemProperty();
            cell.textProperty().bind(listViewRowObjectProperty.asString());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        };
        return value;

    }


    public static Callback singleButton(String message, InnerStringCallBack innerStringCallBack) {
        InnerButtonDescription innerButtonDescription = new InnerButtonDescription(message, innerStringCallBack);
        return multipleButton(innerButtonDescription);
    }

    public interface InnerStringCallBack {
        public void run(RowPackaging rowPackaging);
    }


    public static class InnerButtonDescription {
        private String buttonName;
        private InnerStringCallBack innerStringCallBack;

        public InnerButtonDescription(String buttonName, InnerStringCallBack innerStringCallBack) {
            this.buttonName = buttonName;
            this.innerStringCallBack = innerStringCallBack;
        }

        public String getButtonName() {
            return buttonName;
        }

        public InnerStringCallBack getInnerStringCallBack() {
            return innerStringCallBack;
        }
    }
}
