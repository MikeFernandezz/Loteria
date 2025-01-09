package juego;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Carta {
    private int numero;
    private Image imagen;
    private boolean marcada;

    public Carta(int numero, String rutaImagen) {
        this.numero = numero;
        this.marcada = false;
        cargarImagen(rutaImagen);
    }

    private void cargarImagen(String rutaImagen) {
        try {
            ImageIcon icon = new ImageIcon(rutaImagen + numero + ".png");
            this.imagen = icon.getImage();
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de la carta " + numero);
        }
    }

    public int getNumero() { return numero; }
    public Image getImagen() { return imagen; }
    public boolean estaMarcada() { return marcada; }
    public void marcar() { this.marcada = true; }
}
