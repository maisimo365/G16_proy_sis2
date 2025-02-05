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
 * @author 62646
 */
public class DataBaseQueries {
    public void insertarDatosSecretaria(int idSecretaria,String Nombre, int carnet){
        String query = "INSERT INTO secretaria (idSecretaria, Nombre, carnet) VALUES (?, ?, ?)";
    }
}
