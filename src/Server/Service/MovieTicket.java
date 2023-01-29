package Server.Service;

import Constant.ServerConstant;
import Server.Interface.IMovieTicket;
import Shared.data.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MovieTicket extends UnicastRemoteObject implements IMovieTicket {
    // MovieName,MovieID,BookingCapacity
    private Map<String, Map<String, MovieState>> movies;
    //CustomerID,MovieID,BookingCapacity
    private Map<String, Map<String, User>> customers;
    private IServerInfo serverInfo;
    private IUdp udpService;
    private IMovie movieService;
    public MovieTicket(IServerInfo serverInfo, IUdp udpService, IMovie movieService) throws RemoteException {
        super();
        this.movies = new ConcurrentHashMap<>();
        this.customers = new ConcurrentHashMap<>();
        this.serverInfo = serverInfo;
        this.udpService = udpService;
        this.movieService = movieService;
        addUsers();
    }

    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) throws RemoteException {
        Map<String, MovieState> listOfTheaters = this.movies.get(movieName);
        MovieState movie = listOfTheaters.get(movieId);
        if(movie==null) {
            listOfTheaters.put(movieId,new MovieState(movieName,movieId,bookingCapacity));
            return "Added new slot";
        }
        movie.addingMovieSeats(bookingCapacity);
        return "Updated slot";
    }

    public String removeMovieSlots(String movieId, String movieName) throws RemoteException {

        for (Map.Entry<String, Map<String,User>> customer : customers.entrySet()) {
            String cutomerID = customer.getKey().toString();
            Map<String, User> movieObj = customer.getValue();

            User ifBookingExist = movieObj.get(movieId);
            Map<String,MovieState> moviesObj = movies.get(movieName);

            if(ifBookingExist!=null) {
                for (Map.Entry<String, MovieState> movieObjRefInsideMovieMap : moviesObj.entrySet()) {
                    String movieIdObjRefKey = movieObjRefInsideMovieMap.getKey().toString();
                    MovieState movieIdObjRefValue = movieObjRefInsideMovieMap.getValue();

                    if(movieIdObjRefKey.substring(0,3).equals(movieId.substring(0,3)) &&
                            movieIdObjRefKey.substring(5,10).equals(movieId.substring(5,10))){
                        if(movieIdObjRefKey.substring(3,4).equals("M")) {
                            if(movieIdObjRefKey.substring(3,4).equals("A") ||
                                    movieIdObjRefKey.substring(3,4).equals("E")) {
                                User usr = new User();
                                usr.addTicketToUserProfile(ifBookingExist.numberOfTicketsBooked());
                                customers.put(cutomerID, (Map<String, User>) new ConcurrentHashMap<>().put(movieIdObjRefKey,usr));
                                movieObj.remove(movieId); // Remove from customer
                                //movieRefObj.remove(movieIdObjRefKey); //Removed from movies map
                                return "Added and Deleted from cust";
                            }
                        }
                        if(movieIdObjRefKey.substring(3,4).equals("A")) {
                            if(movieIdObjRefKey.substring(3,4).equals("E")) {
                                User usr = new User();
                                usr.addTicketToUserProfile(ifBookingExist.numberOfTicketsBooked());
                                customers.put(cutomerID, (Map<String, User>) new ConcurrentHashMap<>().put(movieIdObjRefKey,usr));
                                movieObj.remove(movieId); // Remove from customer
                                //movieRefObj.remove(movieIdObjRefKey); //Removed from movies map
                                return "Added and Deleted from cust";
                            }
                        }
                    }
                }
            }
            moviesObj.remove(movieId);
//            for (Map.Entry<String, User> movieObjIt : movieObj.entrySet()) {
//                String movieIDKey = movieObjIt.getKey().toString();
//                User movieRefObjValue = movieObjIt.getValue();
//
//                if(movieIDKey.equals(movieId)){
//                    if(movieId.substring(3,4).equals("M")) {
//                        Map<String, MovieState> movieRefObj = movies.get(movieName);
//                        for (Map.Entry<String, MovieState> movieObjRefInsideMovieMap : movieRefObj.entrySet()) {
//                            String movieIdObjRefKey = movieObjRefInsideMovieMap.getKey().toString();
//                            MovieState movieIdObjRefValue = movieObjRefInsideMovieMap.getValue();
//
//                            if(movieIdObjRefKey.substring(0,3).equals(movieId.substring(0,3)) &&
//                                    movieIdObjRefKey.substring(5,10).equals(movieId.substring(5,10)) && movieIdObjRefKey.substring(3,4).equals()){
//                                if(movieIDKey.substring(3,4).equals("A")||movieIDKey.substring(3,4).equals("E")) {
//                                    User usr = new User();
//                                    usr.addTicketToUserProfile(movieRefObjValue.numberOfTicketsBooked());
//                                    customers.put(cutomerID, (Map<String, User>) new ConcurrentHashMap<>().put(movieIdObjRefKey,usr));
//                                    movieIdObjRefValue.reduceBookingCapacity(movieRefObjValue.numberOfTicketsBooked());
//                                    movieObj.remove(movieIDKey); // Remove from customer
//                                    //movieRefObj.remove(movieIdObjRefKey); //Removed from movies map
//                                    return "Added and Deleted from cust";
//                                }
//                            }
//                        }
//                    }
//                    if(movieId.substring(3,4).equals("A")) {
//                        Map<String, MovieState> movieRefObj = movies.get(movieName);
//                        for (Map.Entry<String, MovieState> movieObjRefInsideMovieMap : movieRefObj.entrySet()) {
//                            String movieIdObjRefKey = movieObjRefInsideMovieMap.getKey().toString();
//                            MovieState movieIdObjRefValue = movieObjRefInsideMovieMap.getValue();
//
//                            if(movieIdObjRefKey.substring(0,3).equals(movieId.substring(0,3)) &&
//                                    movieIdObjRefKey.substring(5,10).equals(movieId.substring(5,10)) && movieIdObjRefKey.substring(3,4).equals()){
//                                if(movieIDKey.substring(3,4).equals("E")) {
//                                    User usr = new User();
//                                    usr.addTicketToUserProfile(movieRefObjValue.numberOfTicketsBooked());
//                                    customers.put(cutomerID, (Map<String, User>) new ConcurrentHashMap<>().put(movieIdObjRefKey,usr));
//                                    movieIdObjRefValue.reduceBookingCapacity(movieRefObjValue.numberOfTicketsBooked());
//                                    movieObj.remove(movieIDKey);
//                                    return "Added and Deleted from cust";
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
        return null;
    }

    public String listMovieShowsAvailability(String movieName) throws RemoteException {
        String response = this.getMoviesListInTheatre(movieName);
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) response = response + this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_ATWATER_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1);
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) response = response + this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_VERDUN_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1);
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) response = response + this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_OUTREMONT_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1);
        return response;
    }

    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets) throws RemoteException {
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

    private void addUsers() {
        this.movies.put("AVTAAR", (Map<String, MovieState>) new ConcurrentHashMap<>().put("ATWM160523",new MovieState("AVTAAR","ATWM160523",10)));
        this.movies.put("AVENGERS", (Map<String, MovieState>) new ConcurrentHashMap<>().put("VERM091222",new MovieState("AVENGERs","VERM091222",20)));
        this.movies.put("TITANIC", (Map<String, MovieState>) new ConcurrentHashMap<>().put("OUTE290922",new MovieState("TITANIC","OUTE290922",30)));
        this.customers.put("ATWA2345", (Map<String, User>) new ConcurrentHashMap<>().put("ATWM160523",new User()));
        this.customers.put("ATWM8845", (Map<String, User>) new ConcurrentHashMap<>().put("OUTE290922",new User()));
    }

    public String getMoviesListInTheatre(String movieName) {
        Map<String, MovieState> movieSlots = this.movies.get(movieName);
        StringBuilder builder = new StringBuilder();
        builder.append(this.serverInfo.getServerName() + " Server " + movieName + ":\n");
        for (MovieState movie :
                movieSlots.values()) {
            builder.append(movie.toString() + " || ");
        }
        return builder.toString();
    }

    public String bookTicket(String customerID, String movieId, String movieName, int numberOfTickets) {
        Map<String, User> userMapRef = customers.get(customerID);
        User usr = new User();
        usr.addTicketToUserProfile(numberOfTickets);
        userMapRef.put(movieId,usr);
        this.movies.get(movieName).get(movieId).reduceBookingCapacity(numberOfTickets);
        this.movies.get(movieName).get(movieId).addBookingCustomerID(customerID);
        return "Success";
    }

    public String getCustomerBookingList(String customerID) {
        Map<String,User> customerObj = this.customers.get(customerID);
        StringBuilder builder = new StringBuilder();
        for (User user :
                customerObj.values()) {
            builder.append(user.toString() + " || ");
        }
        return builder.toString();
    }

    public String cancelTicket(String customerID, String movieID, String movieName, int numberOfTickets) {
        return this.customers.get(customerID).get(movieID).cancelTicket(numberOfTickets,customerID);
    }
}
