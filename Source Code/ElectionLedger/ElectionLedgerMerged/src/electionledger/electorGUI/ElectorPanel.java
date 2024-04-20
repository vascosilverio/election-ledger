package electionledger.electorGUI;

import electionledger.masterGUI.MasterPanel;
import electionledger.node.RemoteInterface;
import java.awt.Color;
import java.awt.FlowLayout;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import electionledger.utils.Credentials;
import electionledger.utils.ImageUtils;
import electionledger.commonGUI.blockchainPanel;
import electionledger.commonGUI.infoPanel;
import electionledger.commonGUI.minersPanel;
import electionledger.commonGUI.pieChart;
import electionledger.commonGUI.resultadosPanel;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class ElectorPanel extends javax.swing.JFrame {

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
    private boletimPanel gPanel;
    private infoPanel iPanel;
    private minersPanel mPanel;
    private resultadosPanel rPanel;
    private pieChart pChart;

    /**
     * Creates new form TemplarCoinGUI
     */
    public ElectorPanel(Credentials user) {
        setUndecorated(true);
        initComponents();
        this.myUser = user;
        candidatesInit = new CopyOnWriteArrayList<>();
        electorsInit = new ConcurrentHashMap<>();
        startupLayeredPane();
        windowDesign();
        setLocationRelativeTo(null);
        bPanel.setVisible(false);
        gPanel.setVisible(false);
        iPanel.setVisible(false);
        mPanel.setVisible(false);
        rPanel.setVisible(false);
        votobt.setEnabled(false);
        resultadosPanelbt.setEnabled(false);
        manageButtons();
    }

    /**
     * Gerencia os botões dinamicamente com base na fase atual.
     */
    public void manageButtons() {
        Thread thr = new Thread(
                () -> {
                    while (true) {
                        try {
                            Thread.currentThread().sleep(100);
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    // Habilita o botão de voto se a fase for maior ou igual a 3
                                    if (remote.getPhase() >= 3) {
                                        votobt.setEnabled(true);
                                    }
                                    // Habilita o botão de resultados se a fase for igual a 4
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
     * Atualiza o rótulo da fase na interface gráfica.
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
                            Logger.getLogger(ElectorPanel.class  

.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        );
        thr.start();
    }

    /**
     * Inicializa os componentes da interface gráfica.
     */
    public void startupLayeredPane() {
        mPanel = new minersPanel();
        mPanel.setBounds(0, 0, 1038, 560);
        mPanel.setOpaque(true);

        remote = mPanel.getRemote();

        gPanel = new boletimPanel(myUser, remote);
        gPanel.setBounds(0, 0, 1038, 560);
        gPanel.setOpaque(true);

        bPanel = new blockchainPanel(myUser, remote, candidatesInit, electorsInit);
        bPanel.setBounds(0, 0, 1038, 560);
        bPanel.setOpaque(true);

        iPanel = new infoPanel(myUser, remote, candidatesInit, electorsInit);
        iPanel.setBounds(0, 0, 1038, 560);
        iPanel.setOpaque(true);

        rPanel = new resultadosPanel(remote, electorsInit);
        rPanel.setBounds(0, 0, 1038, 560);
        rPanel.setOpaque(true);

        EStartupLayeredPane.add(bPanel, JLayeredPane.DEFAULT_LAYER);
        EStartupLayeredPane.add(gPanel, JLayeredPane.DEFAULT_LAYER);
        EStartupLayeredPane.add(iPanel, JLayeredPane.DEFAULT_LAYER);
        EStartupLayeredPane.add(rPanel, JLayeredPane.DEFAULT_LAYER);
        EStartupLayeredPane.add(mPanel, JLayeredPane.DEFAULT_LAYER);
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
        resultadosPanelbt = new javax.swing.JButton();
        blockchainPanelbt = new javax.swing.JButton();
        votobt = new javax.swing.JButton();
        networkPanelbt = new javax.swing.JButton();
        infoComissaoPanelbt = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        EStartupLayeredPane = new javax.swing.JLayeredPane();
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

        votobt.setBackground(new java.awt.Color(0, 0, 0));
        votobt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        votobt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/voting.png")),50,50));
        votobt.setText("Voto");
        votobt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        votobt.setContentAreaFilled(false);
        votobt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        votobt.setMargin(new java.awt.Insets(1, 1, 1, 1));
        votobt.setMaximumSize(new java.awt.Dimension(10, 10));
        votobt.setMinimumSize(new java.awt.Dimension(10, 10));
        votobt.setPreferredSize(new java.awt.Dimension(50, 50));
        votobt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                votobtMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                votobtMouseReleased(evt);
            }
        });
        votobt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                votobtActionPerformed(evt);
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

        infoComissaoPanelbt.setBackground(new java.awt.Color(0, 0, 0));
        infoComissaoPanelbt.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        infoComissaoPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/info.png")),50,50));
        infoComissaoPanelbt.setText("Info Eleitor");
        infoComissaoPanelbt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        infoComissaoPanelbt.setContentAreaFilled(false);
        infoComissaoPanelbt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        infoComissaoPanelbt.setMargin(new java.awt.Insets(1, 1, 1, 1));
        infoComissaoPanelbt.setMaximumSize(new java.awt.Dimension(10, 10));
        infoComissaoPanelbt.setMinimumSize(new java.awt.Dimension(10, 10));
        infoComissaoPanelbt.setPreferredSize(new java.awt.Dimension(50, 50));
        infoComissaoPanelbt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                infoComissaoPanelbtMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                infoComissaoPanelbtMouseReleased(evt);
            }
        });
        infoComissaoPanelbt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoComissaoPanelbtActionPerformed(evt);
            }
        });

        logout.setBackground(new java.awt.Color(0, 0, 0));
        logout.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        logout.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/result.png")),50,50));
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
                    .addComponent(votobt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultadosPanelbt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(blockchainPanelbt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(networkPanelbt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(infoComissaoPanelbt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(votobt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultadosPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blockchainPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(networkPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoComissaoPanelbt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 222, Short.MAX_VALUE)
                .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout EStartupLayeredPaneLayout = new javax.swing.GroupLayout(EStartupLayeredPane);
        EStartupLayeredPane.setLayout(EStartupLayeredPaneLayout);
        EStartupLayeredPaneLayout.setHorizontalGroup(
            EStartupLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1038, Short.MAX_VALUE)
        );
        EStartupLayeredPaneLayout.setVerticalGroup(
            EStartupLayeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 30)); // NOI18N
        jLabel2.setText("Boletim Eleitor");

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
                        .addComponent(EStartupLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(EStartupLayeredPane)))
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
            EStartupLayeredPane.moveToFront(rPanel);
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
                Logger.getLogger(ElectorPanel.class  

.getName()).log(Level.SEVERE, null, ex);
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
        EStartupLayeredPane.moveToFront(bPanel);
        bPanel.setVisible(true);
        gPanel.setVisible(false);
        iPanel.setVisible(false);
        mPanel.setVisible(false);
        rPanel.setVisible(false);
    }//GEN-LAST:event_blockchainPanelbtActionPerformed

    private void votobtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_votobtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/voting.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        votobt.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_votobtMousePressed

    private void votobtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_votobtMouseReleased
        votobt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/voting.png")), 50, 50));
    }//GEN-LAST:event_votobtMouseReleased

    private void votobtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_votobtActionPerformed
        EStartupLayeredPane.moveToFront(gPanel);
        bPanel.setVisible(false);
        gPanel.setVisible(true);
        iPanel.setVisible(false);
        mPanel.setVisible(false);
        rPanel.setVisible(false);
    }//GEN-LAST:event_votobtActionPerformed

    private void networkPanelbtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkPanelbtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/network.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        networkPanelbt.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_networkPanelbtMousePressed

    private void networkPanelbtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkPanelbtMouseReleased
        networkPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/network.png")), 50, 50));

    }//GEN-LAST:event_networkPanelbtMouseReleased

    private void networkPanelbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_networkPanelbtActionPerformed
        EStartupLayeredPane.moveToFront(mPanel);
        bPanel.setVisible(false);
        gPanel.setVisible(false);
        iPanel.setVisible(false);
        mPanel.setVisible(true);
        rPanel.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_networkPanelbtActionPerformed

    private void infoComissaoPanelbtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoComissaoPanelbtMousePressed
        // Scale down the icon when the button is pressed
        ImageIcon icon = (ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/info.png")), 50, 50));
        int newWidth = (int) (icon.getIconWidth() * 0.9); // Adjust the scaling factor as needed
        int newHeight = (int) (icon.getIconHeight() * 0.9); // Adjust the scaling factor as needed
        Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        infoComissaoPanelbt.setIcon(new ImageIcon(scaledImage)); // TODO add your handling code here:
    }//GEN-LAST:event_infoComissaoPanelbtMousePressed

    private void infoComissaoPanelbtMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoComissaoPanelbtMouseReleased
        infoComissaoPanelbt.setIcon(ImageUtils.resizeIcon(new javax.swing.ImageIcon(getClass().getResource("/electionledger/multimedia/info.png")), 50, 50));

    }//GEN-LAST:event_infoComissaoPanelbtMouseReleased

    private void infoComissaoPanelbtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoComissaoPanelbtActionPerformed
        EStartupLayeredPane.moveToFront(iPanel);
        bPanel.setVisible(false);
        gPanel.setVisible(false);
        iPanel.setVisible(true);
        mPanel.setVisible(false);
        rPanel.setVisible(false);          // TODO add your handling code here:
    }//GEN-LAST:event_infoComissaoPanelbtActionPerformed

    private void logoutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_logoutMousePressed

    private void logoutMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_logoutMouseReleased

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        System.exit(1);        // TODO add your handling code here:
    }//GEN-LAST:event_logoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane EStartupLayeredPane;
    private javax.swing.JButton Fechar;
    private javax.swing.JButton Minimizar;
    private javax.swing.JButton blockchainPanelbt;
    private javax.swing.JLabel faseLabel;
    private javax.swing.JButton infoComissaoPanelbt;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton logout;
    private javax.swing.JList<String> lstUserTransactions;
    private javax.swing.JButton networkPanelbt;
    private javax.swing.JPanel pnUserTransactions;
    private javax.swing.JButton resultadosPanelbt;
    private javax.swing.JLabel txtBalance;
    private javax.swing.JTextArea txtUserTransactions;
    private javax.swing.JButton votobt;
    // End of variables declaration//GEN-END:variables

}
