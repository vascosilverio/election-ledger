/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package electionledger.masterGUI;

import electionledger.blockchain.Transfer;
import electionledger.node.RemoteInterface;
import java.security.PublicKey;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.DefaultListModel;
import electionledger.utils.Credentials;
import electionledger.utils.ImageUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class gerirEleicaoPanel extends javax.swing.JPanel {

    Credentials myUser;                                 //Chaves e CC/nome do utilizador autenticado
    RemoteInterface remote;                             //Objeto remoto
    CopyOnWriteArrayList<String> candidatesInit;        //Array list concorrente de candidatos
    ConcurrentHashMap<String, PublicKey> electorsInit;  //Hashmap concorrente de CC's de eleitor e a sua public key

    public gerirEleicaoPanel(Credentials MainUser, RemoteInterface MainRemote, CopyOnWriteArrayList<String> MainCandidates, ConcurrentHashMap<String, PublicKey> MainElectors) {
        initComponents();
        myUser = MainUser;
        remote = MainRemote;
        candidatesInit = MainCandidates;
        candidatesInit.add("Voto Em Branco");
        electorsInit = MainElectors;
        manageButtons();
        printCandidates();

    }

    /**
     * Atualiza periodicamente o estado dos botões com base na fase atual.
     */
    public void manageButtons() {
        Thread thr = new Thread(
                () -> {
                    while (true) {
                        try {
                            Thread.currentThread().sleep(100);
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    switch (remote.getPhase()) {
                                        case 0: {
                                            if (remote.getBlockchain().getChain().size() == 1) {
                                                btnStartElection.setEnabled(true);
                                                btnConfirmCandidates.setEnabled(false);
                                                btnStartVotes.setEnabled(false);
                                                btnInicVotacao.setEnabled(false);
                                                btnTermVot.setEnabled(false);
                                            } else {
                                                check1.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/check.png")), 50, 50));
                                                btnStartElection.setEnabled(false);
                                                btnConfirmCandidates.setEnabled(true);
                                                btnStartVotes.setEnabled(false);
                                                btnInicVotacao.setEnabled(false);
                                                btnTermVot.setEnabled(false);
                                            }
                                            break;
                                        }
                                        case 1: {
                                            check2.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/check.png")), 50, 50));
                                            btnStartElection.setEnabled(false);
                                            btnConfirmCandidates.setEnabled(false);
                                            btnStartVotes.setEnabled(true);
                                            btnInicVotacao.setEnabled(false);
                                            btnTermVot.setEnabled(false);
                                            break;
                                        }
                                        case 2: {
                                            check3.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/check.png")), 50, 50));
                                            btnStartElection.setEnabled(false);
                                            btnConfirmCandidates.setEnabled(false);
                                            btnStartVotes.setEnabled(false);
                                            btnInicVotacao.setEnabled(true);
                                            btnTermVot.setEnabled(false);
                                            break;
                                        }
                                        case 3: {
                                            check4.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/check.png")), 50, 50));
                                            btnStartElection.setEnabled(false);
                                            btnConfirmCandidates.setEnabled(false);
                                            btnStartVotes.setEnabled(false);
                                            btnInicVotacao.setEnabled(false);
                                            btnTermVot.setEnabled(true);
                                            break;
                                        }
                                        case 4: {
                                            check5.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/check.png")), 50, 50));
                                            btnStartElection.setEnabled(false);
                                            btnConfirmCandidates.setEnabled(false);
                                            btnStartVotes.setEnabled(false);
                                            btnInicVotacao.setEnabled(false);
                                            btnTermVot.setEnabled(false);
                                            break;
                                        }
                                        default:
                                            break;

                                    }
                                } catch (Exception e) {
                                    // Lidar com exceções ao atualizar os botões
                                }

                            });
                        } catch (Exception ex) {
                            Logger.getLogger(MasterPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        );
        thr.start();
    }

    /**
     * Mostra os candidatos na interface gráfica.
     */
    public void printCandidates() {
        lstCandidates.removeAll();
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Object candidate : candidatesInit) {
            // Converte cada candidato para String antes de adicioná-lo ao modelo
            System.out.println("Candidato: " + candidate.toString());
            model.addElement(String.valueOf(candidate));
        }
        lstCandidates.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        initEl = new javax.swing.JPanel();
        btnStartElection = new javax.swing.JButton();
        numEleitores = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        check1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtAddCandidate = new javax.swing.JTextField();
        btnAddCandidate = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnConfirmCandidates = new javax.swing.JButton();
        jScrollPane13 = new javax.swing.JScrollPane();
        lstCandidates = new javax.swing.JList<>();
        check2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnStartVotes = new javax.swing.JButton();
        check3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnInicVotacao = new javax.swing.JButton();
        check4 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnTermVot = new javax.swing.JButton();
        check5 = new javax.swing.JButton();

        initEl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnStartElection.setText("Iniciar eleição");
        btnStartElection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartElectionActionPerformed(evt);
            }
        });

        jLabel1.setText("Número de Eleitores ");

        check1.setBorderPainted(false);
        check1.setContentAreaFilled(false);
        check1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout initElLayout = new javax.swing.GroupLayout(initEl);
        initEl.setLayout(initElLayout);
        initElLayout.setHorizontalGroup(
            initElLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(initElLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(initElLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(numEleitores)
                    .addComponent(btnStartElection, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
            .addGroup(initElLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        initElLayout.setVerticalGroup(
            initElLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, initElLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numEleitores, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnStartElection, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAddCandidate.setText("+");
        btnAddCandidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCandidateActionPerformed(evt);
            }
        });

        jButton2.setText("-");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnConfirmCandidates.setText("Confirmar candidatos");
        btnConfirmCandidates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmCandidatesActionPerformed(evt);
            }
        });

        jScrollPane13.setPreferredSize(new java.awt.Dimension(200, 146));

        lstCandidates.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        lstCandidates.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstCandidatesValueChanged(evt);
            }
        });
        jScrollPane13.setViewportView(lstCandidates);

        check2.setBorderPainted(false);
        check2.setContentAreaFilled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAddCandidate, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAddCandidate))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConfirmCandidates, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(check2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 214, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddCandidate, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddCandidate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConfirmCandidates, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnStartVotes.setText("Validar Eleitores");
        btnStartVotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartVotesActionPerformed(evt);
            }
        });

        check3.setBorderPainted(false);
        check3.setContentAreaFilled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStartVotes, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnStartVotes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnInicVotacao.setText("Iniciar Votação");
        btnInicVotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInicVotacaoActionPerformed(evt);
            }
        });

        check4.setBorderPainted(false);
        check4.setContentAreaFilled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnInicVotacao, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnInicVotacao, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnTermVot.setText("Terminar Votação");
        btnTermVot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTermVotActionPerformed(evt);
            }
        });

        check5.setBorderPainted(false);
        check5.setContentAreaFilled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTermVot, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTermVot, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(initEl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(initEl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartElectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartElectionActionPerformed

        try {
            if (numEleitores.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Indique o número de eleitores esperados para esta eleição.");
            } else {
                Transfer init = new Transfer(
                        "SYSTEM",
                        "MASTER",
                        Integer.valueOf(numEleitores.getText()),
                        myUser.getPrivKey());

                remote.addTransaction(init.toText());
            }

        } catch (Exception ex) {
            System.out.println("Exceção.");
        }
    }//GEN-LAST:event_btnStartElectionActionPerformed

    private void btnAddCandidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCandidateActionPerformed

        candidatesInit.add(txtAddCandidate.getText());
        printCandidates();
    }//GEN-LAST:event_btnAddCandidateActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        candidatesInit.remove(lstCandidates.getSelectedValue());
        printCandidates();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnConfirmCandidatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmCandidatesActionPerformed

        Thread thr = new Thread(
                () -> {
                    try {
                        remote.setPhase(1);
                        remote.setCandidates(candidatesInit);
                        for (String string : candidatesInit) {

                            Transfer t = new Transfer(
                                    myUser.getPubKey(),
                                    string,
                                    0,
                                    myUser.getPrivKey());
                            remote.addTransaction(t.toText());
                        }

                    } catch (Exception ex) {
                    }
                }
        );
        thr.start();
    }//GEN-LAST:event_btnConfirmCandidatesActionPerformed

    private void lstCandidatesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstCandidatesValueChanged

    }//GEN-LAST:event_lstCandidatesValueChanged

    private void btnInicVotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInicVotacaoActionPerformed

        try {
            if (remote.isMining()) {
                JOptionPane.showMessageDialog(null, "Aguarde que os miners validem os eleitores!");
            } else {
                remote.setPhase(3);
            }
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_btnInicVotacaoActionPerformed

    private void btnTermVotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTermVotActionPerformed
        Thread thr = new Thread(
                () -> {

                    try {
                        
                        if(!remote.getTransactionsList().isEmpty()){
                            remote.endBlock();
                        }
                        
                        remote.setPhase(4);
                        
                        try {
                            Transfer init = new Transfer(
                                    Base64.getEncoder().encodeToString(myUser.getPrivKey().getEncoded()),
                                    "MASTER",
                                    1,
                                    myUser.getPrivKey()
                            );
                            remote.addTransaction(init.toText());
                        } catch (Exception ex) {
                        }
                    } catch (Exception ex) {
                    }

                }
        );
        thr.start();
    }//GEN-LAST:event_btnTermVotActionPerformed

    private void btnStartVotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartVotesActionPerformed
        Thread thr = new Thread(
                () -> {
                    try {

                        remote.setPhase(2);

                        remote.setElectors(electorsInit);

                        remote.setNumElectors(electorsInit.size());

                        electorsInit.forEach((user, pubKey) -> {
                            try {
                                Transfer t = new Transfer(
                                        myUser.getPubKey(),
                                        Base64.getEncoder().encodeToString(pubKey.getEncoded()),
                                        1,
                                        myUser.getPrivKey());
                                Thread.currentThread().sleep(1000);
                                remote.addTransaction(t.toText());
                            } catch (Exception ex) {
                            }
                        });

                    } catch (Exception ex) {
                    }
                }
        );
        thr.start();

    }//GEN-LAST:event_btnStartVotesActionPerformed

    private void check1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCandidate;
    private javax.swing.JButton btnConfirmCandidates;
    private javax.swing.JButton btnInicVotacao;
    private javax.swing.JButton btnStartElection;
    private javax.swing.JButton btnStartVotes;
    private javax.swing.JButton btnTermVot;
    private javax.swing.JButton check1;
    private javax.swing.JButton check2;
    private javax.swing.JButton check3;
    private javax.swing.JButton check4;
    private javax.swing.JButton check5;
    private javax.swing.JPanel initEl;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JList<String> lstCandidates;
    private javax.swing.JTextField numEleitores;
    private javax.swing.JTextField txtAddCandidate;
    // End of variables declaration//GEN-END:variables
}
