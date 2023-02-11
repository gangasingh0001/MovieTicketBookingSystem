package Server;

import Constant.ServerConstant;
import Log.ILogging;
import Log.Logging;
import Shared.Database.CustomerBooking;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.Database.Movies;
import Shared.data.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerInstance {
    public ServerInstance() {}
    public static void main(String[] args) {
        IServerInfo serverInfo;
        IUdp udpService;
        IMovie movieService;
        ICustomerBooking customerBookingDb;
        IMovies moviesDb;
        ILogging logging;
        try {
            Logger logger = Logger.getLogger(Util.getServerNameByServerPrefix(ServerConstant.SERVER_ATWATER_PREFIX));
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            customerBookingDb = new CustomerBooking();
            moviesDb = new Movies();
            moviesDb.addMovie("AVATAR", "ATWM070223", 50);
            moviesDb.addMovieSlot("AVATAR", "ATWA070223", 40);
            moviesDb.addMovieSlot("AVATAR", "ATWE070223", 40);
            moviesDb.addMovieSlot("AVATAR", "ATWM080223", 10);
            moviesDb.addMovieSlot("AVATAR", "ATWA080223", 20);
            moviesDb.addMovieSlot("AVATAR", "ATWA140223", 50);
            moviesDb.addMovieSlot("AVATAR", "ATWA190223", 50);
            moviesDb.addMovie("AVENGERS", "ATWE190822", 100);
            customerBookingDb.addMovieByCustomerID("ATWM1212", "ATWA070223", "AVATAR", 3);
            customerBookingDb.addMovieByCustomerID("ATWM1212", "ATWA140223", "AVATAR", 3);
            customerBookingDb.addMovieByCustomerID("ATWM1234", "ATWM080223", "AVATAR", 4);
            serverInfo.setServerName(ServerConstant.SERVER_ATWATER_PREFIX);
            logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()),false,true);
            logger = logging.attachFileHandlerToLogger(logger);
            Server Atwater = new Server(logger,ServerConstant.SERVER_ATWATER_PREFIX,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
            Atwater.setPriority(1);
            Atwater.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Logger logger = Logger.getLogger(Util.getServerNameByServerPrefix(ServerConstant.SERVER_VERDUN_PREFIX));
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            customerBookingDb = new CustomerBooking();
            moviesDb = new Movies();
            moviesDb.addMovie("AVATAR", "VERM150223", 30);
            moviesDb.addMovieSlot("AVATAR", "VERA170223", 30);
            moviesDb.addMovieSlot("AVATAR", "VERA160223", 30);
            moviesDb.addMovieSlot("AVATAR", "VERE160223", 30);
            moviesDb.addMovie("AVENGERS", "VERM110223", 40);
            moviesDb.addMovieSlot("AVENGERS", "VERE160223", 30);
            moviesDb.addMovie("TITANIC", "VERM110223", 40);
            moviesDb.addMovieSlot("TITANIC", "VERE160223", 30);
            customerBookingDb.addMovieByCustomerID("VERM1212","VERA170223","AVATAR",23);
            customerBookingDb.addMovieByCustomerID("ATWM1234", "VERE160223", "AVENGERS", 12);
            customerBookingDb.addMovieByCustomerID("ATWM1212", "VERA160223", "AVENGERS", 12);
            customerBookingDb.addMovieByCustomerID("ATWM1212", "VERE160223", "AVENGERS", 12);
            customerBookingDb.addMovieByCustomerID("ATWM1212", "VERE160223", "AVATAR", 12);
            customerBookingDb.addMovieByCustomerID("ATWM1212", "VERE160223", "TITANIC", 12);
            //customerBookingDb.addMovieByCustomerID("VERM9876","VERM240822","AVATAR",25);
            serverInfo.setServerName(ServerConstant.SERVER_VERDUN_PREFIX);
            logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()),false,true);
            logger = logging.attachFileHandlerToLogger(logger);
            Server Verdun = new Server(logger,ServerConstant.SERVER_VERDUN_PREFIX,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
            Verdun.setPriority(2);
            Verdun.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Logger logger = Logger.getLogger(Util.getServerNameByServerPrefix(ServerConstant.SERVER_OUTREMONT_PREFIX));
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            customerBookingDb = new CustomerBooking();
            moviesDb = new Movies();
            moviesDb.addMovie("TITANIC","OUTM190223",30);
            moviesDb.addMovie("AVATAR","OUTE140223",30);
            moviesDb.addMovieSlot("AVATAR","OUTE190223",30);
            customerBookingDb.addMovieByCustomerID("ATWM1212","OUTM190223","TITANIC",4);
            serverInfo.setServerName(ServerConstant.SERVER_OUTREMONT_PREFIX);
            logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()),false,true);
            logger = logging.attachFileHandlerToLogger(logger);
            Server Outremont = new Server(logger,ServerConstant.SERVER_OUTREMONT_PREFIX,serverInfo,udpService,movieService,customerBookingDb,moviesDb);
            Outremont.setPriority(3);
            Outremont.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
