package Client;

import Constant.ClientConstant;
import Constant.ServiceConstant;
import Constant.ServerConstant;
import Server.Interface.IMovieTicket;
import Shared.data.IMovie;
import Shared.data.IServerInfo;
import Shared.data.IUser;
import Shared.data.Util;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.util.Scanner;

public class FrontEnd {
    private IMovie movieService = null;
    private IUser userService = null;
    Registry registry = null;
    IMovieTicket movieTicketServiceObj = null;
    int menuSelection = -1;
    String response = null;
    Scanner scanner = null;
    public FrontEnd(
            IUser userService,
            IMovie movieService) {
        this.userService = userService;
        this.movieService = movieService;
        scanner = new Scanner(System.in);
    }

    public String login() throws NotBoundException, RemoteException, ParseException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your UserID:");
        String userID = scanner.nextLine().trim().toUpperCase();

        System.out.println("Login Successful" + userID);

        this.userService.setUserID(userID.toUpperCase());

//        System.out.println("User registered to SERVER: " + this.serverInfoService.getServerNameByCustomerID(userID) + " and PORT: " + this.serverInfoService.getServerPortNumber(this.serverInfoService.getServerPrefixNameByCustomerID(userID)));
//        this.serverInfoService.setServerPort(this.serverInfoService.getServerPortNumber(this.serverInfoService.getServerPrefixNameByCustomerID(userID)));
        System.out.println("CustomerID: "+ this.userService.getUserID());
        System.out.println("Server Name: "+ Util.getServerFullNameByCustomerID(this.userService.getUserID()));
        System.out.println("Server PORT: "+ Util.getServerPortByCustomerID(this.userService.getUserID()));
        this.getRegistry();
        this.getRemoteObjectRef(registry);

        if(this.userService.isAdmin()) {
            return adminMenu();
        }else {
            return null;
        }
    }

    public void getRegistry() {
        try {
            System.out.println("Fetching registry by server having PORT: " + Util.getServerPortByCustomerID(this.userService.getUserID()) + " associated with user");
            registry = LocateRegistry.getRegistry(Util.getServerPortByCustomerID(this.userService.getUserID()));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String adminMenu() throws RemoteException, ParseException {
        //Admin specific operations
        System.out.println("1."+ServerConstant.ADD_MOVIE_SLOTS);
        System.out.println("2."+ServerConstant.REMOVE_MOVIE_SLOTS);
        System.out.println("3."+ServerConstant.LIST_MOVIE_SHOWS_AVAILABILITY);

        //Access to customer operations
        System.out.println("4."+ClientConstant.BOOK_MOVIE);
        System.out.println("5."+ClientConstant.GET_BOOKING_SCHEDULE);
        System.out.println("6."+ClientConstant.CANCEL_MOVIE_TICKET);

        //Logout
        System.out.println("7."+ClientConstant.LOGOUT);

        menuSelection = getMenuInput();
        response = referToSelectedMenuObj();
        return response;
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

    private int getBookingCapacityInput() {
        return scanner.nextInt();
    }

    private String getMovieIDInput() {
        return scanner.nextLine();
    }

    private String referToSelectedMenuObj() throws RemoteException, ParseException {
        switch (menuSelection) {
            case 1 -> {
                this.movieService.moviesPrompt("Select Movie");
                int selectedMovie = getMovieInput();
                this.movieService.bookingCapacityPrompt("Enter booking capacity");
                int bookingCapacity = getBookingCapacityInput();
                System.out.println("Please enter the MovieID (e.g ATWM190120)");
                scanner.nextLine();
                String movieID = getMovieIDInput();

                if(this.movieService.validateMovieID(movieID)!=null)
                    return movieTicketServiceObj.addMovieSlots(movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity);
                return null;
            }
            case 2 -> {
                this.movieService.moviesPrompt("Select Movie");
                int selectedMovie = getMovieInput();
                System.out.println("Please enter the MovieID (e.g ATWM190120)");
                scanner.nextLine();
                String movieID = getMovieIDInput();
                return movieTicketServiceObj.removeMovieSlots(movieID,this.movieService.getMovieName(selectedMovie).toUpperCase());
            }
            case 3 -> {
                this.movieService.moviesPrompt("Select Movie");
                int selectedMovie = getMovieInput();
                scanner.nextLine();
                return movieTicketServiceObj.listMovieShowsAvailability(this.movieService.getMovieName(selectedMovie).toUpperCase());
            }
            case 4 -> {
                this.movieService.moviesPrompt("Select Movie");
                int selectedMovie = getMovieInput();
                this.movieService.bookingCapacityPrompt("Enter booking capacity");
                int bookingCapacity = getBookingCapacityInput();
                System.out.println("Please enter the MovieID (e.g ATWM190120)");
                scanner.nextLine();
                String movieID = getMovieIDInput();
                return movieTicketServiceObj.bookMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),bookingCapacity);
            }
            case 5 -> {
                return movieTicketServiceObj.getBookingSchedule(this.userService.getUserID());
            }
            case 6 -> {
                this.movieService.moviesPrompt("Select movie to cancel booking");
                int selectedMovie = getMovieInput();
                System.out.println("Please enter the MovieID (e.g ATWM190120)");
                scanner.nextLine();
                String movieID = getMovieIDInput();
                return movieTicketServiceObj.cancelMovieTickets(this.userService.getUserID(),movieID,this.movieService.getMovieName(selectedMovie).toUpperCase(),0);
            }
            case 7 -> {
                return null;
            }
        }
        return null;
    }

    public void getRemoteObjectRef(Registry registryRef) throws NotBoundException, RemoteException {
        movieTicketServiceObj = (IMovieTicket) registryRef.lookup(ServiceConstant.MovieTicketService);
    }
}
