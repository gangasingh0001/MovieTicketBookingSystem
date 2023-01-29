package Client;

import Constant.ClientConstant;
import Constant.ServiceConstant;
import Constant.ServerConstant;
import Server.Interface.IMovieTicket;
import Shared.data.IMovie;
import Shared.data.IServerInfo;
import Shared.data.IUser;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class FrontEnd {
    private IMovie movieService = null;
    private IServerInfo serverInfoService = null;
    private IUser userService = null;
    Registry registry = null;
    IMovieTicket movieTicketServiceObj = null;
    int menuSelection = -1;
    String response = null;
    Scanner scanner = null;
    public FrontEnd(
            IServerInfo serverInfoService,
            IUser userService,
            IMovie movieService) {
        this.serverInfoService = serverInfoService;
        this.userService = userService;
        this.movieService = movieService;
        scanner = new Scanner(System.in);
    }

    public String login() throws NotBoundException, RemoteException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your UserID:");
        String userID = scanner.nextLine().trim().toUpperCase();

        System.out.println("Login Successful" + userID);

        this.userService.setUserID(userID);

        this.serverInfoService.getServerPortNumber(this.userService.getUserRegisteredServerPrefix());

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
            registry = LocateRegistry.getRegistry(this.serverInfoService.getServerPort());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String adminMenu() throws RemoteException {
        //Admin specific operations
        System.out.println(ServerConstant.ADD_MOVIE_SLOTS);
        System.out.println(ServerConstant.REMOVE_MOVIE_SLOTS);
        System.out.println(ServerConstant.LIST_MOVIE_SHOWS_AVAILABILITY);

        //Access to customer operations
        System.out.println(ClientConstant.BOOK_MOVIE);
        System.out.println(ClientConstant.GET_BOOKING_SCHEDULE);
        System.out.println(ClientConstant.CANCEL_MOVIE_TICKET);

        //Logout
        System.out.println(ClientConstant.LOGOUT);

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
        return scanner.nextLine().trim().toUpperCase();
    }

    private String referToSelectedMenuObj() throws RemoteException {
        switch (menuSelection) {
            case 1 -> {
                this.movieService.moviesPrompt("Select Movie");
                int selectedMovie = getMovieInput();
//                this.movieService.slotsPrompt("Select Slot");
//                int selectedSlot = getSlotInput();
                this.movieService.bookingCapacityPrompt("Enter booking capacity");
                int bookinCapacity = getBookingCapacityInput();
                System.out.println("Please enter the MovieID (e.g ATWM190120)");
                String movieID = getMovieIDInput();
                if(this.movieService.validateMovieID(movieID)!=null)
                    return movieTicketServiceObj.addMovieSlots(movieID,this.movieService.getMovieName(selectedMovie),bookinCapacity);
                return null;
            }
            case 2 -> {
                this.movieService.moviesPrompt("Select Movie");
                int selectedMovie = getMovieInput();
                System.out.println("Please enter the MovieID (e.g ATWM190120)");
                String movieID = getMovieIDInput();
                return movieTicketServiceObj.removeMovieSlots(movieID,this.movieService.getMovieName(selectedMovie));
            }
            case 3 -> {
                this.movieService.moviesPrompt("Select Movie");
                int selectedMovie = getMovieInput();
                return movieTicketServiceObj.listMovieShowsAvailability(this.movieService.getMovieName(selectedMovie));
            }
//            case 4 -> {
//                return movieTicketServiceObj.bookMovieTickets();
//            }
//            case 5 -> {
//                return movieTicketServiceObj.getBookingSchedule();
//            }
//            case 6 -> {
//                return movieTicketServiceObj.cancelMovieTickets();
//            }
            case 7 -> {
                return null;
            }
        }
        return null;
    }

    public void getRemoteObjectRef(Registry registryRef) throws NotBoundException, RemoteException {
        movieTicketServiceObj = (IMovieTicket) registryRef.lookup(ServiceConstant.MovieTicketController);
    }
}
