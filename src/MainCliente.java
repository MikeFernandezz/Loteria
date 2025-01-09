import javax.swing.SwingUtilities;

import juego.Cliente;

public class MainCliente {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Cliente().setVisible(true);
        });
    }
}