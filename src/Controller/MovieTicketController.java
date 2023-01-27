package Controller;

import Interface.MovieTicketInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MovieTicketController extends UnicastRemoteObject implements MovieTicketInterface {
    public MovieTicketController() throws RemoteException {
        super();
    }

    @Override
    public String addMovieSlots(String movieId, String movieName, Integer bookingCapacity) throws RemoteException {

        return null;
    }

    @Override
    public String removeMovieSlots(String movieId, String movieName) throws RemoteException {
        return null;
    }

    @Override
    public String listMovieShowsAvailability(String movieName) throws RemoteException {
        return null;
    }

    @Override
    public String bookMovieTickets(String customerID, String movieId, String movieName, String numberOfTickets) throws RemoteException {
        return null;
    }

    @Override
    public String getBookingSchedule(String customerID) throws RemoteException {
        return null;
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, Integer numberOfTickets) throws RemoteException {
        return null;
    }
}
