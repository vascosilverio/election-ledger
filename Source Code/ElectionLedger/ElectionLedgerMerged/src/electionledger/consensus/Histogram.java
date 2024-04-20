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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 06/12/2023, 17:59:01
 *
 * @author manso - computer
 */
public class Histogram {

    /**
     * Retorna o elemento mais comum numa lista.
     *
     * @param lst Lista de elementos
     * @return Elemento mais comum na lista
     */
    public Object getMostCommon(List lst) {
        // Mapa para armazenar a contagem de cada elemento
        Map<Object, Integer> hist = new HashMap<>();

        // Construir o histograma
        for (Object object : lst) {
            if (hist.get(object) == null) {
                hist.put(object, 1);
            } else {
                hist.put(object, hist.get(object) + 1);
            }
        }

        // Calcular o elemento mais frequente
        int max = 0;
        Object mostCommon = null;
        for (Object object : hist.keySet()) {
            if (hist.get(object) > max) {
                max = hist.get(object);
                mostCommon = object;
            }
        }
        return mostCommon;
    }

}
