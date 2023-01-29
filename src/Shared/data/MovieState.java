package Shared.data;

import java.util.List;

public class MovieState {
    private String movieID;
    private String movieName;
    private int bookingCapacity;
    private String movieSlot;
    private String movieDate;
    private String movieTheatrePrefix;
    private List<String> customerIDs;
    public MovieState(String movieName,
                      String movieID,
                      int bookingCapacity) {
        this.movieID = movieID;
        this.movieName = movieName;
        this.bookingCapacity = bookingCapacity;
        this.movieSlot = movieID.substring(3,4).toUpperCase();
        this.movieDate = movieID.substring(4,10).toUpperCase();
        this.movieTheatrePrefix = movieID.substring(0,3).toUpperCase();
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

    public String getMovieDate() {
        return movieDate;
    }

    public String getMovieTheatrePrefix(){
        return movieTheatrePrefix;
    }

    public String addingMovieSeats(int noOfSeats) {
        this.bookingCapacity+=noOfSeats;
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
