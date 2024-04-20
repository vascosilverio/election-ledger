//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
//::                                                                         ::
//::     I N S T I T U T O    P O L I T E C N I C O   D E   T O M A R        ::
//::     Escola Superior de Tecnologia de Tomar                              ::
//::     e-mail: manso@ipt.pt                                                ::
//::     url   : http://orion.ipt.pt/~manso                                  ::
//::                                                                         ::
//::     This software was build with the purpose of investigate and         ::
//::     learning.                                                           ::
//::                                                                         ::
//::                                                               (c)2023   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package electionledger.node;
import electionledger.blockchain.Transfer;
import electionledger.blockchain.Block;
import electionledger.blockchain.BlockChain;
import electionledger.blockchain.BlockchainException;
import electionledger.blockchain.Transactions;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import electionledger.utils.MerkleTree;
import electionledger.utils.SecurityUtils;
import electionledger.utils.Serializer;
import electionledger.miner.MinerP2P;
import electionledger.miner.MiningListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author manso, Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class RemoteObject extends UnicastRemoteObject implements RemoteInterface {

    MiningListener listener;                                    //Listener do minerador
    MinerP2P myMiner;                                           //Minerador distribuído multithread
    Transactions transactions;                                  //Classe que contém a lista concorrente de Transfers em base64
    MerkleTree merkle;                                          //Merkle tree para o bloco

    public Block miningBlock;                                   //instâcia de bloco 
    public BlockChain blockchain;                               //instância da blockchain

    private String address;                                     // endereço do servidor
    private CopyOnWriteArrayList<RemoteInterface> network;      // rede de nodes de mineradores
    CopyOnWriteArrayList<String> genesis;                       // bloco genesis

    private int startTokens = 500;
    private int phase = 0;                                      //fase da eleição inicialmente a 0
    private boolean endBlock = false;                           //boolean para semaforo de obrigação de mineração
    private int numElectors;                                    //contagem de eleitores
    private final Semaphore semaforo = new Semaphore(1);  //semaforo para controlar mineração de novos blocos
    int MAX_ELECTORS = 2;                                       //máximo de 2 eleitores por bloco para validação de eleitores
    CopyOnWriteArrayList<String> candidates;                    //array list concorrente de candidatos
    ConcurrentHashMap<String, PublicKey> electors;              //hasmap concorrente de eleitores e as suas chaves públicas
    CopyOnWriteArrayList electorsBlock;                         //array list concorrente para a validação de 2 eleitores para um bloco

    /**
     * creates a object listening the port
     *
     * @param port port to listen
     * @param listener listener do
     * @throws RemoteException
     */
    public RemoteObject(int port, MiningListener listener) throws RemoteException {
        super(port);
        try {
            this.listener = listener;
            this.myMiner = new MinerP2P(listener);
            //atualizar o endereço do objeto remoto
            address = "//" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + RemoteInterface.OBJECT_NAME;
            //inicializar nova rede

            network = new CopyOnWriteArrayList<>();
            //inicializar novas transações
            transactions = new Transactions();
            genesis = new CopyOnWriteArrayList<String>();
            candidates = new CopyOnWriteArrayList<>();
            electors = new ConcurrentHashMap<String, PublicKey>();
            electorsBlock = new CopyOnWriteArrayList();
            System.out.println("teste");
            blockchain = new BlockChain();

            genesis.add("First Block");
            merkle = new MerkleTree(genesis);
            this.miningBlock = new Block("First Block", "", "Empty", 5, 0);
            //inicializar blockchain
            //election = new Election();            

            //election = new Election(blockchain, transactions);
            System.out.println("TEST");

            listener.onStartServer(electionledger.utils.RMI.getRemoteName(port, RemoteInterface.OBJECT_NAME));
            AutomaticP2P.sendAddress(address, 5_000); // 15 segundos
            listener.onMessage("UDP SEND", address);
            AutomaticP2P.listenToNodes(this);
            listener.onMessage("UDP Listener", address);

        } catch (Exception e) {
            address = "unknow" + ":" + port;

        }
    }

    /**
     * Obtém o endereço associado a este nó remoto.
     *
     * @return O endereço do nó remoto.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public String getAdress() throws RemoteException {
        return address;
    }

    /**
     * Obtém o nome do cliente associado a este nó remoto.
     *
     * @return O nome do cliente (endereço do cliente, caso disponível).
     */
    public String getClientName() {
        // Informação do cliente
        try {
            return RemoteServer.getClientHost();
        } catch (ServerNotActiveException ex) {
        }
        return "Anonymous";
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               M I N E I R O 
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Retorna o hash resultante da concatenação de nonce e mensagem.
     *
     * @param nonce O número utilizado na mineração.
     * @param msg A mensagem a ser incluída no hash.
     * @return O hash resultante.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public String getHash(int nonce, String msg) throws RemoteException {
        // Informação do cliente
        System.out.println("Hashing to " + getClientName());
        // Calcular o hash
        return MinerP2P.getHash(nonce + msg);
    }

    /**
     * Inicia o processo de mineração com a mensagem e a dificuldade fornecidas.
     *
     * @param msg A mensagem a ser utilizada na mineração.
     * @param dificulty A dificuldade desejada para a mineração.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void startMining(String msg, int dificulty) throws RemoteException {
        try {
            // Se já estiver minerando, não faz nada
            if (myMiner.isMining()) {
                return;
            }
            // Inicia o minerador
            myMiner.startMining(msg, dificulty);
            listener.onStartMining(msg, dificulty);
            // Informa a rede para iniciar a mineração
            for (RemoteInterface node : network) {
                if (AutomaticP2P.isAlive(node)) {
                    node.startMining(msg, dificulty);
                } else {
                    network.remove(node);
                    listener.onRemoveNode();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RemoteObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Para a mineração com o nonce fornecido.
     *
     * @param nonce O valor de nonce a ser utilizado para parar a mineração.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void stopMining(int nonce) throws RemoteException {
        // Se não estiver minerando, sai
        if (!myMiner.isMining()) {
            return;
        }
        // Para o minerador
        myMiner.stopMining(nonce);
        // Atualiza o bloco com o nonce
        this.miningBlock.setNonce(nonce);
        // Informa a rede para parar a mineração
        for (RemoteInterface node : network) {
            if (AutomaticP2P.isAlive(node)) {
                listener.onMessage("Stop Miner", node.getAdress());
                //node.stopMining(nonce);
            } else {
                network.remove(node);
                listener.onRemoveNode();
            }
        }
    }

    /**
     * Obtém o valor atual do nonce utilizado na mineração.
     *
     * @return O valor atual do nonce.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public int getNonce() throws RemoteException {
        return myMiner.getNonce();
    }

    /**
     * Obtém o ticket associado ao processo de mineração.
     *
     * @return O ticket atual.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public int getTicket() throws RemoteException {
        return myMiner.getTicket();
    }

    /**
     * Verifica se o processo de mineração está em execução.
     *
     * @return true se estiver minerando, false caso contrário.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public boolean isMining() throws RemoteException {
        return myMiner.isMining();
    }

    /**
     * Inicia o processo de mineração e aguarda a conclusão, retornando o nonce
     * calculado.
     *
     * @param msg A mensagem a ser utilizada na mineração.
     * @param dificulty A dificuldade desejada para a mineração.
     * @return O nonce calculado após a mineração.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public int mine(String msg, int dificulty) throws RemoteException {
        try {
            // Informação do cliente
            System.out.println("Mining to " + getClientName());
            // Inicia a mineração do bloco
            myMiner.startMining(msg, dificulty);
            // Aguarda a conclusão da mineração
            return myMiner.waitToNonce();
        } catch (Exception ex) {
            throw new RemoteException(ex.getMessage(), ex.getCause());
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::                R E D E   M I N E I R A 
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Adiciona um nó à rede.
     *
     * @param node Nó a ser adicionado à rede.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void addNode(RemoteInterface node) throws RemoteException {
        // Se a rede não contiver o nó, adiciona-o
        if (!network.contains(node)) {
            listener.onAddNode(node);
            // Adiciona o nó à rede
            network.add(node);
            // Adiciona o nosso nó à rede do nó remoto
            node.addNode(this);
            // Espalha o nó pela rede
            for (RemoteInterface remote : network) {
                // Adiciona o novo nó aos nós da rede remota
                remote.addNode(node);
            }
            // Notifica o ouvinte sobre a adição do nó
            listener.onAddNode(node);
        }
    }

    /**
     * Obtém a lista de nós na rede.
     *
     * @return Lista de nós na rede.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public CopyOnWriteArrayList<RemoteInterface> getNetwork() throws RemoteException {
        // Transforma a network em uma CopyOnWriteArrayList
        return new CopyOnWriteArrayList<>(network);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               TRANSACTIONS
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Define a lista de candidatos.
     *
     * @param list Lista de candidatos a ser definida.
     * @throws RemoteException Exceção do objeto remoto.
     */
    public void setCandidates(CopyOnWriteArrayList list) throws RemoteException {
        this.candidates = list;
    }

    /**
     * Define a lista de eleitores.
     *
     * @param list Lista de eleitores a ser definida.
     * @throws RemoteException Exceção do objeto remoto.
     */
    public void setElectors(ConcurrentHashMap list) throws RemoteException {
        this.electors = list;
    }

    /**
     * Define o número de eleitores.
     *
     * @param num Número de eleitores a ser definido.
     * @throws RemoteException Exceção do objeto remoto.
     */
    public void setNumElectors(int num) throws RemoteException {
        this.numElectors = num;
    }

    /**
     * Verifica se um eleitor já votou.
     *
     * @param pubKey Chave pública do eleitor.
     * @return true se o eleitor já votou, false caso contrário.
     * @throws RemoteException Exceção do objeto remoto.
     */
    public boolean checkVote(PublicKey pubKey) throws RemoteException {
        int tokens = 0;

        // Itera sobre os blocos na blockchain
        for (Block block : blockchain.getChain()) {
            try {
                // Itera sobre as transações no bloco
                for (String trans : block.getTransactions()) {
                    Transfer t = Transfer.fromText(trans);

                    try {
                        // Verifica se a chave pública do destinatário da transação é igual à chave pública do eleitor
                        if (SecurityUtils.getPublicKey(Base64.getDecoder().decode(t.getTo())).equals(pubKey)) {
                            tokens += t.getValue();
                        }
                    } catch (Exception ex) {
                        System.out.println("Não obteve a chave pública do To.");
                    }

                    try {
                        // Verifica se a chave pública do remetente da transação é igual à chave pública do eleitor
                        if (SecurityUtils.getPublicKey(Base64.getDecoder().decode(t.getFrom())).equals(pubKey)) {
                            tokens -= t.getValue();
                        }
                    } catch (Exception ex) {
                        System.out.println("Não obteve a chave pública do From.");
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(RemoteObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Itera sobre as transações na lista de transações local
        for (String trans : transactions.getList()) {
            Transfer t = Transfer.fromText(trans);
            try {
                SecurityUtils.getPublicKey(Base64.getDecoder().decode(t.getFrom()));

                try {
                    // Verifica se a chave pública do destinatário da transação é igual à chave pública do eleitor
                    if (SecurityUtils.getPublicKey(Base64.getDecoder().decode(t.getTo())).equals(pubKey)) {
                        tokens += t.getValue();
                    }
                } catch (Exception ex) {
                    System.out.println("NAO CONSEGUIU DESENCRIPTAR O TO");
                }

                try {
                    // Verifica se a chave pública do remetente da transação é igual à chave pública do eleitor
                    if (SecurityUtils.getPublicKey(Base64.getDecoder().decode(t.getFrom())).equals(pubKey)) {
                        tokens -= t.getValue();
                    }
                } catch (Exception ex) {
                    System.out.println("NAO CONSEGUIU DESENCRIPTAR O FROM");
                }
            } catch (Exception ex) {
                System.out.println("Não obteve a chave pública.");
            }
        }

        // Retorna true se o eleitor já votou (tem tokens), false caso contrário
        return tokens > 0;
    }

    /**
     * Confirma o voto de um eleitor.
     *
     * @param pubKey Chave pública do eleitor.
     * @return Mensagem de confirmação ou informação sobre o voto do eleitor.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public String confirmElectorVote(PublicKey pubKey) throws RemoteException {
        if (myMiner.isMining()) {
            return "Tente novamente."; // sair
        }

        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
        blocks = getBlockchain().getChain();
        listener.onMessage("TAMANHO DA CHAIN", String.valueOf(blocks.size()));

        int id = 0;
        // Itera sobre os blocos na blockchain
        //for (Block block : blocks) {
        for (int i = 0; i < blocks.size(); i++) {

            try {
                // Itera sobre as transações no bloco
                for (String trans : blocks.get(i).getTransactions()) {
                    Transfer t = Transfer.fromText(trans);

                    try {
                        // Verifica se a chave pública do remetente da transação é igual à chave pública do eleitor
                        if (SecurityUtils.getPublicKey(Base64.getDecoder().decode(t.getFrom())).equals(pubKey)) {
                            // Validação da integridade dos dados do bloco
                            MerkleTree merkleValidate = new MerkleTree(blocks.get(i).getTransactions());
                            byte[] decrypt = SecurityUtils.decrypt(Base64.getDecoder().decode(t.getTo()), masterPrivateKey());
                            String extractTo = new String(decrypt);
                            String merkleRoot = merkleValidate.getRoot();
                            boolean integrity = false;
                            if (blocks.get(i).getMerkleTreeRoot().equals(merkleRoot)) {
                                integrity = true;
                            }

                            // Retorna mensagem de confirmação ou informação sobre o voto do eleitor
                            return "O seu voto foi registado no bloco: "
                                    + "\nID : " + id
                                    + "\nO seu voto: " + extractTo
                                    + "\nHash do seu voto : " + t.getTo()
                                    + "\nMerkle root do bloco : " + blocks.get(i).getMerkleTreeRoot()
                                    + "\nVerificação da Merkle root : " + merkleRoot
                                    + "\nDados integros : " + integrity;
                        }
                    } catch (Exception ex) {
                        System.out.println("Não obteve a chave pública do To.");
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(RemoteObject.class.getName()).log(Level.SEVERE, null, ex);
            }
            id++;
        }

        return "O seu voto não foi registado.";
    }

    /**
     * Valida a assinatura de uma transação.
     *
     * @param transaction Transação a ser validada.
     * @return true se a assinatura for válida, false caso contrário.
     * @throws Exception Exceção relacionada à validação da assinatura.
     */
    public boolean validateSignature(String transaction) throws Exception {
        // Dados da transação
        Transfer decodedTransaction = Transfer.fromText(transaction);
        String from = decodedTransaction.getFrom();
        String to = decodedTransaction.getTo();
        String signature = decodedTransaction.getSignature();
        PublicKey pubKey = null;

        // Se a transação for originada pelo sistema, considera a assinatura válida
        if (from.equals("SYSTEM")) {
            return true;
        }

        int value = decodedTransaction.getValue();

        // Dados para array de bytes
        byte[] data = (from + to + value).getBytes();

        // Dados da assinatura para array de bytes
        byte[] sign = Base64.getDecoder().decode(signature);

        if (phase < 4) {
            // Chave pública do remetente para array de bytes
            byte[] pk = Base64.getDecoder().decode(from);
            pubKey = SecurityUtils.getPublicKey(pk);
        } else {
            // Verifica a assinatura usando a chave pública do sistema (masterPublicKey)
            return SecurityUtils.verifySign(data, sign, masterPublicKey());
        }

        // Verifica a assinatura usando a chave pública do remetente
        return SecurityUtils.verifySign(data, sign, pubKey);
    }

    public void endBlock() throws RemoteException {
        this.endBlock = true;
        buildNewBlock();
    }

    /**
     * Adiciona uma transação à lista de transações.
     *
     * @param transaction Transação a ser adicionada.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void addTransaction(String transaction) throws RemoteException {
        // Se a transação já existir, não faz nada
        if (this.transactions.contains(transaction)) {
            listener.onMessage("Duplicated Transaction", transaction);
            return;
        }

        try {
            // Se a assinatura da transação for válida
            if (validateSignature(transaction)) {

                // Adiciona a transação à lista de transações
                this.transactions.addTransaction(transaction);
                listener.onUpdateTransactions(transaction);
                listener.onMessage("addTransaction", getClientName());

                if (phase == 0) {
                    if ((this.transactions.getList().size() >= 1) && !myMiner.isMining()) {
                        merkle = new MerkleTree(this.transactions.getList());
                        buildNewBlock();
                        endBlock = false;

                    } else {
                        // Sincroniza a transação
                        for (RemoteInterface node : network) {
                            if (AutomaticP2P.isAlive(node)) {
                                node.synchonizeTransactions(this.transactions.getList());
                            } else {
                                network.remove(node);
                                listener.onRemoveNode();
                            }
                        }
                    }
                } else if (phase == 1) {
                    if ((this.transactions.getList().size() == candidates.size() || endBlock) && !myMiner.isMining()) {
                        merkle = new MerkleTree(this.transactions.getList());
                        buildNewBlock();
                        endBlock = false;
                    } else {
                        // Sincroniza a transação
                        for (RemoteInterface node : network) {
                            if (AutomaticP2P.isAlive(node)) {
                                node.synchonizeTransactions(this.transactions.getList());
                            } else {
                                network.remove(node);
                                listener.onRemoveNode();
                            }
                        }
                    }
                } else if (phase == 2) {
                    if ((this.transactions.getList().size() == electors.size() || endBlock) && !myMiner.isMining()) {
                        merkle = new MerkleTree(this.transactions.getList());
                        buildNewBlock();
                        endBlock = false;
                    } else {
                        // Sincroniza a transação
                        for (RemoteInterface node : network) {
                            if (AutomaticP2P.isAlive(node)) {
                                node.synchonizeTransactions(this.transactions.getList());
                            } else {
                                network.remove(node);
                                listener.onRemoveNode();
                            }
                        }
                    }
                } else if (phase == 3) {
                    if ((this.transactions.getList().size() >= Transactions.MAXTRANSACTIONS || endBlock) && !myMiner.isMining()) {
                        merkle = new MerkleTree(this.transactions.getList());
                        buildNewBlock();
                        endBlock = false;
                    } else {
                        // Sincroniza a transação
                        for (RemoteInterface node : network) {
                            if (AutomaticP2P.isAlive(node)) {
                                node.synchonizeTransactions(this.transactions.getList());
                            } else {
                                network.remove(node);
                                listener.onRemoveNode();
                            }
                        }
                    }
                } else if (phase == 4) {
                    if (!this.transactions.getList().isEmpty() && !myMiner.isMining()) {
                        merkle = new MerkleTree(this.transactions.getList());
                        buildNewBlock();
                    } else {
                        // Sincroniza a transação
                        for (RemoteInterface node : network) {
                            if (AutomaticP2P.isAlive(node)) {
                                node.synchonizeTransactions(this.transactions.getList());
                            } else {
                                network.remove(node);
                                listener.onRemoveNode();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("falhou");
        } finally {
            semaforo.release();
        }
    }

    /**
     * Obtém a lista de transações.
     *
     * @return Lista de transações.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public CopyOnWriteArrayList<String> getTransactionsList() throws RemoteException {
        return transactions.getList();
    }

    /**
     * Sincroniza a lista de transações com uma lista remota.
     *
     * @param list Lista remota de transações.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void synchonizeTransactions(CopyOnWriteArrayList<String> list) throws RemoteException {
        // Se as listas forem iguais, não faz nada
        if (list.equals(this.transactions.getList())) {
            return;
        }

        // Adiciona cada transação da lista remota localmente
        for (String string : list) {
            addTransaction(string);
        }

        // Sincroniza a transação na rede
        for (RemoteInterface node : network) {
            if (AutomaticP2P.isAlive(node)) {
                node.synchonizeTransactions(this.transactions.getList());
            } else {
                network.remove(node);
                listener.onRemoveNode();
            }
        }

        listener.onMessage("synchonizeTransactions", getClientName());
    }
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               B L O C K 
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    /**
     * Inicia o processo de mineração para um novo bloco.
     *
     * @param newBlock Novo bloco a ser minerado.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void startMiningBlock(Block newBlock) throws RemoteException {
        // Se o bloco já estiver em processo de mineração, interromper o processo
        if (miningBlock.getCurrentHash().equals(newBlock.getCurrentHash())) {
            System.out.println("Parou de minar!!!");
            listener.onMessage("Bloco duplicado", address);
            return;
        }

        // Informar sobre o início do processo de mineração para o novo bloco
        listener.onMessage("New Mining Block", newBlock + "");
        miningBlock = newBlock;

        // Remover as transações associadas ao bloco minerado
        System.out.println("Dados do bloco + " + newBlock.getData());
        CopyOnWriteArrayList<String> lst = (CopyOnWriteArrayList<String>) Serializer.base64ToObject(newBlock.getData());
        this.transactions.removeTransactions(lst);
        listener.onUpdateTransactions(null);

        // Espalhar o bloco pela rede
        for (RemoteInterface node : network) {
            if (AutomaticP2P.isAlive(node)) {
                node.startMiningBlock(miningBlock);
            } else {
                network.remove(node);
                listener.onRemoveNode();
            }
        }

        // Iniciar o processo de mineração para o bloco
        startMining(newBlock.getMiningData(), newBlock.getNumberOfZeros());
    }

    /**
     * Constrói um novo bloco para ser adicionado à blockchain.
     *
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void buildNewBlock() throws RemoteException {
        if (transactions.getList().size() == 0 && phase == 0) {
            return;
        }
        if (transactions.getList().size() == 0 && phase == 1) {
            return;
        }
        if (transactions.getList().size() == 0 && phase == 2) {
            return;
        }
        if (((transactions.getList().size() < Transactions.MAXTRANSACTIONS) && !endBlock) && phase == 3 || (transactions.getList().isEmpty() && endBlock && phase == 3)) {
            return;
        }
        if (transactions.getList().isEmpty() && phase == 4) {
            return;
        }

        listener.onUpdateBlockchain();
        //espalhar o bloco pela rede
        for (RemoteInterface node : network) {
            listener.onMessage("Synchronize blockchain", node.getAdress());
            node.synchonizeBlockchain(this);
        }

        String lastHash = blockchain.getLastBlockHash();
        //String lastHash = new LastBlock(this).getLastBlock().getCurrentHash();

        //dados do bloco são as lista de transaçoes 
        String data = Serializer.objectToBase64(transactions.getList());
        //Construir um novo bloco logado ao último

        System.out.println(
                "Data -> " + data + " \nlastHash -> " + lastHash + " \ndificulty -> " + Block.DIFICULTY);
        Block newBlock = new Block(data, merkle.getRoot(), lastHash, Block.DIFICULTY, phase);

        System.out.println(
                "novo bloco dificulty -> " + newBlock.DIFICULTY);
        System.out.println(
                "dados do novo bloco -> " + newBlock.getData());
        for (String string
                : this.transactions.getList()) {
            System.out.println("Transactions: " + string);

        }

        //Começar a minar o bloco
        startMiningBlock(newBlock);
    }

    /**
     * Atualiza o bloco em processo de mineração com um novo bloco.
     *
     * @param newBlock Novo bloco a ser atualizado.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void updateMiningBlock(Block newBlock) throws RemoteException {
        try {
            System.out.println("ATUALIZAR O BLOCO A MINAR!" + newBlock.isValid());

            // Se o novo bloco for válido e encaixar na blockchain local
            if (newBlock.isValid() && newBlock.getPreviousHash().equals(blockchain.getLastBlock().getCurrentHash())) {
                try {
                    // Adicionar o novo bloco à blockchain local
                    blockchain.addBlock(newBlock);
                    this.miningBlock = newBlock;

                    // Informar sobre a atualização da blockchain
                    listener.onUpdateBlockchain();

                    // Espalhar o bloco pela rede
                    for (RemoteInterface node : network) {
                        if (AutomaticP2P.isAlive(node)) {
                            node.updateMiningBlock(newBlock);
                        } else {
                            network.remove(node);
                            listener.onRemoveNode();
                        }
                    }
                } catch (BlockchainException ex) {
                    throw new RemoteException("Update mining Block", ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RemoteObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               B L O C K C H A I N
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Obtém o tamanho da blockchain.
     *
     * @return Tamanho da blockchain.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public int getBlockchainSize() throws RemoteException {
        return blockchain.getChain().size();
    }

    /**
     * Obtém a instância atual da blockchain.
     *
     * @return Instância da blockchain.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public BlockChain getBlockchain() throws RemoteException {
        return blockchain;
    }

    /**
     * Sincroniza a blockchain local com a blockchain de um nó remoto.
     *
     * @param syncNode Nó remoto a ser sincronizado.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void synchonizeBlockchain(RemoteInterface syncNode) throws RemoteException {
        // Se os tamanhos da blockchain forem diferentes
        if (syncNode.getBlockchainSize() != this.getBlockchainSize()) {
            // Se o tamanho local for menor, atualizar a blockchain local
            if (syncNode.getBlockchainSize() > this.getBlockchainSize()) {
                listener.onUpdateBlockchain();
                this.blockchain = syncNode.getBlockchain();
            } else if (syncNode.getBlockchainSize() < this.getBlockchainSize()) {
                // Se o tamanho local for maior, sincronizar com o nó remoto
                syncNode.synchonizeBlockchain(this);
            }
            // Sincronizar a blockchain pela rede
            for (RemoteInterface node : network) {
                if (AutomaticP2P.isAlive(node)) {
                    node.synchonizeBlockchain(this);
                } else {
                    network.remove(node);
                    listener.onRemoveNode();
                }
            }
        }
    }

    // Mapa para rastrear se um bloco com determinado timestamp já foi solicitado
    Map<Long, Boolean> flagLastBlock = new ConcurrentHashMap<>();

    /**
     * Obtém os blocos da blockchain até um determinado timestamp com uma
     * profundidade máxima.
     *
     * @param timeStamp Timestamp limite para obter blocos.
     * @param dept Profundidade atual na busca.
     * @param maxDep Profundidade máxima permitida.
     * @return Lista de blocos até o timestamp especificado.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public CopyOnWriteArrayList getLastBlock(long timeStamp, int dept, int maxDep) throws RemoteException {
        // Código com acesso exclusivo para a thread
        synchronized (this) {
            // Verificar se já respondi a esta solicitação
            if (flagLastBlock.get(timeStamp) != null) {
                return null;
            }
            // Verificar se atingi o limite de profundidade
            if (dept > maxDep) {
                return null;
            }
            // Responder e marcar como respondido
            flagLastBlock.put(timeStamp, Boolean.TRUE);
        }

        // Informar sobre o consenso alcançado
        listener.onConsensus("Last Block", address);

        // Calcular o último bloco na blockchain local
        CopyOnWriteArrayList myList = new CopyOnWriteArrayList();
        myList.add(blockchain.getLastBlock().getCurrentHash());
        System.out.println("Último Bloco -> " + blockchain.getLastBlock());

        // Iniciar threads para obter o último bloco de outros nós na rede
        Thread thr[] = new Thread[network.size()];
        for (int i = 0; i < thr.length; i++) {
            final int index = i;
            thr[i] = new Thread(() -> {
                try {
                    RemoteInterface node = network.get(index);
                    listener.onConsensus("Get Last Block List", node.getAdress());
                    List resp = node.getLastBlock(timeStamp, dept + 1, maxDep);
                    if (resp != null) {
                        myList.addAll(resp);
                    }
                } catch (Exception e) {
                    // Lidar com exceções, se necessário
                }
            });
            thr[i].start();
        }

        // Aguardar até que todas as threads terminem
        for (int i = 0; i < thr.length; i++) {
            try {
                thr[i].join();
            } catch (Exception e) {
                // Lidar com exceções, se necessário
            }
        }

        // Retornar a lista de blocos até o timestamp especificado
        return myList;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////
    ////                    ELECTION
    ////
    ////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Método chamado para iniciar a fase de candidatura no sistema de eleição.
     *
     * @param candidatos Lista de candidatos a serem registrados.
     * @param dataFaseRegisto Data de início da fase de registro.
     * @param dataFaseVotos Data de início da fase de votação.
     * @param dataFimEleicao Data de encerramento da eleição.
     * @throws RemoteException Exceção do objeto remoto.
     */

    /**
     * Obtém a lista de candidatos registrados na blockchain.
     *
     * @return Lista de candidatos registrados.
     */
    @Override
    public CopyOnWriteArrayList getCandidates() {
        CopyOnWriteArrayList<String> transfersFromBlocks = new CopyOnWriteArrayList<String>();
        CopyOnWriteArrayList<String> candidatesFromChain = new CopyOnWriteArrayList<String>();
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();

        blocks = blockchain.getChain();
        for (Block block : blocks) {
            if (block.getPhase() == 1) {
                try {
                    transfersFromBlocks = block.getTransactions();
                    for (String trans : transfersFromBlocks) {
                        Transfer transfer = Transfer.fromText(trans);
                        candidatesFromChain.add(transfer.getTo());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(RemoteObject.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        for (String candidate : candidatesFromChain) {
            System.out.println("CANDIDATOS DA LISTA A RETORNAR! : " + candidate);
        }
        return candidatesFromChain;
    }

    /**
     * Verifica o estado do eleitor com a chave pública fornecida.
     *
     * @param pubKey Chave pública do eleitor.
     * @return 0 se não existe, 1 se é destinatário, 2 se é remetente.
     */
    @Override
    public int stateElector(String pubKey) {
        CopyOnWriteArrayList<String> transfersFromBlocks = new CopyOnWriteArrayList<String>();
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();

        AtomicInteger exists = new AtomicInteger(0);
        blocks = blockchain.getChain();
        for (Block block : blocks) {
            try {
                transfersFromBlocks = block.getTransactions();
                for (String trans : transfersFromBlocks) {
                    Transfer transfer = Transfer.fromText(trans);
                    if (pubKey.equals(transfer.getTo())) {
                        exists = new AtomicInteger(1);
                    }
                    if (pubKey.equals(transfer.getFrom())) {
                        exists = new AtomicInteger(2);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(RemoteObject.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return exists.get();
    }

    /**
     * Obtém a chave pública da comissão eleitoral
     *
     * @return Chave pública
     */
    @Override
    public PublicKey masterPublicKey() {
        CopyOnWriteArrayList<String> transfersFromBlocks = new CopyOnWriteArrayList<String>();
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
        PublicKey pk = null;

        blocks = blockchain.getChain();
        for (Block block : blocks) {
            if (block.getPhase() == 1) {
                try {
                    transfersFromBlocks = block.getTransactions();
                    Transfer transfer = Transfer.fromText(transfersFromBlocks.get(1));
                    pk = SecurityUtils.getPublicKey(Base64.getDecoder().decode(transfer.getFrom()));

                } catch (Exception ex) {
                    Logger.getLogger(RemoteObject.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return pk;
    }

    /**
     * Obtém a chave privada mestra da fase de encerramento da eleição.
     *
     * @return Chave privada mestra.
     */
    @Override
    public PrivateKey masterPrivateKey() {
        CopyOnWriteArrayList<String> transfersFromBlocks = new CopyOnWriteArrayList<String>();
        CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<Block>();
        PrivateKey pk = null;

        blocks = blockchain.getChain();
        for (Block block : blocks) {
            if (block.getPhase() == 4) {
                try {
                    transfersFromBlocks = block.getTransactions();
                    Transfer transfer = Transfer.fromText(transfersFromBlocks.get(0));
                    pk = SecurityUtils.getPrivateKey(Base64.getDecoder().decode(transfer.getFrom()));

                } catch (Exception ex) {
                    Logger.getLogger(RemoteObject.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return pk;
    }


    /**
     * Obtém a blockchain atual.
     *
     * @return Instância da blockchain.
     */
    public BlockChain getBlockChain() {
        return blockchain;
    }

    /**
     * Obtém a fase atual do processo eleitoral.
     *
     * @return Número representando a fase.
     * @throws RemoteException Exceção do objeto remoto.
     */
    public int getPhase() throws RemoteException {
        return phase;
    }

    /**
     * Sincroniza a fase do sistema com os outros nós da rede.
     *
     * @param phase Fase a ser sincronizada.
     * @throws RemoteException Exceção do objeto remoto.
     */
    public void synchronizePhase(int phase) throws RemoteException {
        if (this.phase == phase) {
            return;
        }
        this.phase = phase;
        //mandar sincronizar a rede
        for (RemoteInterface node : network) {
            if (AutomaticP2P.isAlive(node)) {
                node.synchronizePhase(this.phase);
            } else {
                network.remove(node);
                listener.onRemoveNode();
            }
        }
        listener.onMessage("synchronize Phase", getClientName());
    }

    /**
     * Verifica se a fase atual é igual à fase fornecida.
     *
     * @param phase Fase a ser verificada.
     * @return True se as fases são iguais, false caso contrário.
     */
    public boolean checkPhase(int phase) {
        return this.phase == phase;
    }

    /**
     * Obtém os resultados para um candidato específico na fase de contagem de
     * votos.
     *
     * @param candidate Candidato para o qual os resultados são desejados.
     * @param masterKey Chave privada
     * @return Total de votos para o candidato.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public int getResults(String candidate, PrivateKey masterKey) throws RemoteException {
        int total = 0;

        for (Block block : blockchain.getChain()) {
            if (phase == 4 && block.getPhase() == 3) {
                try {
                    for (String transaction : block.getTransactions()) {
                        Transfer trans = Transfer.fromText(transaction);
                        System.out.println("ENCRYPT SIZE GETRESULTS: " + Base64.getDecoder().decode(trans.getTo()));
                        byte[] data = Base64.getDecoder().decode(trans.getTo());

                        byte[] decrypt = SecurityUtils.decrypt(data, masterKey);
                        String candidateText = new String(decrypt);

                        System.out.println("PARTIDO! ----------------> " + candidateText);
                        if (candidate.equals(candidateText)) {
                            total++;
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(RemoteObject.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return total;
    }

    /**
     * Define a fase da eleição
     *
     * @param phase Fase a ser definida.
     * @throws RemoteException Exceção do objeto remoto.
     */
    @Override
    public void setPhase(int phase) throws RemoteException {
        endBlock = false;
        if (this.phase == phase) {
            return;
        }
        this.phase = phase;

        //mandar sincronizar a rede
        for (RemoteInterface node : network) {
            if (AutomaticP2P.isAlive(node)) {
                listener.onMessage("Synchronize phase", node.getAdress());
                node.setPhase(phase);
            } else {
                network.remove(node);
                listener.onRemoveNode();
            }
        }
        listener.onMessage("synchonizePhase", getClientName());
    }

    /**
     * Retorna uma representação em texto do tipo de bloco com base na fase
     * atual.
     *
     * @return Tipo de bloco (Lista de Candidatos, Registo de Eleitores, Votos
     * de Eleitores, ou Bloco Inválido).
     */
    public String blockType() {
        switch (this.phase) {
            case 1:
                return "Lista de Candidatos";
            case 2:
                return "Registo de Eleitores";
            case 3:
                return "Votos de Eleitores";
            default:
                return "Bloco Inválido";
        }
    }

    /**
     * Implementação do método de ping remoto. Este método é chamado para
     * verificar a conectividade com o nó remoto. Emite uma mensagem indicando
     * que o nó está vivo, junto com o endereço do nó.
     *
     * @throws RemoteException Se ocorrer um problema durante a execução remota.
     */
    @Override
    public void ping() throws RemoteException {
        // Emite uma mensagem indicando que o nó está vivo, junto com o endereço do nó
        System.out.println("Estou vivo: " + address);
    }

}
