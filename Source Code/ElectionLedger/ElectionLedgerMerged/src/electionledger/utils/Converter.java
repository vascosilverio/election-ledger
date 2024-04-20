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
package electionledger.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created on 19/09/2023, 21:22:39
 *
 * @author manso - computer
 */
public class Converter {

    /**
     * Converte um array de bytes em uma representação hexadecimal.
     *
     * @param data O array de bytes a ser convertido.
     * @return Uma string hexadecimal representando o array de bytes.
     */
    public static String byteArrayToHex(byte[] data) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

    /**
     * Converte uma string hexadecimal em um array de bytes.
     *
     * @param hex A string hexadecimal a ser convertida.
     * @return Um array de bytes representado pela string hexadecimal.
     */
    public static byte[] hexToByteArray(String hex) {
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    /**
     * Converte um objeto para um array de bytes.
     *
     * @param data O objeto a ser convertido.
     * @return Um array de bytes representando o objeto.
     * @throws IOException Em caso de erro durante a conversão.
     */
    public static byte[] objectToByteArray(Object data) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(data);
            return bos.toByteArray();
        }
    }

    /**
     * Converte um array de bytes de volta para um objeto.
     *
     * @param bytes O array de bytes a ser convertido.
     * @return O objeto representado pelos bytes.
     * @throws Exception Em caso de erro durante a conversão.
     */
    public static Object byteArrayToObject(byte[] bytes) throws Exception {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

}
