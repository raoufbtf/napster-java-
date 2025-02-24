package org.example;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.RemoteException;



public class MonServiceMetierImpl extends UnicastRemoteObject implements messervice{
    protected MonServiceMetierImpl() throws RemoteException {
        super();
    }
    @Override
     public boolean publish (String user ,String nom_fichier, int taille ){
        return Metier.publish(user,nom_fichier,taille);

    }
    @Override
    public void pong(String username){
        Metier.pong(username);
    }
    @Override
    public boolean login(String username, String password){
        return Metier.login(username,password);
    }
    @Override
    public boolean signUp(String username, String password, int port, String ip){
        return Metier.signUp(username,password,port,ip);
    }
    @Override
     public String findfile(String nomfichier) {
        return Metier.findfile(nomfichier);
    }
}
