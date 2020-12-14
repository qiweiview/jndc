package jndc_client.gui_support.utils;

public class RowPackaging<T> {
    private T t;

    public RowPackaging(T t) {
        this.t = t;
    }


    public <Z> Z getValue(Class<Z> tClass) {
        return (Z) t;
    }

    @Override
    public String toString() {
        return t == null ? "" : t.toString();
    }
}
