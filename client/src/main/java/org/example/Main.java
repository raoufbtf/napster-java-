package org.example;

import java.io.*;
import java.net.*;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Adresse du serveur
    private static final int SERVER_PORT = 5000; // Port du serveur
    private static final String SAVE_DIR = "C:\\Users\\raoufbtf\\napster-java-\\client\\src\\main\\java\\org\\example\\downloads\\"; // Dossier de téléchargement

    public static void main(String[] args) {
        String fileName = "example.txt"; // Nom du fichier demandé

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connecté au serveur");

            requestFile(socket, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void requestFile(Socket socket, String fileName) {
        try (
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream())
        ) {
            // Envoyer le nom du fichier au serveur
            dos.writeUTF(fileName);

            // Lire la réponse du serveur
            String response = dis.readUTF();
            if ("OK".equals(response)) {
                receiveFile(socket, fileName);
            } else {
                System.out.println(response); // Message d'erreur si le fichier n'existe pas
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveFile(Socket socket, String fileName) {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (
                FileOutputStream fos = new FileOutputStream(SAVE_DIR + fileName);
                DataInputStream dis = new DataInputStream(socket.getInputStream())
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = dis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            System.out.println("Fichier " + fileName + " téléchargé avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
