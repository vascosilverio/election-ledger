///****************************************************************************/
///****************************************************************************/
///****     Copyright (C) 2012                                             ****/
///****     Antonio Manuel Rodrigues Manso                                 ****/
///****     e-mail: manso@ipt.pt                                           ****/
///****     url   : http://orion.ipt.pt/~manso                             ****/
///****     Instituto Politecnico de Tomar                                 ****/
///****     Escola Superior de Tecnologia de Tomar                         ****/
///****************************************************************************/
///****************************************************************************/
///****     This software was built with the purpose of investigating      ****/
///****     and learning. Its use is free and is not provided any          ****/
///****     guarantee or support.                                          ****/
///****     If you met bugs, please, report them to the author             ****/
///****                                                                    ****/
///****************************************************************************/
///****************************************************************************/
package electionledger.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Conversao entre objectos e arrays de bytes
 *
 * @author ZULU
 */
public class Serializer {

    /**
     * converte um objecto num array de bytes
     *
     * @param obj objecto
     * @return array de bytes
     * @throws IOException
     */
    public static byte[] objectToByteArray(Object obj) throws IOException {
        //stream em memoria
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        //stream de objectos
        ObjectOutputStream o = new ObjectOutputStream(b);
        //escrever o objecto na stream
        o.writeObject(obj);
        //converter para bytes
        return b.toByteArray();
    }

    /**
     * converte um objecto num array de bytes
     *
     * @param obj objecto
     * @return array de bytes
     * @throws IOException
     */
    public static String objectToBase64(Object obj) {
        try {
            return Base64.getEncoder().encodeToString(objectToByteArray(obj));
        } catch (IOException ex) {
            return "ERROR in Serializer objectToBase64";
        }
    }

    /**
     * converte array de bytes num objecto
     *
     * @param bytes array de bytes
     * @return objecto
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object byteArrayToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        //stream em memoria
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        //stream de objectos
        ObjectInputStream o = new ObjectInputStream(b);
        //ler o objecto da stream
        return o.readObject();
    }

    /**
     * converte array de bytes num objecto
     *
     * @param b64 base 64
     * @return objecto
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object base64ToObject(String b64) {

        try {
            return byteArrayToObject(Base64.getDecoder().decode(b64));
        } catch (Exception ex) {
            Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
