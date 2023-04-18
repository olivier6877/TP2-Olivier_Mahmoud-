package courseudem.demo.client;

import courseudem.demo.server.models.Course;
import courseudem.demo.server.models.RegistrationForm;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import static courseudem.demo.client.ClientUtils.*;

public class GUI extends Application {
    ObservableList<CourseFx> courses;
    TableView<CourseFx> tableauCourse;

    /***
     * Creer la partie droite du GUI (le formulaire)
     * @return la partie droite du GUI
     */
    public VBox makeRightPane() {

        // creation de la Pane
        VBox rightPane = new VBox();

        // creation du titre
        Label paneTitle = new Label("Formulaire d'inscription");
        paneTitle.setFont(Font.font("System", FontWeight.BOLD, 20));

        // Creation du formulaire
        GridPane inscriptionArguments = new GridPane();

        // argument des formulaires :
        Label prenomLabel = new Label("prenom");
        TextField prenomTextField = new TextField();
        inscriptionArguments.add(prenomLabel, 0, 1);
        inscriptionArguments.add(prenomTextField, 1, 1);

        Label nomLabel = new Label("nom");
        TextField nomTextField = new TextField();
        inscriptionArguments.add(nomLabel, 0, 2);
        inscriptionArguments.add(nomTextField, 1, 2);

        Label emailLabel = new Label("email");
        TextField emailTextField = new TextField();
        inscriptionArguments.add(emailLabel, 0, 3);
        inscriptionArguments.add(emailTextField, 1, 3);

        Label matriculeLabel = new Label("matricule");
        TextField matriculeTextFiel = new TextField();
        inscriptionArguments.add(matriculeLabel, 0, 4);
        inscriptionArguments.add(matriculeTextFiel, 1, 4);

        // Ajout d'un boutton pour envoyer
        Button sendButton = new Button("Envoyer");
        sendButton.setOnAction(event -> {

            // retrouver le cours selectionner
            Course selectedCourse = tableauCourse.getSelectionModel().getSelectedItem().toCourse();
            if (selectedCourse == null) {
                System.out.println("no selected courses! request not sent");
                return;
            }
            // Fenetre contextuelle :
            String msg = "";
            // retrouver les argument du formulaire
            String prenom = prenomTextField.getText();
            String nom = nomTextField.getText();

            String email = emailTextField.getText();
            // validation du courriel
            if(!validerEmail(email)){
                msg += "Email n'est pas conforme!\n";
                emailTextField.setStyle("-fx-border-color: red;");
            }else emailTextField.setStyle("");

            String matricule = matriculeTextFiel.getText();
            // validation de la matricule
            if (!validerMatricule(matricule)) {
                msg += "matricule n'est pas conforme";
                matriculeTextFiel.setStyle("-fx-border-color: red");
            } else matriculeTextFiel.setStyle("");
            if (!msg.isEmpty()) {
                creerFenetreContextuelle("Erreur de saisie",msg);
                return;
            }

            // envoie
            RegistrationForm form = new RegistrationForm(prenom, nom, email, matricule, selectedCourse);
            // confirmation
            envoyerFormulaire(form);
            creerFenetreContextuelle("Confirmation", "l'inscription est confirmer!");
        });
        // ajouter des espaces pour l'esthetique
        inscriptionArguments.setVgap(10);
        VBox.setMargin(inscriptionArguments, new Insets(0, 0, 10, 0));
        VBox.setMargin(paneTitle, new Insets(0, 0, 10, 0));

        // ajouter le tout dans la partie droite
        rightPane.getChildren().add(paneTitle);
        rightPane.getChildren().add(inscriptionArguments);
        rightPane.getChildren().add(sendButton);
        rightPane.setAlignment(Pos.TOP_CENTER);
        return rightPane;
    }

    /***
     * Creer une fenetre contextuelle pour confirmer l'inscription
     */
    public void creerFenetreContextuelle(String title, String message) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        popupStage.initModality(Modality.APPLICATION_MODAL);

        VBox popupVBox = new VBox();
        Label popupLabel = new Label(message);
        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> popupStage.close());
        closeButton.setAlignment(Pos.BOTTOM_CENTER);
        popupVBox.getChildren().addAll(popupLabel, closeButton);
        Scene popupScene = new Scene(popupVBox, 200, 100);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    /***
     * Creation de la partie gauche du GUI
     * @return la partie gauche du GUI qui contient la tables des cours et le boutton pour charger
     */
    public VBox makeLeftPane() {
        // creer la partie gauche
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.TOP_CENTER);

        // creet un titre et l'ajouter
        Label title = new Label("Liste des cours");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        VBox.setMargin(title, new Insets(0, 0, 10, 0));
        leftPane.getChildren().add(title);

        // Creation des colonnes pour le tableau
        tableauCourse = new TableView<>();
        // colonne pour le code du cours
        TableColumn<CourseFx, String> courseCode = new TableColumn<>("Code");

        // colonne pour le nom
        TableColumn<CourseFx, String> courseNom = new TableColumn<>("Nom");

        // ajout des colonne
        tableauCourse.getColumns().addAll(courseCode, courseNom);
        courseCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNom.setCellValueFactory(new PropertyValueFactory<>("name"));

        // ajout du tableau
        VBox.setMargin(tableauCourse, new Insets(0, 0, 10, 0));
        leftPane.getChildren().add(tableauCourse);

        // partie pour le chargement
        GridPane sessionPane = new GridPane();

        // menu deroulant
        ComboBox<String> sessionBox = new ComboBox<>();
        sessionBox.getItems().addAll("Hiver", "Ete", "Automne");
        sessionBox.getSelectionModel().select("Hiver");
        sessionPane.add(sessionBox, 0, 1);
        // boutton deroulant
        Button chargerButton = new Button("Charger");

        chargerButton.setOnAction(event -> {
            String session = sessionBox.getValue();

            // chargement des cours du serveur
            List<Course> selectedCourses = chargerCoursOffers(session);
            if (selectedCourses == null) {
                System.out.println("problem de chargement!");
                return;
            }

            // conversion des Cours en cours affichable
            courses = FXCollections.observableArrayList();
            for (Course course :
                    selectedCourses) {
                CourseFx courseFx = new CourseFx(course);
                courses.add(courseFx);
            }

            // afficher les donner dans le tableau
            tableauCourse.setItems(courses);
        });

        // assembler le tout
        sessionPane.add(chargerButton, 2, 1);
        sessionPane.setAlignment(Pos.TOP_CENTER);
        leftPane.getChildren().add(sessionPane);
        return leftPane;
    }

    /***
     * Cration de la fenetre principale
     * @param primaryStage default
     */
    @Override
    public void start(Stage primaryStage) {

        SplitPane splitPane = new SplitPane(makeLeftPane(), makeRightPane());
        Scene scene = new Scene(splitPane, 600, 600);

        primaryStage.setTitle("Inscription udem");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
