/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package queries;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import queries.Mysql;

/**
 *
 * @author migue
 */
public class Consultas {
     
        
        // Método para obtener el modelo de la tabla de secretaria
    public DefaultTableModel LlenarTablaRegistroSecretaria() {
    DefaultTableModel model = new DefaultTableModel();
    String[] titulos = {"ID_ADMINISTRATIVOS", "NOMBRECOMPLETO", "CARNET", "DOMICILIO", "TELEFONO", "CELULAR", "USUARIO", "CONTRASEÑA"};
    model.setColumnIdentifiers(titulos);

    String sql = "SELECT ID_ADMINISTRATIVOS, NOMBRECOMPLETO, CARNET, DOMICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA\n" +
    "FROM administrativos\n" +
    "WHERE ROL IN ('Secretaria', 'Secretario');";

    try (Connection conn = Mysql.getConnection();
         Statement sent = conn.createStatement();
         ResultSet rs = sent.executeQuery(sql)) {
        
        while (rs.next()) {
            String[] fila = new String[8];
            fila[0] = rs.getString("ID_ADMINISTRATIVOS");
            fila[1] = rs.getString("NOMBRECOMPLETO");
            fila[2] = rs.getString("CARNET");
            fila[3] = rs.getString("DOMICILIO");
            fila[4] = rs.getString("TELEFONO");
            fila[5] = rs.getString("CELULAR");
            fila[6] = rs.getString("USUARIO");
            fila[7] = rs.getString("CONTRASEÑA");
            
            // 🔹 Imprimir en consola para ver si hay datos recuperados
            System.out.println("Fila recuperada: " + String.join(", ", fila));
            
            model.addRow(fila);
        }
    } catch (SQLException e) {
        System.err.println("❌ Error al llenar la tabla de secretarias: " + e.getMessage());
        e.printStackTrace();
    }

    return model;
}
    //Metodo para obtener el modelo de la tabla de cursos y paralelos
    public DefaultTableModel LlenarTablaRegistroCursosyParalelos() {
    DefaultTableModel model = new DefaultTableModel();
    String[] titulos = {"ID_CURSOS", "GRADO", "PARALELO"};
    model.setColumnIdentifiers(titulos);

    String sql = "SELECT * FROM cursos";

    try (Connection conn = Mysql.getConnection();
         Statement sent = conn.createStatement();
         ResultSet rs = sent.executeQuery(sql)) {
        
        while (rs.next()) {
            String[] fila = new String[3];
            fila[0] = rs.getString("ID_CURSOS");
            fila[1] = rs.getString("GRADO");
            fila[2] = rs.getString("PARALELO");
             
            // 🔹 Imprimir en consola para ver si hay datos recuperados
            System.out.println("Fila recuperada: " + String.join(", ", fila));
            
            model.addRow(fila);
        }
    } catch (SQLException e) {
        System.err.println("❌ Error al llenar la tabla de secretarias: " + e.getMessage());
        e.printStackTrace();
    }
    return model;
}

public boolean registrarSecretaria(String NOMBRECOMPLETO, String CARNET, String DOMICILIO, String TELEFONO, String CELULAR, 
                                   String USUARIO, String CONTRASEÑA) {
    // Definir el ROL como "Secretaria" directamente
    String ROL = "Secretaria";

    try (Connection conn = Mysql.getConnection()) {
        // Verificar si el CARNET o USUARIO ya existen
        String sqlVerificar = "SELECT COUNT(*) FROM administrativos WHERE CARNET = ? OR USUARIO = ?";
        try (PreparedStatement pstVerificar = conn.prepareStatement(sqlVerificar)) {
            pstVerificar.setString(1, CARNET);
            pstVerificar.setString(2, USUARIO);
            ResultSet rs = pstVerificar.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("El CARNET o USUARIO ya están registrados.");
                return false; // Evita la inserción si ya existe
            }
        }

        // Si no hay duplicados, proceder con la inserción
        String sqlInsertar = "INSERT INTO administrativos (ROL, NOMBRECOMPLETO, CARNET, DOMICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstInsertar = conn.prepareStatement(sqlInsertar)) {
            pstInsertar.setString(1, ROL);
            pstInsertar.setString(2, NOMBRECOMPLETO);
            pstInsertar.setString(3, CARNET);
            pstInsertar.setString(4, DOMICILIO);
            pstInsertar.setString(5, TELEFONO);
            pstInsertar.setString(6, CELULAR);
            pstInsertar.setString(7, USUARIO);
            pstInsertar.setString(8, CONTRASEÑA);

            return pstInsertar.executeUpdate() > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error al registrar secretaria: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}


    // Método para registrar curso en la base de datos
    public boolean registrarCurso(String GRADO, String PARALELO) {
        String sql = "INSERT INTO cursos (GRADO, PARALELO) VALUES (?, ?)";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, GRADO);
            pst.setString(2, PARALELO);


            int resultado = pst.executeUpdate();
            return resultado > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar secretaria/o: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para eliminar una secretaria por ID_ADMINISTRATIVOS
   public boolean eliminarSecretaria(int idAdministrativo) {
    // Consulta para obtener los datos de la secretaria
    String sqlVerificar = "SELECT NOMBRECOMPLETO, CARNET, DOMICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA FROM administrativos WHERE ID_ADMINISTRATIVOS = ?";
    String sqlEliminar = "DELETE FROM administrativos WHERE ID_ADMINISTRATIVOS = ?";

    try (Connection conn = Mysql.getConnection()) {
        // Verificar si el registro tiene valores vacíos
        try (PreparedStatement pstVerificar = conn.prepareStatement(sqlVerificar)) {
            pstVerificar.setInt(1, idAdministrativo);
            ResultSet rs = pstVerificar.executeQuery();

            if (rs.next()) {
                // Verificar si alguno de los campos clave está vacío
                if (rs.getString("NOMBRECOMPLETO").isEmpty() || rs.getString("CARNET").isEmpty() ||
                    rs.getString("DOMICILIO").isEmpty() || rs.getString("TELEFONO").isEmpty() ||
                    rs.getString("CELULAR").isEmpty() || rs.getString("USUARIO").isEmpty() ||
                    rs.getString("CONTRASEÑA").isEmpty()) {
                    System.out.println("No se puede eliminar. Algunos campos están vacíos.");
                    return false; // No eliminar si hay campos vacíos
                }
            } else {
                System.out.println("El ID no existe.");
                return false; // Si no se encuentra el ID, no eliminar
            }
        }

        // Si pasa la validación, proceder con la eliminación
        try (PreparedStatement pstEliminar = conn.prepareStatement(sqlEliminar)) {
            pstEliminar.setInt(1, idAdministrativo);
            int resultado = pstEliminar.executeUpdate();
            return resultado > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error al eliminar secretaria: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}


// Método para eliminar cursos(GRADO Y PARALELO) por ID_CURSOS
    public boolean eliminarCurso(int idcurso) {
        String sql = "DELETE FROM cursos WHERE ID_CURSOS = ?";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idcurso);

            int resultado = pst.executeUpdate();
            return resultado > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar CURSO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
// Método para MODIFICAR una secretaria en la base de datos 
   public boolean modificarSecretaria(int ID_ADMINISTRATIVOS, String NOMBRECOMPLETO, String CARNET, String DOMICILIO, String TELEFONO, String CELULAR, String USUARIO, String CONTRASEÑA) {
    String sql = "UPDATE administrativos SET NOMBRECOMPLETO = ?, CARNET = ?, DOMICILIO = ?, TELEFONO = ?, CELULAR = ?, USUARIO = ?, CONTRASEÑA = ? WHERE ID_ADMINISTRATIVOS = ? AND ROL IN ('Secretaria', 'Secretario')";

    try (Connection conn = Mysql.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setString(1, NOMBRECOMPLETO);
        pst.setString(2, CARNET);
        pst.setString(3, DOMICILIO);
        pst.setString(4, TELEFONO);
        pst.setString(5, CELULAR);
        pst.setString(6, USUARIO);
        pst.setString(7, CONTRASEÑA);
        pst.setInt(8, ID_ADMINISTRATIVOS); // Se usa el ID para identificar el registro a modificar

        int resultado = pst.executeUpdate();
        return resultado > 0;
    } catch (SQLException e) {
        System.err.println("Error al modificar secretaria/o: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Método para MODIFICAR una secretaria en la base de datos 
    public boolean modificarCurso(int ID_CURSOS, String GRADO, String PARALELO) {
        String sql = "UPDATE cursos SET GRADO = ?, PARALELO = ? WHERE ID_CURSOS = ?";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, GRADO);
            pst.setString(2, PARALELO);
            pst.setInt(3, ID_CURSOS); 

            int resultado = pst.executeUpdate();
            return resultado > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar secretaria/o: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void obtenerDatosEstudiante(String carnet, JTextField txtNombre, JComboBox<String> cbTipoBeca, JTextField txtPorcentaje) {
        try {
            Connection conn = Mysql.getConnection();
            String sql = "SELECT e.NOMBREESTUDIANTE, COALESCE(b.`TIPO DE BECA`, 'Sin beca') AS TIPO_BECA, COALESCE(b.PORCENTAJE, 0) AS PORCENTAJE " +
                         "FROM estudiantes e " +
                         "LEFT JOIN becas b ON e.ID_ESTUDIANTES = b.ID_ESTUDIANTES " +
                         "WHERE e.CARNETESTUDIANTE = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, carnet);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtNombre.setText(rs.getString("NOMBREESTUDIANTE"));
                cbTipoBeca.setSelectedItem(rs.getString("TIPO_BECA")); // Asigna el tipo de beca al JComboBox
                txtPorcentaje.setText(String.valueOf(rs.getInt("PORCENTAJE"))); // Convierte el porcentaje a texto
            } else {
                txtNombre.setText("");
                cbTipoBeca.setSelectedItem("Sin beca");
                txtPorcentaje.setText("0");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//Metodo para obtener el modelo de la tabla de cursos y paralelos
    public DefaultTableModel LlenarTablalistCursos() {
    DefaultTableModel model = new DefaultTableModel();
    String[] titulos = { "GRADO", "PARALELO"};
    model.setColumnIdentifiers(titulos);

    String sql = "SELECT * FROM cursos";

    try (Connection conn = Mysql.getConnection();
         Statement sent = conn.createStatement();
         ResultSet rs = sent.executeQuery(sql)) {
        
        while (rs.next()) {
            String[] fila = new String[2];
            fila[0] = rs.getString("GRADO");
            fila[1] = rs.getString("PARALELO");
             
            // 🔹 Imprimir en consola para ver si hay datos recuperados
            System.out.println("Fila recuperada: " + String.join(", ", fila));
            
            model.addRow(fila);
        }
    } catch (SQLException e) {
        System.err.println("❌ Error al llenar la tabla de secretarias: " + e.getMessage());
        e.printStackTrace();
    }
    return model;
}
        public DefaultTableModel LlenarTablaListSecretaria() {
    DefaultTableModel model = new DefaultTableModel();
    String[] titulos = { "NOMBRECOMPLETO", "CARNET", "DOMICILIO", "TELEFONO", "CELULAR", "USUARIO", "CONTRASEÑA"};
    model.setColumnIdentifiers(titulos);

    String sql = "SELECT  NOMBRECOMPLETO, CARNET, DOMICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA\n" +
"FROM administrativos\n" +
"WHERE ROL IN ('Secretaria', 'Secretario');";

    try (Connection conn = Mysql.getConnection();
         Statement sent = conn.createStatement();
         ResultSet rs = sent.executeQuery(sql)) {
        
        while (rs.next()) {
            String[] fila = new String[7];
            fila[0] = rs.getString("NOMBRECOMPLETO");
            fila[1] = rs.getString("CARNET");
            fila[2] = rs.getString("DOMICILIO");
            fila[3] = rs.getString("TELEFONO");
            fila[4] = rs.getString("CELULAR");
            fila[5] = rs.getString("USUARIO");
            fila[6] = rs.getString("CONTRASEÑA");
            
            // 🔹 Imprimir en consola para ver si hay datos recuperados
            System.out.println("Fila recuperada: " + String.join(", ", fila));
            
            model.addRow(fila);
        }
    } catch (SQLException e) {
        System.err.println("❌ Error al llenar la tabla de secretarias: " + e.getMessage());
        e.printStackTrace();
    }

    return model;
}
}
