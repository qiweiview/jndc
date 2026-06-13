package jndc_server.web_support.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChannelTrafficTrendVO {
    private String range;
    private String bucketUnit;
    private List<ChannelTrafficTrendPointVO> points = new ArrayList<>();
}
