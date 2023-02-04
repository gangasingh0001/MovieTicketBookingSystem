package Shared.Database;

import Shared.data.MovieState;

import java.util.*;

import Shared.data.Util;

import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerBooking implements ICustomerBooking{
    //CustomerID,MovieID,BookingCapacity
    private Map<String, Map<String, MovieState>> customerBooking;

    public CustomerBooking() {
        this.customerBooking = new ConcurrentHashMap<>();
    }

    public boolean addMovieByCustomerID(String customerID, String movieID, String movieName, int numberOfTicketsBooked) throws ParseException {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                bookingMap.get(movieID).addMovieToExistingSlot(movieName,numberOfTicketsBooked);
            } else {
                MovieState movieObj = new MovieState(movieName,movieID,numberOfTicketsBooked);
                this.customerBooking.get(customerID).put(movieID,movieObj);
            }
            return true;
        }
        Map<String,MovieState> movieInfo = new ConcurrentHashMap<>();
        MovieState movieObj = new MovieState(movieName,movieID,numberOfTicketsBooked);
        movieInfo.put(movieID,movieObj);
        this.customerBooking.put(customerID,movieInfo);
        return true;
    }

    public Map<String,MovieState> getTicketsBookedByCustomerID(String customerID) {
        return this.customerBooking.get(customerID);
    }

    public Map<String, MovieState> getTicketsBookedHashMapByCustomerID(String customerID) {
        return this.customerBooking.get(customerID);
    }

    public int getNoOfTicketsBookedByMovieID(String customerID, String movieID, String movieName) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null)
                return bookingMap.get(movieID).getMovieTicketInfo().get(movieName);
            return -1;
        }
        return -1;
    }

    public boolean ifMovieBookingExist(String customerID, String movieID, String movieName) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null)
                return bookingMap.get(movieID).getMovieTicketInfo().get(movieName) != null;
            return false;
        }
        return false;
    }

    public String cancelMovieByMovieID(String customerID, String movieID, String movieName) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                bookingMap.get(movieID).getMovieTicketInfo().remove(movieName);
                return "Movie booking deleted successfully";
            }
            return "No booking found against movieID: "+ movieID;
        }
        return "Customer not found";
    }

    public String cancelMovieTickets(String customerID, String movieID, String movieName) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                MovieState movieRef = bookingMap.get(movieID);
                movieRef.getMovieTicketInfo().remove(movieName);
                this.customerBooking.get(customerID).put(movieID,movieRef);
                return "Movie booking deleted successfully";
            }
            return "No booking found against movieID: "+ movieID;
        }
        return "Customer not found";
    }

    public List<String> getAllCustomerIDs() {
        List<String> keyList = new ArrayList<String>();
        for (String key :
                this.customerBooking.keySet()) {
            keyList.add(key);
        }
        return keyList;
    }

    public int noOfMoviesBookedInAWeek(String customerID, String movieID) {
        int weekOfBooking = Util.getWeekOfMonth(Util.getSlotDateByMovieID(movieID));
        int monthOfBooking = Util.getMonth(Util.getSlotDateByMovieID(movieID));
        int noOfMoviesBooked = 0;
        ConcurrentHashMap<String, MovieState> map = (ConcurrentHashMap<String, MovieState>) this.getTicketsBookedByCustomerID(customerID);
        for (Map.Entry<String, MovieState> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            MovieState movieInfo = entry.getValue();
            if(Util.getWeekOfMonth(Util.getSlotDateByMovieID(key))==weekOfBooking &&
                    Util.getMonth(Util.getSlotDateByMovieID(key))==monthOfBooking) {
                noOfMoviesBooked = noOfMoviesBooked + movieInfo.getMovieTicketInfo().size();
            }
        }
        return noOfMoviesBooked;
    }
}
