package jndc.core.data_store_support;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T>  {
    private List<T> data;

    private int total;


}
