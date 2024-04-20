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

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import electionledger.utils.Hash;
import electionledger.utils.MerkleTree;
import electionledger.utils.Serializer;
import electionledger.miner.MinerP2P;

/*
 *
 * @author manso, Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class Block implements Serializable {

    //header do bloco
    String previousHash;    // hash do bloco anterior
    String merkleTreeRoot;  // root da merkle tree
    int nonce;              // nonce da solução
    String currentHash;     // hash do bloco
    long timestamp;         // timestamp do bloco
    int phase;              // fase de eleição do bloco
    //data header
    MerkleTree merkleTree;  // merkle tree completa
    String data;            // dados do bloco em Base64

    public static int DIFICULTY = 4;
    int numberOfZeros = DIFICULTY;  // número de zeros do hash

    public Block(String message, String root, String previous, int zeros, int phase) {
        try {
            this.data = message;
            this.previousHash = previous;
            this.numberOfZeros = zeros;
            this.nonce = 0;
            this.currentHash = MinerP2P.getHash(getMiningData());
            this.timestamp = System.currentTimeMillis();
            this.phase = phase;
            this.merkleTreeRoot = root;

        } catch (Exception ex) {
            System.out.println("Erro ao criar novo bloco");
        }
    }

    /**
     * Retorna um array list concorrente das transações em base64 neste bloco.
     *
     * @return array list concorrente de transações em base64
     */
    public CopyOnWriteArrayList<String> getTransactions() throws Exception {
        CopyOnWriteArrayList<String> transactions = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<String> transactionsMerged = new CopyOnWriteArrayList<>();
        try {
            //conversao de base64 para array list concorrente
            transactions = (CopyOnWriteArrayList<String>) Serializer.base64ToObject(data);
        } catch (Exception ex) {
            transactions.add(data);
        }
        if (transactions != null) {
            for (String transaction : transactions) {
                transactionsMerged.add(transaction);
                System.out.println("TRANSACAO DECODED!!! -> ");
            }
        }
        return transactions;
    }

    /**
     * Construtor padrão da classe Block. Inicializa o timestamp com o valor
     * zero.
     */
    public Block() {
        this.timestamp = 0;
    }

    /**
     * Retorna os dados do cabeçalho do bloco.
     *
     * @return hash do bloco anterior, transações do bloco em base64,
     * dificuldade do bloco
     */
    public String getHeader() {
        return previousHash + data + numberOfZeros;
    }

    /**
     * Retorna os dados completos do bloco.
     *
     * @return informações formatadas do bloco, incluindo hash do bloco
     * anterior, transações, nonce, hash atual, número de zeros, raiz da árvore
     * de Merkle, fase e validade.
     */
    public String getInfo() {
        try {
            return "Anterior:" + previousHash
                    + "\nData    :" + data
                    + "\nNonce   :" + nonce
                    + "\nHash    :" + currentHash
                    + "\nNº Zeros:" + numberOfZeros
                    + "\nRaiz Merkle:" + merkleTreeRoot
                    + "\nFase    :" + phase
                    + "\nVálido  :" + isValid();
        } catch (Exception ex) {
            return "erro";
        }
    }

    /**
     * Atualiza o nonce deste bloco com a solução fornecida pelo minerador
     * concorrente. Calcula então o novo hash do bloco.
     *
     * @param nonce solução do bloco
     */
    public void setNonce(int nonce) {
        this.nonce = nonce;
        // calcular o hash
        this.currentHash = MinerP2P.getHash(nonce + getMiningData());
    }

    /**
     * Obtém o número de zeros utilizados na dificuldade do bloco.
     *
     * @return número de zeros
     */
    public int getNumberOfZeros() {
        return numberOfZeros;
    }

    /**
     * Retorna os dados do cabeçalho do bloco usados durante o processo de
     * mineração.
     *
     * @return hash do bloco anterior, transações do bloco em base64,
     * dificuldade do bloco
     */
    public final String getMiningData() {
        return previousHash + data + numberOfZeros;
    }

    /**
     * Obtém o hash do bloco anterior.
     *
     * @return hash do bloco anterior
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Obtém os dados (transações) do bloco.
     *
     * @return dados do bloco
     */
    public String getData() {
        return data;
    }

    /**
     * Obtém a fase atual do bloco.
     *
     * @return fase do bloco
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Obtém a raiz da árvore de Merkle do bloco.
     *
     * @return raiz da árvore de Merkle
     */
    public String getMerkleTreeRoot() {
        return merkleTreeRoot;
    }

    /**
     * Define a raiz da árvore de Merkle do bloco.
     *
     * @param merkleTreeRoot nova raiz da árvore de Merkle
     */
    public void setMerkleTreeRoot(String merkleTreeRoot) {
        this.merkleTreeRoot = merkleTreeRoot;
    }

    /**
     * Obtém a instância da árvore de Merkle associada a este bloco.
     *
     * @return instância da árvore de Merkle
     */
    public MerkleTree getMerkleTree() {
        return merkleTree;
    }

    /**
     * Define a instância da árvore de Merkle associada a este bloco.
     *
     * @param merkleTree nova instância da árvore de Merkle
     */
    public void setMerkleTree(MerkleTree merkleTree) {
        this.merkleTree = merkleTree;
    }

    /**
     * Obtém o nonce atual do bloco.
     *
     * @return nonce do bloco
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * Obtém o hash atual do bloco.
     *
     * @return hash atual do bloco
     */
    public String getCurrentHash() {
        return currentHash;
    }

    /**
     * Obtém o timestamp do bloco.
     *
     * @return timestamp do bloco
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Obtém o valor da versão de serialização da classe.
     *
     * @return versão de serialização
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * Calcula e retorna o hash do bloco com base no nonce, hash anterior, raiz
     * da árvore de Merkle e timestamp.
     *
     * @return hash calculado
     * @throws Exception se ocorrer um erro durante o cálculo do hash
     */
    public String calculateHash() throws Exception {
        //return Hash.getHash(nonce + previousHash + merkleTreeRoot + timestamp);
        return Hash.getHash(nonce + previousHash + timestamp);
    }

    /**
     * Sobrescreve o método toString para retornar os dados do bloco.
     *
     * @return dados do bloco
     */
    @Override
    public String toString() {
        return data;
    }

    /**
     * devolve true se o bloco tiver o currentHash com o número de zeros
     * definido em numberOfZeros ao início compara o hash atual ao hash
     * devolvido pelo miner concorrente
     *
     * @return
     */
    public boolean isValid() {
        try {

            //zeros do prefix
            String prefix = String.format("%0" + numberOfZeros + "d", 0);
            if (!currentHash.startsWith(prefix)) {
                throw new BlockchainException("Wrong prefix Hash" + currentHash);
            }
            //comparar o hash da mensagem com o hash actual
            String realHash = MinerP2P.getHash(nonce + getMiningData());
            if (!realHash.equals(currentHash)) {
                throw new BlockchainException("Corrupted data : " + data);
            }
            //OK
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202208220923L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////

}
