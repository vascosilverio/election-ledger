//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
//::                                                                         ::
//::     Biosystems & Integrative Sciences Institute                         ::
//::     Faculty of Sciences University of Lisboa                            ::
//::     http://www.fc.ul.pt/en/unidade/bioisi                               ::
//::                                                                         ::
//::                                                                         ::
//::     I N S T I T U T O    P O L I T E C N I C O   D E   T O M A R        ::
//::     Escola Superior de Tecnologia de Tomar                              ::
//::     e-mail: manso@ipt.pt                                                ::
//::     url   : http://orion.ipt.pt/~manso                                  ::
//::                                                                         ::
//::     This software was build with the purpose of investigate and         ::
//::     learning.                                                           ::
//::                                                                         ::
//::                                                               (c)2021   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package electionledger.blockchain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import electionledger.utils.MerkleTree;

/**
 *
 * @author manso, Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class BlockChain implements Serializable {

    public static String DEFAULT_FILENAME = "blockchain.blc";

    CopyOnWriteArrayList<Block> chain;      //array list concorrente de blockchain
    CopyOnWriteArrayList<String> genesis;   //array list concorrente para o bloco inicial

    public BlockChain() {
        chain = new CopyOnWriteArrayList<>();
        genesis = new CopyOnWriteArrayList<>();
        //introduzir um bloco vazio        
        genesis.add("First Block");
        Block firstBlock = new Block("First Block", "", "Empty", 3, 0);
        chain.add(firstBlock);
    }

    /**
     * Retorna o último hash da chain. Se a chain estiver vazia, retorna o hash
     * formatado do bloco genesis.
     *
     * @return Último hash da chain
     */
    public String getLastBlockHash() {
        // Bloco genesis
        if (chain.isEmpty()) {
            return String.format("%08d", 0);
        }
        // Hash do último bloco na lista
        return chain.get(chain.size() - 1).currentHash;
    }

    /**
     * Retorna o último bloco da chain. Se a chain estiver vazia, retorna um
     * novo bloco vazio.
     *
     * @return Último bloco da chain
     */
    public Block getLastBlock() {
        if (chain.isEmpty()) {
            return new Block();
        }
        return chain.get(chain.size() - 1);
    }

    /**
     * Adiciona um novo bloco à blockchain.
     *
     * @param newBlock Bloco a ser adicionado
     * @throws BlockchainException Lança exceção se o bloco não for válido ou
     * não se encaixar no último bloco
     */
    public void addBlock(Block newBlock) throws BlockchainException {
        try {
            // Se o bloco não for válido
            if (!newBlock.isValid()) {
                throw new BlockchainException("Bloco corrompido");
            }
            // Se não se encaixar no último bloco
            if (!newBlock.getPreviousHash().equals(getLastBlock().getCurrentHash())) {
                throw new BlockchainException("Bloco não se encaixa no último");
            }
            // Adiciona o bloco à chain
            this.chain.add(newBlock);
        } catch (Exception ex) {
            Logger.getLogger(BlockChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adiciona um bloco verificado à chain.
     *
     * @param block Bloco verificado a ser adicionado
     */
    public void addVerifiedBlock(Block block) {
        chain.add(block);
    }

    /**
     * Retorna a lista completa da chain.
     *
     * @return Lista de blocos da chain
     */
    public CopyOnWriteArrayList<Block> getChain() {
        return chain;
    }

    /**
     * Define a lista de blocos da chain.
     *
     * @param chain Nova lista de blocos da chain
     */
    public void setChain(CopyOnWriteArrayList<Block> chain) {
        this.chain = chain;
    }

    /**
     * Retorna o bloco no índice especificado da chain.
     *
     * @param index Índice do bloco 
     * @return Bloco no índice 
     */
    public Block getBlock(int index) {
        return chain.get(index);
    }

    /**
     * Gera uma representação em string da blockchain.
     *
     * @return String representando a blockchain
     */
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append("Blockchain size = ").append(chain.size()).append("\n");
        for (Block block : chain) {
            txt.append(block.toString()).append("\n");
        }
        return txt.toString();
    }

    /**
     * Salva a blockchain num arquivo.
     *
     * @param fileName Nome do arquivo onde a blockchain será guardada
     * @throws Exception Lança exceção em caso de erro ao guardar
     */
    public void save(String fileName) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(chain);
        }
    }

    /**
     * Carrega a blockchain de um arquivo.
     *
     * @param fileName Nome do arquivo de onde a blockchain será carregada
     * @throws Exception Lança exceção em caso de erro ao carregar
     */
    public void load(String fileName) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.chain = (CopyOnWriteArrayList<Block>) in.readObject();
        }
    }

    /**
     * Verifica se a blockchain é válida. Valida os blocos e os links entre
     * eles.
     *
     * @return True se a blockchain for válida, False caso contrário
     * @throws Exception Lança exceção em caso de erro na validação
     */
    public boolean isValid() throws Exception {
        // Validação dos blocos
        for (Block block : chain) {
            if (!block.isValid()) {
                return false;
            }
        }
        // Validação dos links entre os blocos (começa no segundo bloco)
        for (int i = 1; i < chain.size(); i++) {
            // Hash do bloco anterior diferente do hash do bloco anterior
            if (!chain.get(i).previousHash.equals(chain.get(i - 1).currentHash)) {
                return false;
            }
        }
        return true;
    }
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202208221009L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
