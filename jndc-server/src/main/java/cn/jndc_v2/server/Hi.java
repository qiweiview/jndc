package cn.jndc_v2.server;


public class Hi {
    public static void main(String[] args) throws InterruptedException {
        String message = "欢迎来到 www.didispace.com ❤️，要不要来杯 ☕️ ？";
        if (message.codePoints().anyMatch(Character::isEmoji)) {
            System.out.println("Message包含表情");
        }
    }
}
