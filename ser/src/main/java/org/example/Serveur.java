package org.example;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


// Implémentation du service

// Serveur RMI
public class Serveur {
    public static void main(String[] args) {
        try {
            RemoteService service = new RemoteServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099); // Création du registre RMI sur le port 1099
            registry.rebind("RemoteService", service);
            System.out.println("Serveur RMI en attente de connexions...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}