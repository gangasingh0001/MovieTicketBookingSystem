package Shared.Database;

import Shared.data.Util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Movies implements IMovies {
    //MovieName,MovieID,booking Capacity
    private final Map<String, Map<String, Integer>> movies;

    public Movies() {
        this.movies = new ConcurrentHashMap<>();
    }

    public String addMovie(String movieName, String movieID, int bookingCapacity) {
        Map<String, Integer> slotsMap = new ConcurrentHashMap<>();
        slotsMap.put(movieID,bookingCapacity);
        this.movies.put(movieName,slotsMap);
        return "Movie Added Successfully";
    }

    public String addMovieSlot(String movieName, String movieID, int bookingCapacity) {
        this.movies.get(movieName).put(movieID, bookingCapacity);
        return "Movie slot Added Successfully";
    }

    public String updateMovieSlot(String movieName, String movieID, int bookingCapacity) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            if(slotsMap.get(movieID)!=null) {
                this.movies.get(movieName).put(movieID,bookingCapacity);
                return "Movie slot updated successfully";
            }
            this.movies.get(movieName).put(movieID,bookingCapacity);
            return "Movie slot created successfully";
        }
        return "Movie not found";
    }

    public boolean ifMovieNameExist(String movieName) {
        return this.movies.get(movieName) != null;
    }

    public boolean ifMovieIDExist(String movieName, String movieID) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            if (slotsMap.containsKey(movieID)) return true;
        }
        return false;
    }

    public List<String> getMovieSlotsByMovieName(String movieName) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            return Util.getKeyListByHashMap(this.movies.get(movieName));
        }
        return null;
    }

    public Map<String,Integer> getMovieSlotsHashMapByMovieName(String movieName) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            return slotsMap;
        }
        return null;
    }

    public List<String> getMovieSlotsAtSpecificAreaAndSpecificDate(String movieName, String areaOrServerPrefix, String date) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            return Util.getKeyListByHashMap(this.movies.get(movieName)).stream().filter(x->x.contains(areaOrServerPrefix) && x.contains(date)).toList();
        }
        return null;
    }

    public List<String> getMovieSlotsAtSpecificArea(String movieName, String areaOrServerPrefix) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            return Util.getKeyListByHashMap(this.movies.get(movieName)).stream().filter(x->x.contains(areaOrServerPrefix)).toList();
        }
        return null;
    }

    public String deleteMovieSlotByMovieNameAndMovieID(String movieName, String movieID) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            if(slotsMap.get(movieID)!=null) {
                slotsMap.remove(movieID);
                return "Movie deleted successfully";
            }
            return "Movie slot not found";
        }
        return "Movie not found";
    }

    public String incrementBookingCapacity(String movieName, String movieID, int noOfMoreSeats) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            if(slotsMap.get(movieID)!=null) {
                int bookingCapacity = slotsMap.get(movieID);
                slotsMap.put(movieID,bookingCapacity+ noOfMoreSeats);
                return "Movie booking capacity updated successfully to "+ bookingCapacity + noOfMoreSeats +" seats";
            }
            return "Movie slot not found";
        }
        return "Movie not found";
    }

    public String decrementBookingCapacity(String movieName, String movieID, int noOfSeatsOccupied) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            if(slotsMap.get(movieID)!=null) {
                int bookingCapacity = slotsMap.get(movieID);
                slotsMap.put(movieID,bookingCapacity - noOfSeatsOccupied);
                return "Movie booking capacity updated successfully to "+ (bookingCapacity - noOfSeatsOccupied) +" seats";
            }
            return "Movie slot not found";
        }
        return "Movie not found";
    }

    public String addSlot(String movieName, String movieID, int bookingCapacity) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        slotsMap.put(movieID,bookingCapacity);
        return "Slot Added Successfully";
    }

    public Integer getSlotBookingCapacity(String movieName,String movieID) {
        Map<String, Integer> slotsMap = this.movies.get(movieName);
        if(slotsMap!=null){
            if(slotsMap.get(movieID)!=null) {
                return slotsMap.get(movieID);
            }
            return -1;
        }
        return -1;
    }
}
