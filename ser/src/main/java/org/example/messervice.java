package org.example;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface messervice extends Remote{
    boolean signUp(String username, String password, int port, String ip) throws RemoteException;
    boolean login(String username, String password)throws RemoteException;
    void pong(String username) throws RemoteException;
    boolean publish (String user ,String nom_fichier, int taille ) throws RemoteException;
    String findfile(String nomfichier) throws RemoteException;


}

