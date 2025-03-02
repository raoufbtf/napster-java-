package org.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;


// Implémentation du service

// Serveur RMI
public class Serveur {
    public static void main(String[] args) {
        try {
            RemoteService service = new RemoteServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099,null, new RMIServerSocketFactory() {
                @Override
                public ServerSocket createServerSocket(int port) throws IOException {
                    return new ServerSocket(port, 0, InetAddress.getByName("localhost" )); // Cambia por tu IP
                }
            });
            registry.rebind("RemoteService", service);
            System.out.println("Serveur RMI en attente de connexions...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}