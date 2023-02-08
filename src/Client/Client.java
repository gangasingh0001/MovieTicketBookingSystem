package Client;

import Shared.data.Movie;
import Shared.data.User;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            User user = new User();
            Movie movie = new Movie();
            FrontEnd fe = new FrontEnd(user, movie, args);

            Scanner sn = new Scanner(System.in);
            System.out.println("Press 1 for concurrency test mode or Press 2 for application mode");
            int option = sn.nextInt();
            if(option==1) fe.test();
            else fe.login();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
