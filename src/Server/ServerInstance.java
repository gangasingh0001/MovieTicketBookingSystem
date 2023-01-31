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
            moviesDb.addMovieSlot("AVATAR","ATWA190822",40);
            moviesDb.addMovieSlot("AVATAR","ATWM190822",30);
            moviesDb.addMovieSlot("AVATAR","ATWA200822",20);
            moviesDb.addMovieSlot("AVATAR","ATWM210822",10);
            moviesDb.addMovieSlot("AVATAR","ATWE210822",5);
            moviesDb.addMovie("AVENGERS","ATWE190822",100);
            customerBookingDb.addMovieByCustomerID("ATWA1212","ATWA190822","AVATAR",4);
            customerBookingDb.addMovieByCustomerID("ATWA1212","ATWM190822","AVATAR",3);
            customerBookingDb.addMovieByCustomerID("ATWA1212","ATWE190822","AVATAR",6);
            customerBookingDb.addMovieByCustomerID("ATWA1212","ATWE210822","AVATAR",2);
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
