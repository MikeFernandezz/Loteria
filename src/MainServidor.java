import javax.swing.SwingUtilities;
import juego.ConfiguracionServidor;
import juego.Servidor;

public class MainServidor {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConfiguracionServidor config = new ConfiguracionServidor();
            config.setVisible(true);
            
            new Thread(() -> {
                while (!config.isConfiguracionCompletada()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                new Servidor(config.getJugadoresRequeridos()).iniciar();
            }).start();
        });
    }
}
