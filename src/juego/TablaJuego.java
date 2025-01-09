package juego;

import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class TablaJuego extends JPanel {
    private Carta[][] tabla;
    private static final int FILAS = 4;
    private static final int COLUMNAS = 4;
    private JButton[][] botones;
    private Cliente cliente;

    public TablaJuego(Cliente cliente) {
        this.cliente = cliente;
        this.tabla = new Carta[FILAS][COLUMNAS];
        this.botones = new JButton[FILAS][COLUMNAS];
        setLayout(new GridLayout(FILAS, COLUMNAS));
        inicializarTabla();
        crearBotones();
    }

    private void inicializarTabla() {
        List<Integer> numerosDisponibles = new ArrayList<>();
        for (int i = 1; i <= 52; i++) {
            numerosDisponibles.add(i);
        }
        Collections.shuffle(numerosDisponibles);

        int index = 0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                int numeroCarta = numerosDisponibles.get(index++);
                tabla[i][j] = new Carta(numeroCarta, 
                    "C:\\Users\\manto\\Downloads\\Loteria\\src\\imagenes_cartas\\");
            }
        }
    }

    private void crearBotones() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                JButton boton = new JButton();
                boton.setIcon(new ImageIcon(tabla[i][j].getImagen()
                    .getScaledInstance(100, 150, Image.SCALE_SMOOTH)));
                
                final int fila = i;
                final int columna = j;
                
                boton.addActionListener(e -> {
                    if (cliente.puedeMarcarCarta(tabla[fila][columna].getNumero())) {
                        tabla[fila][columna].marcar();
                        boton.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                        cliente.verificarVictoria();
                    }
                });

                botones[i][j] = boton;
                add(boton);
            }
        }
    }

    public boolean verificarVictoria(Set<Integer> cartasJugadas) {
        // Verifica si todas las cartas marcadas han sido jugadas
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (tabla[i][j].estaMarcada() && 
                    !cartasJugadas.contains(tabla[i][j].getNumero())) {
                    return false;
                }
            }
        }

        // Verifica si todas las cartas están marcadas
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (!tabla[i][j].estaMarcada()) {
                    return false;
                }
            }
        }
        return true;
    }
}
