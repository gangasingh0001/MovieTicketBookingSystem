package Client;

import Constant.ClientConstant;
import Constant.ServerConstant;
import Log.ILogging;
import Log.Logging;
import MovieTicketApp.IMovieTicket;
import MovieTicketApp.IMovieTicketHelper;
import Shared.data.IMovie;
import Shared.data.IUser;
import Shared.data.Util;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

public class FrontEnd {
    ILogging logging;
    Logger logger;
    private IMovie movieService = null;
    private IUser userService = null;
    IMovieTicket movieTicketServantObj = null;
    int menuSelection = -1;
    String response = null;
    Scanner scanner = null;
    boolean logout = false;
    private String[] args;
    public FrontEnd(
            IUser userService,
            IMovie movieService,
            String[] args) {
        this.userService = userService;
        this.movieService = movieService;
        scanner = new Scanner(System.in);
        logger = Logger.getLogger(Client.class.getName());
        this.args = args;
    }

    public void attachLogging(String userID) {
        logging = new Logging(userID, true, false);
        logger = logging.attachFileHandlerToLogger(logger);
    }

    public void test() {
        Runnable client1 = () -> {
            System.out.println("Listing movie shows for ATWM5678");
            attachLogging("ATWM1234");
            this.userService.setUserID("ATWM1234");
            getServantRef(this.args);
            response = movieTicketServantObj.listMovieShowsAvailability("AVATAR");
            System.out.println(response);
            logger.severe(response);
            System.out.println("********** END Task 1 ***********");
        };
        Runnable client2 = () -> {
            System.out.println("Listing movie shows for ATWM5678");
            attachLogging("ATWM5678");
            this.userService.setUserID("ATWM5678");
            getServantRef(this.args);
            response = movieTicketServantObj.listMovieShowsAvailability("AVATAR");
            System.out.println(response);
            logger.severe(response);
            System.out.println("********** END Task 2 ***********");
        };
        Runnable client3 = () -> {
            attachLogging("ATWM1234");
            this.userService.setUserID("ATWM1234");
            getServantRef(this.args);
            String response = movieTicketServantObj.addMovieSlots("ATWE070223", "AVATAR", 10);
            System.out.println(response);
            System.out.println("********** END Task 3 ***********");
        };
        Runnable client4 = () -> {
            attachLogging("ATWM5678");
            this.userService.setUserID("ATWM5678");
            getServantRef(this.args);
            String response = movieTicketServantObj.addMovieSlots("ATWE070223", "AVATAR", 10);
            System.out.println(response);
            System.out.println("********** END Task 4 ***********");
        };
        Thread thread1 = new Thread(client1);
        Thread thread2 = new Thread(client2);
        Thread thread3 = new Thread(client3);
        Thread thread4 = new Thread(client4);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your UserID:");
        String userID = scanner.nextLine().trim().toUpperCase();

        while (!this.movieService.validateUserID(userID)) {
            System.out.println("Invalid User ID Please enter again:");
            userID = scanner.nextLine().trim().toUpperCase();
        }

        System.out.println("Login Successful " + userID);

        attachLogging(userID);

        this.userService.setUserID(userID.toUpperCase());

        getServantRef(this.args);

        logger.severe("CustomerID: "+ this.userService.getUserID());
        logger.severe("Server Name: "+ Util.getServerFullNameByCustomerID(this.userService.getUserID()));
        logger.severe("Server PORT: "+ Util.getServerPortByCustomerID(this.userService.getUserID()));

        while (!logout)
            menu();
    }

    public void getServantRef(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            movieTicketServantObj = IMovieTicketHelper.narrow(ncRef.resolve_str(this.userService.getUserRegisteredServerPrefix()));
        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (CannotProceed cannotProceed) {
            cannotProceed.printStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        };
    }

    public void menu() {
        //Admin specific operations
        if(this.userService.isAdmin()) {
            System.out.println("1. " + ServerConstant.ADD_MOVIE_SLOTS);
            System.out.println("2. " + ServerConstant.REMOVE_MOVIE_SLOTS);
            System.out.println("3. " + ServerConstant.LIST_MOVIE_SHOWS_AVAILABILITY);
        }

        if(!this.userService.isAdmin()) {
            System.out.println("1. "+ClientConstant.BOOK_MOVIE);
            System.out.println("2. "+ClientConstant.GET_BOOKING_SCHEDULE);
            System.out.println("3. "+ClientConstant.CANCEL_MOVIE_TICKET);
            System.out.println("4. "+ClientConstant.EXCHANGE_MOVIE_TICKET);
            System.out.println("5. "+ClientConstant.LOGOUT);
        } else {
            System.out.println("4. "+ClientConstant.BOOK_MOVIE);
            System.out.println("5. "+ClientConstant.GET_BOOKING_SCHEDULE);
            System.out.println("6. "+ClientConstant.CANCEL_MOVIE_TICKET);
            System.out.println("7. "+ClientConstant.EXCHANGE_MOVIE_TICKET);
            System.out.println("8. "+ClientConstant.LOGOUT);
        }

        menuSelection = getMenuInput();
        response = referToSelectedMenuObj();
        System.out.println(response);
    }

    private int getMenuInput() {
        return scanner.nextInt();
    }

    private int getMovieInput() {
        return scanner.nextInt();
    }

    private int getTheaterInput() {
        return scanner.nextInt();
    }

    private int getBookingCapacityInput() {
        return scanner.nextInt();
    }

    private String getMovieIDInput() {
        return scanner.nextLine();
    }

    private String referToSelectedMenuObj() {
        if(this.userService.isAdmin()) {
            switch (menuSelection) {
                case 1 : {
                    this.movieService.moviesPrompt("Select Movie (e.g 1 or 2 etc)");
                    int selectedMovie = getMovieInput();
                    this.movieService.bookingCapacityPrompt("Enter booking capacity");
                    int bookingCapacity = getBookingCapacityInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    LocalDate today = LocalDate.now();
                    LocalDate afterSevenDays = today.plusDays(7);
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    Date sevenDaysAfter = Date.from(afterSevenDays.atStartOfDay(defaultZoneId).toInstant());
                    if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())>0 && Util.getSlotDateByMovieID(movieID).compareTo(sevenDaysAfter)<0) {
                        if(this.movieService.validateMovieID(movieID)) {
                            String response = movieTicketServantObj.addMovieSlots(movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity);
                            logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, response));
                            return response;
                        }else {
                            logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Incorrect movie ID"));
                            break;
                        }
                    }else if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())<0){
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Cannot add slot for already finished movie shows"));
                        return "Cannot add slot for already finished movie shows";
                    } else {
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Cannot add slot for more than one week ahead"));
                        return "Cannot add slot for more than one week ahead";
                    }
                }
                case 2 : {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())>0){
                        res = movieTicketServantObj.removeMovieSlots(movieID,this.movieService.getMovieName(selectedMovie).toUpperCase());
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                        return res;
                    }
                    res = "Movie cannot be removed for the past show";
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 3 : {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    res = movieTicketServantObj.listMovieShowsAvailability(this.movieService.getMovieName(selectedMovie).toUpperCase());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), null, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 4 : {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter CustomerID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    while (!this.movieService.validateUserID(customerID)) {
                        System.out.println("Invalid User ID Please enter again:");
                        customerID = scanner.nextLine().trim().toUpperCase();
                    }
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    this.movieService.bookingCapacityPrompt("Enter no of tickets you want to book");
                    int bookingCapacity = getBookingCapacityInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServantObj.bookMovieTickets(customerID,movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity);
                    logger.severe(Util.createLogMsg(customerID, movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, res));
                    return res;
                }
                case 5 : {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter CustomerID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    while (!this.movieService.validateUserID(customerID)) {
                        System.out.println("Invalid User ID Please enter again:");
                        customerID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServantObj.getBookingSchedule(customerID);
                    logger.severe(Util.createLogMsg(customerID,null, null, -1, res));
                    return res;
                }
                case 6 : {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter CustomerID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    while (!this.movieService.validateUserID(customerID)) {
                        System.out.println("Invalid User ID Please enter again:");
                        customerID = scanner.nextLine().trim().toUpperCase();
                    }
                    this.movieService.moviesPrompt("Select movie to cancel booking");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServantObj.cancelMovieTickets(customerID,movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),0);
                    logger.severe(Util.createLogMsg(customerID,movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 7 : {
                    logout = true;
                    return null;
                }
            }
        } else {
            switch (menuSelection) {
                case 1 : {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    this.movieService.bookingCapacityPrompt("Enter no of tickets you want to book");
                    int bookingCapacity = getBookingCapacityInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServantObj.bookMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity);
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, res));
                    return res;
                }
                case 2 : {
                    String res;
                    res = movieTicketServantObj.getBookingSchedule(this.userService.getUserID());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), null, null, -1, res));
                    return res;
                }
                case 3 : {
                    String res;
                    this.movieService.moviesPrompt("Select movie to cancel booking");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    String movieID = getMovieIDInput();
                    while (!this.movieService.validateMovieID(movieID)) {
                        System.out.println("Invalid Movie ID Please enter again:");
                        movieID = scanner.nextLine().trim().toUpperCase();
                    }
                    res = movieTicketServantObj.cancelMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),0);
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 4 : {
                    logout = true;
                    return null;
                }
            }
        }

        return null;
    }
}
