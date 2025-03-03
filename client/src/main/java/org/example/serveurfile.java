package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.*;

public class serveurfile {
    private static final int PORT = 5000;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String SENT_DIR = dotenv.get("SENT_DIR");

    public static void serverfile(String IP) {
        try (ServerSocket serverSocket = new ServerSocket(PORT,50, InetAddress.getByName(IP))) {
            System.out.println("Serveur en attente de connexion sur le port " + PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connecté : " + socket.getInetAddress());

                // Assurer que le dossier existe
                assert SENT_DIR != null;
                File dir = new File(SENT_DIR);
                if (!dir.exists()) dir.mkdirs();

                // Attendre et traiter la demande du client
                handleClientRequest(socket);

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket socket) {
        try (
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            // Lire le nom du fichier demandé par le client
            String fileName = dis.readUTF();
            File file = new File(SENT_DIR + fileName);

            if (file.exists() && file.isFile()) {
                dos.writeUTF("OK"); // Indique que le fichier existe
                sendFile(socket, file);
            } else {
                dos.writeUTF("ERROR: Fichier introuvable !");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(Socket socket, File file) {
        try (
                FileInputStream fis = new FileInputStream(file);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            System.out.println("Fichier " + file.getName() + " envoyé avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}