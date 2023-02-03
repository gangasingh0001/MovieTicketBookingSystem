package Shared.data;

import java.io.IOException;
import java.net.*;

public class Udp implements IUdp{
    public String sendUDPMessage(int serverPort, String methodToInvoke, String customerID, String movieName, String movieID, int noOfTickets) {
        System.out.println("Calling UDP server: "+serverPort);
        //System.out.println(" Customer ID "+ customerID + " UDP calling " + methodToInvoke + methodToInvoke + " movieID: " + movieID + " movieName: " + movieName);
        DatagramSocket aSocket = null;
        String result = "";
        String dataFromClient = methodToInvoke + ";" + customerID + ";" + movieName + ";" + movieID + ";" + noOfTickets + ";"+false;
        try {
            aSocket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            result = new String(reply.getData());
            String[] parts = result.split(";");
            result = parts[0];
            System.out.println("Response from UDP request: " + result);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return result;
    }
}
