package electionledger.commonGUI;

import electionledger.node.RemoteInterface;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import electionledger.utils.Credentials;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class infoPanel extends javax.swing.JPanel {

    Credentials myUser;                                 //Chaves e CC/nome do utilizador autenticado
    RemoteInterface remote;                             //Objeto remoto
    CopyOnWriteArrayList<String> candidatesInit;        //Array list concorrente de candidatos
    ConcurrentHashMap<String, PublicKey> electorsInit;  //Hashmap concorrente de CC's de eleitor e a sua public key

    public infoPanel(Credentials MainUser, RemoteInterface MainRemote, CopyOnWriteArrayList<String> MainCandidates, ConcurrentHashMap<String, PublicKey> MainElectors) {
        myUser = MainUser;
        remote = MainRemote;
        candidatesInit = MainCandidates;
        electorsInit = MainElectors;
        initComponents();
        displayUser();
    }

    public void displayUser() {
        txtPublicKey.setText(myUser.getPubKey());
        txtPrivateKey.setText(myUser.getPrivKey().toString());
        txtSecretKey.setText(myUser.getKey().toString());
        txtInfo.setText("Utilizador: " + myUser.getName());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtPrivateKey = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtSecretKey = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtPublicKey = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextArea();

        txtPrivateKey.setColumns(20);
        txtPrivateKey.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtPrivateKey.setLineWrap(true);
        txtPrivateKey.setRows(5);
        txtPrivateKey.setWrapStyleWord(true);
        jScrollPane7.setViewportView(txtPrivateKey);

        jTabbedPane2.addTab("Private Key", jScrollPane7);

        txtSecretKey.setColumns(20);
        txtSecretKey.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtSecretKey.setLineWrap(true);
        txtSecretKey.setRows(5);
        txtSecretKey.setWrapStyleWord(true);
        jScrollPane8.setViewportView(txtSecretKey);

        jTabbedPane2.addTab("Secret Key", jScrollPane8);

        txtPublicKey.setColumns(20);
        txtPublicKey.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtPublicKey.setLineWrap(true);
        txtPublicKey.setRows(5);
        txtPublicKey.setWrapStyleWord(true);
        jScrollPane6.setViewportView(txtPublicKey);

        jTabbedPane2.addTab("Public Key", jScrollPane6);

        txtInfo.setColumns(20);
        txtInfo.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtInfo.setLineWrap(true);
        txtInfo.setRows(5);
        txtInfo.setWrapStyleWord(true);
        jScrollPane5.setViewportView(txtInfo);

        jTabbedPane2.addTab("Info", jScrollPane5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1026, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextArea txtInfo;
    private javax.swing.JTextArea txtPrivateKey;
    private javax.swing.JTextArea txtPublicKey;
    private javax.swing.JTextArea txtSecretKey;
    // End of variables declaration//GEN-END:variables
}