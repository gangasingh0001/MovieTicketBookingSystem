package Server.Service;

import Constant.ServerConstant;
import Server.Interface.IMovieTicket;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.data.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MovieTicket extends UnicastRemoteObject implements IMovieTicket{
    // MovieName,MovieID,BookingCapacity
    private Map<String, Map<String, MovieState>> movies;
    //CustomerID,MovieID,BookingCapacity
    private Map<String, Map<String, User>> customers;
    private IServerInfo serverInfo;
    private IUdp udpService;
    private IMovie movieService;
    private ICustomerBooking customerBookingDb;
    private IMovies moviesDb;
    public MovieTicket(IServerInfo serverInfo,
                       IUdp udpService,
                       IMovie movieService,
                       ICustomerBooking customerBookingDb,
                       IMovies moviesDb) throws RemoteException {
        super();
        this.movies = new ConcurrentHashMap<>();
        this.customers = new ConcurrentHashMap<>();
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        this.customerBookingDb = customerBookingDb;
        this.moviesDb = moviesDb;
    }

    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) throws RemoteException {
        if(this.moviesDb.ifMovieNameExist(movieName)) {
            if(this.moviesDb.ifMovieIDExist(movieName,movieId)){
                return this.moviesDb.updateMovieSlot(movieName,movieId,bookingCapacity);
            }
            return this.moviesDb.addMovieSlot(movieName,movieId,bookingCapacity);
        }
        return "Movie does not exist";
    }

    public String removeMovieSlots(String movieId, String movieName) throws RemoteException, ParseException {
        if (this.moviesDb.ifMovieNameExist(movieName)) {
            if (this.moviesDb.ifMovieIDExist(movieName, movieId)) {
                //TODO search if customer booked the ticket that is going to be deleted by Admin
                List<String> bookingCustomerIDs = this.customerBookingDb.getAllCustomerIDs();
                String bookingCustomerID = "";
                for (int i = 0; i < bookingCustomerIDs.size(); i++) {
                    String customerID = bookingCustomerIDs.get(i);
                    Map<String, MovieState> bookings = (Map<String, MovieState>) this.customerBookingDb.getTicketsBookedByCustomerID(customerID);
                    if (bookings.get(movieId) != null) {
                        if(bookings.get(movieId).getMovieName().equals(movieName)) {
                            bookingCustomerID = customerID;
                            break;
                        }
                    }
                }
                if (!bookingCustomerID.isEmpty()) {
                    List<String> availableMovieSlots = this.moviesDb.getMovieSlotsAtSpecificArea(movieName, this.serverInfo.getServerName());
                    if (!availableMovieSlots.isEmpty()) {
                        List<MovieState> movieInfo = new ArrayList<MovieState>();
                        for (String availableMovieSlot : availableMovieSlots) {
                            movieInfo.add(new MovieState(movieName, String.valueOf(availableMovieSlot), 0));
                        }


                        movieInfo.sort(new Comparator<MovieState>() {
                            public int compare(MovieState movieA, MovieState movieB) {
                                return movieA.getMovieDate().compareTo(movieB.getMovieDate());
                            }
                        });

                        String nextAvailableBookingID = "";
                        int currentNumberOfTicketBookedByCustomer = this.customerBookingDb.getNoOfTicketsBookedByMovieID(bookingCustomerID, movieId, movieName);
                        for (int i = 0; i < movieInfo.size(); i++) {
                            if (movieInfo.size()>=2 && movieInfo.get(i).getMovieID().equals(movieId)) {
                                //if(i-1 >= 0 && movieInfo.get(i - 1).getMovieDate().before())
                                if(i-1<0) {
                                    if(Util.isDateEqual(movieInfo.get(i).getMovieDate(),movieInfo.get(i+1).getMovieDate())) {
                                        String currentSlot = movieInfo.get(i).getMovieSlot();
                                        String nextSlot = movieInfo.get(i+1).getMovieSlot();
                                        if(currentSlot.equals(Movie.Slots.Morning.toString())) {
                                            nextAvailableBookingID = movieInfo.get(i + 1).getMovieID();
                                        } else if(currentSlot.equals(Movie.Slots.Afternoon.toString())) {
                                            if(nextSlot.equals(Movie.Slots.Morning.toString())) {
                                                i++;
                                                continue;
                                            }
                                            nextAvailableBookingID = movieInfo.get(i + 1).getMovieID();
                                            break;
                                        }else {
                                            i++;
                                            continue;
                                        }
                                    }else {
                                        nextAvailableBookingID = movieInfo.get(i + 1).getMovieID();
                                        break;
                                    }
                                }else if(i+1>movieInfo.size()) {
                                    if(Util.isDateEqual(movieInfo.get(i).getMovieDate(),movieInfo.get(i-1).getMovieDate())) {
                                        String currentSlot = movieInfo.get(i).getMovieSlot();
                                        String prevSlot = movieInfo.get(i-1).getMovieSlot();
                                        if(currentSlot.equals(Movie.Slots.Morning.toString())) {
                                            if(prevSlot.equals(Movie.Slots.Afternoon.toString())) {
                                                nextAvailableBookingID = movieInfo.get(i - 1).getMovieID();
                                                break;
                                            }
                                        } else if(currentSlot.equals(Movie.Slots.Afternoon.toString())) {
                                            if(prevSlot.equals(Movie.Slots.Morning.toString())) {
                                                if(i-2>0 && Util.isDateEqual(movieInfo.get(i-1).getMovieDate(),movieInfo.get(i-2).getMovieDate())) {
                                                    nextAvailableBookingID = movieInfo.get(i - 2).getMovieID();
                                                    break;
                                                }
                                                break;
                                            } else {
                                                nextAvailableBookingID = movieInfo.get(i - 1).getMovieID();
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                } else {
                                    if(Util.isDateEqual(movieInfo.get(i).getMovieDate(),movieInfo.get(i-1).getMovieDate())) {
                                        String currentSlot = movieInfo.get(i).getMovieSlot();
                                        String prevSlot = movieInfo.get(i-1).getMovieSlot();
                                        if(currentSlot.equals(Movie.Slots.Morning.toString())) {
                                            if(prevSlot.equals(Movie.Slots.Afternoon.toString())) {
                                                nextAvailableBookingID = movieInfo.get(i - 1).getMovieID();
                                                break;
                                            }
                                        } else if(currentSlot.equals(Movie.Slots.Afternoon.toString())) {
                                            if(prevSlot.equals(Movie.Slots.Morning.toString())) {
                                                if(i-2>0 && Util.isDateEqual(movieInfo.get(i-1).getMovieDate(),movieInfo.get(i-2).getMovieDate())) {
                                                    nextAvailableBookingID = movieInfo.get(i - 2).getMovieID();
                                                    break;
                                                }
                                                break;
                                            } else {
                                                nextAvailableBookingID = movieInfo.get(i - 1).getMovieID();
                                                break;
                                            }
                                        }
                                    }else {
                                        if(i+1<movieInfo.size()) {
                                            nextAvailableBookingID = movieInfo.get(i + 1).getMovieID();
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
//                        for (int i = 0; i < movieInfo.size(); i++) {
//                            if (movieInfo.size()>=2 && movieInfo.get(i).getMovieID().equals(movieId)) {
//                                //if(i-1 >= 0 && movieInfo.get(i - 1).getMovieDate().before())
//                                int reverseIteration = i;
//                                int forwardIteration = i;
//                                while(reverseIteration>0) {
//                                    String currentSlot = movieInfo.get(reverseIteration).getMovieSlot();
//                                    String prevSlot = movieInfo.get(i-1).getMovieSlot();
//                                    if()
//                                }
//                            }
//                        }
                        if(!nextAvailableBookingID.isEmpty()) this.customerBookingDb.addMovieByCustomerID(bookingCustomerID, nextAvailableBookingID, movieName, currentNumberOfTicketBookedByCustomer);
                    }
                    this.customerBookingDb.cancelMovieByMovieID(bookingCustomerID, movieId);
                }
                return this.moviesDb.deleteMovieSlotByMovieNameAndMovieID(movieName, movieId);
            }
        }
        return "Movie does not exist";
    }

    public String listMovieShowsAvailability(String movieName) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getMoviesListInTheatre(movieName));
        System.out.println("Calling Server 1");
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_ATWATER_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1));
        System.out.println("Calling Server 2");
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_VERDUN_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1));
        System.out.println("Calling Server 3");
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_OUTREMONT_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1));
        return sb.toString();
    }

    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets) throws RemoteException, ParseException {
        String movieInTheater = this.movieService.grepServerPrefixByMovieID(movieId);
        if(!movieInTheater.equals(this.serverInfo.getServerName())) return this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(movieInTheater),"bookTicket",customerID,movieName,movieId,numberOfTickets);
        return this.bookTicket(customerID,movieId,movieName,numberOfTickets);
    }

    public String getBookingSchedule(String customerID) throws RemoteException {
        String customerRegisteredToServer = customerID.substring(0,3);
        if(!customerRegisteredToServer.equals(this.serverInfo.getServerName())) return this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(customerRegisteredToServer),"getCustomerBookingList",customerID,null,null,-1);
        return this.getCustomerBookingList(customerID);
    }

    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        String customerRegisteredToServer = customerID.substring(0,3);
        if(!customerRegisteredToServer.equals(this.serverInfo.getServerName())) return this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(customerRegisteredToServer),"cancelTicket",customerID,movieName,movieID,numberOfTickets);
        return cancelTicket(customerID, movieID, movieName, numberOfTickets);
    }

    public String getMoviesListInTheatre(String movieName) throws RemoteException {
        Map<String, Integer> movieSlots = this.moviesDb.getMovieSlotsHashMapByMovieName(movieName);
        if(movieSlots!=null) {
            StringBuilder builder = new StringBuilder();
            builder.append(this.serverInfo.getServerName()).append(" ");
            for (Map.Entry<String,Integer> slot : movieSlots.entrySet()) {
                builder.append(movieName + ": " +slot.getValue() +" "+ slot.getKey()+", ");
            }
            return builder.toString();
        }
        return "";
    }

    public String bookTicket(String customerID, String movieId, String movieName, int numberOfTickets) throws RemoteException, ParseException {
        StringBuilder builder = new StringBuilder();
        if(this.moviesDb.ifMovieIDExist(movieName,movieId)) {
            this.customerBookingDb.addMovieByCustomerID(customerID,movieId,movieName,numberOfTickets);
            builder.append("Movie Added Successfully");
            builder.append(this.moviesDb.decrementBookingCapacity(movieName,movieId,numberOfTickets));
            return builder.toString();
        }
        builder.append("Booking Failed");
        return builder.toString();
    }

    public String getCustomerBookingList(String customerID) throws RemoteException {
        Map<String,MovieState> customerObj = this.customerBookingDb.getTicketsBookedByCustomerID(customerID);
        if(customerObj!=null) {
            StringBuilder builder = new StringBuilder();
            for (MovieState bookingSchedule :
                    customerObj.values()) {
                builder.append(bookingSchedule.getMovieName().toString() + ": " + bookingSchedule.getMovieID() + " " + bookingSchedule.getRemainingSlots());
            }
            return builder.toString();
        }
        return "No customer bookings";
    }

    public String cancelTicket(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        this.moviesDb.incrementBookingCapacity(movieName,movieID,numberOfTickets);
        return this.customerBookingDb.cancelMovieTickets(customerID,movieID,movieName);
    }
}
