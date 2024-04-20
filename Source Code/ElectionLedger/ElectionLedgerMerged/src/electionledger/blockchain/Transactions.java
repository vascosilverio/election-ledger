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
package electionledger.blockchain;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 05/12/2023, 09:10:56
 *
 * @author IPT - computer
 * @version 1.0
 */
public class Transactions implements Serializable {

    public static int MAXTRANSACTIONS = 2; //Número máximo de transações permitidas.

    /**
     * Lista de transações usando uma implementação segura para threads.
     */
    private final CopyOnWriteArrayList<String> list;

    /**
     * Construtor padrão que inicializa a lista de transações.
     */
    public Transactions() {
        list = new CopyOnWriteArrayList<>();
    }

    /**
     * Obtém a lista de transações.
     * 
     * @return lista de transações
     */
    public CopyOnWriteArrayList<String> getList() {
        return list;
    }

    /**
     * Verifica se a lista de transações contém uma transação específica.
     * 
     * @param trans transação a ser verificada
     * @return true se a transação estiver na lista, false caso contrário
     */
    public boolean contains(String trans) {
        return list.contains(trans);
    }

    /**
     * Adiciona uma nova transação à lista, desde que ela ainda não esteja presente.
     * 
     * @param newTrans nova transação a ser adicionada
     */
    public void addTransaction(String newTrans) {
        if (!list.contains(newTrans)) {
            list.add(newTrans);
        }
    }

    /**
     * Remove um conjunto de transações da lista.
     * 
     * @param lst lista de transações a ser removida
     */
    public void removeTransactions(CopyOnWriteArrayList<String> lst) {
        list.removeAll(lst);
    }

    /**
     * Sincroniza a lista de transações com outra lista, adicionando transações ausentes.
     * 
     * @param other outra lista de transações
     */
    public void synchronize(CopyOnWriteArrayList<String> other) {
        for (String trans : other) {
            addTransaction(trans);
        }
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202312050910L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2023  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
