package Shared.Database;

import Shared.data.MovieState;
import Shared.data.Util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            MovieState movieObj = new MovieState(movieName,movieID,numberOfTicketsBooked);
            bookingMap.put(movieID,movieObj);
            this.customerBooking.get(customerID).put(movieID,movieObj);
            return true;
        }
        Map<String,MovieState> movieInfo = new ConcurrentHashMap<>();
        this.customerBooking.put(customerID,movieInfo);
        MovieState movieObj = new MovieState(movieName,movieID,numberOfTicketsBooked);
        movieInfo.put(movieID,movieObj);
        this.customerBooking.get(customerID).put(movieID,movieObj);
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
                if(bookingMap.get(movieID).getMovieName().equals(movieName)) return bookingMap.get(movieID).getRemainingSlots();
            return -1;
        }
        return -1;
    }

    public String cancelMovieByMovieID(String customerID, String movieID) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                bookingMap.remove(movieID);
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
                for (String movieObj :
                        bookingMap.keySet()) {
                    if(movieObj.equals(movieID)&&movieRef.getMovieName().equals(movieName)) {
                        movieRef.setBookingCapacity(0);
                        this.customerBooking.get(customerID).put(movieID,movieRef);
                        return "Movie booking deleted successfully";
                    }
                }
            }
            return "No booking found against movieID: "+ movieID;
        }
        return "Customer not found";
    }

    public String incrementBookedSeatsByMovieID(String customerID, String movieID, int noOfMoreSeats) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                int bookedSeats = bookingMap.get(movieID).getRemainingSlots();
                MovieState bookingInfo = bookingMap.get(movieID);
                bookingInfo.setBookingCapacity(bookedSeats + noOfMoreSeats);
                bookingMap.put(movieID,bookingInfo);
                return "Movie booking seats updated successfully to "+ bookedSeats +" seats";
            }
            return "Movie not found";
        }
        return "Customer not found";
    }

    public String decrementBookedSeatsByMovieID(String customerID, String movieID, int noOfSeatsReleased) {
        Map<String, MovieState> bookingMap = this.customerBooking.get(customerID);
        if(bookingMap!=null){
            if(bookingMap.get(movieID)!=null) {
                int bookedSeats = bookingMap.get(movieID).getRemainingSlots();
                MovieState bookingInfo = bookingMap.get(movieID);
                bookingInfo.setBookingCapacity(bookedSeats - noOfSeatsReleased);
                bookingMap.put(movieID,bookingInfo);
                return "Movie booking capacity updated successfully to "+ bookedSeats +" seats";
            }
            return "Movie not found";
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
}
