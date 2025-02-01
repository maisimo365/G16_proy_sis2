/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package queries;

/**
 *
 * @author nzapa
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/bdactualizadasis2oficial"; // Cambiar "sisinfo2bd" al nombre de tu base de datos
    private static final String USUARIO = "root"; // Usuario de MariaDB
    private static final String CONTRASENA = ""; // Contraseña de MariaDB

    public static Connection conectar() {
        Connection conexion = null;
        try {
            // Cargar el driver de MariaDB
            Class.forName("org.mysql.jdbc.Driver");

            // Conectar a la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver JDBC.");
        } catch (SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }
        return conexion;
    }
    
    
    private java.util.List<Object[]> obtenerProfesores() {
    java.util.List<Object[]> listaProfesores = new java.util.ArrayList<>();
    
    String query = "SELECT id, nombre, carnet, domicilio, telefono_fijo, telefono_celular, materia FROM profesores";
    try (java.sql.Connection conn = ConexionDB.conectar();
         java.sql.PreparedStatement stmt = conn.prepareStatement(query);
         java.sql.ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Object[] profesor = new Object[7];
            profesor[0] = rs.getInt("id");
            profesor[1] = rs.getString("nombre");
            profesor[2] = rs.getString("carnet");
            profesor[3] = rs.getString("domicilio");
            profesor[4] = rs.getString("telefono_fijo");
            profesor[5] = rs.getString("telefono_celular");
            profesor[6] = rs.getString("materia");
            listaProfesores.add(profesor);
        }
    } catch (java.sql.SQLException e) {
        e.printStackTrace();
    }

    return listaProfesores;
}

}


