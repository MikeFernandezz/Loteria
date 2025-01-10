package juego;

import java.net.*;
import java.io.*;
import java.util.*;

public class Servidor {
    private static final int PUERTO = 5000;
    private static final int MAX_JUGADORES = 16;
    private final int jugadoresRequeridos;
    private List<ManejadorCliente> clientes;
    private Set<Integer> cartasJugadas;
    private boolean juegoEnCurso;
    @SuppressWarnings("unused")
    private Random random;
    private int sigClienteId = 1;

    public Servidor(int jugadoresRequeridos) {
        this.jugadoresRequeridos = jugadoresRequeridos;
        this.clientes = new ArrayList<>();
        this.cartasJugadas = new HashSet<>();
        this.juegoEnCurso = false;
        this.random = new Random();
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
            System.out.println("Esperando " + jugadoresRequeridos + " jugadores...");

            while (true) {
                if (clientes.size() < MAX_JUGADORES && !juegoEnCurso) {
                    Socket clienteSocket = serverSocket.accept();
                    ManejadorCliente manejador = new ManejadorCliente(clienteSocket);
                    clientes.add(manejador);
                    new Thread(manejador).start();
                    
                    // Notificar a todos los clientes el número de jugadores actual
                    broadcast("ESPERANDO:" + clientes.size() + ":" + jugadoresRequeridos);

                    if (clientes.size() >= jugadoresRequeridos) {
                        iniciarJuego();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private void iniciarJuego() {
        juegoEnCurso = true;
        new Thread(() -> {
            List<Integer> cartas = new ArrayList<>();
            for (int i = 1; i <= 52; i++) {
                cartas.add(i);
            }
            Collections.shuffle(cartas);

            for (Integer carta : cartas) {
                cartasJugadas.add(carta);
                broadcast("CARTA:" + carta);
                try {
                    Thread.sleep(300); // Espera 3 segundos entre cartas
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
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
        private int clienteId;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
            this.clienteId = sigClienteId++;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                // Enviar ID al cliente cuando se conecta
                out.println("ID:" + clienteId);
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
                        broadcast("GANADOR:" + clienteId);
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
}