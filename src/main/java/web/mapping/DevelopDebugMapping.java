package web.mapping;


import web.core.FrontProjectLoader;
import web.core.JNDCHttpRequest;
import web.core.WebMapping;

import java.util.HashMap;

/**
 * singleton， thread unsafe
 */
public class DevelopDebugMapping {



    @WebMapping(path = "/reloadFront")
    public HashMap run(JNDCHttpRequest jndcHttpRequest){
        FrontProjectLoader.jndcStaticProject.reloadProject();
        HashMap objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("message","success");
        return objectObjectHashMap;

    }

}
