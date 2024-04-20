/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electionledger.blockchain;


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author manso
 */
public class BlockchainException extends Exception {

    /**
     * Construtor que recebe uma mensagem de erro e a passa para o construtor da classe base.
     * 
     * @param msg mensagem de erro
     */
    public BlockchainException(String msg) {
        super(msg);
    }

    /**
     * Exibe uma caixa de diálogo com a mensagem de erro da exceção.
     */
    public void show() {
        JOptionPane.showMessageDialog(null, getMessage(),
                "Exceção na Blockchain", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Sobrescreve o método para imprimir a pilha de chamadas no log usando um Logger anônimo.
     */
    @Override
    public void printStackTrace() {
        Logger.getAnonymousLogger().log(Level.SEVERE, getMessage(), this);
    }

}
