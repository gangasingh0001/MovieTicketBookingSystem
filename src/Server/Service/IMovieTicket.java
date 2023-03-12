package Server.Service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * This Interface has method declaration for both admin and
 * user operations on Booking Movie Ticket Facility.
 * Declared all the business methods that can be invoked by the user/admin in this interface.
 */
@WebService
@SOAPBinding(style= SOAPBinding.Style.RPC)
public interface IMovieTicket {
    /**
     * Permission Set - Admin Only.
     * This method add movie for particular movie if exist in hash map
     * and if the movie does not exist it will create a new movie entry.
     * @param movieId
     * @param movieName
     * @param bookingCapacity
     * @return String If operation successful or not
     */
    @WebMethod()
    String addMovieSlots(String movieId, String movieName, int bookingCapacity) ;

    /**
     * Permission Set - Admin Only.
     * This method removes the movie if existed.
     * If movie show exist and customer has booked a ticket then
     * movie gets deleted and will book the same movie in next available slot for that customer.
     * @param movieId
     * @param movieName
     * @return String If operation successful or not
     */
    @WebMethod()
    String removeMovieSlots(String movieId, String movieName);

    /**
     * Permission Set - Admin Only.
     * This method shows the availability for a particular movie in all theaters.
     * @param movieName
     * @return String If operation successful or not
     */
    @WebMethod()
    String listMovieShowsAvailability(String movieName) ;

    /**
     * Permission Set - Admin and User.
     * This method is used tp book movie tickets.
     * @param customerID
     * @param movieId
     * @param movieName
     * @param numberOfTickets
     * @return String If operation successful or not
     */
    @WebMethod()
    String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets);

    /**
     * Permission Set - Admin and User.
     * This method will get the booking schedule as per customerID from all the different theaters from all area.
     * @param customerID
     * @return String If operation successful or not
     */
    @WebMethod()
    String getBookingSchedule(String customerID) ;

    /**
     * Permission Set - Admin and User.
     * This method will allow cancelling of movie ticket with following parameters.
     * @param customerID
     * @param movieID
     * @param movieName
     * @param numberOfTickets
     * @return
     */
    @WebMethod()
    String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) ;

    @WebMethod()
    public String exchangeTicket(String customerID, String movieID, String movieName, String newMovieID, String newMovieName);

    public String getMoviesListInTheatre(String movieName) ;

    public String bookTicket(String customerID, String movieId, String movieName, int numberOfTickets,boolean isUserRegisteredToServer);

    public String getCustomerBookingList(String customerID) ;

    public String cancelTicket(String customerID, String movieID, String movieName, int numberOfTickets) ;
}