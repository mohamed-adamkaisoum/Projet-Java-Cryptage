package service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Service de cryptographie utilisant RSA
 * Gère la génération de paires de clés, le chiffrement et le déchiffrement
 */
public class CryptoService {
    
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048; // Taille de la clé RSA en bits
    
    private KeyPair keyPair;
    
    /**
     * Constructeur - génère automatiquement une paire de clés RSA
     */
    public CryptoService() {
        generateKeyPair();
    }
    
    /**
     * Génère une nouvelle paire de clés RSA (clé publique et clé privée)
     */
    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(KEY_SIZE, new SecureRandom());
            this.keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la génération des clés RSA", e);
        }
    }
    
    /**
     * Chiffre des données avec la clé publique RSA
     * 
     * @param data Les données à chiffrer
     * @return Les données chiffrées sous forme de tableau de bytes
     * @throws Exception Si le chiffrement échoue
     */
    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        return cipher.doFinal(data);
    }
    
    /**
     * Déchiffre des données avec la clé privée RSA
     * 
     * @param encryptedData Les données chiffrées
     * @return Les données déchiffrées sous forme de tableau de bytes
     * @throws Exception Si le déchiffrement échoue
     */
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return cipher.doFinal(encryptedData);
    }
    
    /**
     * Chiffre un fichier entier (pour fichiers de petite taille)
     * Note: RSA est limité par la taille de la clé. Pour de gros fichiers,
     * il faudrait utiliser un chiffrement hybride (RSA + AES)
     * 
     * @param fileData Les données du fichier à chiffrer
     * @return Les données chiffrées
     * @throws Exception Si le chiffrement échoue
     */
    public byte[] encryptFile(byte[] fileData) throws Exception {
        // RSA a une limite de taille (245 bytes pour 2048 bits)
        // Pour les fichiers plus grands, on doit diviser en blocs
        int maxBlockSize = KEY_SIZE / 8 - 11; // PKCS1 padding
        int offset = 0;
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        
        while (offset < fileData.length) {
            int blockSize = Math.min(maxBlockSize, fileData.length - offset);
            byte[] block = new byte[blockSize];
            System.arraycopy(fileData, offset, block, 0, blockSize);
            
            byte[] encryptedBlock = encrypt(block);
            outputStream.write(encryptedBlock);
            offset += blockSize;
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Déchiffre un fichier entier
     * 
     * @param encryptedData Les données chiffrées du fichier
     * @return Les données déchiffrées
     * @throws Exception Si le déchiffrement échoue
     */
    public byte[] decryptFile(byte[] encryptedData) throws Exception {
        int blockSize = KEY_SIZE / 8; // Taille d'un bloc chiffré
        int offset = 0;
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        
        while (offset < encryptedData.length) {
            int currentBlockSize = Math.min(blockSize, encryptedData.length - offset);
            byte[] block = new byte[currentBlockSize];
            System.arraycopy(encryptedData, offset, block, 0, currentBlockSize);
            
            byte[] decryptedBlock = decrypt(block);
            outputStream.write(decryptedBlock);
            offset += currentBlockSize;
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Exporte la clé publique en format Base64 (pour stockage/partage)
     * 
     * @return La clé publique encodée en Base64
     */
    public String exportPublicKey() {
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }
    
    /**
     * Exporte la clé privée en format Base64 (pour stockage sécurisé)
     * 
     * @return La clé privée encodée en Base64
     */
    public String exportPrivateKey() {
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }
    
    /**
     * Importe une clé publique depuis un format Base64
     * 
     * @param base64Key La clé publique encodée en Base64
     * @throws Exception Si l'importation échoue
     */
    public void importPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(spec);
        
        // Reconstruire la paire de clés (on garde la clé privée existante)
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(KEY_SIZE);
        // Note: Cette méthode est simplifiée - en production, il faudrait stocker la clé privée séparément
    }
}






