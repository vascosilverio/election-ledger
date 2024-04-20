package electionledger.mainGUI;

import electionledger.masterGUI.MasterPanel;
import electionledger.node.AutomaticP2P;
import electionledger.electorGUI.ElectorPanel;
import electionledger.node.RemoteInterface;
import java.awt.Color;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import electionledger.utils.Credentials;
import electionledger.utils.ImageUtils;
import electionledger.utils.RMI;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.rmi.RemoteException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class Authentication extends javax.swing.JFrame {

    public static RemoteInterface remote; //Objeto remoto
    MasterPanel masterPanel = null;

    int xDrag;
    int yDrag;
    int xPress;
    int yPress;

    public Authentication() {
        setLocationRelativeTo(null);
        setUndecorated(true);
        initComponents();
        connectToNetwork();
        windowDesign();
        refreshFaseLabel();
        loginPanel.setPreferredSize(new Dimension(490, 375));
        registoPanel.setPreferredSize(new Dimension(490, 375));
        loginPanel.setVisible(false);
        registoPanel.setVisible(false);
    }

    /**
     * Conecta-se à rede, configura o design da janela e atualiza dinamicamente
     * o rótulo da fase.
     */
    public void connectToNetwork() {

        try {
            remote = (RemoteInterface) RMI.getRemote(AutomaticP2P.getNodes());
            AutomaticP2P.setConnectedNode(remote);
            System.out.println("Ligado ao servidor: " + remote.getAdress());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao ligar ao servidor");
            System.exit(1);
        }
    }

    private void nodeDown() {
        JOptionPane optionPane = new JOptionPane(
                "A ligação ao servidor foi perdida. Volte a entrar na aplicação.",
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.OK_OPTION,
                null,
                new Object[]{},
                null);

        // Criar um JDialog para envolver o JOptionPane
        JDialog dialog = optionPane.createDialog(this, "Erro de ligação.");

        this.dispose();
        // Mostrar o JDialog
        dialog.setVisible(true);
    }

    /**
     * Configura o design da janela.
     */
    public void windowDesign() {
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
        // Adiciona um Listener que permite arrastar a janela
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                xDrag = e.getX();
                yDrag = e.getY();

                JFrame sFrame = (JFrame) e.getSource();
                sFrame.setLocation(sFrame.getLocation().x + xDrag - xPress,
                        sFrame.getLocation().y + yDrag - yPress);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                xPress = e.getX();
                yPress = e.getY();
            }
        });
    }

    /**
     * Retorna a representação da fase atual em texto.
     *
     * @return String representando a fase atual
     * @throws RemoteException Lança exceção em caso de erro no objeto remoto
     */
    public String faseType() throws RemoteException {
        switch (remote.getPhase()) {
            case 0:
                return "Registo de Candidatos";
            case 1:
                return "Registo de Eleitores";
            case 2:
                return "Validação de Eleitores";
            case 3:
                return "Votação";
            case 4:
                return "Eleição Terminada";
            default:
                return "";
        }
    }

    /**
     * Atualiza dinamicamente o rótulo da fase na interface gráfica.
     */
    public void refreshFaseLabel() {
        Thread thr = new Thread(
                () -> {
                    while (true) {
                        try {
                            Thread.currentThread().sleep(500);
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    faseLabel.setText("Fase Atual: " + Integer.toString(remote.getPhase()) + " - " + faseType());
                                } catch (Exception e) {
                                    // Trata exceções, se ocorrerem
                                }

                            });

                        } catch (Exception ex) {
                            Logger.getLogger(MasterPanel.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        );
        thr.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Minimizar = new javax.swing.JButton();
        Fechar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        faseLabel = new javax.swing.JLabel();
        loginPanel = new javax.swing.JPanel();
        btnAutenticar = new javax.swing.JButton();
        txtCC = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        registoPanel = new javax.swing.JPanel();
        btnRegistar = new javax.swing.JButton();
        registoCC = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        regPassword = new javax.swing.JPasswordField();
        regConfPassword = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        changePaneLogin = new javax.swing.JButton();
        changePaneRegistar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 204, 204));

        Minimizar.setForeground(new java.awt.Color(255, 255, 255));
        Minimizar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/minimize.png")),50,50));
        Minimizar.setBorder(null);
        Minimizar.setBorderPainted(false);
        Minimizar.setContentAreaFilled(false);
        Minimizar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Minimizar.setMargin(new java.awt.Insets(1, 1, 1, 1));
        Minimizar.setMaximumSize(new java.awt.Dimension(10, 10));
        Minimizar.setMinimumSize(new java.awt.Dimension(10, 10));
        Minimizar.setPreferredSize(new java.awt.Dimension(50, 50));
        Minimizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MinimizarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                MinimizarMouseReleased(evt);
            }
        });
        Minimizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MinimizarActionPerformed(evt);
            }
        });

        Fechar.setForeground(new java.awt.Color(255, 255, 255));
        Fechar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/close.png")),50,50));
        Fechar.setBorder(null);
        Fechar.setBorderPainted(false);
        Fechar.setContentAreaFilled(false);
        Fechar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Fechar.setMargin(new java.awt.Insets(1, 1, 1, 1));
        Fechar.setMaximumSize(new java.awt.Dimension(10, 10));
        Fechar.setMinimumSize(new java.awt.Dimension(10, 10));
        Fechar.setPreferredSize(new java.awt.Dimension(50, 50));
        Fechar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                FecharMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                FecharMouseReleased(evt);
            }
        });
        Fechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FecharActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N
        jLabel1.setText("Bem-Vindo");

        faseLabel.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(faseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Minimizar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Fechar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(faseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Fechar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Minimizar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        loginPanel.setBackground(new java.awt.Color(51, 204, 204));
        loginPanel.setMaximumSize(new java.awt.Dimension(490, 375));

        btnAutenticar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/add.png")),50,50));
        btnAutenticar.setBackground(new java.awt.Color(0, 0, 0));
        btnAutenticar.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        btnAutenticar.setText("Confirmar Login");
        btnAutenticar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        btnAutenticar.setContentAreaFilled(false);
        btnAutenticar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnAutenticar.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnAutenticar.setMaximumSize(new java.awt.Dimension(10, 10));
        btnAutenticar.setMinimumSize(new java.awt.Dimension(10, 10));
        btnAutenticar.setPreferredSize(new java.awt.Dimension(50, 50));
        btnAutenticar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnAutenticarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAutenticarMouseReleased(evt);
            }
        });
        btnAutenticar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAutenticarActionPerformed(evt);
            }
        });

        txtCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCCActionPerformed(evt);
            }
        });

        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });

        jLabel2.setText("Nº Cartão Cidadão");

        jLabel3.setText("Introduza a sua Password");

        javax.swing.GroupLayout loginPanelLayout = new javax.swing.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAutenticar, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(txtCC)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(159, Short.MAX_VALUE))
        );
        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCC, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAutenticar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        registoPanel.setBackground(new java.awt.Color(51, 204, 204));
        registoPanel.setMaximumSize(new java.awt.Dimension(353, 375));

        btnRegistar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/add.png")),50,50));
        btnRegistar.setBackground(new java.awt.Color(0, 0, 0));
        btnRegistar.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        btnRegistar.setText("Confirmar Registo");
        btnRegistar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        btnRegistar.setContentAreaFilled(false);
        btnRegistar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRegistar.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnRegistar.setMaximumSize(new java.awt.Dimension(10, 10));
        btnRegistar.setMinimumSize(new java.awt.Dimension(10, 10));
        btnRegistar.setPreferredSize(new java.awt.Dimension(50, 50));
        btnRegistar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnRegistarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnRegistarMouseReleased(evt);
            }
        });
        btnRegistar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistarActionPerformed(evt);
            }
        });

        registoCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registoCCActionPerformed(evt);
            }
        });

        jLabel5.setText("Escolha a sua Password");

        jLabel4.setText("Nº Cartão Cidadão");

        regPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regPasswordActionPerformed(evt);
            }
        });

        regConfPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regConfPasswordActionPerformed(evt);
            }
        });

        jLabel6.setText("Confirmar Password");

        javax.swing.GroupLayout registoPanelLayout = new javax.swing.GroupLayout(registoPanel);
        registoPanel.setLayout(registoPanelLayout);
        registoPanelLayout.setHorizontalGroup(
            registoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registoPanelLayout.createSequentialGroup()
                .addGap(127, 127, 127)
                .addGroup(registoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(regConfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(registoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(registoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(registoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel4)
                                .addComponent(registoCC)
                                .addComponent(regPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(btnRegistar, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        registoPanelLayout.setVerticalGroup(
            registoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registoPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registoCC, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regConfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(btnRegistar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        changePaneLogin.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        changePaneLogin.setText("Login");
        changePaneLogin.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        changePaneLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        changePaneLogin.setMargin(new java.awt.Insets(1, 1, 1, 1));
        changePaneLogin.setMaximumSize(new java.awt.Dimension(10, 10));
        changePaneLogin.setMinimumSize(new java.awt.Dimension(489, 51));
        changePaneLogin.setPreferredSize(new java.awt.Dimension(50, 50));
        changePaneLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                changePaneLoginMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                changePaneLoginMouseReleased(evt);
            }
        });
        changePaneLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePaneLoginActionPerformed(evt);
            }
        });

        changePaneRegistar.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        changePaneRegistar.setText("Registar");
        changePaneRegistar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        changePaneRegistar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        changePaneRegistar.setMargin(new java.awt.Insets(1, 1, 1, 1));
        changePaneRegistar.setMaximumSize(new java.awt.Dimension(10, 10));
        changePaneRegistar.setMinimumSize(new java.awt.Dimension(10, 10));
        changePaneRegistar.setPreferredSize(new java.awt.Dimension(50, 50));
        changePaneRegistar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                changePaneRegistarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                changePaneRegistarMouseReleased(evt);
            }
        });
        changePaneRegistar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePaneRegistarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(changePaneLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(registoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(changePaneRegistar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(changePaneRegistar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changePaneLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(registoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setBounds(0, 0, 1016, 514);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseMoved

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

    }//GEN-LAST:event_formMouseDragged

    private void FecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FecharActionPerformed
        System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_FecharActionPerformed

    private void FecharMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FecharMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/close.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        Fechar.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_FecharMousePressed

    private void FecharMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FecharMouseReleased
        Fechar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/close.png")), 50, 50)); // TODO add your handling code here:
    }//GEN-LAST:event_FecharMouseReleased

    private void MinimizarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizarMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/minimize.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        Minimizar.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_MinimizarMousePressed

    private void MinimizarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizarMouseReleased
        Minimizar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/minimize.png")), 50, 50));
    }//GEN-LAST:event_MinimizarMouseReleased

    private void MinimizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MinimizarActionPerformed
        setState(ICONIFIED);
    }//GEN-LAST:event_MinimizarActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void txtCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCCActionPerformed

    private void btnAutenticarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAutenticarActionPerformed

        if (txtCC.getText().equals("master")) {
            try {
                Credentials master = Credentials.autenticarMaster(txtPassword.getText());
                this.dispose();
                masterPanel = new MasterPanel(master);
                masterPanel.setVisible(true);
                //new MasterPanel(master).setVisible(true);
            } catch (Exception ex) {
            }
        } else {
            try {
                Credentials user = Credentials.autenticar(txtCC.getText(), txtPassword.getText());
                this.dispose();
                new ElectorPanel(user).setVisible(true);
            } catch (Exception ex) {
            }

        }
    }//GEN-LAST:event_btnAutenticarActionPerformed

    private void btnAutenticarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAutenticarMouseReleased
        btnAutenticar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/add.png")), 50, 50));
    }//GEN-LAST:event_btnAutenticarMouseReleased

    private void btnAutenticarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAutenticarMousePressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/add.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        btnAutenticar.setIcon(new ImageIcon(scaledImage));        // Scale down the icon when the button is pressed
    }//GEN-LAST:event_btnAutenticarMousePressed

    private void btnRegistarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistarMousePressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/add.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        btnRegistar.setIcon(new ImageIcon(scaledImage));        // Scale down the icon when the button is pressed       
    }//GEN-LAST:event_btnRegistarMousePressed

    private void btnRegistarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistarMouseReleased
        btnRegistar.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/add.png")), 50, 50));
    }//GEN-LAST:event_btnRegistarMouseReleased

    private void btnRegistarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistarActionPerformed
        try {
            Credentials.registar(registoCC.getText(), regPassword.getText(), regConfPassword.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Registo Inválido!");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_btnRegistarActionPerformed

    private void changePaneLoginMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePaneLoginMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_changePaneLoginMousePressed

    private void changePaneLoginMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePaneLoginMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_changePaneLoginMouseReleased

    private void changePaneLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePaneLoginActionPerformed
        loginPanel.setVisible(true);
        registoPanel.setVisible(false);
    }//GEN-LAST:event_changePaneLoginActionPerformed

    private void changePaneRegistarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePaneRegistarMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_changePaneRegistarMousePressed

    private void changePaneRegistarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePaneRegistarMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_changePaneRegistarMouseReleased

    private void changePaneRegistarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePaneRegistarActionPerformed
        try {
            if (remote.checkPhase(1)) {
                loginPanel.setVisible(false);
                registoPanel.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Só é possível registar-se na fase de registo.");
            }
        } catch (Exception e) {
        }

    }//GEN-LAST:event_changePaneRegistarActionPerformed

    private void registoCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registoCCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_registoCCActionPerformed

    private void regPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_regPasswordActionPerformed

    private void regConfPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regConfPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_regConfPasswordActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html */

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Authentication.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Authentication.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Authentication.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Authentication.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Authentication().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Fechar;
    private javax.swing.JButton Minimizar;
    private javax.swing.JButton btnAutenticar;
    private javax.swing.JButton btnRegistar;
    private javax.swing.JButton changePaneLogin;
    private javax.swing.JButton changePaneRegistar;
    private javax.swing.JLabel faseLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JPasswordField regConfPassword;
    private javax.swing.JPasswordField regPassword;
    private javax.swing.JTextField registoCC;
    private javax.swing.JPanel registoPanel;
    private javax.swing.JTextField txtCC;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
