package Shared.data;

public interface IUser {

    public void setUserID(String userID);

    public static void main(String[] args) {}

    public String getUserName();

    public String getUserRegisteredServerPrefix();

    public boolean isAdmin();

    public String getUserID();
    public int numberOfTicketsBooked();
}
