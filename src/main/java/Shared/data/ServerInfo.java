package Shared.data;

import Constant.ServerConstant;

public class ServerInfo implements IServerInfo{
    private String serverName = null;
    private String serverIP = null;
    private int serverPort = -1;

    public ServerInfo() {}

    public void setServerIP(String serverIP){
        this.serverIP = serverIP;
    }

    public void setServerName(String serverName){
        this.serverName = serverName;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerIP() {
        return serverIP;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    /**
     * This method will return port number associated to server name
     */
    public int getServerPortNumber(String serverPrefix) {
        if(serverPrefix.equalsIgnoreCase(ServerConstant.SERVER_ATWATER_PREFIX)) {
            return ServerConstant.SERVER_ATWATER_PORT;
        } else if (serverPrefix.equalsIgnoreCase(ServerConstant.SERVER_VERDUN_PREFIX)) {
            return ServerConstant.SERVER_VERDUN_PORT;
        } else if (serverPrefix.equalsIgnoreCase(ServerConstant.SERVER_OUTREMONT_PREFIX)) {
            return ServerConstant.SERVER_OUTREMONT_PORT;
        }
        return -1;
    }
}
