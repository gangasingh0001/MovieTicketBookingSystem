package Server.Service;

import Constant.ServerConstant;
import Server.Interface.IMovieTicket;
import Shared.Database.ICustomerBooking;
import Shared.Database.IMovies;
import Shared.data.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class MovieTicket extends UnicastRemoteObject implements IMovieTicket{
    private final IServerInfo serverInfo;
    private final IUdp udpService;
    private final IMovie movieService;
    private final ICustomerBooking customerBookingDb;
    private final IMovies moviesDb;
    private Logger logger;
    private String response;
    public MovieTicket(Logger logger,
                       IServerInfo serverInfo,
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
        this.logger = logger;
    }

    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) throws RemoteException {
        if(this.moviesDb.ifMovieNameExist(movieName)) {
            if(this.moviesDb.ifMovieIDExist(movieName,movieId)){
                response = this.moviesDb.updateMovieSlot(movieName,movieId,bookingCapacity);
                logger.severe(Util.createLogMsg(null, movieId, movieName, bookingCapacity, response));
                return response;
            }
            response = this.moviesDb.addMovieSlot(movieName,movieId,bookingCapacity);
            logger.severe(Util.createLogMsg(null, movieId, movieName, bookingCapacity, response));
            return response;
        }
        response = "Movie does not exist";
        logger.severe(Util.createLogMsg(null, movieId, movieName, bookingCapacity, response));
        return response;
    }

    public String removeMovieSlots(String movieId, String movieName) throws RemoteException, ParseException {
        if (!Util.getServerPrefixByMovieID(movieId).equals(this.serverInfo.getServerName())) {
            logger.severe(Util.createLogMsg(null, movieId, movieName, -1, "Unauthorised: Cannot delete slots on other theaters"));
            return "Unauthorised: Cannot delete slots on other theaters";
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
                        if(!nextAvailableBookingID.isEmpty()) {
                            movieInfo.add(new MovieState(movieName, nextAvailableBookingID.trim(), 0));
                        }
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

                                this.bookMovieTickets(bookingCustomerID, sortedList.get(0).getMovieID(), movieName, currentNumberOfTicketBookedByCustomer);
                                this.customerBookingDb.cancelMovieByMovieID(bookingCustomerID, movieId,movieName);
                                this.moviesDb.deleteMovieSlotByMovieNameAndMovieID(movieName, movieId);
                                sb.append("Next Slot: "+ sortedList.get(0).getMovieID() +" at " + Util.getServerNameByServerPrefix(Util.getServerPrefixByMovieID(sortedList.get(0).getMovieID())) + " theater booked for Customer: " +bookingCustomerID + "\nSlot: " + movieId +  " deleted from " + Util.getServerNameByServerPrefix(Util.getServerPrefixByMovieID(movieId)) + " theater");
                                logger.severe(Util.createLogMsg(null, movieId, movieName, -1, sb.toString()));
                                return sb.toString();
                            }
                        } else {
                            response = this.moviesDb.deleteMovieSlotByMovieNameAndMovieID(movieName, movieId) + "No next slot available for booking...";;
                            logger.severe(Util.createLogMsg(null, movieId, movieName, -1, response));
                            return response;
                        }
                    }
                    response = this.moviesDb.deleteMovieSlotByMovieNameAndMovieID(movieName, movieId);
                    logger.severe(Util.createLogMsg(null, movieId, movieName, -1, response));
                    return response;
                }
            }
        }
        response = "Movie does not exist";
        logger.severe(Util.createLogMsg(null, movieId, movieName, -1, response));
        return response;
    }

    public String listMovieShowsAvailability(String movieName) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getMoviesListInTheatre(movieName));
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_ATWATER_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1)).append("\n");
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_VERDUN_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1)).append("\n");
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_OUTREMONT_PREFIX),"getMoviesListInTheatre",null,movieName,null,-1)).append("\n");
        logger.severe(Util.createLogMsg(null, null, movieName, -1, sb.toString()));
        return sb.toString();
    }

    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets) throws RemoteException, ParseException {
        StringBuilder sb = new StringBuilder();
        if (!Util.getServerPrefixByMovieID(movieId).equals(this.serverInfo.getServerName())) {
            if(!this.customerBookingDb.ifMovieBookingExist(customerID,movieId,movieName)) {
                int  moviesBookedAtOtherTheaters = 0;
                if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) {
                    String res = this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_ATWATER_PREFIX),"getNoOfBookingsInWeek",customerID,movieName,movieId,-1);
                    if(!res.isEmpty())
                        moviesBookedAtOtherTheaters += Integer.parseInt(res);
                }
                if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) {
                    String res = this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_VERDUN_PREFIX),"getNoOfBookingsInWeek",customerID,movieName,movieId,-1);
                    if(!res.isEmpty())
                        moviesBookedAtOtherTheaters += Integer.parseInt(res);
                }
                if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) {
                    String res = this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_OUTREMONT_PREFIX),"getNoOfBookingsInWeek",customerID,movieName,movieId,-1);
                    if(!res.isEmpty())
                        moviesBookedAtOtherTheaters += Integer.parseInt(res);
                }
                if(moviesBookedAtOtherTheaters<3) {
                    response = this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(Util.getServerPrefixByMovieID(movieId)), "bookTicket", customerID, movieName, movieId, numberOfTickets);
                    logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, response));
                    return response;
                }
                response = "Movies cannot be booked for more than 3 movies in a week";
                logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, response));
                return response;
            }
            else {
                sb.append("Movie Already booked at ").append(Util.getServerFullNameByCustomerID(customerID)).append(" theater");
                logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, sb.toString()));
                return sb.toString();
            }
        }
        if(numberOfTickets<this.moviesDb.getSlotBookingCapacity(movieName,movieId)) {
            response = this.bookTicket(customerID, movieId, movieName, numberOfTickets,true);
            logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, response));
            return response;
        }
        sb.append("Seats cannot be booked for more than ").append(this.moviesDb.getSlotBookingCapacity(movieName,movieId)).append(" customers");
        logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, sb.toString()));
        return sb.toString();
    }

    public String getBookingSchedule(String customerID) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append(Util.getServerNameByServerPrefix(this.serverInfo.getServerName())).append("\n");
        sb.append(this.getCustomerBookingList(customerID)).append("\n");
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_ATWATER_PREFIX)) {
            sb.append("ATWATER \n");
            sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_ATWATER_PREFIX),"getCustomerBookingList",customerID,null,null,-1));
        }
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_VERDUN_PREFIX)) {
            sb.append("VERDUN \n");
            sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_VERDUN_PREFIX),"getCustomerBookingList",customerID,null,null,-1)).append("\n");
        }
        if(!this.serverInfo.getServerName().equals(ServerConstant.SERVER_OUTREMONT_PREFIX)) {
            sb.append("OUTERMONT \n");
            sb.append(this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(ServerConstant.SERVER_OUTREMONT_PREFIX),"getCustomerBookingList",customerID,null,null,-1));
        }
        logger.severe(Util.createLogMsg(customerID, null, null, -1, sb.toString()));
        return sb.toString();
    }

    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        if (!Util.getServerPrefixByMovieID(movieID).equals(this.serverInfo.getServerName())) {
            response = this.udpService.sendUDPMessage(this.serverInfo.getServerPortNumber(Util.getServerPrefixByMovieID(movieID)),"cancelTicket",customerID,movieName,movieID,numberOfTickets);
            logger.severe(Util.createLogMsg(customerID, movieID, movieName, numberOfTickets, response));
            return response;
        }
        response = cancelTicket(customerID, movieID, movieName, numberOfTickets);
        logger.severe(Util.createLogMsg(customerID, movieID, movieName, numberOfTickets, response));
        return response;
    }

    public String getMoviesListInTheatre(String movieName) throws RemoteException {
        Map<String, Integer> movieSlots = this.moviesDb.getMovieSlotsHashMapByMovieName(movieName);
        if(movieSlots!=null) {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append(Util.getServerNameByServerPrefix(this.serverInfo.getServerName())).append("\n");
            for (Map.Entry<String,Integer> slot : movieSlots.entrySet()) {
                builder.append("Movie Name: "+ movieName + " |" + " MovieID: "+slot.getKey() + " | Seats Available: "+ slot.getValue()+ "| Slot: " + Util.getSlotByMovieID(slot.getKey()) + " | Movie Date: " + new SimpleDateFormat("dd/MM/yyyy").format(Util.getSlotDateByMovieID(slot.getKey()))).append("\n");
            }
            logger.severe(Util.createLogMsg(null, null, movieName, -1, builder.toString()));
            return builder.toString();
        }
        logger.severe(Util.createLogMsg(null, null, movieName, -1, movieName+" not found"));
        return "";
    }

    public String bookTicket(String customerID, String movieId, String movieName, int numberOfTickets, boolean isUserRegisteredToServer) throws RemoteException, ParseException {
        StringBuilder builder = new StringBuilder();
        int seatsAvailable = this.moviesDb.getSlotBookingCapacity(movieName,movieId);
        if(seatsAvailable!=0 && numberOfTickets<=seatsAvailable){
            if(this.moviesDb.ifMovieIDExist(movieName,movieId)) {
                if(isUserRegisteredToServer) {
                    if(this.customerBookingDb.addMovieByCustomerID(customerID,movieId,movieName,numberOfTickets))
                        builder.append("Movie Booked Successfully \n");
                    else
                        builder.append("Movie Booking Failed \n");
                    this.moviesDb.decrementBookingCapacity(movieName,movieId,numberOfTickets);
                }else {
                    if(numberOfTickets<=this.moviesDb.getSlotBookingCapacity(movieName,movieId)) {
                        if(this.customerBookingDb.noOfMoviesBookedInAWeek(customerID,movieId)<3) {
                            if(this.customerBookingDb.addMovieByCustomerID(customerID,movieId,movieName,numberOfTickets))
                                builder.append("Movie Booked Successfully \n");
                            else
                                builder.append("Movie Booking Failed \n");
                            this.moviesDb.decrementBookingCapacity(movieName,movieId,numberOfTickets);
                        }else {
                            builder.append("You cannot book more than 3 movie in this theater\n");
                        }
                    }else {
                        builder.append("Seats cannot be booked for more than ").append(this.moviesDb.getSlotBookingCapacity(movieName,movieId)).append(" customers");
                    }
                }
                logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, builder.toString()));
                return builder.toString();
            } else {
                builder.append("Booking Failed");
                logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, builder.toString()));
                return builder.toString();
            }
        } else if(numberOfTickets>seatsAvailable){
            StringBuilder append = builder.append("Booking cannot be made for more than " + seatsAvailable + " seats");
            logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, builder.toString()));
            return builder.toString();
        } else {
            StringBuilder append = builder.append("Booking is full");
            logger.severe(Util.createLogMsg(customerID, movieId, movieName, numberOfTickets, builder.toString()));
            return builder.toString();
        }
    }

    public String getCustomerBookingList(String customerID) throws RemoteException {
        Map<String,MovieState> customerObj = this.customerBookingDb.getTicketsBookedByCustomerID(customerID);
        if(customerObj!=null && !customerObj.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (MovieState bookingSchedule : customerObj.values()) {
                bookingSchedule.getMovieTicketInfo().forEach((key, value) -> builder.append("Movie Name: ").append(key).append(" | Slot: ").append(Util.getSlotByMovieID(bookingSchedule.getMovieID())).append(" | MovieID: ").append(bookingSchedule.getMovieID()).append(" | Seats Booked: ").append(value).append(" | Movie Date: " + new SimpleDateFormat("dd/MM/yyyy").format(Util.getSlotDateByMovieID(bookingSchedule.getMovieID())) ).append(", \n"));
            }
            logger.severe(Util.createLogMsg(customerID, null, null, -1, builder.toString()));
            return builder.toString();
        }
        response = "No customer bookings";
        logger.severe(Util.createLogMsg(customerID, null, null, -1, response));
        return response;
    }

    public String cancelTicket(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        response = this.customerBookingDb.cancelMovieByMovieID(customerID,movieID,movieName);
        if(response.equals("Movie booking deleted successfully")){
            int currentNumberOfTicketBookedByCustomer = this.customerBookingDb.getNoOfTicketsBookedByMovieID(customerID, movieID, movieName);
            this.moviesDb.incrementBookingCapacity(movieName,movieID,currentNumberOfTicketBookedByCustomer);
        }
        sb.append(response);
        logger.severe(Util.createLogMsg(customerID, movieID, movieName, numberOfTickets, sb.toString()));
        return sb.toString();
    }

    public String findNextAvailableSlot(String customerID,String movieID, String movieName) throws ParseException {
        List<String> availableMovieSlots = this.moviesDb.getMovieSlotsAtSpecificArea(movieName, this.serverInfo.getServerName());
        if (availableMovieSlots!=null && !availableMovieSlots.isEmpty() && availableMovieSlots.size()>1) {
            List<MovieState> movieInfo = new ArrayList<MovieState>();
            for (String availableMovieSlot : availableMovieSlots) {
                movieInfo.add(new MovieState(movieName, availableMovieSlot, 0));
            }

            movieInfo = Util.sortMovieBySlots(movieInfo);
            String nextAvailableBookingID = "";
            if(!movieInfo.isEmpty()) {
                for (int j = 0; j < movieInfo.size(); j++) {
                    if(movieInfo.get(j).getMovieID().equals(movieID)) {
                        if(j+1<movieInfo.size()) nextAvailableBookingID = movieInfo.get(j+1).getMovieID().trim();
                    }
                }
                if(nextAvailableBookingID.isEmpty()) {
                    nextAvailableBookingID = movieInfo.get(0).getMovieID().trim();
                }
            }
            logger.severe(Util.createLogMsg(customerID, movieID, movieName, -1, "nextAvailableBookingID: "+nextAvailableBookingID + " at Theater: "+Util.getServerNameByServerPrefix(this.serverInfo.getServerName())));
            return nextAvailableBookingID;
        }
        if(availableMovieSlots != null && availableMovieSlots.size()==1) {
            logger.severe(Util.createLogMsg(customerID, movieID, movieName, -1, "nextAvailableBookingID: "+availableMovieSlots.get(0)+ " at Theater: "+Util.getServerNameByServerPrefix(this.serverInfo.getServerName())));
            return availableMovieSlots.get(0);
        }
        logger.severe(Util.createLogMsg(customerID, movieID, movieName, -1, "nextAvailableBookingID: "+"No Slot availabe"));
        return "";
    }

    public String getNoOfBookingsInWeek(String customerID, String movieId) {
        return String.valueOf(this.customerBookingDb.noOfMoviesBookedInAWeek(customerID,movieId));
    }
}
