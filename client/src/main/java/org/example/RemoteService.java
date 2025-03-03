package org.example;
import java.rmi.Remote;
import java.rmi.RemoteException;

interface RemoteService extends Remote {
    String signUp(String username, String password, int port, String ip) throws RemoteException;
    String login(String username, String password, String ip) throws RemoteException;
    void ping(String username, String ip) throws RemoteException;
    String publish(String username, String filename, int size) throws RemoteException;
    String findfile(String filename) throws RemoteException;
    String getip () throws RemoteException;
}