package org.example;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            RemoteService service = (RemoteService) registry.lookup("RemoteService");

            // Exemple d'utilisation des méthodes distantes
            String response = service.signUp("user1", "password123", 12345, "127.0.0.1");
            System.out.println("Réponse signUp: " + response);

            response = service.login("user1", "password123", "127.0.0.1");
            System.out.println("Réponse login: " + response);

            service.pong("user1", "127.0.0.1");
            System.out.println("Pong envoyé");

            response = service.publish("user1", "file.txt", 1024);
            System.out.println("Réponse publish: " + response);

            response = service.findfile("file.txt");
            System.out.println("Réponse findfile: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}