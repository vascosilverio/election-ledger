package electionledger.commonGUI;

import electionledger.node.RemoteInterface;
import electionledger.blockchain.Block;
import electionledger.masterGUI.MasterPanel;
import electionledger.blockchain.Transfer;
import electionledger.utils.Credentials;
import electionledger.electorGUI.ElectorPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class blockchainPanel extends javax.swing.JPanel {

    Credentials myUser;                                 //Chaves e CC/nome do utilizador autenticado
    RemoteInterface remote;                             //Objeto remoto
    CopyOnWriteArrayList<String> candidatesInit;        //Array list concorrente de candidatos
    ConcurrentHashMap<String, PublicKey> electorsInit;  //Hashmap concorrente de CC's de eleitor e a sua public key
    boolean nodeUp = false;                             //Booleano auxiliar para ajudar a detetar se o nó a que está ligado foi abaixo

    public blockchainPanel(Credentials MainUser, RemoteInterface MainRemote, CopyOnWriteArrayList<String> MainCandidates, ConcurrentHashMap<String, PublicKey> MainElectors) {
        myUser = MainUser;
        remote = MainRemote;
        candidatesInit = MainCandidates;
        electorsInit = MainElectors;
        refreshThread();
        initComponents();
    }

    /**
     * Inicia uma thread para atualizar a representação gráfica da blockchain
     * periodicamente.
     */
    public void refreshThread() {
        Thread thr = new Thread(() -> {
            try {
                while (true) {
                    Thread.currentThread().sleep(500);
                    SwingUtilities.invokeLater(() -> refreshBlockchain());
                }
            } catch (Exception ex) {
                Logger.getLogger(MasterPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thr.start();
    }

    /**
     * Mostra informações detalhadas sobre um bloco selecionado.
     *
     * @param blockId O identificador do bloco selecionado
     * @throws Exception Lança exceção em caso de erro ao explorar o bloco
     */
    public void exploreBlock(int blockId) throws Exception {
        // Obtém o bloco correspondente ao identificador do bloco
        Block block = remote.getBlockchain().getBlock(blockId);

        // Inicializa JTextArea para exibir informações do bloco
        JTextArea title = new JTextArea();
        JTextArea blockHash = new JTextArea();
        JTextArea blockPreviousHash = new JTextArea();
        JTextArea blockNonce = new JTextArea();
        JTextArea blockMerkle = new JTextArea();
        JTextArea blockPhase = new JTextArea();
        JTextArea blockTimeStamp = new JTextArea();

        // Limpa o painel de exploração de bloco e configura seu layout
        jPanelBlockExplorer.removeAll();
        jPanelBlockExplorer.revalidate();
        jPanelBlockExplorer.setLayout(new BoxLayout(jPanelBlockExplorer, BoxLayout.Y_AXIS));

        // Configuração dos JTextAreas com as informações do bloco
        title.setText("Bloco Nº" + blockId);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setEditable(false);

        blockHash.setText("Hash: " + block.getCurrentHash());
        blockHash.setFont(new Font("Arial", Font.BOLD, 16));
        blockHash.setEditable(false);

        blockPreviousHash.setText("Previous Hash: " + block.getPreviousHash());
        blockPreviousHash.setFont(new Font("Arial", Font.BOLD, 16));
        blockPreviousHash.setEditable(false);

        blockNonce.setText("Nonce: " + block.getNonce());
        blockNonce.setFont(new Font("Arial", Font.BOLD, 16));
        blockNonce.setEditable(false);

        blockMerkle.setText("Merkle Root: " + block.getMerkleTreeRoot());
        blockMerkle.setFont(new Font("Arial", Font.BOLD, 16));
        blockMerkle.setEditable(false);

        blockPhase.setText("Phase: " + block.getPhase());
        blockPhase.setFont(new Font("Arial", Font.BOLD, 16));
        blockPhase.setEditable(false);

        blockTimeStamp.setText("Timestamp: " + block.getTimestamp());
        blockTimeStamp.setFont(new Font("Arial", Font.BOLD, 16));
        blockTimeStamp.setEditable(false);

        // Adiciona JTextAreas ao painel de exploração de bloco
        jPanelBlockExplorer.removeAll();
        jPanelBlockExplorer.add(title);
        jPanelBlockExplorer.add(blockHash);
        jPanelBlockExplorer.add(blockPreviousHash);
        jPanelBlockExplorer.add(blockNonce);
        jPanelBlockExplorer.add(blockMerkle);
        jPanelBlockExplorer.add(blockPhase);
        jPanelBlockExplorer.add(blockTimeStamp);
        jPanelBlockExplorer.repaint();
    }

    /**
     * Atualiza a representação gráfica da blockchain.
     */
    public void refreshBlockchain() {
        try {
            try {
                remote.ping();
                nodeUp = true;
            } catch (Exception ex) {
                nodeUp = false;
            }
            if (nodeUp) {
                // Obtém o tamanho atual da blockchain a partir da interface remota
                AtomicInteger blockchainSize = new AtomicInteger(remote.getBlockchainSize());
                // Limpa o painel de blocos
                blocksPanel.removeAll();

                // Ajusta a largura do painel de acordo com o tamanho da blockchain
                if (blockchainSize.get() > 5) {
                    blocksPanel.setPreferredSize(new Dimension(blockchainSize.get() * 110, blocksPanel.getHeight()));
                    blocksPanel.revalidate();
                }

                blocksPanel.revalidate();
                blocksPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

                // Itera sobre os blocos da blockchain para criar os painéis de blocos
                for (int i = 0; i < blockchainSize.get(); i++) {
                    JPanel block = new JPanel();
                    block.setPreferredSize(new Dimension(90, 90));
                    block.setBackground(Color.CYAN);
                    block.add(new JLabel("Bloco " + i));
                    block.add(new JLabel());
                    final AtomicInteger blockNumber = new AtomicInteger(i);

                    // Adiciona um mouseListener para cada bloco
                    block.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                // Ao clicar num bloco, explora suas informações e transações
                                exploreBlock(blockNumber.get());
                                exploreTransactions(blockNumber.get());
                            } catch (Exception ex) {
                                Logger.getLogger(ElectorPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    // Adiciona o bloco ao painel de blocos
                    blocksPanel.add(block);
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ElectorPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Explora as transações de um bloco selecionado.
     *
     * @param blockId O identificador do bloco selecionado
     * @throws Exception Lança exceção em caso de erro ao explorar as transações
     */
    public void exploreTransactions(int blockId) throws Exception {
        // Limpa o painel de exploração de transações
        jPanelTransactionsExplorer.removeAll();
        jPanelTransactionsExplorer.revalidate();
        jPanelTransactionsExplorer.setLayout(new BoxLayout(jPanelTransactionsExplorer, BoxLayout.Y_AXIS));

        // Obtém o bloco correspondente ao identificador do bloco
        Block block = remote.getBlockchain().getBlock(blockId);
        // Obtém a lista de transações do bloco
        CopyOnWriteArrayList<String> blockTransactions = block.getTransactions();

        // Itera sobre as transações do bloco para criar JTextAreas com informações das transações
        for (String trans : blockTransactions) {
            Transfer transfer = Transfer.fromText(trans);
            JTextArea infoTransaction = new JTextArea();
            infoTransaction.setText(transfer.toString());
            infoTransaction.setFont(new Font("Arial", Font.BOLD, 16));
            infoTransaction.setEditable(false);

            // Adiciona JTextAreas ao painel de exploração de transações
            jPanelTransactionsExplorer.add(infoTransaction);
            jPanelTransactionsExplorer.repaint();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        blocksPanel = new javax.swing.JPanel();
        jPanelBlockExplorer = new javax.swing.JPanel();
        jPanelTransactionsExplorer = new javax.swing.JPanel();

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(500, 100));

        blocksPanel.setMaximumSize(new java.awt.Dimension(32767, 100));
        blocksPanel.setMinimumSize(new java.awt.Dimension(500, 100));

        javax.swing.GroupLayout blocksPanelLayout = new javax.swing.GroupLayout(blocksPanel);
        blocksPanel.setLayout(blocksPanelLayout);
        blocksPanelLayout.setHorizontalGroup(
            blocksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        blocksPanelLayout.setVerticalGroup(
            blocksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 139, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(blocksPanel);

        jPanelBlockExplorer.setAutoscrolls(true);
        jPanelBlockExplorer.setMaximumSize(new java.awt.Dimension(300, 800));
        jPanelBlockExplorer.setPreferredSize(new java.awt.Dimension(430, 418));

        javax.swing.GroupLayout jPanelBlockExplorerLayout = new javax.swing.GroupLayout(jPanelBlockExplorer);
        jPanelBlockExplorer.setLayout(jPanelBlockExplorerLayout);
        jPanelBlockExplorerLayout.setHorizontalGroup(
            jPanelBlockExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 694, Short.MAX_VALUE)
        );
        jPanelBlockExplorerLayout.setVerticalGroup(
            jPanelBlockExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 365, Short.MAX_VALUE)
        );

        jPanelTransactionsExplorer.setAutoscrolls(true);
        jPanelTransactionsExplorer.setPreferredSize(new java.awt.Dimension(300, 418));

        javax.swing.GroupLayout jPanelTransactionsExplorerLayout = new javax.swing.GroupLayout(jPanelTransactionsExplorer);
        jPanelTransactionsExplorer.setLayout(jPanelTransactionsExplorerLayout);
        jPanelTransactionsExplorerLayout.setHorizontalGroup(
            jPanelTransactionsExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );
        jPanelTransactionsExplorerLayout.setVerticalGroup(
            jPanelTransactionsExplorerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelBlockExplorer, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelTransactionsExplorer, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelBlockExplorer, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .addComponent(jPanelTransactionsExplorer, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blocksPanel;
    private javax.swing.JPanel jPanelBlockExplorer;
    private javax.swing.JPanel jPanelTransactionsExplorer;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
