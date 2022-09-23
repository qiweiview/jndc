package jndc_server.exmaple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jndc_server.databases_object.IpFilterRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class JacksonTest {


    @Test
    public void test() throws JsonProcessingException {
        String json = "[{\"timeStamp\":1663893659336,\"ip\":\"59.1.115.162\",\"vCount\":1},{\"timeStamp\":1663889313973,\"ip\":\"42.157.194.16\",\"vCount\":242},{\"timeStamp\":1663888541502,\"ip\":\"117.25.149.164\",\"vCount\":394},{\"timeStamp\":1663888186590,\"ip\":\"89.248.165.82\",\"vCount\":15},{\"timeStamp\":1663887301296,\"ip\":\"45.61.186.4\",\"vCount\":53},{\"timeStamp\":1663886551099,\"ip\":\"60.217.75.70\",\"vCount\":15},{\"timeStamp\":1663884702840,\"ip\":\"45.61.185.149\",\"vCount\":324},{\"timeStamp\":1663880921666,\"ip\":\"8.219.71.118\",\"vCount\":588},{\"timeStamp\":1663880666460,\"ip\":\"87.236.176.30\",\"vCount\":1},{\"timeStamp\":1663880498397,\"ip\":\"213.226.123.219\",\"vCount\":12}]";

        ObjectMapper objectMapper = new ObjectMapper();
        IpFilterRecord[] ipFilterRecords1 = objectMapper.readValue(json, IpFilterRecord[].class);


    }
}
