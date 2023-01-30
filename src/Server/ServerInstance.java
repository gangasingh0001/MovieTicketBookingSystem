package Server;

import Constant.ServerConstant;
import Shared.Database.CustomerBooking;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.Database.Movies;
import Shared.data.*;

public class ServerInstance {
    public ServerInstance() {}
    public static void main(String[] args) {
        IServerInfo serverInfo;
        IUdp udpService;
        IMovie movieService;
        ICustomerBooking customerBookingDb;
        IMovies moviesDb;
        try {
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            customerBookingDb = new CustomerBooking();
            moviesDb = new Movies();
            moviesDb.addMovie("AVATAR","ATWE190822",50);
            customerBookingDb.addMovieByCustomerID("ATWA1212","ATWE190822","AVATAR",23);
            serverInfo.setServerName(ServerConstant.SERVER_ATWATER_PREFIX);
            Server Atwater = new Server(ServerConstant.SERVER_ATWATER_PREFIX,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
            Atwater.getServerInfo();
            Atwater.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            customerBookingDb = new CustomerBooking();
            moviesDb = new Movies();
            moviesDb.addMovie("AVATAR","VERM190822",30);
            moviesDb.addMovie("AVENGERS","VERM190822",40);
            serverInfo.setServerName(ServerConstant.SERVER_VERDUN_PREFIX);
            Server Verdun = new Server(ServerConstant.SERVER_VERDUN_PREFIX,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
            Verdun.getServerInfo();
            Verdun.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            customerBookingDb = new CustomerBooking();
            moviesDb = new Movies();
            moviesDb.addMovie("TITANIC","OUTM190822",30);
            serverInfo.setServerName(ServerConstant.SERVER_OUTREMONT_PREFIX);
            Server Outremont = new Server(ServerConstant.SERVER_OUTREMONT_PREFIX,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
            Outremont.getServerInfo();
            Outremont.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
