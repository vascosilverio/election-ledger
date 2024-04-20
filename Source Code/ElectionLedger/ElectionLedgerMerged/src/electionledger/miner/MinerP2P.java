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
package electionledger.miner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manso, Rúben Garcia Nº16995, Vasco Silvério Nº22350
 *
 */
public class MinerP2P {

    String data;                //dados de minagem    
    AtomicInteger ticket;       //ticket para com números para testar 
    AtomicInteger globalNonce;  //noce que mina a mensagem 
    MiningListener listener;    //listener do mineiro   
    MinerTHR[] threads;         //array de thread para minar em paralelo
    ExecutorService exe;        // Executor das Threads

    public MinerP2P(MiningListener listener) {
        this.listener = listener;
    }

    /**
     * Inicia o processo de mineração.
     *
     * @param data Os dados a serem minerados.
     * @param dificuldade O nível de dificuldade desejado para encontrar o
     * nonce.
     * @throws InterruptedException Lança uma exceção se a thread for
     * interrompida durante a mineração.
     */
    public void startMining(String data, int dificuldade) throws InterruptedException {
        // Se já está minerando, não faz nada
        if (isMining()) {
            return;
        }
        // Notifica o ouvinte sobre o início da mineração
        if (listener != null) {
            listener.onStartMining(data, dificuldade);
        }
        this.data = data;
        // Inicializa o ticket com um número aleatório positivo
        Random rnd = new Random();
        ticket = new AtomicInteger(Math.abs(rnd.nextInt() / 2));
        // Configura os atributos    
        int numCores = Runtime.getRuntime().availableProcessors();
        threads = new MinerTHR[numCores];
        exe = Executors.newFixedThreadPool(numCores);
        // Inicializa o globalNonce
        globalNonce = new AtomicInteger();

        // Executa as threads
        for (int i = 0; i < numCores; i++) {
            threads[i] = new MinerTHR(ticket, globalNonce, dificuldade, data, listener);
            exe.execute(threads[i]);
        }
        // Fecha a pool de threads após a execução
        exe.shutdown();
    }

    /**
     * Para o processo de mineração.
     *
     * @param nonce O valor do nonce que indica a parada da mineração.
     */
    public void stopMining(int nonce) {
        globalNonce.set(nonce);
    }

    /**
     * Obtém o valor atual do ticket.
     *
     * @return O valor do ticket.
     */
    public int getTicket() {
        return ticket.get();
    }

    /**
     * Obtém os dados sendo minerados.
     *
     * @return Os dados sendo minerados.
     */
    public String getData() {
        return data;
    }

    /**
     * Verifica se o processo de mineração está em andamento.
     *
     * @return true se estiver minerando, false caso contrário.
     */
    public boolean isMining() {
        return globalNonce != null && globalNonce.get() <= 0;
    }

    /**
     * Obtém o valor atual do nonce.
     *
     * @return O valor atual do nonce.
     */
    public int getNonce() {
        return globalNonce.get();
    }

    /**
     * *
     * Espera que as threads executem terminem o trabalho e devolve o nonce
     *
     * @return
     * @throws Exception
     */
    public int waitToNonce() throws Exception {
        exe.awaitTermination(1, TimeUnit.DAYS);
        return globalNonce.get();
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::      THREAD         :::::::::::::::::::::::::::::::::    
    ///////////////////////////////////////////////////////////////////////////
    private class MinerTHR extends Thread {

        AtomicInteger myTicket;     //ticket atribuído à thread.
        AtomicInteger myNonce;      //nonce atribuído à thread.
        int dificulty;              //nível de dificuldade para encontrar o nonce.
        String myData;              //dados a serem minerados.
        MiningListener listener;    //ouvinte para notificações de mineração.

        public MinerTHR(AtomicInteger ticket, AtomicInteger nonce, int dificulty, String data, MiningListener listener) {
            this.myTicket = ticket;
            this.myNonce = nonce;
            this.dificulty = dificulty;
            this.myData = data;
            this.listener = listener;
        }

        /**
         * Para a thread definindo o valor do nonce.
         *
         * @param number O valor do nonce a ser definido.
         */
        public void stop(int number) {
            myNonce.set(number);
            interrupt();
        }

        /**
         * Método principal da thread de mineração.
         */
        public void run() {
            // String de zeros correspondentes ao nível de dificuldade
            String zeros = String.format("%0" + dificulty + "d", 0);
            int number;
            while (myNonce.get() == 0) {
                // Calcula o hash do bloco   
                number = myTicket.getAndIncrement();
                // Notifica o ouvinte a cada 100 milissegundos
                if (System.currentTimeMillis() % 100 == 0) {
                    listener.onMining(number);
                }
                String hash = getHash(number + myData);
                // Verifica se o hash começa com zeros conforme o nível de dificuldade
                if (hash.startsWith(zeros)) {
                    listener.onNounceFound(number);
                    System.out.println("NOUNCE ENCONTRADO! HASH -----> + " + hash);
                    myNonce.set(number);
                }
            }
            // Notifica o ouvinte sobre a interrupção da mineração
            if (listener != null) {
                listener.onStopMining(myNonce.get());
            }
        }
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::      I N T E G R I T Y         :::::::::::::::::::::::::::::::::    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Obtém o hash SHA-256 de uma string de entrada.
     *
     * @param data A string de entrada para a qual o hash deve ser calculado.
     * @return O hash SHA-256 em formato Base64.
     */
    public static String getHash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MinerP2P.class.getName()).log(Level.SEVERE, null, ex);
            return data;
        }
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202209281113L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
