package Server;

import Constant.ServerConstant;
import Constant.ServiceConstant;
import Server.Service.MovieTicket;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.data.IMovie;
import Shared.data.IServerInfo;
import Shared.data.IUdp;
import Shared.data.Util;

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
        Server.serverID = serverID;
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        this.customerBookingDb = customerBookingDb;
        this.moviesDb = moviesDb;
        this.logger = logger;
        getServerInfo();
        movieTicketService = new MovieTicket(logger,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
        startThread();
    }

    public static void main(String[] args) {}

    private void startRegistry(int serverPort, MovieTicket movieTicketService) {
        try {
            logger.severe("Creating registry at port: " + serverPort);
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.rebind(ServiceConstant.MovieTicketService,movieTicketService);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void startThread() {
        Runnable listenerTask = () -> {
            requestlistener(movieTicketService, serverPort, serverName);
        };
        Runnable rmiRegistryTask = () -> {
            startRegistry(serverPort, movieTicketService);
        };

        Thread listenerThread = new Thread(listenerTask);
        Thread rmiRegistryThread = new Thread(rmiRegistryTask);
        listenerThread.setPriority(1);
        rmiRegistryThread.setPriority(2);
        rmiRegistryThread.start();
        listenerThread.start();
        listenerThread.setName(serverName);
        rmiRegistryThread.setName(serverName);

        System.out.println("Server is up and Running: " + Util.getServerNameByServerPrefix(serverID));

        logger.severe("Listener thread name: "+ listenerThread.getName());
        logger.severe("Listener thread state: " + listenerThread.getState());

        logger.severe("RMI Registry thread name: "+ rmiRegistryThread.getName());
        logger.severe("RMI Registry thread state: " + rmiRegistryThread.getState());
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

    private void requestlistener(MovieTicket movieTicketService,int serverPort,String serverName) {
        String response = "";
        try (DatagramSocket socket = new DatagramSocket(serverPort)) {
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String requestParams = new String(request.getData(), 0, request.getLength());
                String[] requestParamsArray = requestParams.split(";");
                response = methodInvocation(movieTicketService, requestParamsArray, response);
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

    private String methodInvocation(MovieTicket movieTicketService, String[] requestParamsArray, String response) throws RemoteException, ParseException {
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
