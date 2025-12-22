package app;

import javafx.application.Application;
import javafx.stage.Stage;
import service.AuthentificationService;
import service.CryptoService;
import service.SecureStorageService;
import ui.LoginView;
import ui.MainView;
import model.Utilisateur;

/**
 * Point d'entrée principal de l'application
 * Gère l'initialisation des services et la navigation entre les vues
 */
public class MainApp extends Application {
    
    private Stage primaryStage;
    private AuthentificationService authService;
    private CryptoService cryptoService;
    private SecureStorageService storageService;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialisation des services
        authService = new AuthentificationService();
        cryptoService = new CryptoService();
        storageService = new SecureStorageService(cryptoService);
        
        // Afficher la vue de connexion
        showLoginView();
    }
    
    /**
     * Affiche la vue de connexion
     */
    private void showLoginView() {
        LoginView loginView = new LoginView(primaryStage, authService, this::onLoginSuccess);
        loginView.show();
    }
    
    /**
     * Callback appelé après une authentification réussie
     * Affiche la vue principale de l'application
     * 
     * @param currentUser L'utilisateur authentifié
     */
    private void onLoginSuccess(Utilisateur currentUser) {
        MainView mainView = new MainView(primaryStage, currentUser, cryptoService, storageService);
        mainView.show();
    }
    
    /**
     * Méthode main pour lancer l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}

