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

import electionledger.blockchain.Block;
import electionledger.blockchain.BlockChain;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author manso, Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public interface RemoteInterface extends Remote {

    public static String OBJECT_NAME = "RemoteMiner";
    
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               M I N E I R O 
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public void startMining(String msg, int dificulty) throws RemoteException;

    public void stopMining(int nonce) throws RemoteException;

    public int getNonce() throws RemoteException;

    public int getTicket() throws RemoteException;

    public boolean isMining() throws RemoteException;

    public int mine(String msg, int dificulty) throws RemoteException;

    public String getHash(int nonce, String msg) throws RemoteException;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::                R E D E   M I N E I R A 
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    
    public void ping() throws RemoteException;
    
    public String getAdress() throws RemoteException;

    public void addNode(RemoteInterface node) throws RemoteException;

    public CopyOnWriteArrayList<RemoteInterface> getNetwork() throws RemoteException;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               TRANSACTIONS
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    
    public boolean validateSignature(String transaction) throws Exception;

    public void addTransaction(String transaction) throws RemoteException;

    public void synchonizeTransactions(CopyOnWriteArrayList<String> list) throws RemoteException;

    public CopyOnWriteArrayList<String> getTransactionsList() throws RemoteException;
    
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               B L O C K 
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public void startMiningBlock(Block bl) throws RemoteException;

    public void updateMiningBlock(Block bl) throws RemoteException;

    public void buildNewBlock() throws RemoteException;
    
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               B L O C K C H A I N
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public void synchonizeBlockchain(RemoteInterface syncNode) throws RemoteException;

    public int getBlockchainSize() throws RemoteException;

    public BlockChain getBlockchain() throws RemoteException;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::               C O N S E N S U S
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    
    public CopyOnWriteArrayList getLastBlock(long timeStamp, int dept, int maxDep) throws RemoteException;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::                                                         :::::::::::::
    //:::::                ELECTION 
    //:::::                                                         :::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    
    public void synchronizePhase(int phase) throws RemoteException;
    
    public int stateElector(String pubKey) throws RemoteException;

    public CopyOnWriteArrayList getCandidates() throws RemoteException;

    public PublicKey masterPublicKey() throws RemoteException;

    public PrivateKey masterPrivateKey() throws RemoteException;

    public int getPhase() throws RemoteException;

    public boolean checkPhase(int phase) throws RemoteException;

    public void setCandidates(CopyOnWriteArrayList list) throws RemoteException;

    public void setElectors(ConcurrentHashMap list) throws RemoteException;

    public boolean checkVote(PublicKey pubKey) throws RemoteException;
    
    public String confirmElectorVote(PublicKey pubKey) throws RemoteException;

    public int getResults(String candidate, PrivateKey masterKey) throws RemoteException;

    public void setPhase(int phase) throws RemoteException;
    
    public void endBlock() throws RemoteException;

    public String blockType() throws RemoteException;

    public void setNumElectors(int num) throws RemoteException;

}
