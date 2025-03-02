package org.example;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {
    protected RemoteServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String signUp(String username, String password, int port, String ip) throws RemoteException {
        return Metier.signUp(username, password, port, ip);
    }

    @Override
    public String login(String username, String password, String ip) throws RemoteException {
        return Metier.login(username, password, ip);
    }

    @Override
    public void pong(String username, String ip) throws RemoteException {
        Metier.pong(username, ip);
    }

    @Override
    public String publish(String username, String filename, int size) throws RemoteException {
        return Metier.publish(username, filename, size);
    }

    @Override
    public String findfile(String filename) throws RemoteException {
        return Metier.findfile(filename);
    }
}
