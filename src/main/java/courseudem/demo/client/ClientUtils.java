package courseudem.demo.client;

import courseudem.demo.server.models.Course;
import courseudem.demo.server.models.RegistrationForm;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientUtils {
    static Socket client;
    private static ObjectOutputStream sortie;
    private static ObjectInputStream entree;
    final static int PORT = 1552;
    public static void connect() throws IOException{
        client = new Socket("localhost", PORT);
        sortie = new ObjectOutputStream(client.getOutputStream());
        entree = new ObjectInputStream(client.getInputStream());
    }
    public static List<Course> chargerCoursOffers(String session){
        List<Course> coursOffert = new ArrayList<>();
        try {
            connect();
            sortie.writeObject("CHARGER " + session);
            Object object;
            while ((object = entree.readObject()) != null ) {
                coursOffert.add((Course) object);
            }
            return coursOffert;
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void envoyerFormulaire(RegistrationForm formulaire)  {

        if (formulaire == null) {
            System.out
                    .println("le cours n'est pas dans la liste de cours offert pour cette session, veuillez reessayer");
            return;
        }
        try {
            connect();
            sortie.writeObject("INSCRIRE");
            sortie.writeObject(formulaire);
            System.out.println("Inscription confirmer!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean validerMatricule(String matricule) {
        return matricule != null && matricule.matches("\\d{6}");
    }

    public static boolean validerEmail(String email){
        return email.contains("@") && ( email.endsWith(".ca") || email.endsWith(".com") ) ;
    }
}
