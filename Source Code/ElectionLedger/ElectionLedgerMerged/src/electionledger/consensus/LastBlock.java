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
package electionLedger.consensus;

import electionledger.node.RemoteObject;
import electionledger.blockchain.Block;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * Created on 06/12/2023, 18:06:52
 *
 * @author manso - computer
 */
public class LastBlock {

    RemoteObject myNode;    //Objeto remoto

    public LastBlock(RemoteObject myNode) {
        this.myNode = myNode;
    }

    /**
     * Obtém o último bloco do nó.
     *
     * @return Último bloco do nó
     * @throws RemoteException Lança exceção em caso de erro no objeto remoto
     */
    public Block getLastBlock() throws RemoteException {
        // Obtém uma lista de blocos do nó usando informações de data, nonce e dificuldade
        List<Block> blks = myNode.getLastBlock(new Date().getTime(), 0, 7);

        // Retorna o bloco mais comum da lista usando um histograma
        return (Block) new Histogram().getMostCommon(blks);
    }
}
