package Shared.Database;

import Shared.data.MovieState;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ICustomerBooking {
    public boolean addMovieByCustomerID(String customerID, String movieID,String movieName, int numberOfTicketsBooked) throws ParseException;

    public Map<String, MovieState> getTicketsBookedByCustomerID(String customerID);

    public int getNoOfTicketsBookedByMovieID(String customerID, String movieID,String movieName);

    public String cancelMovieByMovieID(String customerID, String movieID, String movieName);
    public String cancelMovieTickets(String customerID, String movieID, String movieName);
    public List<String> getAllCustomerIDs();
    public int noOfMoviesBookedInAWeek(String customerID, String movieID);
    public boolean ifMovieBookingExist(String customerID, String movieID, String movieName);
}
