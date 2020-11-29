package web.utils;

import jndc.utils.AESUtils;
import web.model.data_object.ManagementLoginUser;
import java.util.Base64;

/**
 * simple password check,need to be replace with more Safer framework
 */
public class AuthUtils {


    public static String name="";
    public static String passWord="";

    /**
     *
     * @param managementLoginUser
     * @return
     */
    public static boolean doLogin(ManagementLoginUser managementLoginUser){
        if (name.equals(managementLoginUser.getName())&&passWord.equals(managementLoginUser.getPassWord())){
            return true;
        }
        return false;
    }


    /**
     * encode  step with AESUtils
     * @param uniqueBytes
     * @return
     */
    public static String webAuthTokenEncode(byte[] uniqueBytes){
        byte[] encode = AESUtils.encode(uniqueBytes);
        Base64.Encoder encoder = Base64.getEncoder();
        String s1 = encoder.encodeToString(encode);
        return s1;
    }

    public static byte[] webAuthTokenDecode(String token){
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(token);
        byte[] decode1 = AESUtils.decode(decode);
        return decode1;
    }
}
