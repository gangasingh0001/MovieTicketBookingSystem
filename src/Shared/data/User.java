package Shared.data;

public class User implements IUser{
    private String userID = null;
    private String userName = null;
    private boolean isAdmin = false;
    private String userRegisteredToServer = null;

    private int noOfTicketsBooked;

    public User() {}

    private void setPermission() {
        this.isAdmin = this.userID.substring(3, 4).equalsIgnoreCase("A");
    }

    public void setUserID(String userID) {
        this.userID = userID;
        setPermission();
        setUserRegisteredToServer();
    }

    private void setUserRegisteredToServer() {
        this.userRegisteredToServer = this.userID.substring(0,3).toUpperCase();
    }

    public static void main(String[] args) {}

    public String getUserName() {
        return userName;
    }

    public String getUserRegisteredServerPrefix() {
        return userRegisteredToServer;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getUserID() {
        return userID;
    }

    public int numberOfTicketsBooked() {
        return this.noOfTicketsBooked;
    }

    public void addTicketToUserProfile(int noOfTickets) {
        this.noOfTicketsBooked+=noOfTickets;
    }

    public String cancelTicket(int noOfTicketsToDelete, String userID) {
        if(this.userID.equals(userID)) {
            this.noOfTicketsBooked -= noOfTicketsToDelete;
            return "Success";
        }
        else
            return "Not a valid user to perform this action";
    }
}
