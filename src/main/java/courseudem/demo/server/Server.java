package courseudem.demo.server;

import courseudem.demo.server.models.Course;
import courseudem.demo.server.models.RegistrationForm;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Construits un objet Server qui ecoute sur un port specifique.
     *
     * @param port numero du port a ecouter
     * @throws IOException Si une exception est levee lors de l'ouverture du port
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajoute un EventHandler au serveur
     * 
     * @param h l'eventHandler a ajouter
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Alertes tout les eventHandler d'un evenement
     * 
     * @param cmd la commande associe a l'evenement
     * @param arg les argument de la commande
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Commence le serveur et ecoute sur le port pour des connexions
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * lis les messages du client
     * 
     * @throws IOException            quand il y a erreur du input stream
     * @throws ClassNotFoundException si on ne peut pas trouver la classe qu'on
     *                                souhaite serialise
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * execute une commande recu du client
     * 
     * @param line la ligne de commande a execute
     * @return une pair contenant la commande et ses arguments
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * rompt la connexion entre le serveur et client
     * 
     * @throws IOException quand une erreur se produit lors de la deconnection
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Gère les evenement recu du client
     * 
     * @param cmd la commande associe a l'evenemnt
     * @param arg les arguments de la commande
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les
     * transofmer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en
     * utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du
     * fichier ou de l'écriture de l'objet dans le flux.
     * 
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        // Lire le fichier et transformer en une liste de cours
        final String FICHIER_COURS = "./src/main/java/courseudem/demo/server/data/cours.txt";
        List<Course> listeCours = new ArrayList<>();

        try {
            BufferedReader lire = new BufferedReader(new FileReader(FICHIER_COURS));
            String line = lire.readLine();
            while (line != null) {

                String[] attributs = line.split("\t");
                if (attributs[2].equalsIgnoreCase(arg))
                    listeCours.add(new Course(attributs[1], attributs[0], attributs[2]));
                line = lire.readLine();
            }
            lire.close();
            for (Course course : listeCours) {
                System.out.print('.');
                objectOutputStream.writeObject(course);
            }
            System.out.println();
            objectOutputStream.writeObject(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant
     * 'objectInputStream', l'enregistrer dans un fichier texte
     * et renvoyer un message de confirmation au client.
     * La méthode gére les exceptions si une erreur se produit lors de la lecture de
     * l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        final String FICHIER_INSCRIPTION = "./src/main/java/courseudem/demo/server/data/inscription.txt";
        PrintWriter ecrire;
        try {
            ecrire = new PrintWriter(new FileWriter(FICHIER_INSCRIPTION, true));
            RegistrationForm registration = (RegistrationForm) objectInputStream.readObject();
            ecrire.printf("%s\t%s\t%s\t%s\t%s\t%s\t\n",
                    registration.getCourse().getSession(),
                    registration.getCourse().getCode(),
                    registration.getMatricule(),
                    registration.getPrenom(),
                    registration.getNom(),
                    registration.getEmail());
            ecrire.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
