package juego;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;

public class Servidor {
     private static final int PUERTO = 5000;
    private static final int MAX_JUGADORES = 16;
    private List<ManejadorCliente> clientes;
    private Set<Integer> cartasJugadas;
    private boolean juegoEnCurso;
    @SuppressWarnings("unused")
    private Random random;
    private int jugadoresRequeridos;
    private JFrame ventanaEstado;
    private JLabel labelEstado;

    public Servidor(int jugadoresRequeridos) {
        this.jugadoresRequeridos = jugadoresRequeridos;
        this.clientes = new ArrayList<>();
        this.cartasJugadas = new HashSet<>();
        this.juegoEnCurso = false;
        this.random = new Random();
        inicializarVentanaEstado();
    }

private void inicializarVentanaEstado() {
        ventanaEstado = new JFrame("Estado del Servidor");
        ventanaEstado.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaEstado.setSize(300, 150);
        ventanaEstado.setLayout(new BorderLayout());

        labelEstado = new JLabel("Esperando jugadores: 0/" + jugadoresRequeridos, SwingConstants.CENTER);
        labelEstado.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        ventanaEstado.add(labelEstado, BorderLayout.CENTER);

        ventanaEstado.setLocationRelativeTo(null);
        ventanaEstado.setVisible(true);
    }

    private void actualizarEstado() {
        SwingUtilities.invokeLater(() -> {
            labelEstado.setText("Esperando jugadores: " + clientes.size() + "/" + jugadoresRequeridos);
            if (juegoEnCurso) {
                labelEstado.setText("¡Juego en curso!");
            }
        });
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
            System.out.println("Esperando " + jugadoresRequeridos + " jugadores para iniciar");

            while (true) {
                if (clientes.size() < MAX_JUGADORES && !juegoEnCurso) {
                    Socket clienteSocket = serverSocket.accept();
                    ManejadorCliente manejador = new ManejadorCliente(clienteSocket);
                    clientes.add(manejador);
                    new Thread(manejador).start();
                    actualizarEstado();

                    broadcast("JUGADORES:" + clientes.size() + "/" + jugadoresRequeridos);

                    if (clientes.size() >= jugadoresRequeridos) {
                        Thread.sleep(1000); // Pequeña pausa antes de iniciar
                        iniciarJuego();
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private void iniciarJuego() {
        juegoEnCurso = true;
        actualizarEstado();
        broadcast("INICIO_JUEGO");
        
        new Thread(() -> {
            List<Integer> cartas = new ArrayList<>();
            for (int i = 1; i <= 52; i++) {
                cartas.add(i);
            }
            Collections.shuffle(cartas);

            try {
                Thread.sleep(2000);
                
                for (Integer carta : cartas) {
                    cartasJugadas.add(carta);
                    broadcast("CARTA:" + carta);

                    Thread.sleep(3000); // Espera 3 segundos entre cartas
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            }
        }).start();
    }

    private void broadcast(String mensaje) {
        for (ManejadorCliente cliente : clientes) {
            cliente.enviarMensaje(mensaje);
        }
    }

    private class ManejadorCliente implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("Error al crear streams: " + e.getMessage());
            }
        }

        public void enviarMensaje(String mensaje) {
            out.println(mensaje);
        }

        @Override
        public void run() {
            try {
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    if (mensaje.startsWith("VICTORIA")) {
                        broadcast("GANADOR:" + socket.getInetAddress());
                        juegoEnCurso = false;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error en conexión con cliente: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar socket: " + e.getMessage());
                }
                clientes.remove(this);
            }
        }
    }
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
                
                Servidor servidor = new Servidor(config.getJugadoresRequeridos());
                servidor.iniciar();
            }).start();
        });
    }
}