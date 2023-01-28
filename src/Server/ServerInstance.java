package Server;

import Constant.ServerConstant;

public class ServerInstance {
    public ServerInstance() {}

    public static void main(String[] args) {
        try {
            Server Atwater = new Server(ServerConstant.SERVER_ATWATER_PREFIX);
            Atwater.getServerInfo();
            Atwater.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Server Verdun = new Server(ServerConstant.SERVER_VERDUN_PREFIX);
            Verdun.getServerInfo();
            Verdun.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Server Outremont = new Server(ServerConstant.SERVER_OUTREMONT_PREFIX);
            Outremont.getServerInfo();
            Outremont.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
