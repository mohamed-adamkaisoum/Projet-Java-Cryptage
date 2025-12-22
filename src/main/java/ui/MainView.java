package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.CryptoService;
import service.SecureStorageService;
import model.SecureFile;
import model.Utilisateur;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Interface graphique principale de l'application
 * Permet de gérer les fichiers chiffrés (sélection, chiffrement, stockage, déchiffrement)
 */
public class MainView {
    
    private Stage stage;
    private Utilisateur currentUser;
    private CryptoService cryptoService;
    private SecureStorageService storageService;
    private FileChooser fileChooser;
    
    private ListView<SecureFile> fileListView;
    private Label statusLabel;
    private Label userLabel;
    
    public MainView(Stage stage, Utilisateur currentUser, CryptoService cryptoService, SecureStorageService storageService) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.cryptoService = cryptoService;
        this.storageService = storageService;
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Sélectionner un fichier");
    }
    
    /**
     * Affiche la vue principale de l'application
     */
    public void show() {
        stage.setTitle("Système de Cryptographie - Application principale");
        
        // En-tête avec informations utilisateur
        userLabel = new Label("Utilisateur connecté: " + currentUser.getUsername());
        userLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Button logoutButton = new Button("Déconnexion");
        logoutButton.setOnAction(e -> {
            stage.close();
            Platform.exit();
        });
        
        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(15));
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(userLabel, logoutButton);
        headerBox.setStyle("-fx-background-color: #e0e0e0;");
        
        // Zone centrale avec liste des fichiers
        Label fileListLabel = new Label("Mes fichiers chiffrés:");
        fileListLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        fileListView = new ListView<>();
        fileListView.setPrefHeight(300);
        fileListView.setCellFactory(param -> new ListCell<SecureFile>() {
            @Override
            protected void updateItem(SecureFile file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                } else {
                    setText(file.toString());
                }
            }
        });
        
        refreshFileList();
        
        // Zone de boutons d'action
        Button selectFileButton = new Button("Sélectionner un fichier");
        selectFileButton.setPrefWidth(180);
        selectFileButton.setStyle("-fx-font-size: 12px;");
        
        Button encryptButton = new Button("Chiffrer & Stocker");
        encryptButton.setPrefWidth(180);
        encryptButton.setStyle("-fx-font-size: 12px;");
        
        Button listFilesButton = new Button("Actualiser la liste");
        listFilesButton.setPrefWidth(180);
        listFilesButton.setStyle("-fx-font-size: 12px;");
        
        Button decryptButton = new Button("Déchiffrer & Ouvrir");
        decryptButton.setPrefWidth(180);
        decryptButton.setStyle("-fx-font-size: 12px;");
        
        // Variables pour stocker le fichier sélectionné
        File[] selectedFile = {null};
        
        // Gestionnaire pour sélectionner un fichier
        selectFileButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                selectedFile[0] = file;
                statusLabel.setText("Fichier sélectionné: " + file.getName());
            }
        });
        
        // Gestionnaire pour chiffrer et stocker
        encryptButton.setOnAction(e -> {
            if (selectedFile[0] == null) {
                showAlert(Alert.AlertType.WARNING, "Aucun fichier sélectionné", 
                    "Veuillez d'abord sélectionner un fichier.");
                return;
            }
            
            try {
                SecureFile secureFile = storageService.encryptAndStore(
                    selectedFile[0], 
                    currentUser.getUsername()
                );
                
                if (secureFile != null) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "Fichier chiffré et stocké avec succès: " + secureFile.getFileName());
                    refreshFileList();
                    selectedFile[0] = null;
                    statusLabel.setText("Prêt");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Échec du chiffrement et du stockage du fichier.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Erreur lors du chiffrement: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Gestionnaire pour actualiser la liste
        listFilesButton.setOnAction(e -> refreshFileList());
        
        // Gestionnaire pour déchiffrer et ouvrir
        decryptButton.setOnAction(e -> {
            SecureFile selectedSecureFile = fileListView.getSelectionModel().getSelectedItem();
            
            if (selectedSecureFile == null) {
                showAlert(Alert.AlertType.WARNING, "Aucun fichier sélectionné", 
                    "Veuillez sélectionner un fichier dans la liste.");
                return;
            }
            
            // Vérifier que le fichier appartient à l'utilisateur connecté
            if (!selectedSecureFile.getOwnerUsername().equals(currentUser.getUsername())) {
                showAlert(Alert.AlertType.ERROR, "Accès refusé", 
                    "Vous n'avez pas accès à ce fichier.");
                return;
            }
            
            // Demander où sauvegarder le fichier déchiffré
            FileChooser saveChooser = new FileChooser();
            saveChooser.setTitle("Enregistrer le fichier déchiffré");
            saveChooser.setInitialFileName(selectedSecureFile.getFileName());
            File saveFile = saveChooser.showSaveDialog(stage);
            
            if (saveFile != null) {
                try {
                    Path outputPath = saveFile.toPath();
                    boolean success = storageService.decryptAndRetrieve(selectedSecureFile, outputPath);
                    
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Succès", 
                            "Fichier déchiffré et sauvegardé: " + outputPath.getFileName());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", 
                            "Échec du déchiffrement du fichier.");
                    }
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Erreur lors du déchiffrement: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        // Mise en page des boutons
        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(
            selectFileButton,
            encryptButton,
            listFilesButton,
            decryptButton
        );
        
        // Zone de contenu principal
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        contentBox.getChildren().addAll(fileListLabel, fileListView);
        
        // Barre de statut
        statusLabel = new Label("Prêt");
        statusLabel.setPadding(new Insets(10));
        statusLabel.setStyle("-fx-background-color: #f0f0f0;");
        
        // Mise en page principale
        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(contentBox);
        root.setRight(buttonBox);
        root.setBottom(statusLabel);
        
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Actualise la liste des fichiers de l'utilisateur connecté
     */
    private void refreshFileList() {
        List<SecureFile> userFiles = storageService.getUserFiles(currentUser.getUsername());
        fileListView.getItems().clear();
        fileListView.getItems().addAll(userFiles);
        
        if (statusLabel != null) {
            statusLabel.setText("Fichiers chargés: " + userFiles.size());
        }
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






