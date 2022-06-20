package jndc_client.gui_support.utils;

/**
 * 菜单项
 * @param <T>
 */
public class MenuItemPackaging<T> {
    private T t;
    private String tag;


    public MenuItemPackaging(String tag,T t ) {
        this.t = t;
        this.tag = tag;
    }

    public  T  getValue() {
        return t;
    }


    @Override
    public String toString() {
        return tag == null ? "" : tag;
    }
}
