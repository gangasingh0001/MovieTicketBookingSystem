package Client;

import Constant.ControllerConstant;
import Constant.ServerConstant;
import Interface.MovieTicketInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry(2501);

            // Looking up the registry for the remote object
            MovieTicketInterface MovieTicketControllerObj = (MovieTicketInterface) registry.lookup(ControllerConstant.MovieTicketController);
            String response = MovieTicketControllerObj.addMovieSlots("hello","Ganga",3);
            // Calling the remote method using the obtained object
            //stub.printMsg();

            System.out.println("Response "+ response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static int getServerPortNumber(String serverPrefix) {
        if(serverPrefix.equalsIgnoreCase(ServerConstant.SERVER_ATWATER_PREFIX)) {
            return ServerConstant.SERVER_ATWATER_PORT;
        } else if (serverPrefix.equalsIgnoreCase(ServerConstant.SERVER_VERDUN_PREFIX)) {
            return ServerConstant.SERVER_VERDUN_PORT;
        } else if (serverPrefix.equalsIgnoreCase(ServerConstant.SERVER_OUTREMONT_PREFIX)) {
            return ServerConstant.SERVER_OUTREMONT_PORT;
        }
        return -1;
    }
}
