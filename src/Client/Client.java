package Client;

import Shared.data.Movie;
import Shared.data.ServerInfo;
import Shared.data.User;

public class Client {
    public static void main(String[] args) {
        try {
            User user = new User();
            ServerInfo serverInfo = new ServerInfo();
            Movie movie = new Movie();
            FrontEnd fe = new FrontEnd(serverInfo,user,movie);

            String response = fe.login();

            System.out.println("Response "+ response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
