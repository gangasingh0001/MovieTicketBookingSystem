package Shared;

import java.rmi.registry.Registry;

public interface RmiRegistry {
    public Registry lookUp(String remoteRefName);
    public Registry getRegistry(int port);
    public Registry createRegistry(int port);
}
