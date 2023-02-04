package Client;

import Constant.ClientConstant;
import Constant.ServiceConstant;
import Constant.ServerConstant;
import Log.ILogging;
import Log.Logging;
import Server.Interface.IMovieTicket;
import Shared.data.IMovie;
import Shared.data.IUser;
import Shared.data.Util;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.time.ZoneId;
import java.util.Scanner;
import java.util.logging.Logger;

public class FrontEnd {
    ILogging logging;
    Logger logger;
    private IMovie movieService = null;
    private IUser userService = null;
    Registry registry = null;
    IMovieTicket movieTicketServiceObj = null;
    int menuSelection = -1;
    String response = null;
    Scanner scanner = null;
    boolean logout = false;
    public FrontEnd(
            IUser userService,
            IMovie movieService) {
        this.userService = userService;
        this.movieService = movieService;
        scanner = new Scanner(System.in);
        logger = Logger.getLogger(Client.class.getName());
    }

    public void attachLogging(String userID) {
        logging = new Logging(userID,true,false);
        logger = logging.attachFileHandlerToLogger(logger);
    }

    public void login() throws NotBoundException, RemoteException, ParseException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your UserID:");
        String userID = scanner.nextLine().trim().toUpperCase();

        System.out.println("Login Successful" + userID);

        attachLogging(userID);

        this.userService.setUserID(userID.toUpperCase());

        logger.severe("CustomerID: "+ this.userService.getUserID());
        logger.severe("Server Name: "+ Util.getServerFullNameByCustomerID(this.userService.getUserID()));
        logger.severe("Server PORT: "+ Util.getServerPortByCustomerID(this.userService.getUserID()));
        this.getRegistry();
        this.getRemoteObjectRef(registry);

        while (!logout)
            menu();
    }

    public void getRegistry() {
        try {
            logger.severe("Fetching registry by server having PORT: " + Util.getServerPortByCustomerID(this.userService.getUserID()) + " associated with user");
            registry = LocateRegistry.getRegistry(Util.getServerPortByCustomerID(this.userService.getUserID()));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void menu() throws RemoteException, ParseException {
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
            System.out.println("4. "+ClientConstant.LOGOUT);
        } else {
            System.out.println("4. "+ClientConstant.BOOK_MOVIE);
            System.out.println("5. "+ClientConstant.GET_BOOKING_SCHEDULE);
            System.out.println("6. "+ClientConstant.CANCEL_MOVIE_TICKET);
            System.out.println("7. "+ClientConstant.LOGOUT);
        }

        menuSelection = getMenuInput();
        response = referToSelectedMenuObj();
        System.out.println(response);
    }

    private int getMenuInput() {
        return scanner.nextInt();
    }

    private int getSlotInput() {
        return scanner.nextInt();
    }

    private int getMovieInput() {
        return scanner.nextInt();
    }

    private int getTheaterInput() {
        return scanner.nextInt();
    }

    private String getCustomerID() {
        return scanner.nextLine();
    }

    private int getBookingCapacityInput() {
        return scanner.nextInt();
    }

    private String getMovieIDInput() {
        return scanner.nextLine();
    }

    private String referToSelectedMenuObj() throws RemoteException, ParseException {
        if(this.userService.isAdmin()) {
            switch (menuSelection) {
                case 1 -> {
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    this.movieService.bookingCapacityPrompt("Enter booking capacity");
                    int bookingCapacity = getBookingCapacityInput();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    scanner.nextLine();
                    String movieID = getMovieIDInput();
                    LocalDate today = LocalDate.now();
                    LocalDate afterSevenDays = today.plusDays(7);
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    Date sevenDaysAfter = Date.from(afterSevenDays.atStartOfDay(defaultZoneId).toInstant());
                    if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())>0 && Util.getSlotDateByMovieID(movieID).compareTo(sevenDaysAfter)<0) {
                        if(this.movieService.validateMovieID(movieID)!=null) {
                            String response = movieTicketServiceObj.addMovieSlots(movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity);
                            logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, response));
                            return response;
                        }else {
                            logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Incorrect movie ID"));
                        }
                    }else {
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, "Cannot create slot for more than one week ahead"));
                        return "Cannot create slot for more than one week ahead";
                    }
                    return null;
                }
                case 2 -> {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    scanner.nextLine();
                    String movieID = getMovieIDInput();
                    if(Util.getSlotDateByMovieID(movieID).compareTo(new Date())>0){
                        res = movieTicketServiceObj.removeMovieSlots(movieID,this.movieService.getMovieName(selectedMovie).toUpperCase());
                        logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                        return res;
                    }
                    res = "Movie cannot be removed for the past show";
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 3 -> {
                    String res;
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    res = movieTicketServiceObj.listMovieShowsAvailability(this.movieService.getMovieName(selectedMovie).toUpperCase());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), null, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 4 -> {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter your UserID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    scanner.nextLine();
                    this.movieService.theaterPrompt("Select Theater");
                    int selectedTheater = getTheaterInput();
                    scanner.nextLine();
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    scanner.nextLine();
                    this.movieService.bookingCapacityPrompt("Enter no of tickets you want to book");
                    int bookingCapacity = getBookingCapacityInput();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    scanner.nextLine();
                    String movieID = getMovieIDInput();
                    res = movieTicketServiceObj.bookMovieTickets(customerID,movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity);
                    logger.severe(Util.createLogMsg(customerID, movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, res));
                    return res;
                }
                case 5 -> {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter your UserID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    res = movieTicketServiceObj.getBookingSchedule(customerID);
                    logger.severe(Util.createLogMsg(customerID,null, null, -1, res));
                    return res;
                }
                case 6 -> {
                    String res;
                    scanner.nextLine();
                    System.out.println("Enter your UserID:");
                    String customerID = scanner.nextLine().trim().toUpperCase();
                    this.movieService.moviesPrompt("Select movie to cancel booking");
                    int selectedMovie = getMovieInput();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    scanner.nextLine();
                    String movieID = getMovieIDInput();
                    res = movieTicketServiceObj.cancelMovieTickets(customerID,movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),0);;
                    logger.severe(Util.createLogMsg(customerID,movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 7 -> {
                    logout = true;
                    return null;
                }
            }
        } else {
            switch (menuSelection) {
                case 1 -> {
                    String res;
                    this.movieService.theaterPrompt("Select Theater");
                    int selectedTheater = getTheaterInput();
                    this.movieService.moviesPrompt("Select Movie");
                    int selectedMovie = getMovieInput();
                    this.movieService.bookingCapacityPrompt("Enter no of tickets you want to book");
                    int bookingCapacity = getBookingCapacityInput();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    scanner.nextLine();
                    String movieID = getMovieIDInput();
                    res = movieTicketServiceObj.bookMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity);
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), bookingCapacity, res));
                    return res;
                }
                case 2 -> {
                    String res;
                    res = movieTicketServiceObj.getBookingSchedule(this.userService.getUserID());
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), null, null, -1, res));
                    return res;
                }
                case 3 -> {
                    String res;
                    this.movieService.moviesPrompt("Select movie to cancel booking");
                    int selectedMovie = getMovieInput();
                    System.out.println("Please enter the MovieID (e.g ATWM190120)");
                    scanner.nextLine();
                    String movieID = getMovieIDInput();
                    res = movieTicketServiceObj.cancelMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),0);
                    logger.severe(Util.createLogMsg(this.userService.getUserID(), movieID, this.movieService.getMovieName(selectedMovie).toUpperCase(), -1, res));
                    return res;
                }
                case 4 -> {
                    logout = true;
                    return null;
                }
            }
        }

        return null;
    }

    public void getRemoteObjectRef(Registry registryRef) throws NotBoundException, RemoteException {
        movieTicketServiceObj = (IMovieTicket) registryRef.lookup(ServiceConstant.MovieTicketService);
    }
}
