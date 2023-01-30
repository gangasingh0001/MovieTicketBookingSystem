package Shared.Database;

import Shared.data.Util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface IMovies {
    public String addMovie(String movieName, String movieID, int numberOfTicketsBooked);
    public String addMovieSlot(String movieName, String movieID, int bookingCapacity);
    public boolean ifMovieNameExist(String movieName);
    public String updateMovieSlot(String movieName, String movieID, int bookingCapacity);
    public List<String> getMovieSlotsAtSpecificAreaAndSpecificDate(String movieName, String areaOrServerPrefix, String date);
    public List<String> getMovieSlotsAtSpecificArea(String movieName, String areaOrServerPrefix);
    public Map<String,Integer> getMovieSlotsHashMapByMovieName(String movieName);
    public List<String> getMovieSlotsByMovieName(String movieName);
    public boolean ifMovieIDExist(String movieName, String movieID);

    public String deleteMovieSlotByMovieNameAndMovieID(String movieName, String movieID);

    public String incrementBookingCapacity(String movieName, String movieID, int noOfMoreSeats);

    public String decrementBookingCapacity(String movieName, String movieID, int noOfSeatsOccupied);

    public String addSlot(String movieName, String movieID, int bookingCapacity);

    public Integer getSlotBookingCapacity(String movieName,String movieID);
}
