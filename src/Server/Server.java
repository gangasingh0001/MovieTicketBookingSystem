package Server;

import Constant.ServerConstant;
import Constant.ServiceConstant;
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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.util.logging.Logger;

public class Server extends Thread{
    private MovieTicket movieTicketService = null;
    private static String serverID;
    private static String serverName;
    //private static int serverRegistryPort;
    private static int serverPort;
    private final IServerInfo serverInfo;
    private final IUdp udpService;
    private final IMovie movieService;
    private final ICustomerBooking customerBookingDb;
    private final IMovies moviesDb;
    private final Logger logger;

    public Server (Logger logger,
                    String serverID,
                   IServerInfo serverInfo,
                   IUdp udpService,
                   IMovie movieService,
                   ICustomerBooking customerBookingDb,
                   IMovies moviesDb) throws Exception{
        System.out.println("Server ID " + serverID);
        Server.serverID = serverID;
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        this.customerBookingDb = customerBookingDb;
        this.moviesDb = moviesDb;
        this.logger = logger;
    }

    public static void main(String[] args) {}

    public void runServer() throws RemoteException {
        movieTicketService = new MovieTicket(logger,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
        startRegistry();
        startThread();
    }

    private void startRegistry() {
        try {
            logger.severe("Creating registry with port: " + serverPort);
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.rebind(ServiceConstant.MovieTicketService,movieTicketService);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void startThread() {
        Runnable task = this::requestlistener;

        Thread thread = new Thread(task);
        thread.start();
        Thread.currentThread().setName(serverName);

        logger.severe("Thread name: "+ Thread.currentThread().getName());
        logger.severe("State of thread: " + Thread.currentThread().getState());
    }

    public void getServerInfo() {
        switch (serverID) {
            case ServerConstant.SERVER_ATWATER_PREFIX -> {
                serverName = ServerConstant.SERVER_ATWATER;
                serverPort = ServerConstant.SERVER_ATWATER_PORT;
            }
            case ServerConstant.SERVER_VERDUN_PREFIX -> {
                serverName = ServerConstant.SERVER_VERDUN;
                serverPort = ServerConstant.SERVER_VERDUN_PORT;
            }
            case ServerConstant.SERVER_OUTREMONT_PREFIX -> {
                serverName = ServerConstant.SERVER_OUTREMONT;
                serverPort = ServerConstant.SERVER_OUTREMONT_PORT;
            }
            default -> {
            }
            // TODO: Implement Exception Handling if serverID is null.
        }
    }

    private void requestlistener() {
        String response = "";
        try (DatagramSocket socket = new DatagramSocket(serverPort)) {
            byte[] buffer = new byte[1000];
            while (true) {
                System.out.println("Request from Client");
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String requestParams = new String(request.getData(), 0, request.getLength());
                String[] requestParamsArray = requestParams.split(";");
                response = methodInvocation(requestParamsArray, response);
                byte[] sendData = response.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, response.length(), request.getAddress(),
                        request.getPort());
                socket.send(reply);
            }
        } catch (SocketException socketException) {
            System.out.println("SocketException: " + socketException.getMessage());
        } catch (IOException | ParseException ioException) {
            System.out.println("IOException: " + ioException.getMessage());
        }
    }

    private String methodInvocation(String[] requestParamsArray, String response) throws RemoteException, ParseException {
        String invokedMethod = requestParamsArray[0];
        String customerID = requestParamsArray[1];
        String movieName = requestParamsArray[2];
        String movieID = requestParamsArray[3];

        boolean isRegisteredToServer = Boolean.parseBoolean(requestParamsArray[4]);
        int numberOfTickets = Integer.parseInt(requestParamsArray[4]);
        if (invokedMethod.equalsIgnoreCase(ServiceConstant.getMoviesListInTheatre)) {
            response = movieTicketService.getMoviesListInTheatre(movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.bookTicket)) {
            response = movieTicketService.bookTicket(customerID,movieID,movieName,numberOfTickets,isRegisteredToServer);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getCustomerBookingList)) {
            response = movieTicketService.getCustomerBookingList(customerID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.cancelTicket)) {
            response = movieTicketService.cancelTicket(customerID,movieID,movieName,numberOfTickets);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.findNextAvailableSlot)) {
            response = movieTicketService.findNextAvailableSlot(customerID,movieID,movieName);
        }   else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getNoOfBookingsInWeek)) {
            response = movieTicketService.getNoOfBookingsInWeek(customerID,movieID);
        }
        return response;
    }
}
