package juego;

import javax.swing.*;
import java.awt.*;

public class ConfiguracionServidor extends JFrame {
    private JSpinner spinnerJugadores;
    private JButton btnIniciar;
    private int jugadoresRequeridos;
    private boolean configuracionCompletada;

    public ConfiguracionServidor() {
        configuracionCompletada = false;
        initComponents();
    }

    private void initComponents() {
        setTitle("Configuración del Servidor de Lotería");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.add(new JLabel("Número de jugadores requeridos:"));
        SpinnerModel spinnerModel = new SpinnerNumberModel(2, 1, 16, 1);
        spinnerJugadores = new JSpinner(spinnerModel);
        spinnerPanel.add(spinnerJugadores);
        mainPanel.add(spinnerPanel);

        btnIniciar = new JButton("Iniciar Servidor");
        btnIniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIniciar.addActionListener(e -> {
            jugadoresRequeridos = (int) spinnerJugadores.getValue();
            configuracionCompletada = true;
            dispose();
        });
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(btnIniciar);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    public int getJugadoresRequeridos() {
        return jugadoresRequeridos;
    }

    public boolean isConfiguracionCompletada() {
        return configuracionCompletada;
    }
}
