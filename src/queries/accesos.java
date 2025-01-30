/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author migue
 */
public class accesos {
    public boolean registrarUsuario(String rol, String nombreCompleto, String carnet, String domicilio, String telefono, String celular, String usuario, String contrasena) {
        String sql = "INSERT INTO administrativos (ROL, NOMBRECOMPLETO, CARNET, DOMIICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rol);
            pstmt.setString(2, nombreCompleto);
            pstmt.setString(3, carnet);
            pstmt.setString(4, domicilio);
            pstmt.setString(5, telefono);
            pstmt.setString(6, celular);
            pstmt.setString(7, usuario);
            pstmt.setString(8, contrasena);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String obtenerRolUsuario(String usuario, String contrasena) {
        String sql = "SELECT ROL FROM administrativos WHERE USUARIO = ? AND CONTRASEÑA = ?";
        try (Connection conn = Mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("ROL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
