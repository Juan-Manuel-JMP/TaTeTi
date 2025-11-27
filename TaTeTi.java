import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

@SuppressWarnings("unused")
public class TaTeTi {

    int anchoTablero = 800; 
    int altoTablero = 650;

    JFrame ventana = new JFrame("Ta-Te-Ti ❤️");
    JLabel etiquetaTexto = new JLabel();
    JPanel panelTexto = new JPanel();
    JPanel panelTablero = new JPanel();
    JPanel panelDerecho = new JPanel(); 

    JButton[][] tablero = new JButton[3][3];
    JButton botonReiniciar = new JButton("Reiniciar");
    JButton botonPVP = new JButton("Jugador vs Jugador");
    JButton botonPVC = new JButton("Jugador vs CPU");

    String jugadorX = "X";
    String jugadorO = "O";
    String jugadorActual = jugadorX;

    boolean juegoTerminado = false;
    int turnos = 0;

    int victoriasX = 0;
    int victoriasO = 0;

    boolean contraCPU = false;
    boolean cpuFacil = true; 
    boolean primerMovimientoCPU = false; 

    Random aleatorio = new Random();

    JLabel marcadorX = new JLabel("X: 0");
    JLabel marcadorO = new JLabel("O: 0");

    TaTeTi() {
        ventana.setVisible(true);
        ventana.setSize(anchoTablero, altoTablero);
        ventana.setLocationRelativeTo(null);
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          // 🔹 Ícono de la aplicación
        ImageIcon icono = new ImageIcon("icono.png");
        ventana.setIconImage(icono.getImage());
        ventana.setLayout(new BorderLayout());

        // --- Panel de texto ---
        etiquetaTexto.setBackground(Color.darkGray);
        etiquetaTexto.setForeground(Color.WHITE);
        etiquetaTexto.setFont(new Font("Arial", Font.BOLD, 50));
        etiquetaTexto.setHorizontalAlignment(JLabel.CENTER);
        etiquetaTexto.setText(" Ta-Te-Ti ");
        etiquetaTexto.setOpaque(true);

        panelTexto.setLayout(new BorderLayout());
        panelTexto.add(etiquetaTexto);
        ventana.add(panelTexto, BorderLayout.NORTH);

        // --- Panel del tablero ---
        panelTablero.setLayout(new GridLayout(3, 3, 5, 5));
        panelTablero.setBackground(Color.BLACK);
        ventana.add(panelTablero, BorderLayout.CENTER);

        // --- Crear botones del tablero ---
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton casilla = new JButton();
                tablero[r][c] = casilla;
                panelTablero.add(casilla);

                casilla.setBackground(new Color(245, 245, 220));
                casilla.setForeground(Color.WHITE);
                casilla.setFont(new Font("Arial", Font.BOLD, 120));
                casilla.setFocusable(false);
                casilla.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

                casilla.addActionListener(e -> {
                    if (juegoTerminado) return;
                    if (casilla.getText().equals("")) colocarFicha(casilla);
                });
            }
        }

        // --- Panel derecho estilizado ---
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(50, 50, 50));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tituloMarcador = new JLabel("Marcador");
        tituloMarcador.setFont(new Font("Arial", Font.BOLD, 35));
        tituloMarcador.setForeground(Color.WHITE);
        tituloMarcador.setAlignmentX(Component.CENTER_ALIGNMENT);

        marcadorX.setFont(new Font("Arial", Font.BOLD, 28));
        marcadorX.setForeground(Color.BLUE);
        marcadorX.setAlignmentX(Component.CENTER_ALIGNMENT);

        marcadorO.setFont(new Font("Arial", Font.BOLD, 28));
        marcadorO.setForeground(Color.RED);
        marcadorO.setAlignmentX(Component.CENTER_ALIGNMENT);

        botonPVP.setFont(new Font("Arial", Font.BOLD, 22));
        botonPVC.setFont(new Font("Arial", Font.BOLD, 22));
        botonReiniciar.setFont(new Font("Arial", Font.BOLD, 24));

        botonPVP.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonPVC.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonReiniciar.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelDerecho.add(tituloMarcador);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 20)));
        panelDerecho.add(marcadorX);
        panelDerecho.add(marcadorO);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 40)));
        panelDerecho.add(botonPVP);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 10)));
        panelDerecho.add(botonPVC);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 30)));
        panelDerecho.add(botonReiniciar);

        ventana.add(panelDerecho, BorderLayout.EAST);

        // --- Listeners ---
        botonReiniciar.addActionListener(e -> reiniciarJuego());
        botonPVP.addActionListener(e -> {
            contraCPU = false;
            elegirQuienEmpieza();
        });
        botonPVC.addActionListener(e -> {
            contraCPU = true;
            elegirModoCPU();
            elegirQuienEmpieza();
            primerMovimientoCPU = true;
            if (jugadorActual.equals(jugadorO)) movimientoCPU();
        });
    }

    void colocarFicha(JButton casilla) {
        casilla.setText(jugadorActual);

        if (jugadorActual.equals(jugadorX)) casilla.setForeground(Color.BLUE);
        else casilla.setForeground(Color.RED);

        turnos++;
        verificarGanador();

        if (!juegoTerminado) {
            jugadorActual = jugadorActual.equals(jugadorX) ? jugadorO : jugadorX;
            actualizarEtiqueta();
            if (contraCPU && jugadorActual.equals(jugadorO)) movimientoCPU();
        }
    }

    void elegirModoCPU() {
        Object[] opciones = {"Fácil", "Imposible"};
        int n = JOptionPane.showOptionDialog(ventana,
                "Selecciona la dificultad de la CPU:",
                "Modo CPU",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
        cpuFacil = n == 0;
    }

    void elegirQuienEmpieza() {
        Object[] opciones = {"Jugador X", "Jugador O / CPU"};
        int n = JOptionPane.showOptionDialog(ventana,
                "¿Quién empieza?",
                "Turno inicial",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
        jugadorActual = n == 0 ? jugadorX : jugadorO;
        actualizarEtiqueta();
    }

    void actualizarEtiqueta() {
        etiquetaTexto.setText("Turno de " + jugadorActual);
    }

    void actualizarMarcador() {
        marcadorX.setText("X: " + victoriasX);
        marcadorO.setText("O: " + victoriasO);
    }

    void movimientoCPU() {
        if (juegoTerminado) return;

        int r, c;
        if (primerMovimientoCPU) {
            do { r = aleatorio.nextInt(3); c = aleatorio.nextInt(3); } 
            while (!tablero[r][c].getText().equals(""));
            primerMovimientoCPU = false;
        } else {
            if (cpuFacil) {
                do { r = aleatorio.nextInt(3); c = aleatorio.nextInt(3); } 
                while (!tablero[r][c].getText().equals(""));
            } else {
                int[] mejor = movimientoMinimax();
                r = mejor[0]; c = mejor[1];
            }
        }
        colocarFicha(tablero[r][c]);
    }

    int[] movimientoMinimax() {
        int mejorPuntaje = Integer.MIN_VALUE;
        int[] movimiento = new int[2];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (tablero[r][c].getText().equals("")) {
                    tablero[r][c].setText(jugadorO);
                    int puntaje = minimax(false);
                    tablero[r][c].setText("");
                    if (puntaje > mejorPuntaje) {
                        mejorPuntaje = puntaje;
                        movimiento[0] = r;
                        movimiento[1] = c;
                    }
                }
            }
        }
        return movimiento;
    }

    int minimax(boolean maximizando) {
        String ganador = obtenerGanador();
        if (ganador != null) {
            if (ganador.equals(jugadorO)) return 1;
            else if (ganador.equals(jugadorX)) return -1;
            else return 0;
        }
        if (maximizando) {
            int mejor = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    if (tablero[r][c].getText().equals("")) {
                        tablero[r][c].setText(jugadorO);
                        mejor = Math.max(mejor, minimax(false));
                        tablero[r][c].setText("");
                    }
            return mejor;
        } else {
            int mejor = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    if (tablero[r][c].getText().equals("")) {
                        tablero[r][c].setText(jugadorX);
                        mejor = Math.min(mejor, minimax(true));
                        tablero[r][c].setText("");
                    }
            return mejor;
        }
    }

    String obtenerGanador() {
        for (int i = 0; i < 3; i++) {
            if (!tablero[i][0].getText().equals("") &&
                tablero[i][0].getText().equals(tablero[i][1].getText()) &&
                tablero[i][1].getText().equals(tablero[i][2].getText())) return tablero[i][0].getText();

            if (!tablero[0][i].getText().equals("") &&
                tablero[0][i].getText().equals(tablero[1][i].getText()) &&
                tablero[1][i].getText().equals(tablero[2][i].getText())) return tablero[0][i].getText();
        }

        if (!tablero[0][0].getText().equals("") &&
            tablero[0][0].getText().equals(tablero[1][1].getText()) &&
            tablero[1][1].getText().equals(tablero[2][2].getText())) return tablero[0][0].getText();

        if (!tablero[0][2].getText().equals("") &&
            tablero[0][2].getText().equals(tablero[1][1].getText()) &&
            tablero[1][1].getText().equals(tablero[2][0].getText())) return tablero[0][2].getText();

        boolean lleno = true;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (tablero[r][c].getText().equals("")) lleno = false;

        if (lleno) return "empate";
        return null;
    }

    void verificarGanador() {
        String ganador = obtenerGanador();

        if (ganador != null) {
            if (ganador.equals(jugadorX) || ganador.equals(jugadorO)) {
                for (int r = 0; r < 3; r++)
                    for (int c = 0; c < 3; c++)
                        if (tablero[r][c].getText().equals(ganador))
                            tablero[r][c].setBackground(new Color(200, 255, 200));

                etiquetaTexto.setText("¡Jugador " + ganador + " ha ganado!");
                if (ganador.equals(jugadorX)) victoriasX++;
                else victoriasO++;
            } else {
                etiquetaTexto.setText("¡Empate!");
            }
            juegoTerminado = true;
            botonReiniciar.setEnabled(true);
            actualizarMarcador();
        }
    }

    void reiniciarJuego() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                tablero[r][c].setText("");
                tablero[r][c].setBackground(new Color(245, 245, 220));
                tablero[r][c].setForeground(Color.WHITE);
            }
        turnos = 0;
        juegoTerminado = false;
        primerMovimientoCPU = contraCPU;
        botonReiniciar.setEnabled(false);
        actualizarEtiqueta();

        if (contraCPU && jugadorActual.equals(jugadorO)) movimientoCPU();
    }

    public static void main(String[] args) {
        new TaTeTi();
    }
}
