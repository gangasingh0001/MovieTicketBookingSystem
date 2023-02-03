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

public class MovieTicket extends UnicastRemoteObject implements IMovieTicket{
    private final IServerInfo serverInfo;
    private final IUdp udpService;
    private final IMovie movieService;
    private final ICustomerBooking customerBookingDb;
    private final IMovies moviesDb;
    public MovieTicket(IServerInfo serverInfo,
                       IUdp udpService,
                       IMovie movieService,
                       ICustomerBooking customerBookingDb,
                       IMovies moviesDb) throws RemoteException {
        super();
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
        if (!Util.getServerPrefixByMovieID(movieId).equals(this.serverInfo.getServerName())) {
            this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(Util.getServerPrefixByMovieID(movieId)), "removeMovieSlots", null, movieName, movieId, -1);
        } else {
            if (this.moviesDb.ifMovieNameExist(movieName)) {
                if (this.moviesDb.ifMovieIDExist(movieName, movieId)) {
                    List<String> bookingCustomerIDs = this.customerBookingDb.getAllCustomerIDs();
                    String bookingCustomerID = "";
                    for (String customerID : bookingCustomerIDs) {
                        Map<String, MovieState> bookings = this.customerBookingDb.getTicketsBookedByCustomerID(customerID);
                        if (bookings.get(movieId) != null) {
                            if (bookings.get(movieId).getMovieTicketInfo().get(movieName) != null) {
                                bookingCustomerID = customerID;
                                break;
                            }
                        }
                    }
                    if (!bookingCustomerID.isEmpty()) {
                        String nextAvailableBookingID = this.findNextAvailableSlot(bookingCustomerID,movieId,movieName);
                        if(!nextAvailableBookingID.isEmpty()) {
                            int currentNumberOfTicketBookedByCustomer = this.customerBookingDb.getNoOfTicketsBookedByMovieID(bookingCustomerID, movieId, movieName);
                            this.customerBookingDb.addMovieByCustomerID(bookingCustomerID, nextAvailableBookingID, movieName, currentNumberOfTicketBookedByCustomer);
                            this.customerBookingDb.cancelMovieByMovieID(bookingCustomerID, movieId,movieName);
                        } else {
                            String nextAvailableMovieIDAtwater = "";
                            String nextAvailableMovieIDVerdun = "";
                            String nextAvailableMovieIDOutermont = "";
                            if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) {
                                nextAvailableMovieIDAtwater = this.udpService.sendUDPMessage(ServerConstant.SERVER_ATWATER_PORT, "findNextAvailableSlot", bookingCustomerID, movieName, movieId, -1);
                            }
                            if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) {
                                nextAvailableMovieIDVerdun = this.udpService.sendUDPMessage(ServerConstant.SERVER_VERDUN_PORT, "findNextAvailableSlot", bookingCustomerID, movieName, movieId, -1);
                            }
                            if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) {
                                nextAvailableMovieIDOutermont = this.udpService.sendUDPMessage(ServerConstant.SERVER_OUTREMONT_PORT, "findNextAvailableSlot", bookingCustomerID, movieName, movieId, -1);
                            }
                            List<MovieState> movieInfo = new ArrayList<MovieState>();
                            if(!nextAvailableMovieIDAtwater.isEmpty())
                                movieInfo.add(new MovieState(movieName, nextAvailableMovieIDAtwater.trim(), 0));
                            if(!nextAvailableMovieIDVerdun.isEmpty())
                                movieInfo.add(new MovieState(movieName, nextAvailableMovieIDVerdun.trim(), 0));
                            if(!nextAvailableMovieIDOutermont.isEmpty())
                                movieInfo.add(new MovieState(movieName, nextAvailableMovieIDOutermont.trim(), 0));
                            List<MovieState> sortedList;
                            StringBuilder sb = new StringBuilder();
                            if(movieInfo.size()>=1) {
                                sortedList = Util.sortMovieByDates(movieInfo);
                                if(sortedList.size()>0) {
                                    int currentNumberOfTicketBookedByCustomer = this.customerBookingDb.getNoOfTicketsBookedByMovieID(bookingCustomerID, movieId, movieName);
                                    //this.customerBookingDb.addMovieByCustomerID(bookingCustomerID, sortedList.get(0).getMovieID(), movieName, currentNumberOfTicketBookedByCustomer);

                                    sb.append(this.bookMovieTickets(bookingCustomerID, sortedList.get(0).getMovieID(), movieName, currentNumberOfTicketBookedByCustomer)).append("\n");
                                    this.customerBookingDb.cancelMovieByMovieID(bookingCustomerID, movieId,movieName);
                                    //this.moviesDb.decrementBookingCapacity(movieName,movieId,currentNumberOfTicketBookedByCustomer);
                                    sb.append(this.moviesDb.deleteMovieSlotByMovieNameAndMovieID(movieName, movieId)).append("\n");
                                    return sb.append("Next Slot Booked and Slot deleted from theater").toString();
                                }
                            } else {
                                this.moviesDb.deleteMovieSlotByMovieNameAndMovieID(movieName, movieId);
                                return "No next slot available for booking... Booking deleted from availability";
                            }
                        }
                    }
                    return this.moviesDb.deleteMovieSlotByMovieNameAndMovieID(movieName, movieId);
                }
            }
        }
        return "Movie does not exist";
    }

    public String listMovieShowsAvailability(String movieName) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getMoviesListInTheatre(movieName));
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_ATWATER_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1));
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_VERDUN_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1));
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_OUTREMONT_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1));
        return sb.toString();
    }

    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets) throws RemoteException, ParseException {
        StringBuilder sb = new StringBuilder();
        if (!Util.getServerPrefixByMovieID(movieId).equals(this.serverInfo.getServerName())) {
            if(!this.customerBookingDb.ifMovieBookingExist(customerID,movieId,movieName))
                return this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(Util.getServerPrefixByMovieID(movieId)), "bookTicket", customerID, movieName, movieId, numberOfTickets);
            else {
                sb.append("Movie Already booked at ").append(Util.getServerFullNameByCustomerID(customerID)).append(" theater");
                return sb.toString();
            }
        }
        if(numberOfTickets<this.moviesDb.getSlotBookingCapacity(movieName,movieId)) {
            return this.bookTicket(customerID, movieId, movieName, numberOfTickets,true);
        }
        sb.append("Seats cannot be booked for more than ").append(this.moviesDb.getSlotBookingCapacity(movieName,movieId)).append(" customers");
        return sb.toString();
    }

    public String getBookingSchedule(String customerID) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append(Util.getServerFullNameByCustomerID(customerID)).append("\n");
        sb.append(this.getCustomerBookingList(customerID));
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) {
            sb.append("\n ATWATER");
            sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_ATWATER_PREFIX),"getCustomerBookingList",customerID,null,null,-1));
        }
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) {
            sb.append("VERDUN \n");
            sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_VERDUN_PREFIX),"getCustomerBookingList",customerID,null,null,-1));
        }
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) {
            sb.append("\n OUTERMONT \n");
            sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_OUTREMONT_PREFIX),"getCustomerBookingList",customerID,null,null,-1));
        }
        return sb.toString();
    }

    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        if (!Util.getServerPrefixByMovieID(movieID).equals(this.serverInfo.getServerName())) {
            return this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(Util.getServerPrefixByMovieID(movieID)),"cancelTicket",customerID,movieName,movieID,numberOfTickets);
        }
        return cancelTicket(customerID, movieID, movieName, numberOfTickets);
    }

    public String getMoviesListInTheatre(String movieName) throws RemoteException {
        Map<String, Integer> movieSlots = this.moviesDb.getMovieSlotsHashMapByMovieName(movieName);
        if(movieSlots!=null) {
            StringBuilder builder = new StringBuilder();
            builder.append(Util.getServerNameByServerPrefix(this.serverInfo.getServerName())).append(" \n");
            for (Map.Entry<String,Integer> slot : movieSlots.entrySet()) {
                builder.append("Movie Name: "+ movieName + " |" + " Seats Available: "+ slot.getValue()+ "| Slot: " + Util.getSlotByMovieID(slot.getKey()) + "| Movie Date: " + Util.getSlotDateByMovieID(slot.getKey()) + ", \n\n");
            }
            return builder.toString();
        }
        return "";
    }

    public String bookTicket(String customerID, String movieId, String movieName, int numberOfTickets, boolean isUserRegisteredToServer) throws RemoteException, ParseException {
        StringBuilder builder = new StringBuilder();
        if(numberOfTickets<this.moviesDb.getSlotBookingCapacity(movieName,movieId)){
            if(this.moviesDb.ifMovieIDExist(movieName,movieId)) {
                if(isUserRegisteredToServer) {
                    this.customerBookingDb.addMovieByCustomerID(customerID,movieId,movieName,numberOfTickets);
                    builder.append("Movie Added Successfully \n");
                    builder.append(this.moviesDb.decrementBookingCapacity(movieName,movieId,numberOfTickets));
                }else {
                    if(numberOfTickets<=this.moviesDb.getSlotBookingCapacity(movieName,movieId)) {
                        if(this.customerBookingDb.noOfMoviesBookedInAWeek(customerID,movieId)<3) {
                            this.customerBookingDb.addMovieByCustomerID(customerID,movieId,movieName,numberOfTickets);
                            builder.append("Movie Added Successfully \n");
                            builder.append(this.moviesDb.decrementBookingCapacity(movieName,movieId,numberOfTickets));
                        }else {
                            builder.append("You cannot book more than 3 movie in this theater\n");
                        }
                    }else {
                        builder.append("Seats cannot be booked for more than ").append(this.moviesDb.getSlotBookingCapacity(movieName,movieId)).append(" customers");
                    }
                }
                return builder.toString();
            } else {
                builder.append("Booking Failed");
                return builder.toString();
            }
        } else {
            builder.append("Booking slots filled");
            return builder.toString();
        }
    }

    public String getCustomerBookingList(String customerID) throws RemoteException {
        Map<String,MovieState> customerObj = this.customerBookingDb.getTicketsBookedByCustomerID(customerID);
        if(customerObj!=null) {
            StringBuilder builder = new StringBuilder();
            for (MovieState bookingSchedule :
                    customerObj.values()) {
                bookingSchedule.getMovieTicketInfo().entrySet().forEach(entry -> {
                    builder.append(entry.getKey()+ " Slot: " + Util.getSlotByMovieID(bookingSchedule.getMovieID()) + "| MovieID: " + bookingSchedule.getMovieID() + "| Seats Booked: " + entry.getValue() + ", \n");
                });
            }
            return builder.toString();
        }
        return "No customer bookings";
    }

    public String cancelTicket(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        this.moviesDb.incrementBookingCapacity(movieName,movieID,numberOfTickets);
        return this.customerBookingDb.cancelMovieTickets(customerID,movieID,movieName);
    }

    public String findNextAvailableSlot(String customerID,String movieID, String movieName) throws ParseException {
        List<String> availableMovieSlots = this.moviesDb.getMovieSlotsAtSpecificArea(movieName, this.serverInfo.getServerName());
        System.out.println("findNextAvailableSlot " + availableMovieSlots);
        if (availableMovieSlots!=null && !availableMovieSlots.isEmpty() && availableMovieSlots.size()>1) {
            List<MovieState> movieInfo = new ArrayList<MovieState>();
            for (String availableMovieSlot : availableMovieSlots) {
                movieInfo.add(new MovieState(movieName, availableMovieSlot, 0));
            }

            movieInfo = Util.sortMovieBySlots(movieInfo);
            System.out.println("MovieInfo = "+movieInfo);
            String nextAvailableBookingID = "";
            for (int j = 0; j < movieInfo.size(); j++) {
                if(movieInfo.get(j).getMovieID().equals(movieID)) {
                    if(j+1<movieInfo.size()) nextAvailableBookingID = movieInfo.get(j+1).getMovieID().trim();
                }
            }
            System.out.println("Next available slot = "+ nextAvailableBookingID);
            return nextAvailableBookingID;
        }
        if(availableMovieSlots != null && availableMovieSlots.size()==1) return availableMovieSlots.get(0);
        return "";
    }
}
