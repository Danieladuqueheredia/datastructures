// Main.java
package com.puerto.contenedores;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        GestionPuerto gp = new GestionPuerto();
        gp.inicializar(); // inicializa tablero y datos
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menu Principal:");
            System.out.println("1. Registro de buques");
            System.out.println("2. Registro de contenedores");
            System.out.println("3. Mostrar peso total de los contenedores");
            System.out.println("4. Listar por origen (agrupado)");
            System.out.println("5. Cerrar");
            System.out.print("Seleccione una opcion: ");

            int op = Integer.parseInt(sc.nextLine().trim());
            switch (op) {
                case 1:
                    System.out.print("ID buque: ");
                    String idB = sc.nextLine();
                    System.out.print("Origen: ");
                    String origenB = sc.nextLine();
                    gp.registrarBuque(new Buque(idB, "Buque-" + idB, origenB, 0.0));
                    break;
                case 2:
                    System.out.print("ID contenedor: ");
                    String idC = sc.nextLine();
                    System.out.print("Peso: ");
                    double peso = Double.parseDouble(sc.nextLine());
                    System.out.print("Origen: ");
                    String origenC = sc.nextLine();
                    Contenedor c = new Contenedor(idC, peso, origenC);
                    gp.registrarContenedor(c);
                    gp.asignarContenedorApuesto(); // si aplica
                    break;
                case 3:
                    System.out.println("Peso total: " + gp.obtenerPesoTotal());
                    break;
                case 4:
                    gp.listarPorOrigen();
                    break;
                case 5:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        sc.close();
    }
}
// Contenedor.java
package com.puerto.contenedores;

public class Contenedor {
    private String id;
    private double peso;
    private String origen;

    public Contenedor(String id, double peso, String origen) {
        this.id = id;
        this.peso = peso;
        this.origen = origen;
    }

    public String getId() { return id; }
    public double getPeso() { return peso; }
    public String getOrigen() { return origen; }

    @Override
    public String toString() {
        return "Contenedor{" + id + ", peso=" + peso + ", origen=" + origen + "}";
    }
}
// Puesto.java
package com.puerto.contenedores;

public class Puesto {
    private int fila;
    private int columna;
    private Contenedor contenedor; // null si libre

    public Puesto(int f, int c) {
        this.fila = f;
        this.columna = c;
        this.contenedor = null;
    }

    public boolean estaLibre() { return contenedor == null; }
    public boolean estaOcupado() { return contenedor != null; }

    public Contenedor getContenedor() { return contenedor; }
    public void colocar(Contenedor cont) { this.contenedor = cont; }
    public void retirar() { this.contenedor = null; }

    public String toString() {
        return estaLibre() ? "[ - ]" : "[ " + contenedor.getId() + " ]";
    }
}
// TableroContenedores.java
package com.puerto.contenedores;

public class TableroContenedores {
    public static final int FILAS = 10;
    public static final int COLUMNAS = 10;
    private Puesto[][] tablero;

    public TableroContenedores() {
        tablero = new Puesto[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                tablero[i][j] = new Puesto(i, j);
            }
        }
    }

    public void mostrarMapa() {
        System.out.println("Mapa de puestos (Filas x Columnas):");
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Ubica en el primer puesto libre de abajo hacia arriba (filas descendentes)
    public boolean colocarContenedor(Contenedor c) {
        for (int f = 0; f < FILAS; f++) {
            for (int col = 0; col < COLUMNAS; col++) {
                Puesto p = tablero[f][col];
                if (p.estaLibre()) {
                    p.colocar(c);
                    return true;
                }
            }
        }
        return false;
    }

    public double obtenerPesoTotal() {
        double total = 0.0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Contenedor c = tablero[i][j].getContenedor();
                if (c != null) total += c.getPeso();
            }
        }
        return total;
    }

    // Listado por origen (agrupado podría ser por mapa)
    public void listarPorOrigen() {
        // simple agrupación en consola
        java.util.Map<String, java.util.List<String>> agrupado = new java.util.HashMap<>();
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Contenedor c = tablero[i][j].getContenedor();
                if (c != null) {
                    agrupado.computeIfAbsent(c.getOrigen(), k -> new java.util.ArrayList<>()).add(c.getId());
                }
            }
        }
        for (String origen : agrupado.keySet()) {
            System.out.println("Origen: " + origen + " -> Contenedores: " + agrupado.get(origen));
        }
    }
}
// GestionPuerto.java
package com.puerto.contenedores;

import java.util.*;

public class GestionPuerto {
    private java.util.List<Buque> barcos;
    private java.util.List<Contenedor> contenedores;
    private TableroContenedores tablero;

    public GestionPuerto() {
        this.barcos = new ArrayList<>();
        this.contenedores = new ArrayList<>();
        this.tablero = new TableroContenedores();
    }

    public void inicializar() {
        // opcional: pre-cargar datos de prueba
        // No obligatorio
        System.out.println("Sistema de gestión de contenedores listo.");
    }

    public void registrarBuque(Buque b) {
        barcos.add(b);
        System.out.println("Buque registrado: " + b);
    }

    public void registrarContenedor(Contenedor c) {
        contenedores.add(c);
        System.out.println("Contenedor registrado: " + c);
    }

    public boolean asignarContenedorApuesto() {
        if (contenedores.isEmpty()) return false;
        Contenedor c = contenedores.remove(0);
        boolean ok = tablero.colocarContenedor(c);
        if (!ok) {
            // regresar al stock si no hay puestos
            contenedores.add(0, c);
        } else {
            System.out.println("Contenedor " + c.getId() + " colocado en el tablero.");
        }
        return ok;
    }

    public double obtenerPesoTotal() {
        return tablero.obtenerPesoTotal();
    }

    public void listarPorOrigen() {
        tablero.listarPorOrigen();
    }

    // Opcional: mostrar mapa de puestos
    public void mostrarMapaPuestos() {
        tablero.mostrarMapa();
    }
}
// Buque
package com.puerto.contenedores;

public class Buque {
    private String id;
    private String nombre;
    private String origen;
    private double pesoCarga;

    public Buque(String id, String nombre, String origen, double pesoCarga) {
        this.id = id;
        this.nombre = nombre;
        this.origen = origen;
        this.pesoCarga = pesoCarga;
    } public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getOrigen() { return origen; }
    public double getPesoCarga() { return pesoCarga; }

    public String toString() {
        return "Buque{id=" + id + ", nombre=" + nombre + ", origen=" + origen + "}";
    }
}
