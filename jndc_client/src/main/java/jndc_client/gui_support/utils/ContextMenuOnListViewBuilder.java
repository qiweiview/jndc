package jndc_client.gui_support.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import jndc.utils.LogPrint;
import jndc_client.core.ClientServiceDescription;

import java.io.ByteArrayInputStream;
import java.util.stream.Stream;

public class ContextMenuOnListViewBuilder {


    public static Callback multipleButton(InnerButtonDescription... innerButtonDescription) {
        Callback<ListView<MenuItemPackaging>, ListCell<MenuItemPackaging>> value = param -> {
            ListCell<MenuItemPackaging> cell = new MenuItemPackagingCell();


            ContextMenu contextMenu = new ContextMenu();
            ObservableList<MenuItem> items = contextMenu.getItems();

            Stream.of(innerButtonDescription).forEach(z -> {
                MenuItem editItem = new MenuItem();
                editItem.textProperty().bind(Bindings.format(z.getButtonName()));
                editItem.setOnAction(event -> {
                    MenuItemPackaging item = cell.getItem();

                    z.getInnerStringCallBack().run(item);
                });
                items.add(editItem);
            });


            ObjectProperty<MenuItemPackaging> listViewRowObjectProperty = cell.itemProperty();


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
        public void run(MenuItemPackaging menuItemPackaging);
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


    /**
     *
     */
    public static class MenuItemPackagingCell extends ListCell<MenuItemPackaging>{
        @Override
        protected void updateItem(MenuItemPackaging item, boolean empty) {
            if (empty){
                item=new MenuItemPackaging("",null);
                setGraphic(null);
            }else {
                ClientServiceDescription clientServiceDescription = (ClientServiceDescription) item.getValue();
                ImageView imageView = new ImageView();
                imageView.setImage(new Image(new ByteArrayInputStream(clientServiceDescription.isServiceEnable()?StaticFileCache.PIC_YES:StaticFileCache.PIC_NO)));
                setGraphic(imageView);
            }
            super.updateItem(item, empty);

        }
    }
}
