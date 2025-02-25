package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;

public class Serveurtcp {
    public static void main(String[] args) {
        String adresseIP = "localhost"; // Adresse IP sur laquelle le serveur doit écouter
        int port = 12345; // Port sur lequel le serveur écoute

        try {
            // Crée un ServerSocket en spécifiant l'adresse IP et le port
            InetAddress adresse = InetAddress.getByName(adresseIP);
            ServerSocket serverSocket = new ServerSocket(port, 50, adresse); // 50 est la taille de la file d'attente

            System.out.println("Serveur en attente de connexions sur " + adresseIP + ":" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accepte la connexion du client
                System.out.println("Client connecté: " + clientSocket.getInetAddress());

                // Crée un nouveau thread pour gérer la connexion du client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String messageClient = in.readLine(); // Lit le message du client

            JsonObject jsonObject = new JsonParser().parse( messageClient).getAsJsonObject();
            switch (jsonObject.get("methode").getAsString()){
                case "singup":{
                    out.println(Metier.signUp(jsonObject.get("username").getAsString(),
                            jsonObject.get("password").getAsString(),
                            jsonObject.get("methode").getAsInt(),clientSocket.getInetAddress().toString()));
                    break;
                }
                case "login":{
                    out.println(Metier.login(jsonObject.get("username").getAsString(),
                            jsonObject.get("password").getAsString()));
                    break;
                }
                case "ping":{
                    Metier.pong(jsonObject.get("username").getAsString());
                    break;
                }
                case "publish":{
                    out.println(Metier.publish(jsonObject.get("username").getAsString(),
                            jsonObject.get("nom_fichier").getAsString(),
                            jsonObject.get("taille").getAsInt()));
                    break;
                }
                case "findfile":{
                    out.println(Metier.findfile(jsonObject.get("nom_fichier").getAsString()));
                    break;
                }
                default:{
                    out.println("error   interpretation");
                }

            }




            // Ferme les flux et la socket
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}