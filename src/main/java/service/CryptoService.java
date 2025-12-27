package service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoService {
    
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    
    private KeyPair keyPair;
    
    public CryptoService() {
        generateKeyPair();
    }
    
    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(KEY_SIZE, new SecureRandom());
            this.keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la génération des clés RSA", e);
        }
    }
    
    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        return cipher.doFinal(data);
    }
    
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return cipher.doFinal(encryptedData);
    }
    
    public byte[] encryptFile(byte[] fileData) throws Exception {
        int maxBlockSize = KEY_SIZE / 8 - 11;
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
    
    public byte[] decryptFile(byte[] encryptedData) throws Exception {
        int blockSize = KEY_SIZE / 8;
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
    
    public String exportPublicKey() {
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }
    
    public String exportPrivateKey() {
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }
    
    public void importPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        keyFactory.generatePublic(spec);
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(KEY_SIZE);
    }
}
