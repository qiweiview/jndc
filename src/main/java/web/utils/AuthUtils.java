package web.utils;

import web.model.data_object.ManagementLoginUser;

/**
 * simple password check,need to be replace with more Safer framework
 */
public class AuthUtils {

    /**
     *
     * @param managementLoginUser
     * @return
     */
    public static boolean doLogin(ManagementLoginUser managementLoginUser){
        if ("test".equals(managementLoginUser.getName())&&"test".equals(managementLoginUser.getPassWord())){
            return true;
        }
        return false;
    }
}
