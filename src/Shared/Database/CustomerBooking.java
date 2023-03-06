package Shared.Database;

import Shared.Entity.IResponse;
import Shared.Entity.Response;
import Shared.data.MovieState;

import java.util.*;

import Shared.data.Util;

import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerBooking implements ICustomerBooking{
    //CustomerID,MovieID,BookingCapacity
    private final Map<String, Map<String, MovieState>> customerBooking;
    private IResponse response;

    public CustomerBooking() {
        this.customerBooking = new ConcurrentHashMap<>();
        this.response = new Response();
    }

    public boolean addMovieByCustomerID(String customerID, String movieID, String movieName, int numberOfTicketsBooked) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                if(bookingMap.get(movieID).getMovieTicketInfo().containsKey(movieName)) {
                    this.customerBooking.get(customerID).get(movieID).getMovieTicketInfo().put(movieName,bookingMap.get(movieID).getMovieTicketInfo().get(movieName)+numberOfTicketsBooked);
                } else {
                    bookingMap.get(movieID).addMovieToExistingSlot(movieName, numberOfTicketsBooked);
                }
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
            System.out.println("Booking map is not null" + bookingMap);
            if(bookingMap.get(movieID)!=null){
                System.out.println("Booking map get movie id not null "+bookingMap.get(movieID));
                return bookingMap.get(movieID).getMovieTicketInfo().get(movieName);
            }
            return -1;
        }
        return -1;
    }

    public boolean ifMovieBookingExist(String customerID, String movieID, String movieName) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null)
                return bookingMap.get(movieID).getMovieTicketInfo().containsKey(movieName);
            return false;
        }
        return false;
    }

    public boolean ifMovieIDExist(String customerID, String movieID) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.containsKey(movieID))
                return true;
            return false;
        }
        return false;
    }

    public String cancelMovieByMovieID(String customerID, String movieID, String movieName) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                this.customerBooking.get(customerID).get(movieID).getMovieTicketInfo().remove(movieName);
                if(this.customerBooking.get(customerID).get(movieID).getMovieTicketInfo().size()==0) this.customerBooking.get(customerID).remove(movieID);
                return "Movie booking deleted successfully";
            }
            return "No booking found against movieID: "+ movieID;
        }
        return "Customer not found";
    }

    public List<String> getAllCustomerIDs() {
        return new ArrayList<String>(this.customerBooking.keySet());
    }

    public int noOfMoviesBookedInAWeek(String customerID, String movieID) {
        int weekOfBooking = Util.getWeekOfMonth(Util.getSlotDateByMovieID(movieID));
        int monthOfBooking = Util.getMonth(Util.getSlotDateByMovieID(movieID));
        int noOfMoviesBooked = 0;
        ConcurrentHashMap<String, MovieState> map = (ConcurrentHashMap<String, MovieState>) this.getTicketsBookedByCustomerID(customerID);
        if(map!=null && !map.isEmpty()) {
            for (Map.Entry<String, MovieState> entry : map.entrySet()) {
                String key = entry.getKey();
                MovieState movieInfo = entry.getValue();
                if (Util.getWeekOfMonth(Util.getSlotDateByMovieID(key)) == weekOfBooking && Util.getMonth(Util.getSlotDateByMovieID(key)) == monthOfBooking)
                    noOfMoviesBooked = noOfMoviesBooked + movieInfo.getMovieTicketInfo().size();
            }
        }
        return noOfMoviesBooked;
    }
}
