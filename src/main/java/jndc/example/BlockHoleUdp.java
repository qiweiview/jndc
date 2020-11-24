package jndc.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class BlockHoleUdp {
    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte[65507];
        DatagramSocket socket = new DatagramSocket(13);
        DatagramPacket datagramPacket = new DatagramPacket(bytes, 0, bytes.length);
        while (true) {
            socket.receive(datagramPacket);
            byte[] data = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());
            datagramPacket.setLength(65507);
            System.out.println(new String(data));
        }
    }
}
