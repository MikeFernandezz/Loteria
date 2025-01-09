package juego;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente extends JFrame {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private TablaJuego tablaJuego;
    private Set<Integer> cartasJugadas;
    private JLabel cartaActual;

    public Cliente() {
        this.cartasJugadas = new HashSet<>();
        inicializarInterfaz();
        conectarAlServidor();
        iniciarRecepcionMensajes();
    }

    private void inicializarInterfaz() {
        setTitle("Lotería Mexicana - Jugador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tablaJuego = new TablaJuego(this);
        add(tablaJuego, BorderLayout.CENTER);

        cartaActual = new JLabel("Esperando carta...", SwingConstants.CENTER);
        cartaActual.setPreferredSize(new Dimension(200, 200));
        add(cartaActual, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void conectarAlServidor() {
        try {
            socket = new Socket(HOST, PUERTO);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al conectar con el servidor: " + e.getMessage());
            System.exit(1);
        }
    }

    private void iniciarRecepcionMensajes() {
        new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    if (mensaje.startsWith("CARTA:")) {
                        int numeroCarta = Integer.parseInt(mensaje.split(":")[1]);
                        cartasJugadas.add(numeroCarta);
                        actualizarCartaActual(numeroCarta);
                    } else if (mensaje.startsWith("GANADOR:")) {
                        mostrarGanador(mensaje.split(":")[1]);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error en la conexión: " + e.getMessage());
            }
        }).start();
    }

    private void actualizarCartaActual(int numeroCarta) {
        SwingUtilities.invokeLater(() -> {
            ImageIcon icon = new ImageIcon(
                "C:\\Users\\manto\\Downloads\\Loteria\\src\\imagenes_cartas\\" + 
                numeroCarta + ".png");
            Image img = icon.getImage().getScaledInstance(150, 200, 
                Image.SCALE_SMOOTH);
            cartaActual.setIcon(new ImageIcon(img));
        });
    }

    private void mostrarGanador(String ganador) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "¡El jugador " + ganador + " ha ganado!");
            System.exit(0);
        });
    }

    public boolean puedeMarcarCarta(int numeroCarta) {
        return cartasJugadas.contains(numeroCarta);
    }

    public void verificarVictoria() {
        if (tablaJuego.verificarVictoria(cartasJugadas)) {
            out.println("VICTORIA");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Cliente().setVisible(true);
        });
    }
}