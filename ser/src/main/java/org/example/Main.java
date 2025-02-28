package org.example;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Adresse IP du serveur
        int serverPort = 12345; // Port du serveur

        try {
            // Connexion au serveur
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connecté au serveur " + serverAddress + ":" + serverPort);

            // Flux de sortie pour envoyer des données au serveur
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Flux d'entrée pour recevoir des données du serveur
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Exemple de message JSON pour l'inscription (signup)
            JsonObject signupJson = new JsonObject();
            signupJson.addProperty("methode", "login");
            signupJson.addProperty("username", "kask");
            signupJson.addProperty("password", "kask");

            // Envoi du message JSON au serveur
            out.println(signupJson.toString());
            System.out.println("Message envoyé au serveur: " + signupJson.toString());

            // Réception de la réponse du serveur
            String response = in.readLine();
            System.out.println("Réponse du serveur: " + response);

            // Fermeture des flux et de la socket
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}