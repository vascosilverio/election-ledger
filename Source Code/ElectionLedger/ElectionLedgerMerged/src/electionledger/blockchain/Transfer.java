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
//::                                                               (c)2022   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package electionledger.blockchain;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Base64;
import electionledger.utils.SecurityUtils;
import electionledger.utils.Serializer;

/**
 *
 * @author manso, Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class Transfer implements Serializable {

    private String from;
    private String to;
    private int value;
    private String signature;

    public Transfer(String from, String to, int value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public Transfer(String from, String to, int value, PrivateKey pkey) throws Exception {
        this.from = from;
        this.to = to;
        this.value = value;
        byte[] sign = SecurityUtils.sign((from + to + value).getBytes(), pkey);
        this.signature = Base64.getEncoder().encodeToString(sign);
    }

    /**
     * Retorna o valor da transferência.
     *
     * @return O valor da transferência
     */
    public int getValue() {
        return value;
    }

    /**
     * Retorna a assinatura associada à transferência.
     *
     * @return A assinatura associada à transferência
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Define a assinatura associada à transferência.
     *
     * @param signature A nova assinatura a ser definida
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * Define o valor da transferência.
     *
     * @param value O novo valor a ser definido
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Retorna o remetente da transferência.
     *
     * @return O remetente da transferência
     */
    public String getFrom() {
        return from;
    }

    /**
     * Define o remetente da transferência.
     *
     * @param from O novo remetente a ser definido
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Retorna o destinatário da transferência.
     *
     * @return O destinatário da transferência
     */
    public String getTo() {
        return to;
    }

    /**
     * Define o destinatário da transferência.
     *
     * @param to O novo destinatário a ser definido
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Retorna uma representação em string da transferência.
     *
     * @return Uma string representando a transferência
     */
    @Override
    public String toString() {
        return "From : " + from
                + "\nTo   : " + to
                + "\nValue: " + value;
    }

    /**
     * Retorna uma representação em texto codificado da transferência.
     *
     * @return Uma string em formato Base64 representando a transferência
     */
    public String toText() {
        return Serializer.objectToBase64(this);
    }

    /**
     * Converte uma string em formato Base64 de volta para um objeto Transfer.
     *
     * @param obj A string em formato Base64 a ser convertida
     * @return Um objeto Transfer reconstruído a partir da string Base64
     */
    public static Transfer fromText(String obj) {
        return (Transfer) Serializer.base64ToObject(obj);
    }

    /**
     * Retorna o código de hash da representação em texto da transferência.
     *
     * @return O código de hash da representação em texto da transferência
     */
    @Override
    public int hashCode() {
        return toText().hashCode();
    }

    /**
     * Verifica se dois objetos Transfer são iguais, comparando suas
     * representações em texto.
     *
     * @param t O objeto Transfer a ser comparado
     * @return true se os objetos Transfer forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object t) {
        if (t instanceof Transfer) {
            return this.toText().equals(((Transfer) t).toText());
        }
        return false;
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202312050910L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2023  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////

}
