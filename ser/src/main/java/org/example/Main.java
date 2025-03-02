package org.example;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            RemoteService service = (RemoteService) registry.lookup("RemoteService");





             String response = service.findfile("file.txt");
            System.out.println("Réponse findfile: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}