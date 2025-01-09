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
    private boolean juegoIniciado;

    public TablaJuego(Cliente cliente) {
        this.cliente = cliente;
        this.tabla = new Carta[FILAS][COLUMNAS];
        this.botones = new JButton[FILAS][COLUMNAS];
        this.juegoIniciado = false;
        setLayout(new GridLayout(FILAS, COLUMNAS, 5, 5));
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
                Image imgRedimensionada = tabla[i][j].getImagen()
                    .getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                boton.setIcon(new ImageIcon(imgRedimensionada));
                
                final int fila = i;
                final int columna = j;
                
                boton.addActionListener(e -> {
                    if (juegoIniciado && cliente.puedeMarcarCarta(tabla[fila][columna].getNumero())) {
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

    public void iniciarJuego() {
        this.juegoIniciado = true;
    }

    public boolean verificarVictoria(Set<Integer> cartasJugadas) {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (tabla[i][j].estaMarcada() && 
                    !cartasJugadas.contains(tabla[i][j].getNumero())) {
                    return false;
                }
            }
        }

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