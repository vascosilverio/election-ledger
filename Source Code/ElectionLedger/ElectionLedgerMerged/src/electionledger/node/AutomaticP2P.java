/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package electionledger.node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import electionledger.utils.RMI;

/**
 *
 * @author manso, Rúben Garcia Nº16995, Vasco Silvério Nº22350
 */
public class AutomaticP2P {

    public static RemoteInterface node;

    /**
     * Envia periodicamente o endereço para a rede multicast.
     *
     * @param remoteAddress O endereço remoto a ser enviado.
     * @param interval O intervalo de envio em milissegundos.
     */
    public static void sendAddress(String remoteAddress, int interval) {
        new Thread(
                () -> {
                    try {
                        InetAddress address = InetAddress.getByName("230.0.0.1");
                        int port = 4446;
                        // Abrir um socket na máquina atual 
                        DatagramSocket socket = new DatagramSocket();
                        // Construção do pacote
                        DatagramPacket packet = new DatagramPacket(
                                remoteAddress.getBytes(), remoteAddress.length(), address, port);
                        // Enviar o pacote para a rede
                        while (true) {
                            socket.send(packet);
                            Thread.currentThread().sleep(interval);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(AutomaticP2P.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        ).start();
    }

    /**
     * Escuta os nós na rede multicast e adiciona à lista de nós remotos.
     *
     * @param remote A interface remota utilizada para adicionar nós à lista.
     */
    public static void listenToNodes(RemoteObject remote) {
        new Thread(
                () -> {
                    try {
                        int port = 4446;
                        // Porta de escuta 4446
                        MulticastSocket socket = new MulticastSocket(port);
                        // Juntar ao grupo 230.0.0.1
                        InetAddress address = InetAddress.getByName("230.0.0.1");
                        socket.joinGroup(address);
                        // Construção do pacote
                        byte[] buf = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);

                        while (true) {
                            System.out.println("A aguardar mensagens");
                            // Obter a mensagem
                            socket.receive(packet);
                            // Processar a mensagem
                            String received = new String(packet.getData(), 0, packet.getLength());

                            RemoteInterface remoteNode = (RemoteInterface) RMI.getRemote(received);
                            remote.addNode(remoteNode);

                            System.out.println("MULTICAST  Mensagem : " + received);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AutomaticP2P.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NotBoundException ex) {
                        Logger.getLogger(AutomaticP2P.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        ).start();
    }

    /**
     * Obtém o endereço dos nós na rede multicast.
     *
     * @return O endereço do nó.
     */
    public static String getNodes() {
        try {
            boolean wait = true;
            int port = 4446;
            // Porta de escuta 4446
            MulticastSocket socket = new MulticastSocket(port);
            // Juntar ao grupo 230.0.0.1
            InetAddress address = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(address);
            // Construção do pacote
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (wait) {
                System.out.println("A aguardar mensagens");
                // Obter a mensagem
                socket.receive(packet);
                // Processar a mensagem
                String received = new String(packet.getData(), 0, packet.getLength());

                if (received != null) {
                    wait = false;
                }

                RemoteInterface remoteNode = (RemoteInterface) RMI.getRemote(received);

                System.out.println("NÓ: " + remoteNode.getAdress());
                System.out.println("MULTICAST  Mensagem : " + received);

                return remoteNode.getAdress();
            }
        } catch (IOException ex) {
            Logger.getLogger(AutomaticP2P.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(AutomaticP2P.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Define o nó remoto conectado. Este método permite atribuir o nó remoto
     * que está atualmente conectado.
     *
     * @param nodeAuth O nó remoto a ser atribuído como o nó conectado.
     */
    public static void setConnectedNode(RemoteInterface nodeAuth) {
        node = nodeAuth;
    }

    /**
     * Obtém o nó remoto atualmente conectado. Retorna o nó remoto que está
     * atualmente atribuído como o nó conectado.
     *
     * @return O nó remoto conectado.
     */
    public static RemoteInterface getConnectedNode() {
        return node;
    }

    /**
     * Verifica se um nó remoto está ativo. Este método verifica se o nó remoto
     * está ativo, realizando um ping e retornando verdadeiro se bem-sucedido,
     * ou falso caso ocorra uma exceção.
     *
     * @param node O nó remoto a ser verificado.
     * @return Verdadeiro se o nó estiver ativo, falso caso contrário.
     */
    public static boolean isAlive(RemoteInterface node) {
        try {
            // Tenta realizar um ping no nó remoto
            node.ping();
            // Retorna verdadeiro se bem-sucedido
            return true;
        } catch (Exception ex) {
            // Retorna falso se ocorrer uma exceção (indicando que o nó não está ativo)
            return false;
        }
    }
}
