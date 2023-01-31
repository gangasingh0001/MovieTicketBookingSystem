package Shared.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MovieState {
    private String movieID;
    private String movieName;
    private int bookingCapacity;
    private String movieSlot;
    private Date movieDate;
    private String movieTheatrePrefix;
    private List<String> customerIDs;
    public MovieState(String movieName,
                      String movieID,
                      int bookingCapacity) throws ParseException {
        this.movieID = movieID;
        this.movieName = movieName;
        this.bookingCapacity = bookingCapacity;
        this.movieSlot = movieID.substring(3,4).toUpperCase();
        this.movieTheatrePrefix = movieID.substring(0,3).toUpperCase();
        this.setMovieDateUTC(movieID.substring(4,10).toUpperCase());
    }

    public void setMovieDateUTC(String date) throws ParseException {
        this.movieDate = new SimpleDateFormat("ddMMyy").parse(date);
    }

    public String getMovieID() {
        return movieID;
    }

    public String getMovieName() {
        return movieName;
    }

    public int getRemainingSlots() {
        return bookingCapacity;
    }

    public String getMovieSlot() {
        return movieSlot;
    }

    public Date getMovieDate() {
        return movieDate;
    }

    public String getMovieTheatrePrefix(){
        return movieTheatrePrefix;
    }

    public String setBookingCapacity(int noOfSeats) {
        this.bookingCapacity=noOfSeats;
        return "True";
    }

    public String reduceBookingCapacity(int noOfSeats) {
        this.bookingCapacity-=noOfSeats;
        return "True";
    }

    public void addBookingCustomerID(String custID) {
        this.customerIDs.add(custID);
    }

    public void removeCustomerIDFromBookingList(String custID) {
        this.customerIDs.remove(custID);
    }

    public List<String> bookingCustomersList() {
        return this.customerIDs.stream().toList();
    }
}
