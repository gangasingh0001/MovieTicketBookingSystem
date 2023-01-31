package Server;

import Constant.ServiceConstant;
import Constant.ServerConstant;
import Server.Service.MovieTicket;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.data.IMovie;
import Shared.data.IServerInfo;
import Shared.data.IUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;

public class Server extends Thread{
    private MovieTicket movieTicketService = null;
    private static String serverID;
    private static String serverName;
    //private static int serverRegistryPort;
    private static int serverPort;
    private IServerInfo serverInfo;
    private IUdp udpService;
    private IMovie movieService;
    private ICustomerBooking customerBookingDb;
    private IMovies moviesDb;

    public Server (String serverID,
                   IServerInfo serverInfo,
                   IUdp udpService,
                   IMovie movieService,
                   ICustomerBooking customerBookingDb,
                   IMovies moviesDb) throws Exception{
        System.out.println("Server ID " + serverID);
        this.serverID = serverID;
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        this.customerBookingDb = customerBookingDb;
        this.moviesDb = moviesDb;
    }

    public static void main(String args[]) {}

    public void runServer() throws RemoteException {
        movieTicketService = new MovieTicket(serverInfo,udpService,movieService,customerBookingDb,moviesDb);
        startRegistry();
        startThread();
    }

    private void startRegistry() {
        try {
            System.out.println("Creating registry with port: " + serverPort);
            Registry registry = LocateRegistry.createRegistry(serverPort);

            System.out.println("Rebinding registry to movieTicketService at port: " + serverPort);
            registry.rebind(ServiceConstant.MovieTicketService,movieTicketService);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void startThread() {
        System.out.println("launching runnable");
        Runnable task = () -> {
            System.out.println("Initiating listener for client requests");
            requestlistener();
        };

        System.out.println("Initialising thread");
        Thread thread = new Thread(task);

        thread.start();
        Thread.currentThread().setName(serverName);
        System.out.println("Running thread name: "+ Thread.currentThread().getName());
        System.out.println("State of thread: " + Thread.currentThread().getState());
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

    private void requestlistener() {
        DatagramSocket socket = null;
        String response = "";
        try {
            socket = new DatagramSocket(serverPort);
            byte[] buffer = new byte[1000];
            while (true) {
                System.out.println("Request from Client");
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String requestParams = new String(request.getData(), 0,request.getLength());
                String[] requestParamsArray = requestParams.split(";");
                response = methodInvocation(requestParamsArray,response);
                byte[] sendData = response.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, response.length(), request.getAddress(),
                        request.getPort());
                socket.send(reply);
            }
        } catch (SocketException socketException) {
            System.out.println("SocketException: " + socketException.getMessage());
        } catch (IOException | ParseException ioException) {
            System.out.println("IOException: " + ioException.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private String methodInvocation(String[] requestParamsArray, String response) throws RemoteException, ParseException {
        String invokedMethod = requestParamsArray[0];
        String customerID = requestParamsArray[1];
        String movieName = requestParamsArray[2];
        String movieID = requestParamsArray[3];
        int numberOfTickets = Integer.parseInt(requestParamsArray[4]);
        if (invokedMethod.equalsIgnoreCase(ServiceConstant.getMoviesListInTheatre)) {
            response = movieTicketService.getMoviesListInTheatre(movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.bookTicket)) {
            response = movieTicketService.bookTicket(customerID,movieID,movieName,numberOfTickets);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getCustomerBookingList)) {
            response = movieTicketService.getCustomerBookingList(customerID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.cancelTicket)) {
            response = movieTicketService.cancelTicket(customerID,movieID,movieName,numberOfTickets);
        }
        return response;
    }
}
