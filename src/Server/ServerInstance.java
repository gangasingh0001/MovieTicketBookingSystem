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
                moviesDb.addMovie("AVATAR", "ATWM070323", 50);
//                moviesDb.addMovieSlot("AVATAR", "ATWA070323", 40);
//                moviesDb.addMovieSlot("AVATAR", "ATWE070323", 40);
//                moviesDb.addMovieSlot("AVATAR", "ATWM080323", 10);
//                moviesDb.addMovieSlot("AVATAR", "ATWA080323", 20);
                moviesDb.addMovieSlot("AVATAR", "ATWA140323", 50);
                moviesDb.addMovieSlot("AVATAR", "ATWA110323", 50);
                moviesDb.addMovieSlot("AVATAR", "ATWA040323", 50);
                //moviesDb.addMovieSlot("AVATAR", "ATWA190323", 50);
                moviesDb.addMovie("AVENGERS", "ATWE190823", 100);
                customerBookingDb.addMovieByCustomerID("ATWM1212", "ATWM070323", "AVATAR", 3);
                customerBookingDb.addMovieByCustomerID("ATWM1212", "ATWA140323", "AVATAR", 3);
                //customerBookingDb.addMovieByCustomerID("ATWM1234", "ATWM080323", "AVATAR", 4);
                serverInfo.setServerName(ServerConstant.SERVER_ATWATER_PREFIX);
                logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()), false, true);
                logger = logging.attachFileHandlerToLogger(logger);
                Server Atwater = new Server(logger, ServerConstant.SERVER_ATWATER_PREFIX, serverInfo, udpService, movieService, customerBookingDb, moviesDb, args);
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
                moviesDb.addMovie("AVATAR", "VERM150323", 30);
                //moviesDb.addMovieSlot("AVATAR", "VERA170323", 30);
                //moviesDb.addMovieSlot("AVATAR", "VERA160323", 30);
                //moviesDb.addMovieSlot("AVATAR", "VERE160323", 30);
                moviesDb.addMovie("AVENGERS", "VERM110323", 40);
                //moviesDb.addMovieSlot("AVENGERS", "VERE160323", 30);
                moviesDb.addMovie("TITANIC", "VERM110323", 40);
                //moviesDb.addMovieSlot("TITANIC", "VERE160323", 30);
                customerBookingDb.addMovieByCustomerID("VERM1212","VERA170323","AVATAR",23);
//                customerBookingDb.addMovieByCustomerID("ATWM1234", "VERE160323", "AVENGERS", 12);
//                customerBookingDb.addMovieByCustomerID("ATWM1212", "VERA160323", "AVENGERS", 12);
//                customerBookingDb.addMovieByCustomerID("ATWM1212", "VERE160323", "AVENGERS", 12);
                //customerBookingDb.addMovieByCustomerID("ATWM1212", "VERE160323", "AVATAR", 12);
                //customerBookingDb.addMovieByCustomerID("ATWM1212", "VERE160323", "TITANIC", 12);
                serverInfo.setServerName(ServerConstant.SERVER_VERDUN_PREFIX);
                logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()), false, true);
                logger = logging.attachFileHandlerToLogger(logger);
                Server Verdun = new Server(logger, ServerConstant.SERVER_VERDUN_PREFIX, serverInfo, udpService, movieService, customerBookingDb, moviesDb, args);
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
                moviesDb.addMovie("TITANIC","OUTM190323",30);
                moviesDb.addMovie("AVATAR","OUTE140323",30);
                //moviesDb.addMovieSlot("AVATAR","OUTE190323",30);
                //customerBookingDb.addMovieByCustomerID("ATWM1212","OUTM190323","TITANIC",4);
                serverInfo.setServerName(ServerConstant.SERVER_OUTREMONT_PREFIX);
                logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()), false, true);
                logger = logging.attachFileHandlerToLogger(logger);
                Server Outremont = new Server(logger, ServerConstant.SERVER_OUTREMONT_PREFIX, serverInfo, udpService, movieService, customerBookingDb, moviesDb, args);
                Outremont.setPriority(3);
                Outremont.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
}
