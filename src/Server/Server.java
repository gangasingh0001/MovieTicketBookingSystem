package Server;

import Constant.ServerConstant;
import Constant.ServiceConstant;
import MovieTicketApp.IMovieTicket;
import MovieTicketApp.IMovieTicketHelper;
import Server.Service.MovieTicket;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.data.IMovie;
import Shared.data.IServerInfo;
import Shared.data.IUdp;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

public class Server extends Thread{
    private MovieTicket movieTicketServant = null;
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
        System.out.println("Server ID " + serverID);
        this.serverID = serverID;
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        this.customerBookingDb = customerBookingDb;
        this.moviesDb = moviesDb;
        this.logger = logger;
        this.args = args;
        getServerInfo();
        // create servant
        movieTicketServant = new MovieTicket(logger,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
        this.startListenerAndORBCore();
    }

    private void createORB(String[] args, String serverName, MovieTicket movieTicketServant) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(movieTicketServant);
            IMovieTicket href = IMovieTicketHelper.narrow(ref);

            org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name(serverName.substring(0,3));
            logger.severe("Path "+ path);
            ncRef.rebind(path, href);

            logger.severe(" orb running at server "+ serverName);
            // wait for invocations from clients
            while (true) {
                orb.run();
            }
        } catch (WrongPolicy ex) {
            ex.getStackTrace();
        } catch (ServantNotActive ex) {
            ex.getStackTrace();
        } catch (InvalidName ex) {
            ex.getStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName ex) {
            ex.getStackTrace();
        } catch (CannotProceed ex) {
            ex.getStackTrace();
        } catch (NotFound ex) {
            ex.getStackTrace();
        } catch (AdapterInactive ex) {
            ex.getStackTrace();
        }
    }

    private void startListenerAndORBCore() {
        Runnable listenerTask = () -> {
            requestlistener(movieTicketServant, serverPort, serverName);
        };
        Runnable orbTask = () -> {
            createORB(args, serverName, movieTicketServant);
        };
        Thread listenerThread = new Thread(listenerTask);
        Thread ORBThread = new Thread(orbTask);
        listenerThread.setPriority(1);
        listenerThread.start();
        ORBThread.setPriority(2);
        ORBThread.start();
        listenerThread.currentThread().setName(serverName);
        ORBThread.currentThread().setName(serverName);

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

    private void requestlistener(MovieTicket movieTicketServant, int serverPort, String serverName) {
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
                response = methodInvocation(movieTicketServant, requestParamsArray, response);
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

     private String methodInvocation(MovieTicket movieTicketServant, String[] requestParamsArray, String response) {
        String invokedMethod = requestParamsArray[0];
        String customerID = requestParamsArray[1];
        String movieName = requestParamsArray[2];
        String movieID = requestParamsArray[3];

        boolean isRegisteredToServer = Boolean.parseBoolean(requestParamsArray[4]);
        int numberOfTickets = Integer.parseInt(requestParamsArray[4]);
        if (invokedMethod.equalsIgnoreCase(ServiceConstant.getMoviesListInTheatre)) {
            response = movieTicketServant.getMoviesListInTheatre(movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.bookTicket)) {
            response = movieTicketServant.bookTicket(customerID,movieID,movieName,numberOfTickets,isRegisteredToServer);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getCustomerBookingList)) {
            response = movieTicketServant.getCustomerBookingList(customerID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.cancelTicket)) {
            response = movieTicketServant.cancelTicket(customerID,movieID,movieName,numberOfTickets);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.findNextAvailableSlot)) {
            response = movieTicketServant.findNextAvailableSlot(customerID,movieID,movieName);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.getNoOfBookingsInWeek)) {
            response = movieTicketServant.getNoOfBookingsInWeek(customerID,movieID);
        } else if (invokedMethod.equalsIgnoreCase(ServiceConstant.checkSlotAndBook)) {
            response = movieTicketServant.checkSlotAndBook(customerID,movieID,movieName,numberOfTickets);
        }
        return response;
    }
}
