package Server;

import Constant.ControllerConstant;
import Constant.ServerConstant;
import Controller.MovieTicketController;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server extends Thread{
    private MovieTicketController movieTicketController = null;
    private static String serverID;
    private static String serverName;
    //private static int serverRegistryPort;
    private static int serverPort;

    public Server (String serverID) throws Exception{
        this.serverID = serverID;
    }

    public static void main(String args[]) {
        getServerInfo();
    }

    private void runServer() throws RemoteException, AlreadyBoundException {
        movieTicketController = new MovieTicketController();
        startRegistry();
        startThread();
    }

    private static void startRegistry() throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(serverPort);
            if(registry.list().length==0)  LocateRegistry.createRegistry(serverPort);
        } catch (RemoteException ex) {
            // TODO: Write Exception handling for startRegistry method.
        }
    }

    private void startThread() {
        Runnable task = () -> {
            requestlistener();
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private static void getServerInfo() {
        switch(serverID) {
            case ServerConstant.SERVER_ATWATER_PREFIX:
                serverName = ServerConstant.SERVER_ATWATER;
                serverPort = ServerConstant.SERVER_ATWATER_PORT;
                break;
            case ServerConstant.SERVER_VERDUN_PREFIX:
                serverName = ServerConstant.SERVER_VERDUN;
                serverPort = ServerConstant.SERVER_VERDUN_PORT;
                break;
            case ServerConstant.SERVER_OUTREMONT_PREFIX:
                serverName = ServerConstant.SERVER_OUTREMONT;
                serverPort = ServerConstant.SERVER_OUTREMONT_PORT;
                break;
            default:
                // TODO: Implement Exception Handling if serverID is null.
        }
    }

    private void requestlistener () {
        DatagramSocket socket = null;
        String response = "";
        try {
            socket = new DatagramSocket(serverPort);
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String requestParams = new String(request.getData(), 0,
                        request.getLength());
                String[] requestParamsArray = requestParams.split(";");
                String invokedMethod = requestParamsArray[0];
                response = methodInvocation(invokedMethod,response);
                byte[] sendData = response.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, response.length(), request.getAddress(),
                        request.getPort());
                socket.send(reply);
            }
        } catch (SocketException socketException) {
            System.out.println("SocketException: " + socketException.getMessage());
        } catch (IOException ioException) {
            System.out.println("IOException: " + ioException.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private String methodInvocation(@NotNull String invokedMethod, String response) {
        if (invokedMethod.equalsIgnoreCase(ControllerConstant.removeMovieSlots)) {
            response = movieTicketController.removeMovieSlots();
        } else if (invokedMethod.equalsIgnoreCase(ControllerConstant.bookMovieTickets)) {
            response = movieTicketController.bookMovieTickets();
        } else if (invokedMethod.equalsIgnoreCase(ControllerConstant.cancelMovieTickets)) {
            response = movieTicketController.cancelMovieTickets();
        } else if (invokedMethod.equalsIgnoreCase(ControllerConstant.addMovieSlots)) {
            response = movieTicketController.addMovieSlots();
        }
        return response;
    }
}
