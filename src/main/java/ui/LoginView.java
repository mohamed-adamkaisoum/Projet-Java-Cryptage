package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.AuthentificationService;
import model.Utilisateur;
import java.util.function.Consumer;

/**
 * Interface graphique de connexion
 * Permet à l'utilisateur de s'authentifier avec un nom d'utilisateur et un mot de passe
 */
public class LoginView {
    
    private Stage stage;
    private AuthentificationService authService;
    private Consumer<Utilisateur> onLoginSuccess;
    
    public LoginView(Stage stage, AuthentificationService authService, Consumer<Utilisateur> onLoginSuccess) {
        this.stage = stage;
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
    }
    
    /**
     * Affiche la vue de connexion
     */
    public void show() {
        stage.setTitle("Système de Cryptographie - Connexion");
        
        // Création des composants UI
        Label titleLabel = new Label("Système de Cryptographie des Données Utilisateur");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label usernameLabel = new Label("Nom d'utilisateur:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Entrez votre nom d'utilisateur");
        
        Label passwordLabel = new Label("Mot de passe:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");
        
        Button loginButton = new Button("Se connecter");
        loginButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px;");
        
        Label infoLabel = new Label("Utilisateurs par défaut: admin/admin123 ou user/user123");
        infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        
        // Gestionnaire d'événement pour le bouton de connexion
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", 
                    "Veuillez remplir tous les champs.");
                return;
            }
            
            Utilisateur user = authService.authenticate(username, password);
            if (user != null) {
                // Authentification réussie
                onLoginSuccess.accept(user);
            } else {
                showAlert(Alert.AlertType.ERROR, "Échec de l'authentification", 
                    "Nom d'utilisateur ou mot de passe incorrect.");
                passwordField.clear();
            }
        });
        
        // Permettre la connexion avec la touche Entrée
        passwordField.setOnAction(e -> loginButton.fire());
        
        // Mise en page
        VBox root = new VBox(15);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        root.getChildren().addAll(
            titleLabel,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            loginButton,
            infoLabel
        );
        
        // Configuration des champs
        usernameField.setPrefWidth(250);
        passwordField.setPrefWidth(250);
        
        Scene scene = new Scene(root, 400, 350);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        // Focus sur le champ nom d'utilisateur
        usernameField.requestFocus();
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

