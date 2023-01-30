package Shared.data;

public interface IUdp {
    public String sendUDPMessage(int serverPort, String methodToInvoke, String customerID, String movieName, String movieID, int noOfTickets);
}
