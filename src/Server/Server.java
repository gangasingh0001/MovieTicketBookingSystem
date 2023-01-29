package Server;

import Constant.ServiceConstant;
import Constant.ServerConstant;
import Server.Service.MovieTicket;
import Shared.data.IMovie;
import Shared.data.IServerInfo;
import Shared.data.IUdp;
import Shared.data.IUser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server extends Thread{
    private MovieTicket movieTicketController = null;
    private static String serverID;
    private static String serverName;
    //private static int serverRegistryPort;
    private static int serverPort;
    private IServerInfo serverInfo;
    private IUdp udpService;
    private IMovie movieService;

    public Server (String serverID, IServerInfo serverInfo, IUdp udpService, IMovie movieService) throws Exception{
        System.out.println("Server ID " + serverID);
        this.serverID = serverID;
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
    }

    public static void main(String args[]) {}

    public void runServer() throws RemoteException {
        movieTicketController = new MovieTicket(serverInfo,udpService,movieService);
        startRegistry();
        startThread();
    }

    private void startRegistry() {
        try {
            System.out.println("Getting Server Port " + serverPort);
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.rebind(ServiceConstant.MovieTicketController,movieTicketController);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void startThread() {
        Runnable task = () -> {
            requestlistener();
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    public void getServerInfo() {
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
                System.out.println("Request");
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String requestParams = new String(request.getData(), 0,
                        request.getLength());
                String[] requestParamsArray = requestParams.split(";");
                response = methodInvocation(requestParamsArray,response);
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

    private String methodInvocation(String[] requestParamsArray, String response) {
        String invokedMethod = requestParamsArray[0];
        String customerID = requestParamsArray[1];
        String movieName = requestParamsArray[2];
        String movieID = requestParamsArray[3];
        int numberOfTickets = Integer.parseInt(requestParamsArray[4]);
        if (invokedMethod.equalsIgnoreCase(ServiceConstant.getMoviesListInTheatre)) {
            response = movieTicketController.getMoviesListInTheatre(movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.bookTicket)) {
            response = movieTicketController.bookTicket(customerID,movieID,movieName,numberOfTickets);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getCustomerBookingList)) {
            response = movieTicketController.getCustomerBookingList(customerID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.cancelTicket)) {
            response = movieTicketController.cancelTicket(customerID,movieID,movieName,numberOfTickets);
        }
        return response;
    }
}
