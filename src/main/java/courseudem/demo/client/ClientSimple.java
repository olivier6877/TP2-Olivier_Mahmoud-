package courseudem.demo.client;

import courseudem.demo.server.models.Course;
import courseudem.demo.server.models.RegistrationForm;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static courseudem.demo.client.ClientUtils.*;

public class ClientSimple {

    final static String line ="-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*";
    public static void main(String[] args) {

        coursOffert = new ArrayList<>();
        commencer();

    }
    final static int PORT = 1552;
    private static List<Course> coursOffert;
    static Socket client;
    private static ObjectOutputStream sortie;
    private static ObjectInputStream entree;



    public static String choisirSession() {
        System.out.println(line);
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
        System.out.println("1.\tAutomne");
        System.out.println("2.\tHiver");
        System.out.println("3.\tEte");
        System.out.println(line);
        Scanner lire = new Scanner(System.in);
        System.out.print("> choix:");
        int choix = Integer.parseInt(lire.nextLine());
        while (choix > 3 || choix < 1) {
            System.out.println("Entrer invalide, choisisser un nombre entre 1 et 3 (inclus):");
            System.out.print("> choix:");
            choix = Integer.parseInt(lire.nextLine());
        }
        switch (choix) {
            case 1:
                return "Automne";
            case 2:
                return "Hiver";
            case 3:
                return "Ete";
            default:
                return null;
        }
    }




    public static void afficherListeCours() {
        String session = choisirSession();
        coursOffert = chargerCoursOffers(session);
        System.out.println(line);
        int counter = 1;
        for (Course course : coursOffert) {
            System.out.printf("%d.\t%s\t%s\n", counter++, course.getCode(), course.getName());
        }
        System.out.println(line);
    }

    public static Course trouverCourse(String code) {
        for (Course course : coursOffert) {
            if (course.getCode().equalsIgnoreCase(code))
                return course;
        }
        return null;
    }

    public static void getUserChoice() {
        Scanner lire = new Scanner(System.in);
        System.out.println("1. consulter les cours offerts pour une autre session");
        System.out.println("2. S'inscrire a un cours");
        System.out.println("> Choix: ");
        int choix = Integer.parseInt(lire.nextLine());
        while (choix > 2 || choix < 1) {
            System.out.println("Entrer invalide, choisisser un nombre entre 1 et 2 :");
            System.out.print("> choix:");
            choix = Integer.parseInt(lire.nextLine());
        }
        switch (choix) {
            case 1:
                afficherListeCours();
                break;
            case 2:
                RegistrationForm formulaire = remplirFormulaire();
                envoyerFormulaire(formulaire);
            default:
                break;
        }
    }

    public static void commencer() {
        System.out.println("*** Bienvenu au portail d'inscription de cours a l'UDEM ***");
        afficherListeCours();
        while (true) {
            getUserChoice();
        }
    }



    public static RegistrationForm remplirFormulaire() {

        String prenom = lireString("prenom");
        String nom = lireString("nom");
        String email = lireString("email");
        if(!validerEmail(email)) {
            System.out.println("email invalide!");
            return null;
        }
        String matricule = lireString("matricule");
        if(validerMatricule(matricule)){
            System.out.println("matricule invalide!");
            return null;
        }
        String codeCours = lireString("code du cours");
        Course course = trouverCourse(codeCours);
        if (course == null) {
            return null;
        }
        return new RegistrationForm(prenom, nom, email, matricule, course);
    }


    private static String lireString(String demande) {
        Scanner lire = new Scanner(System.in);
        System.out.println("Veuillez entrez le " + demande + ":");
        return lire.nextLine();
    }
}
