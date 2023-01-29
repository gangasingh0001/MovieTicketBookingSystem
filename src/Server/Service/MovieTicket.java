package Server.Controller;

import Interface.MovieTicketInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MovieTicketController extends UnicastRemoteObject implements MovieTicketInterface {
    public MovieTicketController() throws RemoteException {
        super();
    }

    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) throws RemoteException {
        return null;
    }

    public String removeMovieSlots(String movieId, String movieName) throws RemoteException {
        return null;
    }

    public String listMovieShowsAvailability(String movieName) throws RemoteException {
        return null;
    }

    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets) throws RemoteException {
        return null;
    }

    public String getBookingSchedule(String customerID) throws RemoteException {
        return null;
    }

    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws RemoteException {
        return null;
    }
}
