package Shared.data;

public interface IServerInfo {
    public void setServerIP(String serverIP);
    public void setServerName(String serverName);
    public void setServerPort(int serverPort);
    public String getServerIP();
    public String getServerName();
    public int getServerPort();
    public int getServerPortNumber(String serverPrefix);
    public String getServerNameFromPrefix(String serverPrefix);
}
