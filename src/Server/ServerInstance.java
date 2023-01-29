package Server;

import Constant.ServerConstant;
import Shared.data.*;

public class ServerInstance {
    public ServerInstance() {}
    public static void main(String[] args) {
        IServerInfo serverInfo;
        IUdp udpService;
        IMovie movieService;
        try {
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            serverInfo.setServerName(ServerConstant.SERVER_ATWATER_PREFIX);
            Server Atwater = new Server(ServerConstant.SERVER_ATWATER_PREFIX,serverInfo,udpService,movieService);
            Atwater.getServerInfo();
            Atwater.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            serverInfo.setServerName(ServerConstant.SERVER_VERDUN_PREFIX);
            Server Verdun = new Server(ServerConstant.SERVER_VERDUN_PREFIX,serverInfo,udpService,movieService);
            Verdun.getServerInfo();
            Verdun.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            serverInfo = new ServerInfo();
            udpService = new Udp();
            movieService = new Movie();
            serverInfo.setServerName(ServerConstant.SERVER_OUTREMONT_PREFIX);
            Server Outremont = new Server(ServerConstant.SERVER_OUTREMONT_PREFIX,serverInfo,udpService,movieService);
            Outremont.getServerInfo();
            Outremont.runServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
