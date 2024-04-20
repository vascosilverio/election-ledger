package electionledger.masterGUI;

import electionledger.node.AutomaticP2P;
import electionledger.commonGUI.resultadosPanel;
import electionledger.commonGUI.pieChart;
import electionledger.commonGUI.minersPanel;
import electionledger.commonGUI.infoPanel;
import electionledger.commonGUI.blockchainPanel;
import electionledger.node.RemoteInterface;
import java.awt.Color;
import static java.awt.Frame.ICONIFIED;
import java.awt.Image;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import electionledger.utils.Credentials;
import electionledger.utils.ImageUtils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.text.html.HTML;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public final class MasterPanel extends JFrame {

    Credentials myUser;                                 //Chaves e CC/nome do utilizador autenticado
    RemoteInterface remote;                             //Objeto remoto
    CopyOnWriteArrayList<String> candidatesInit;        //Array list concorrente de candidatos
    ConcurrentHashMap<String, PublicKey> electorsInit;  //Hashmap concorrente de CC's de eleitor e a sua public key

    int xDrag;
    int yDrag;
    int xPress;
    int yPress;

    public void setRemote(RemoteInterface remote) {
        this.remote = remote;
    }

    private blockchainPanel bPanel;
    private eleitoresPanel ePanel;
    private gerirEleicaoPanel gPanel;
    private infoPanel iPanel;
    private minersPanel mPanel;
    private resultadosPanel rPanel;
    private pieChart pChart;

    /**
     * Creates new form TemplarCoinGUI
     *
     * @param user
     */
    public MasterPanel(Credentials user) {
        setUndecorated(true);
        initComponents();
        candidatesInit = new CopyOnWriteArrayList<>();
        electorsInit = new ConcurrentHashMap<>();
        this.myUser = user;
        //this.election = election;
        startupLayeredPane();
        windowDesign();
        refreshFaseLabel();

        setLocationRelativeTo(null);
        bPanel.setVisible(false);
        ePanel.setVisible(false);
        gPanel.setVisible(false);
        iPanel.setVisible(false);
        mPanel.setVisible(false);
        rPanel.setVisible(false);
        resultadosPanelbt.setEnabled(false);
        manageButtons();

    }

    /**
     * Gere botões, configura o design da janela, fornece informações sobre
     * a fase e atualiza dinamicamente o rótulo da fase.
     */
    public void manageButtons() {
        Thread thr = new Thread(
                () -> {
                    while (true) {
                        try {
                            Thread.currentThread().sleep(100);
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    if (remote.getPhase() == 4) {
                                        resultadosPanelbt.setEnabled(true);
                                    }
                                } catch (Exception e) {
                                    // Trata exceções, se ocorrerem
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
     * Configura o design da janela.
     */
    public void windowDesign() {
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));
        // Adiciona um Listener que permite arrastar a janela
        this.addMouseMotionListener(new MouseAdapter() {
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
                            Logger.getLogger(MasterPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        );
        thr.start();
    }

    /**
     * Configura os painéis iniciais na camada de painéis.
     */
    public void startupLayeredPane() {
        
        mPanel = new minersPanel();
        mPanel.setBounds(0, 0, 1038, 560);
        mPanel.setOpaque(true);

        remote = AutomaticP2P.getConnectedNode();

        bPanel = new blockchainPanel(myUser, remote, candidatesInit, electorsInit);
        bPanel.setBounds(0, 0, 1038, 2000);
        bPanel.setOpaque(true);

        gPanel = new gerirEleicaoPanel(myUser, remote, candidatesInit, electorsInit);
        gPanel.setBounds(0, 0, 1038, 560);
        gPanel.setOpaque(true);

        iPanel = new infoPanel(myUser, remote, candidatesInit, electorsInit);
        iPanel.setBounds(0, 0, 1038, 560);
        iPanel.setOpaque(true);

        rPanel = new resultadosPanel(remote, electorsInit);
        rPanel.setBounds(0, 0, 1038, 560);
        rPanel.setOpaque(true);

        ePanel = new eleitoresPanel(remote, electorsInit);
        ePanel.setBounds(0, 0, 1038, 560);
        ePanel.setOpaque(true);
        
        StartupLayeredPane.add(bPanel, JLayeredPane.DEFAULT_LAYER);
        StartupLayeredPane.add(gPanel, JLayeredPane.DEFAULT_LAYER);
        StartupLayeredPane.add(iPanel, JLayeredPane.DEFAULT_LAYER);
        StartupLayeredPane.add(rPanel, JLayeredPane.DEFAULT_LAYER);
        StartupLayeredPane.add(mPanel, JLayeredPane.DEFAULT_LAYER);
        StartupLayeredPane.add(ePanel, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnUserTransactions = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        lstUserTransactions = new javax.swing.JList<>();
        jScrollPane10 = new javax.swing.JScrollPane();
        txtUserTransactions = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        txtBalance = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        Minimizar = new javax.swing.JButton();
        Fechar = new javax.swing.JButton();
        faseLabel = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        eleitoresPanelbt = new javax.swing.JButton();
        resultadosPanelbt = new javax.swing.JButton();
        blockchainPanelbt = new javax.swing.JButton();
        gerirEleicaobt = new javax.swing.JButton();
        networkPanelbt = new javax.swing.JButton();
        infoPanelbt = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        StartupLayeredPane = new javax.swing.JLayeredPane();
        jLabel2 = new javax.swing.JLabel();

        pnUserTransactions.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 10, 10));

        jScrollPane9.setBorder(javax.swing.BorderFactory.createTitledBorder("Transactions"));

        lstUserTransactions.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane9.setViewportView(lstUserTransactions);

        jPanel1.add(jScrollPane9);

        jScrollPane10.setBorder(javax.swing.BorderFactory.createTitledBorder("Transaction Detail"));

        txtUserTransactions.setColumns(20);
        txtUserTransactions.setRows(5);
        jScrollPane10.setViewportView(txtUserTransactions);

        jPanel1.add(jScrollPane10);

        pnUserTransactions.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        txtBalance.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        txtBalance.setText("0");
        txtBalance.setBorder(javax.swing.BorderFactory.createTitledBorder("Balance"));
        jPanel2.add(txtBalance, java.awt.BorderLayout.CENTER);

        pnUserTransactions.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Templar Coin");

        jPanel4.setBackground(new java.awt.Color(51, 204, 204));

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

        faseLabel.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N

        jPanel8.setBackground(new java.awt.Color(51, 204, 204));

        eleitoresPanelbt.setBackground(new java.awt.Color(0, 0, 0));
        eleitoresPanelbt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        eleitoresPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/elector.png")),50,50));
        eleitoresPanelbt.setText("Eleitores");
        eleitoresPanelbt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        eleitoresPanelbt.setContentAreaFilled(false);
        eleitoresPanelbt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        eleitoresPanelbt.setMargin(new java.awt.Insets(1, 1, 1, 1));
        eleitoresPanelbt.setMaximumSize(new java.awt.Dimension(10, 10));
        eleitoresPanelbt.setMinimumSize(new java.awt.Dimension(10, 10));
        eleitoresPanelbt.setPreferredSize(new java.awt.Dimension(50, 50));
        eleitoresPanelbt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                eleitoresPanelbtMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                eleitoresPanelbtMouseReleased(evt);
            }
        });
        eleitoresPanelbt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eleitoresPanelbtActionPerformed(evt);
            }
        });

        resultadosPanelbt.setBackground(new java.awt.Color(0, 0, 0));
        resultadosPanelbt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        resultadosPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/result.png")),50,50));
        resultadosPanelbt.setText("Resultados");
        resultadosPanelbt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        resultadosPanelbt.setContentAreaFilled(false);
        resultadosPanelbt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        resultadosPanelbt.setMargin(new java.awt.Insets(1, 1, 1, 1));
        resultadosPanelbt.setMaximumSize(new java.awt.Dimension(10, 10));
        resultadosPanelbt.setMinimumSize(new java.awt.Dimension(10, 10));
        resultadosPanelbt.setPreferredSize(new java.awt.Dimension(50, 50));
        resultadosPanelbt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                resultadosPanelbtMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resultadosPanelbtMouseReleased(evt);
            }
        });
        resultadosPanelbt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultadosPanelbtActionPerformed(evt);
            }
        });

        blockchainPanelbt.setBackground(new java.awt.Color(0, 0, 0));
        blockchainPanelbt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        blockchainPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/blockchain2.png")),50,50));
        blockchainPanelbt.setText("Blockchain");
        blockchainPanelbt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        blockchainPanelbt.setContentAreaFilled(false);
        blockchainPanelbt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        blockchainPanelbt.setMargin(new java.awt.Insets(1, 1, 1, 1));
        blockchainPanelbt.setMaximumSize(new java.awt.Dimension(10, 10));
        blockchainPanelbt.setMinimumSize(new java.awt.Dimension(10, 10));
        blockchainPanelbt.setPreferredSize(new java.awt.Dimension(50, 50));
        blockchainPanelbt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                blockchainPanelbtMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                blockchainPanelbtMouseReleased(evt);
            }
        });
        blockchainPanelbt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blockchainPanelbtActionPerformed(evt);
            }
        });

        gerirEleicaobt.setBackground(new java.awt.Color(0, 0, 0));
        gerirEleicaobt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        gerirEleicaobt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/election"
            + ".png")),50,50));
gerirEleicaobt.setText("Gerir Eleição");
gerirEleicaobt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
gerirEleicaobt.setContentAreaFilled(false);
gerirEleicaobt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
gerirEleicaobt.setMargin(new java.awt.Insets(1, 1, 1, 1));
gerirEleicaobt.setMaximumSize(new java.awt.Dimension(10, 10));
gerirEleicaobt.setMinimumSize(new java.awt.Dimension(10, 10));
gerirEleicaobt.setPreferredSize(new java.awt.Dimension(50, 50));
gerirEleicaobt.addMouseListener(new java.awt.event.MouseAdapter() {
public void mousePressed(java.awt.event.MouseEvent evt) {
    gerirEleicaobtMousePressed(evt);
    }
    public void mouseReleased(java.awt.event.MouseEvent evt) {
        gerirEleicaobtMouseReleased(evt);
    }
    });
    gerirEleicaobt.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            gerirEleicaobtActionPerformed(evt);
        }
    });

    networkPanelbt.setBackground(new java.awt.Color(0, 0, 0));
    networkPanelbt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
    networkPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/network.png")),50,50));
    networkPanelbt.setText("Rede");
    networkPanelbt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
    networkPanelbt.setContentAreaFilled(false);
    networkPanelbt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    networkPanelbt.setMargin(new java.awt.Insets(1, 1, 1, 1));
    networkPanelbt.setMaximumSize(new java.awt.Dimension(10, 10));
    networkPanelbt.setMinimumSize(new java.awt.Dimension(10, 10));
    networkPanelbt.setPreferredSize(new java.awt.Dimension(50, 50));
    networkPanelbt.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            networkPanelbtMousePressed(evt);
        }
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            networkPanelbtMouseReleased(evt);
        }
    });
    networkPanelbt.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            networkPanelbtActionPerformed(evt);
        }
    });

    infoPanelbt.setBackground(new java.awt.Color(0, 0, 0));
    infoPanelbt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
    infoPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/info.png")),50,50));
    infoPanelbt.setText("Info Comissão");
    infoPanelbt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
    infoPanelbt.setContentAreaFilled(false);
    infoPanelbt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    infoPanelbt.setMargin(new java.awt.Insets(1, 1, 1, 1));
    infoPanelbt.setMaximumSize(new java.awt.Dimension(10, 10));
    infoPanelbt.setMinimumSize(new java.awt.Dimension(10, 10));
    infoPanelbt.setPreferredSize(new java.awt.Dimension(50, 50));
    infoPanelbt.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            infoPanelbtMousePressed(evt);
        }
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            infoPanelbtMouseReleased(evt);
        }
    });
    infoPanelbt.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            infoPanelbtActionPerformed(evt);
        }
    });

    logout.setBackground(new java.awt.Color(0, 0, 0));
    logout.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
    logout.setText("Logout");
    logout.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
    logout.setContentAreaFilled(false);
    logout.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    logout.setMargin(new java.awt.Insets(1, 1, 1, 1));
    logout.setMaximumSize(new java.awt.Dimension(10, 10));
    logout.setMinimumSize(new java.awt.Dimension(10, 10));
    logout.setPreferredSize(new java.awt.Dimension(50, 50));
    logout.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            logoutMousePressed(evt);
        }
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            logoutMouseReleased(evt);
        }
    });
    logout.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            logoutActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
    jPanel8.setLayout(jPanel8Layout);
    jPanel8Layout.setHorizontalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel8Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(gerirEleicaobt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(eleitoresPanelbt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resultadosPanelbt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(blockchainPanelbt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(networkPanelbt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(infoPanelbt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap())
    );
    jPanel8Layout.setVerticalGroup(
        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel8Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(gerirEleicaobt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(eleitoresPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(resultadosPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(blockchainPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(networkPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(infoPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
            .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    jScrollPane1.setAutoscrolls(true);
    jScrollPane1.setPreferredSize(new java.awt.Dimension(1038, 560));

    StartupLayeredPane.setPreferredSize(new java.awt.Dimension(1038, 1500));

    javax.swing.GroupLayout StartupLayeredPaneLayout = new javax.swing.GroupLayout(StartupLayeredPane);
    StartupLayeredPane.setLayout(StartupLayeredPaneLayout);
    StartupLayeredPaneLayout.setHorizontalGroup(
        StartupLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 1038, Short.MAX_VALUE)
    );
    StartupLayeredPaneLayout.setVerticalGroup(
        StartupLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 1500, Short.MAX_VALUE)
    );

    jScrollPane1.setViewportView(StartupLayeredPane);

    jLabel2.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N
    jLabel2.setText("Comissão Eleitoral");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1038, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addComponent(jLabel2)
                    .addGap(52, 52, 52)
                    .addComponent(faseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Minimizar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(Fechar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(Fechar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(Minimizar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(faseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

    pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void FecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FecharActionPerformed
        System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_FecharActionPerformed

    private void eleitoresPanelbtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eleitoresPanelbtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/elector.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        eleitoresPanelbt.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_eleitoresPanelbtMousePressed

    private void eleitoresPanelbtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eleitoresPanelbtMouseReleased
        eleitoresPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/elector.png")), 50, 50));
    }//GEN-LAST:event_eleitoresPanelbtMouseReleased

    private void eleitoresPanelbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eleitoresPanelbtActionPerformed
        StartupLayeredPane.moveToFront(ePanel);
        bPanel.setVisible(false);
        ePanel.setVisible(true);
        gPanel.setVisible(false);
        iPanel.setVisible(false);
        mPanel.setVisible(false);
        rPanel.setVisible(false);
    }//GEN-LAST:event_eleitoresPanelbtActionPerformed

    private void resultadosPanelbtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultadosPanelbtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/result.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        resultadosPanelbt.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_resultadosPanelbtMousePressed

    private void resultadosPanelbtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultadosPanelbtMouseReleased
        resultadosPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/result.png")), 50, 50));
    }//GEN-LAST:event_resultadosPanelbtMouseReleased

    private void resultadosPanelbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultadosPanelbtActionPerformed
        if (pChart != null) {
            rPanel.revalidate();
            rPanel.repaint();
            StartupLayeredPane.moveToFront(rPanel);
            bPanel.setVisible(false);
            gPanel.setVisible(false);
            iPanel.setVisible(false);
            mPanel.setVisible(false);
            rPanel.setVisible(true);

        } else {

            rPanel.revalidate();
            try {
                pChart = new pieChart(remote);
                System.out.println(pChart.toString());

            } catch (RemoteException e) {
                System.out.println("ups");
            }

            pChart.setLayout(new FlowLayout(FlowLayout.LEFT, 100, 0));
            rPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 100, 0));
            rPanel.add(pChart, BorderLayout.CENTER);

            rPanel.repaint();
            try {
                rPanel.resultsText(remote.masterPrivateKey());
            } catch (RemoteException ex) {
                Logger.getLogger(MasterPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
// TODO add your handling code here:
    }//GEN-LAST:event_resultadosPanelbtActionPerformed

    private void blockchainPanelbtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blockchainPanelbtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/blockchain2.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        blockchainPanelbt.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_blockchainPanelbtMousePressed

    private void blockchainPanelbtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_blockchainPanelbtMouseReleased
        blockchainPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/blockchain2.png")), 50, 50));
    }//GEN-LAST:event_blockchainPanelbtMouseReleased

    private void blockchainPanelbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blockchainPanelbtActionPerformed
        StartupLayeredPane.moveToFront(bPanel);
        bPanel.setVisible(true);
        ePanel.setVisible(false);
        gPanel.setVisible(false);
        iPanel.setVisible(false);
        mPanel.setVisible(false);
        rPanel.setVisible(false);
    }//GEN-LAST:event_blockchainPanelbtActionPerformed

    private void gerirEleicaobtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gerirEleicaobtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/election.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        gerirEleicaobt.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_gerirEleicaobtMousePressed

    private void gerirEleicaobtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gerirEleicaobtMouseReleased
        gerirEleicaobt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/election.png")), 50, 50));
    }//GEN-LAST:event_gerirEleicaobtMouseReleased

    private void gerirEleicaobtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gerirEleicaobtActionPerformed
        StartupLayeredPane.moveToFront(gPanel);
        bPanel.setVisible(false);
        ePanel.setVisible(false);
        gPanel.setVisible(true);
        iPanel.setVisible(false);
        mPanel.setVisible(false);
        rPanel.setVisible(false);
    }//GEN-LAST:event_gerirEleicaobtActionPerformed

    private void networkPanelbtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkPanelbtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/network.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        networkPanelbt.setIcon(new ImageIcon(scaledImage));    // TODO add your handling code here:
    }//GEN-LAST:event_networkPanelbtMousePressed

    private void networkPanelbtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkPanelbtMouseReleased
        networkPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/network.png")), 50, 50));

    }//GEN-LAST:event_networkPanelbtMouseReleased

    private void networkPanelbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_networkPanelbtActionPerformed
        StartupLayeredPane.moveToFront(mPanel);
        bPanel.setVisible(false);
        ePanel.setVisible(false);
        gPanel.setVisible(false);
        iPanel.setVisible(false);
        mPanel.setVisible(true);
        rPanel.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_networkPanelbtActionPerformed

    private void infoPanelbtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoPanelbtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/info.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        infoPanelbt.setIcon(new ImageIcon(scaledImage));    // TODO add your handling code here:
    }//GEN-LAST:event_infoPanelbtMousePressed

    private void infoPanelbtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoPanelbtMouseReleased
        infoPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/info.png")), 50, 50));

    }//GEN-LAST:event_infoPanelbtMouseReleased

    private void infoPanelbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoPanelbtActionPerformed
        StartupLayeredPane.moveToFront(iPanel);
        bPanel.setVisible(false);
        ePanel.setVisible(false);
        gPanel.setVisible(false);
        iPanel.setVisible(true);
        mPanel.setVisible(false);
        rPanel.setVisible(false);          // TODO add your handling code here:
    }//GEN-LAST:event_infoPanelbtActionPerformed

    private void logoutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMousePressed

    }//GEN-LAST:event_logoutMousePressed

    private void logoutMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseReleased

    }//GEN-LAST:event_logoutMouseReleased

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        System.exit(1);
    }//GEN-LAST:event_logoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Fechar;
    private javax.swing.JButton Minimizar;
    private javax.swing.JLayeredPane StartupLayeredPane;
    private javax.swing.JButton blockchainPanelbt;
    private javax.swing.JButton eleitoresPanelbt;
    private javax.swing.JLabel faseLabel;
    private javax.swing.JButton gerirEleicaobt;
    private javax.swing.JButton infoPanelbt;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton logout;
    private javax.swing.JList<String> lstUserTransactions;
    private javax.swing.JButton networkPanelbt;
    private javax.swing.JPanel pnUserTransactions;
    private javax.swing.JButton resultadosPanelbt;
    private javax.swing.JLabel txtBalance;
    private javax.swing.JTextArea txtUserTransactions;
    // End of variables declaration//GEN-END:variables

}
