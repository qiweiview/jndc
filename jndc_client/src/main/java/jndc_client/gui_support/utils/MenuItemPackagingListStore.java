package jndc_client.gui_support.utils;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import jndc.utils.LogPrint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuItemPackagingListStore {
    private static ObservableList<MenuItemPackaging> items;

    public static void register(ObservableList<MenuItemPackaging> menuItemPackagings) {
        if (items != null) {
            LogPrint.err("the list is exist,and will be covered");
        }
        MenuItemPackagingListStore.items = menuItemPackagings;

    }


    public static void deleteItem(MenuItemPackaging menuItemPackaging) {
        items.remove(menuItemPackaging);
    }

    public static void addItem(MenuItemPackaging menuItemPackaging) {
        items.add(menuItemPackaging);
    }

    public static void addItems(List<MenuItemPackaging> list) {
        if (items == null) {
            LogPrint.err("please register list before add");
            return;
        }
        items.addAll(list);
    }

    public static void reloadItem() {
        if (items == null) {
            LogPrint.err("please register list before add");
            return;
        }
        List<MenuItemPackaging> newList=new ArrayList<>();
        items.forEach(x->{
            newList.add(x);
        });
        items.clear();
        items.addAll(newList);
    }
}
