package Shared.Database;

import Shared.data.MovieState;
import Shared.data.Util;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ICustomerBooking {
    public boolean addMovieByCustomerID(String customerID, String movieID,String movieName, int numberOfTicketsBooked) throws ParseException;

    public Map<String, MovieState> getTicketsBookedByCustomerID(String customerID);

    public int getNoOfTicketsBookedByMovieID(String customerID, String movieID,String movieName);

    public String cancelMovieByMovieID(String customerID, String movieID);
    public String cancelMovieTickets(String customerID, String movieID, String movieName);

    public String incrementBookedSeatsByMovieID(String customerID, String movieID, int noOfMoreSeats);

    public String decrementBookedSeatsByMovieID(String customerID, String movieID, int noOfSeatsReleased);
    public List<String> getAllCustomerIDs();
}
