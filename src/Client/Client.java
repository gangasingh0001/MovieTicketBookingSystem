package Client;

import Constant.ServerConstant;
import Log.ILogging;
import Log.Logging;
import Shared.data.Movie;
import Shared.data.User;
import Shared.data.Util;

import java.util.logging.Logger;

public class Client {
    public static void main(String[] args) {
        try {
            User user = new User();
            Movie movie = new Movie();
            FrontEnd fe = new FrontEnd(user,movie,args);

            fe.login();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
