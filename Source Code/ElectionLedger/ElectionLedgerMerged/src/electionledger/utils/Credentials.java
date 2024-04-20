/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package electionledger.utils;

import static electionledger.utils.SecurityUtils.generateRSAKeyPair;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class Credentials implements Serializable {

    public static String USER_PATH = "users/";
    public static String MASTER_PATH = "master/";

    String name;        //username introduzido
    PrivateKey privKey; //chave privada
    PublicKey pubKey;   //chave pública
    Key key;            //chave simétrica
    
    private Credentials(String name) {
        this.name = name;
        this.privKey = null;
        this.pubKey = null;
        this.key = null;
    }

    public String getName() {
        return name;
    }

    public PrivateKey getPrivKey() {
        return privKey;
    }

    public String getPubKey() {
        return Base64.getEncoder().encodeToString(pubKey.getEncoded());
    }

    public Key getKey() {
        return key;
    }

    /**
     * 
     * Regista um utilizador "master" para ser a comissão eleitoral e
     * gera os ficheiros de chave pública, privada e chave simétrica.
     * 
     * @param password
     * @param confirmpassword
     * @throws Exception 
     */
    public static void registarMaster(String password, String confirmpassword) throws Exception {
        if (!password.equals(confirmpassword)) {
            throw new Exception("A Password não é igual!");
        }
        if ((new File(MASTER_PATH + "master" + ".pubkey").exists())) {
            throw new Exception("Utilizador já existe");
        }

        //gerar par de chaves
        KeyPair AssimetricKeys = generateRSAKeyPair(2048);
        //guardar publicKey em ficheiro .pub
        SecurityUtils.saveKey(AssimetricKeys.getPublic(), MASTER_PATH + "master" + ".pubkey");
        //instanciar chave privade encriptada com password
        byte[] encryptedPrivateKey = SecurityUtils.encrypt(AssimetricKeys.getPrivate().getEncoded(), password);
        //guarda chave privada encriptada em ficheiro .priv
        Files.write(Paths.get(MASTER_PATH + "master" + ".priv"), encryptedPrivateKey);
        //gera a chave simetrica
        Key SimetricKey = SecurityUtils.generateAESKey(256);
        byte[] encriptedSimetricKey = SecurityUtils.encrypt(SimetricKey.getEncoded(), AssimetricKeys.getPublic());
        //guarda a chave simetrica encriptada com a publica
        Files.write(Paths.get(MASTER_PATH + "master" + ".sim"), encriptedSimetricKey);
    }

    /**
     * 
     * Regista um utilizador com username e password e gera os 
     * ficheiros de chave pública, privada e chave simétrica.
     * 
     * @param username
     * @param password
     * @param confirmpassword
     * @throws Exception 
     */
    public static void registar(String username, String password, String confirmpassword) throws Exception {
        if (!password.equals(confirmpassword)) {
            throw new Exception("A Password não é igual!");
        }
        if ((new File(USER_PATH + username + ".pubkey").exists())) {
            throw new Exception("Utilizador já existe");
        }

        //gerar par de chaves
        KeyPair AssimetricKeys = generateRSAKeyPair(2048);
        //guardar publicKey em ficheiro .pub
        SecurityUtils.saveKey(AssimetricKeys.getPublic(), USER_PATH + username + ".pubkey");
        //instanciar chave privade encriptada com password
        byte[] encryptedPrivateKey = SecurityUtils.encrypt(AssimetricKeys.getPrivate().getEncoded(), password);
        //guarda chave privada encriptada em ficheiro .priv
        Files.write(Paths.get(USER_PATH + username + ".priv"), encryptedPrivateKey);
        //gera a chave simetrica
        Key SimetricKey = SecurityUtils.generateAESKey(256);
        byte[] encriptedSimetricKey = SecurityUtils.encrypt(SimetricKey.getEncoded(), AssimetricKeys.getPublic());
        //guarda a chave simetrica encriptada com a publica
        Files.write(Paths.get(USER_PATH + username + ".sim"), encriptedSimetricKey);
    }

    /**
     * 
     * Autentica um utilizador com o username e password.
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception 
     */
    public static Credentials autenticar(String username, String password) throws Exception {
        Credentials user = new Credentials(username);
        //username nao existe
        //password
        //abre a private key
        byte[] privatekeyData = Files.readAllBytes(Paths.get(USER_PATH + username + ".priv"));
        //desencripta a private com password
        PrivateKey privateKey = SecurityUtils.getPrivateKey(SecurityUtils.decrypt(privatekeyData, password));
        //abre a public key 
        byte[] publickeyData = Files.readAllBytes(Paths.get(USER_PATH + username + ".pubkey"));
        PublicKey publicKey = SecurityUtils.getPublicKey(publickeyData);
        //desencripto a simetric key com a private key
        byte[] encriptedSimetricKey = Files.readAllBytes(Paths.get(USER_PATH + username + ".sim"));
        byte[] decryptSimetric = SecurityUtils.decrypt(encriptedSimetricKey, privateKey);
        Key decryptSimetricKey = SecurityUtils.getAESKey(decryptSimetric);
        user.name = username;
        user.privKey = privateKey;
        user.pubKey = publicKey;
        user.key = decryptSimetricKey;
        return user;
    }

    /**
     * 
     * Autentica um utilizador se o username for "master"
     * 
     * @param password
     * @return
     * @throws Exception 
     */
    public static Credentials autenticarMaster(String password) throws Exception {
        Credentials user = new Credentials("master");
        //username nao existe
        //password
        //abre a private key
        byte[] privatekeyData = Files.readAllBytes(Paths.get(MASTER_PATH + "master" + ".priv"));
        //desencripta a private com password
        PrivateKey privateKey = SecurityUtils.getPrivateKey(SecurityUtils.decrypt(privatekeyData, password));
        //abre a public key 
        byte[] publickeyData = Files.readAllBytes(Paths.get(MASTER_PATH + "master" + ".pubkey"));
        PublicKey publicKey = SecurityUtils.getPublicKey(publickeyData);
        //desencripto a simetric key com a private key
        byte[] encriptedSimetricKey = Files.readAllBytes(Paths.get(MASTER_PATH + "master" + ".sim"));
        byte[] decryptSimetric = SecurityUtils.decrypt(encriptedSimetricKey, privateKey);
        Key decryptSimetricKey = SecurityUtils.getAESKey(decryptSimetric);
        user.name = "master";
        user.privKey = privateKey;
        user.pubKey = publicKey;
        user.key = decryptSimetricKey;
        return user;
    }
}
