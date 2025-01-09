import javax.swing.SwingUtilities;

import juego.ConfiguracionServidor;

public class MainServidor {
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        ConfiguracionServidor config = new ConfiguracionServidor();
        config.setVisible(true);
    });
}
}
