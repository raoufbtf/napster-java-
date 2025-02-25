package org.example;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;

public class Metier {

    private static final String URL = "jdbc:sqlite:C:\\Users\\raoufbtf\\napster-java-\\napster.db";


    public static String signUp(String username, String password, int port, String ip) {
        String insertSQL = "INSERT INTO user (username, mot_de_passe, portenv, ip, last_connexion) VALUES (?, ?, ?, ?, ?);";

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, port);
            preparedStatement.setString(4, ip);


            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            preparedStatement.setString(5, now.format(formatter));

            int rowsAffected = preparedStatement.executeUpdate();
            return "{" +
                    "reponse='" + "signup" + '\'' +
                            ", etat ='" + "true" + '\'' +
                            '}';

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'inscription : " + e.getMessage());
            return "{" +
                    "reponse='" + "signup" + '\'' +
                    ", etat ='" + "false" + '\'' +
                    '}';
        }
    }
    public static void pong(String username , String ip ) {
        String pong = "UPDATE user SET last_connexion = ?, ip= ? WHERE username = ?;";

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(pong)) {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            statement.setString(1, now.format(formatter));
            statement.setString(2, ip);
            statement.setString(3, username);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Date de dernière connexion mise à jour pour l'utilisateur : " + username);
            } else {
                System.out.println("Aucun utilisateur trouvé avec le nom : " + username);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la dernière connexion : " + e.getMessage());
        }
    }



    public static  String login(String username, String password,String ip) {
        String query = "SELECT EXISTS(SELECT 1 FROM user WHERE username = ?) AS user_exists;";
        String passwordQuery = "SELECT mot_de_passe FROM user WHERE username = ?;";
        boolean rs=false;

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement existsStatement = connection.prepareStatement(query);
             PreparedStatement passwordStatement = connection.prepareStatement(passwordQuery)) {


            existsStatement.setString(1, username);
            try (ResultSet resultSet = existsStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("user_exists") == 1) {

                    passwordStatement.setString(1, username);
                    try (ResultSet passwordResultSet = passwordStatement.executeQuery()) {
                        if (passwordResultSet.next()) {
                            String storedPassword = passwordResultSet.getString("mot_de_passe");

                              rs= storedPassword.equals(password); // Comparer les mots de passe
                        }
                    }
                }
            }
            if ( rs ==true){
                pong(username,ip);
                return "{" +
                        "reponse:'" + "login" + '\'' +
                        ", etat :'" + "true" + '\'' +
                        '}';
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
        }
        return "{" +
                "reponse:'" + "login" + '\'' +
                ", etat :'" + "false" + '\'' +
                '}';
    }
    public static String publish (String user ,String nom_fichier, int taille ){
         String  find  = "SELECT id FROM user WHERE username = ?;" ;
          String   publish = "INSERT INTO fichier (id_user, nom_fichier, taille) VALUES (?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement Statementid  = connection.prepareStatement(find);
             PreparedStatement Statementadd = connection.prepareStatement(publish)) {
            Statementid.setString(1, user);
             try (ResultSet  rs=Statementid.executeQuery();){
                  rs.next();
                  Statementadd.setInt(1, Integer.parseInt(rs.getString("id")));
                  Statementadd.setString(2, nom_fichier);
                  Statementadd.setInt(3, taille);
                  Statementadd.executeUpdate();
                 return "{" +
                         "reponse:'" + "publish"  + '\'' +
                         ", etat :'" + "true" + '\'' +
                         '}';

             }catch (SQLException e) {
                 System.err.println("Erreur lors de la connexion : " + e.getMessage());
                 return "{" +
                         "reponse:'" + "publish"  + '\'' +
                         ", etat :'" + "false" + '\'' +
                         '}';
             }



        }catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
            return "{" +
                    "reponse :'" +"publish"   + '\'' +
                    ", etat :'" + "false" + '\'' +
                    '}';
        }

    }
    public static String findfile(String nomfichier) {
        // Initialisation du résultat JSON
        StringBuilder resultat = new StringBuilder("{" +
                "reponse:'" +"findfile"   + '\'' +
                ", data : [" );


        String findfile = "SELECT json_object('taille', f.taille, 'ip', u.ip, 'portenv', u.portenv) AS json_result " +
                "FROM fichier f JOIN user u ON f.id_user = u.id WHERE f.nom_fichier = ?;";

        try (Connection connection =  DriverManager.getConnection(URL);
             PreparedStatement statementFile = connection.prepareStatement(findfile)) {


            statementFile.setString(1, nomfichier);


            try (ResultSet rs = statementFile.executeQuery()) {
                boolean first = true;


                while (rs.next()) {
                    if (!first) {
                        resultat.append(",");
                    } else {
                        first = false;
                    }


                    String jsonResult = rs.getString("json_result");

                    // Ajouter le JSON au résultat final
                    resultat.append(jsonResult);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
            return ""; // Retourner une chaîne vide en cas d'erreur
        }

        // Fermer le JSON final
        resultat.append(" ]}");

        // Retourner le résultat JSON
        return resultat.toString();
    }



}