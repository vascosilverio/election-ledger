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
package electionledger.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author share
 */
public class ImageUtils {

    /**
     * redimensiona um icone
     *
     * @param icon icone
     * @param width largura
     * @param height altura
     * @return icone redimensionado
     */
    public static ImageIcon resizeIcon(Icon icon, int width, int height) {
        //imagem do icone
        Image img = ((ImageIcon) icon).getImage();
        //redimensionar a imagem
        img = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        //retornar um novo icone
        return new ImageIcon(img);
    }

    /**
     * Guarda uma imagem num ficheiro com o formato jpg
     *
     * @param icon icone da imagem
     * @param filename nome do ficheiro
     * @throws java.io.IOException
     */
    public static void saveIcon(ImageIcon icon, String filename) throws IOException {
        //normalizar o nome do ficheiro
        if (!filename.endsWith(".jpg")) {
            filename += ".jpg";
        }
        Files.write(Paths.get(filename), iconToByteArray(icon));
    }

    /**
     * converte um icone para um array de bytes
     *
     * @param icon icone com a imagem
     * @return array de bytes com a imagem
     * @throws java.io.IOException
     */
    public static byte[] iconToByteArray(Icon icon) throws IOException {
        //tela de desenho
        BufferedImage bi = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        //desenhar a imagem
        icon.paintIcon(null, g, 0, 0);
        //output stream em memória 
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //guardar a imagem na memória
        ImageIO.write(bi, "jpg", out);
        g.dispose();
        //retornar os bytes da memoria
        return out.toByteArray();
    }

}
