package jndc.utils;



public class HexUtils {
    private static final String zero_0 = "";

    private static final String zero_1 = "0";

    private static final String zero_2 = "00";

    private static final String zero_3 = "000";

    private static final String zero_4 = "0000";

    private static final String zero_5 = "00000";

    private static final String zero_6 = "000000";

    private static final String zero_7 = "0000000";

    private static final String zero_8 = "00000000";


    public static int hex2Integer(byte[] bytes) {
        return hex2Integer(new String(bytes));
    }

    public static int hex2Integer(String s) {
        return Integer.parseInt(s, 16);
    }



    /**
     * slow but right
     *
     * @param s
     * @param limit
     * @return
     */
    @Deprecated
    public static byte[] fillBlank2(int s, Integer limit) {
        if (limit > 8 || s > Integer.MAX_VALUE) {
            throw new RuntimeException("unSupportNumber");
        }
        byte[] fillBytes = new byte[limit];

        String hex = Integer.toHexString(s);
        byte[] bytes = hex.getBytes();
        int length = limit - bytes.length;
        for (int i = 0; i < fillBytes.length; i++) {
            if (i < length) {
                fillBytes[i] = 48 & 0xff;//填0
            } else {
                fillBytes[i] = bytes[i - length];
            }
        }
        return fillBytes;
    }


    /**
     * some problem
     * @param s
     * @param limit
     * @return
     */
    public static String fillBlank(int s, int limit) {
        if (limit > 8 || s > Integer.MAX_VALUE) {
            throw new RuntimeException("unSupportNumber");
        }
        String s1 = Integer.toHexString(s);
        int i = limit - s1.length();
        switch (i) {
            case 0:
                return zero_0 + s1;
            case 1:
                return zero_1 + s1;
            case 2:
                return zero_2 + s1;
            case 3:
                return zero_3 + s1;
            case 4:
                return zero_4 + s1;
            case 5:
                return zero_5 + s1;
            case 6:
                return zero_6 + s1;
            case 7:
                return zero_7 + s1;
            case 8:
                return zero_8 + s1;
            default:
                return zero_8;


        }


    }
}
