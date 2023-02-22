package Server;

import Constant.ServerConstant;
import Constant.ServiceConstant;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.data.IMovie;
import Shared.data.IServerInfo;
import Shared.data.IUdp;
import Shared.data.Util;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

public class Server extends Thread{
    private MovieTicket movieTicketObj = null;
    private String serverID;
    private static String serverName;
    //private static int serverRegistryPort;
    private static int serverPort;
    private final IServerInfo serverInfo;
    private final IUdp udpService;
    private final IMovie movieService;
    private final ICustomerBooking customerBookingDb;
    private final IMovies moviesDb;
    private final Logger logger;
    private String[] args;
    public Server (Logger logger,
                    String serverID,
                   IServerInfo serverInfo,
                   IUdp udpService,
                   IMovie movieService,
                   ICustomerBooking customerBookingDb,
                   IMovies moviesDb,
                   String[] args) throws Exception{
        this.serverID = serverID;
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        this.customerBookingDb = customerBookingDb;
        this.moviesDb = moviesDb;
        this.logger = logger;
        this.args = args;
        getServerInfo();
        movieTicketObj = new MovieTicket(logger,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
        this.startListenerAndRegisterEndPoint();
    }

    private void startListenerAndRegisterEndPoint() {
        Runnable listenerTask = () -> {
            requestlistener(movieTicketObj, serverPort, serverName);
        };

        Endpoint endpoint = Endpoint.publish("http://localhost:8080/"+ serverName,movieTicketObj);
        logger.severe("Endpoint: "+ endpoint.toString());

        Thread listenerThread = new Thread(listenerTask);
        listenerThread.start();
        listenerThread.currentThread().setName(serverName);

        logger.severe("Thread name: "+ listenerThread.currentThread().getName());
        logger.severe("State of thread: " + listenerThread.currentThread().getState());
    }

    public void getServerInfo() {
        switch (serverID) {
            case ServerConstant.SERVER_ATWATER_PREFIX : {
                serverName = ServerConstant.SERVER_ATWATER;
                serverPort = ServerConstant.SERVER_ATWATER_PORT;
                break;
            }
            case ServerConstant.SERVER_VERDUN_PREFIX : {
                serverName = ServerConstant.SERVER_VERDUN;
                serverPort = ServerConstant.SERVER_VERDUN_PORT;
                break;
            }
            case ServerConstant.SERVER_OUTREMONT_PREFIX : {
                serverName = ServerConstant.SERVER_OUTREMONT;
                serverPort = ServerConstant.SERVER_OUTREMONT_PORT;
                break;
            }
            default : {
                break;
            }
            // TODO: Implement Exception Handling if serverID is null.
        }
    }

    private void requestlistener(MovieTicket movieTicketObj, int serverPort, String serverName) {
        String response = "";
        logger.severe("Listener Datagram port : "+serverPort);
        try (DatagramSocket socket = new DatagramSocket(serverPort)) {
            byte[] buffer = new byte[1000];
            while (true) {
                logger.severe("Request Listener initiated for server "+serverName);
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String requestParams = new String(request.getData(), 0, request.getLength());
                String[] requestParamsArray = requestParams.split(";");
                response = methodInvocation(movieTicketObj, requestParamsArray, response);
                byte[] sendData = response.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, response.length(), request.getAddress(),
                        request.getPort());
                socket.send(reply);
            }
        } catch (SocketException socketException) {
            System.out.println("Listener SocketException: " + socketException.getMessage());
        } catch (IOException ioException) {
            System.out.println("Listener IOException: " + ioException.getMessage());
        }
    }

     private String methodInvocation(MovieTicket movieTicketObj, String[] requestParamsArray, String response) {
        String invokedMethod = requestParamsArray[0];
        String customerID = requestParamsArray[1];
        String movieName = requestParamsArray[2];
        String movieID = requestParamsArray[3];

        boolean isRegisteredToServer = Boolean.parseBoolean(requestParamsArray[4]);
        int numberOfTickets = Integer.parseInt(requestParamsArray[4]);
        if (invokedMethod.equalsIgnoreCase(ServiceConstant.getMoviesListInTheatre)) {
            response = movieTicketObj.getMoviesListInTheatre(movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.bookTicket)) {
            response = movieTicketObj.bookTicket(customerID,movieID,movieName,numberOfTickets,isRegisteredToServer);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getCustomerBookingList)) {
            response = movieTicketObj.getCustomerBookingList(customerID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.cancelTicket)) {
            response = movieTicketObj.cancelTicket(customerID,movieID,movieName,numberOfTickets);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.findNextAvailableSlot)) {
            response = movieTicketObj.findNextAvailableSlot(customerID,movieID,movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getNoOfBookingsInWeek)) {
            response = movieTicketObj.getNoOfBookingsInWeek(customerID,movieID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.checkSlotAndBook)) {
            response = movieTicketObj.checkSlotAndBook(customerID,movieID,movieName,numberOfTickets);
        }
        return response;
    }
}
