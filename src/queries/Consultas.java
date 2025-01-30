/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package queries;
import Secretaria.RegistrodeDirector;
import Secretaria.VentanaRegEst;
import Director.VentanaRegistroHorarios;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
/**
 *
 * @author migue
 */
public class Consultas {
    
    
    
    public void insertarDatos(VentanaRegEst ventana) {
        // Validación de campos vacíos
        if (ventana.jTextField1.getText().isEmpty() || ventana.jTextField2.getText().isEmpty() ||
            ventana.jTextField3.getText().isEmpty() || ventana.jComboBox1.getSelectedItem() == null ||
            ventana.jTextField4.getText().isEmpty() || ventana.jTextField5.getText().isEmpty() ||
            ventana.jTextField6.getText().isEmpty() || ventana.jTextField7.getText().isEmpty()) {

            JOptionPane.showMessageDialog(ventana, "Todos los campos deben estar llenos.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar mensaje de confirmación
        int confirmacion = JOptionPane.showConfirmDialog(
                ventana,
                "¿Estás seguro de registrar estos datos?",
                "Confirmación de Registro",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sqlInsertRegistrados = "INSERT INTO registrados (NOMBREESTUDIANTE, CARNETESTUDIANTE, DOMICILIOESTUDIANTE, TIPOALUMNO) VALUES (?, ?, ?, ?)";
            String sqlInsertTutores = "INSERT INTO tutores (NOMBRETUTOR, CARNETTUTOR, TELEFONOTUTOR, CELULARTUTOR) VALUES (?, ?, ?, ?)";

            try (Connection conn = Mysql.getConnection();
                 PreparedStatement pstmtRegistrados = conn.prepareStatement(sqlInsertRegistrados);
                 PreparedStatement pstmtTutores = conn.prepareStatement(sqlInsertTutores)) {

                // Parámetros para la tabla registrados
                pstmtRegistrados.setString(1, ventana.jTextField1.getText());
                pstmtRegistrados.setString(2, ventana.jTextField2.getText());
                pstmtRegistrados.setString(3, ventana.jTextField3.getText());
                pstmtRegistrados.setString(4, ventana.jComboBox1.getSelectedItem().toString());
                pstmtRegistrados.executeUpdate();

                // Parámetros para la tabla tutores
                pstmtTutores.setString(1, ventana.jTextField4.getText());
                pstmtTutores.setString(2, ventana.jTextField5.getText());
                pstmtTutores.setString(3, ventana.jTextField6.getText());
                pstmtTutores.setString(4, ventana.jTextField7.getText());
                pstmtTutores.executeUpdate();

                JOptionPane.showMessageDialog(ventana, "Datos insertados correctamente.");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(ventana, "Error al insertar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(ventana, "Registro cancelado.");
        }
    }
    
    public void llenarTablaHorarios(VentanaRegistroHorarios ventanaHorarios) {
        // Definir las columnas de la tabla
        String[] columnNames = {"ID Horario", "Curso", "Materia", "Aula", "Periodo", "Hora Inicio", "Hora Fin"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);

        // Consulta SQL para obtener los datos de la tabla 'horarios' y sus claves foráneas
        String sql = "SELECT h.ID_HORARIOS, c.GRADO, c.PARALELO, m.NOMBREMATERIA, a.NOMBREAULA, p.DIA, p.HORAINICIO, p.HORAFIN " +
                     "FROM horarios h " +
                     "JOIN cursos c ON h.ID_CURSOS = c.ID_CURSOS " +
                     "JOIN materias m ON h.ID_MATERIAS = m.ID_MATERIAS " +
                     "JOIN aulas a ON h.ID_AULAS = a.ID_AULAS " +
                     "JOIN periodos p ON h.ID_PERIODOS = p.ID_PERIODOS";

        // Conectar a la base de datos y ejecutar la consulta
        try (Connection conn = Mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Recorrer los resultados de la consulta y llenar la tabla
            while (rs.next()) {
                int idHorario = rs.getInt("ID_HORARIOS");
                String curso = rs.getString("GRADO") + rs.getString("PARALELO");
                String materia = rs.getString("NOMBREMATERIA");
                String aula = rs.getString("NOMBREAULA");
                String periodo = rs.getString("DIA");
                String horaInicio = rs.getString("HORAINICIO");
                String horaFin = rs.getString("HORAFIN");

                model.addRow(new Object[] {idHorario, curso, materia, aula, periodo, horaInicio, horaFin});
            }

            // Asignar el modelo de la tabla al JTable
            ventanaHorarios.jTable1.setModel(model); // Asegúrate de que la variable jTable1 esté declarada en VentanaRegistroHorarios

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventanaHorarios, "Error al cargar los horarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Método para llenar los JComboBox con los datos de la base de datos
    public void llenarComboBoxes(VentanaRegistroHorarios ventana) {
        // Llenar el JComboBox1 con los días de la tabla 'periodos'
        llenarComboBoxDia(ventana.jComboBox1);

        // Llenar el JComboBox2 con las horas de inicio de la tabla 'periodos'
        llenarComboBoxHoraInicio(ventana.jComboBox2);

        // Llenar el JComboBox3 con los grados de la tabla 'cursos'
        llenarComboBoxGrado(ventana.jComboBox6);

        // Llenar el JComboBox4 con los paralelos de la tabla 'cursos'
        llenarComboBoxParalelo(ventana.jComboBox3);

        // Llenar el JComboBox5 con los nombres de las materias de la tabla 'materias'
        llenarComboBoxMateria(ventana.jComboBox4);

        // Llenar el JComboBox6 con los nombres de las aulas de la tabla 'aulas'
        llenarComboBoxAula(ventana.jComboBox5);
    }

    // Método para llenar el JComboBox sin duplicados
    private void agregarDatosComboBox(DefaultComboBoxModel model, String valor) {
        boolean existe = false;
        // Verifica si el valor ya está en el modelo
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(valor)) {
                existe = true;
                break;
            }
        }
        // Solo agrega si no existe
        if (!existe) {
            model.addElement(valor);
        }
    }

    private void llenarComboBoxDia(JComboBox comboBox) {
        String sql = "SELECT DIA FROM periodos";
        try (Connection conn = Mysql.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel model = new DefaultComboBoxModel();
            while (rs.next()) {
                String dia = rs.getString("DIA");
                agregarDatosComboBox(model, dia);  // Llama al nuevo método
            }
            comboBox.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void llenarComboBoxHoraInicio(JComboBox comboBox) {
        String sql = "SELECT HORAINICIO FROM periodos";
        try (Connection conn = Mysql.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel model = new DefaultComboBoxModel();
            while (rs.next()) {
                String horaInicio = rs.getTime("HORAINICIO").toString();
                agregarDatosComboBox(model, horaInicio);  // Llama al nuevo método
            }
            comboBox.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void llenarComboBoxGrado(JComboBox comboBox) {
        String sql = "SELECT GRADO FROM cursos";
        try (Connection conn = Mysql.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel model = new DefaultComboBoxModel();
            while (rs.next()) {
                String grado = rs.getString("GRADO");
                agregarDatosComboBox(model, grado);  // Llama al nuevo método
            }
            comboBox.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void llenarComboBoxParalelo(JComboBox comboBox) {
        String sql = "SELECT PARALELO FROM cursos";
        try (Connection conn = Mysql.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel model = new DefaultComboBoxModel();
            while (rs.next()) {
                String paralelo = rs.getString("PARALELO");
                agregarDatosComboBox(model, paralelo);  // Llama al nuevo método
            }
            comboBox.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void llenarComboBoxMateria(JComboBox comboBox) {
        String sql = "SELECT NOMBREMATERIA FROM materias";
        try (Connection conn = Mysql.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel model = new DefaultComboBoxModel();
            while (rs.next()) {
                String materia = rs.getString("NOMBREMATERIA");
                agregarDatosComboBox(model, materia);  // Llama al nuevo método
            }
            comboBox.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void llenarComboBoxAula(JComboBox comboBox) {
        String sql = "SELECT NOMBREAULA FROM aulas";
        try (Connection conn = Mysql.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            DefaultComboBoxModel model = new DefaultComboBoxModel();
            while (rs.next()) {
                String aula = rs.getString("NOMBREAULA");
                agregarDatosComboBox(model, aula);  // Llama al nuevo método
            }
            comboBox.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void mostrarDatosConFiltros(VentanaRegistroHorarios ventana) {
        // Obtener las selecciones de los JComboBox de la ventana
        String diaSeleccionado = (String) ventana.jComboBox1.getSelectedItem();
        String horaInicioSeleccionada = (String) ventana.jComboBox2.getSelectedItem();
        String gradoSeleccionado = (String) ventana.jComboBox6.getSelectedItem();
        String paraleloSeleccionado = (String) ventana.jComboBox3.getSelectedItem();
        String materiaSeleccionada = (String) ventana.jComboBox4.getSelectedItem();
        String aulaSeleccionada = (String) ventana.jComboBox5.getSelectedItem();

        // Crear las consultas SQL para obtener los datos
        String sqlPeriodo = "SELECT ID_PERIODOS FROM periodos WHERE DIA = ? AND HORAINICIO = ?";
        String sqlCurso = "SELECT ID_CURSOS FROM cursos WHERE GRADO = ? AND PARALELO = ?";
        String sqlMateria = "SELECT ID_MATERIAS FROM materias WHERE NOMBREMATERIA = ?";
        String sqlAula = "SELECT ID_AULAS FROM aulas WHERE NOMBREAULA = ?";

        try (Connection conn = Mysql.getConnection()) {

            // Obtener ID_PERIODOS
            try (PreparedStatement stmt = conn.prepareStatement(sqlPeriodo)) {
                stmt.setString(1, diaSeleccionado);
                stmt.setString(2, horaInicioSeleccionada);
                ResultSet rsPeriodo = stmt.executeQuery();
                if (rsPeriodo.next()) {
                    ventana.jTextField1.setText(String.valueOf(rsPeriodo.getInt("ID_PERIODOS")));
                } else {
                    ventana.jTextField1.setText("No encontrado");
                }
            }

            // Obtener ID_CURSOS
            try (PreparedStatement stmt = conn.prepareStatement(sqlCurso)) {
                stmt.setString(1, gradoSeleccionado);
                stmt.setString(2, paraleloSeleccionado);
                ResultSet rsCurso = stmt.executeQuery();
                if (rsCurso.next()) {
                    ventana.jTextField2.setText(String.valueOf(rsCurso.getInt("ID_CURSOS")));
                } else {
                    ventana.jTextField2.setText("No encontrado");
                }
            }

            // Obtener ID_MATERIAS
            try (PreparedStatement stmt = conn.prepareStatement(sqlMateria)) {
                stmt.setString(1, materiaSeleccionada);
                ResultSet rsMateria = stmt.executeQuery();
                if (rsMateria.next()) {
                    ventana.jTextField3.setText(String.valueOf(rsMateria.getInt("ID_MATERIAS")));
                } else {
                    ventana.jTextField3.setText("No encontrado");
                }
            }

            // Obtener ID_AULAS
            try (PreparedStatement stmt = conn.prepareStatement(sqlAula)) {
                stmt.setString(1, aulaSeleccionada);
                ResultSet rsAula = stmt.executeQuery();
                if (rsAula.next()) {
                    ventana.jTextField4.setText(String.valueOf(rsAula.getInt("ID_AULAS")));
                } else {
                    ventana.jTextField4.setText("No encontrado");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventana, "Error al obtener los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void insertarDatosHorario(VentanaRegistroHorarios ventana) {
    // Obtener los datos de los JTextField
    String idPeriodo = ventana.jTextField1.getText();
    String idCurso = ventana.jTextField2.getText();
    String idMateria = ventana.jTextField3.getText();
    String idAula = ventana.jTextField4.getText();

    // Validar que los campos no estén vacíos
    if (idPeriodo.isEmpty() || idCurso.isEmpty() || idMateria.isEmpty() || idAula.isEmpty()) {
        JOptionPane.showMessageDialog(ventana, "Todos los campos deben estar llenos.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Mostrar mensaje de confirmación
    int confirmacion = JOptionPane.showConfirmDialog(
            ventana,
            "¿Estás seguro de insertar estos datos?",
            "Confirmación de Inserción",
            JOptionPane.YES_NO_OPTION
    );

    if (confirmacion == JOptionPane.YES_OPTION) {
        String sqlInsert = "INSERT INTO horarios (ID_PERIODOS, ID_CURSOS, ID_MATERIAS, ID_AULAS) VALUES (?, ?, ?, ?)";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            // Establecer los parámetros para la consulta
            pstmt.setInt(1, Integer.parseInt(idPeriodo));
            pstmt.setInt(2, Integer.parseInt(idCurso));
            pstmt.setInt(3, Integer.parseInt(idMateria));
            pstmt.setInt(4, Integer.parseInt(idAula));

            // Ejecutar la consulta
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(ventana, "Datos insertados correctamente en la tabla horarios.");

            // Llamar al método para actualizar la tabla después de la inserción
            llenarTablaHorarios(ventana);  // Llamar al método para refrescar la tabla

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventana, "Error al insertar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(ventana, "Inserción cancelada.");
    }
}


    
    
    
    public void modificarDatosHorario(VentanaRegistroHorarios ventana) {
    // Obtener los datos de los JTextField
    String idHorario = ventana.jTextField5.getText(); // Este es el ID_HORARIOS
    String idPeriodo = ventana.jTextField1.getText();
    String idCurso = ventana.jTextField2.getText();
    String idMateria = ventana.jTextField3.getText();
    String idAula = ventana.jTextField4.getText();

    // Validar que los campos no estén vacíos
    if (idHorario.isEmpty() || idPeriodo.isEmpty() || idCurso.isEmpty() || idMateria.isEmpty() || idAula.isEmpty()) {
        JOptionPane.showMessageDialog(ventana, "Todos los campos deben estar llenos.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Mostrar mensaje de confirmación
    int confirmacion = JOptionPane.showConfirmDialog(
            ventana,
            "¿Estás seguro de modificar estos datos?",
            "Confirmación de Modificación",
            JOptionPane.YES_NO_OPTION
    );

    if (confirmacion == JOptionPane.YES_OPTION) {
        String sqlUpdate = "UPDATE horarios SET ID_PERIODOS = ?, ID_CURSOS = ?, ID_MATERIAS = ?, ID_AULAS = ? WHERE ID_HORARIOS = ?";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            // Establecer los parámetros para la consulta
            pstmt.setInt(1, Integer.parseInt(idPeriodo));
            pstmt.setInt(2, Integer.parseInt(idCurso));
            pstmt.setInt(3, Integer.parseInt(idMateria));
            pstmt.setInt(4, Integer.parseInt(idAula));
            pstmt.setInt(5, Integer.parseInt(idHorario)); // Este es el ID_HORARIOS que se utilizará para buscar el registro

            // Ejecutar la consulta
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Si la actualización fue exitosa, mostrar mensaje
                JOptionPane.showMessageDialog(ventana, "Datos actualizados correctamente en la tabla horarios.");

                // Llamar a llenarTablaHorarios para actualizar la tabla con los datos más recientes
                llenarTablaHorarios(ventana);
            } else {
                // Si no se actualizó ningún registro, puede ser que el ID_HORARIOS no exista
                JOptionPane.showMessageDialog(ventana, "No se encontró el ID_HORARIOS especificado.", "Error", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventana, "Error al modificar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(ventana, "Modificación cancelada.");
    }
}

    public void eliminarDatosHorario(VentanaRegistroHorarios ventana) {
    // Obtener el ID_HORARIOS desde el JTextField
    String idHorario = ventana.jTextField5.getText();

    // Validar que el campo ID_HORARIOS no esté vacío
    if (idHorario.isEmpty()) {
        JOptionPane.showMessageDialog(ventana, "El campo ID_HORARIOS no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Mostrar mensaje de confirmación
    int confirmacion = JOptionPane.showConfirmDialog(
            ventana,
            "¿Estás seguro de eliminar este horario?",
            "Confirmación de Eliminación",
            JOptionPane.YES_NO_OPTION
    );

    if (confirmacion == JOptionPane.YES_OPTION) {
        // Consulta SQL para eliminar el registro
        String sqlDelete = "DELETE FROM horarios WHERE ID_HORARIOS = ?";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {

            // Establecer el parámetro para la consulta
            pstmt.setInt(1, Integer.parseInt(idHorario));

            // Ejecutar la consulta
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Si la eliminación fue exitosa, mostrar mensaje
                JOptionPane.showMessageDialog(ventana, "Horario eliminado correctamente.");
                
                // Llamar a llenarTablaHorarios para actualizar la tabla después de la eliminación
                llenarTablaHorarios(ventana);

            } else {
                // Si no se encontró el registro, mostrar mensaje de error
                JOptionPane.showMessageDialog(ventana, "No se encontró el ID_HORARIOS especificado.", "Error", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventana, "Error al eliminar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(ventana, "Eliminación cancelada.");
    }
}

        public void llenarTablaDirector(RegistrodeDirector ventana) {
    // Definir columnas de la tabla
    String[] columnNames = {"ID", "Nombre Completo", "Carnet", "Domicilio", "Teléfono", "Celular", "Usuario", "Contraseña"};
    DefaultTableModel model = new DefaultTableModel(null, columnNames);

    String sql = "SELECT ID_ADMINISTRATIVOS, NOMBRECOMPLETO, CARNET, DOMICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA FROM administrativos";

    try (Connection conn = Mysql.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("ID_ADMINISTRATIVOS");
            String nombreCompleto = rs.getString("NOMBRECOMPLETO");
            String carnet = rs.getString("CARNET");
            String domicilio = rs.getString("DOMICILIO");
            String telefono = rs.getString("TELEFONO");
            String celular = rs.getString("CELULAR");
            String usuario = rs.getString("USUARIO");
            String contrasena = rs.getString("CONTRASEÑA");

            // Agregar los datos a la tabla
            model.addRow(new Object[]{id, nombreCompleto, carnet, domicilio, telefono, celular, usuario, contrasena});
        }

        // Asignar el modelo actualizado al JTable
        ventana.jTable3.setModel(model);

    } catch (SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(ventana, "Error al cargar datos: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}

        
        
        
        
        public void insertarDirector(RegistrodeDirector ventana) {
    int response = javax.swing.JOptionPane.showConfirmDialog(ventana, "¿Está seguro de que desea agregar el director?", "Confirmar Inserción", javax.swing.JOptionPane.YES_NO_OPTION);
    if (response == javax.swing.JOptionPane.YES_OPTION) {
        String sql = "INSERT INTO administrativos (ROL, NOMBRECOMPLETO, CARNET, DOMICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = new Mysql().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "Director"); // ROL
            ps.setString(2, RegistrodeDirector.jTextFieldNombre.getText()); // NOMBRECOMPLETO
            ps.setString(3, RegistrodeDirector.jTextFieldCarnet.getText()); // CARNET
            ps.setString(4, RegistrodeDirector.jTextFieldDomicilio.getText()); // DOMICILIO
            ps.setString(5, RegistrodeDirector.jTextFieldTelefonoFijo.getText()); // TELEFONO
            ps.setString(6, RegistrodeDirector.jTextFieldTelefonoCelular.getText()); // CELULAR
            ps.setString(7, RegistrodeDirector.jTextFieldUsuario.getText()); // USUARIO
            ps.setString(8, RegistrodeDirector.jTextFieldContraseña.getText()); // CONTRASEÑA

            ps.executeUpdate();
            javax.swing.JOptionPane.showMessageDialog(ventana, "Datos guardados correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            llenarTablaDirector(ventana);  // Actualiza la tabla
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(ventana, "Error al guardar los datos: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}

        public void modificarDirector(RegistrodeDirector ventana) {
    int response = javax.swing.JOptionPane.showConfirmDialog(ventana, "¿Está seguro de que desea modificar los datos del director?", "Confirmar Modificación", javax.swing.JOptionPane.YES_NO_OPTION);
    if (response == javax.swing.JOptionPane.YES_OPTION) {
        String sql = "UPDATE administrativos SET ROL = ?, NOMBRECOMPLETO = ?, CARNET = ?, DOMICILIO = ?, TELEFONO = ?, CELULAR = ?, USUARIO = ?, CONTRASEÑA = ? WHERE ID_ADMINISTRATIVOS = ?";
        try (Connection con = new Mysql().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "Director"); // ROL
            ps.setString(2, RegistrodeDirector.jTextFieldNombre.getText()); // NOMBRECOMPLETO
            ps.setString(3, RegistrodeDirector.jTextFieldCarnet.getText()); // CARNET
            ps.setString(4, RegistrodeDirector.jTextFieldDomicilio.getText()); // DOMICILIO
            ps.setString(5, RegistrodeDirector.jTextFieldTelefonoFijo.getText()); // TELEFONO
            ps.setString(6, RegistrodeDirector.jTextFieldTelefonoCelular.getText()); // CELULAR
            ps.setString(7, RegistrodeDirector.jTextFieldUsuario.getText()); // USUARIO
            ps.setString(8, RegistrodeDirector.jTextFieldContraseña.getText()); // CONTRASEÑA
            ps.setInt(9, Integer.parseInt(RegistrodeDirector.jTextField1.getText())); // ID_ADMINISTRATIVOS

            ps.executeUpdate();
            javax.swing.JOptionPane.showMessageDialog(ventana, "Datos modificados correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            llenarTablaDirector(ventana);  // Actualiza la tabla
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(ventana, "Error al modificar los datos: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}

        public void eliminarDirector(RegistrodeDirector ventana) {
    int response = javax.swing.JOptionPane.showConfirmDialog(ventana, "¿Está seguro de que desea eliminar al director?", "Confirmar Eliminación", javax.swing.JOptionPane.YES_NO_OPTION);
    if (response == javax.swing.JOptionPane.YES_OPTION) {
        String sql = "DELETE FROM administrativos WHERE ID_ADMINISTRATIVOS = ?";
        try (Connection con = new Mysql().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(RegistrodeDirector.jTextField1.getText())); // ID_ADMINISTRATIVOS

            ps.executeUpdate();
            javax.swing.JOptionPane.showMessageDialog(ventana, "Datos eliminados correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            llenarTablaDirector(ventana);  // Actualiza la tabla
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(ventana, "Error al eliminar los datos: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
        
        
        
        
        // Método para obtener el modelo de la tabla de secretaria
    public DefaultTableModel LlenarTablaRegistroSecretaria() {
    DefaultTableModel model = new DefaultTableModel();
    String[] titulos = {"ID_ADMINISTRATIVOS", "ROL", "NOMBRECOMPLETO", "CARNET", "DOMICILIO", "TELEFONO", "CELULAR", "USUARIO", "CONTRASEÑA"};
    model.setColumnIdentifiers(titulos);

    String sql = "SELECT * FROM administrativos WHERE ROL IN ('Secretaria', 'Secretario')";

    try (Connection conn = Mysql.getConnection();
         Statement sent = conn.createStatement();
         ResultSet rs = sent.executeQuery(sql)) {
        
        boolean hayDatos = false;  // Verifica si hay registros
        while (rs.next()) {
            hayDatos = true;
            String[] fila = new String[9];
            fila[0] = rs.getString("ID_ADMINISTRATIVOS");
            fila[1] = rs.getString("ROL");
            fila[2] = rs.getString("NOMBRECOMPLETO");
            fila[3] = rs.getString("CARNET");
            fila[4] = rs.getString("DOMICILIO");
            fila[5] = rs.getString("TELEFONO");
            fila[6] = rs.getString("CELULAR");
            fila[7] = rs.getString("USUARIO");
            fila[8] = rs.getString("CONTRASEÑA");
            
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
        
        boolean hayDatos = false;  // Verifica si hay registros
        while (rs.next()) {
            hayDatos = true;
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

    // Método para registrar una secretaria en la base de datos
    public boolean registrarSecretaria(String ROL, String NOMBRECOMPLETO, String CARNET, String DOMICILIO, String TELEFONO, String CELULAR, String USUARIO, String CONTRASEÑA) {
        String sql = "INSERT INTO administrativos (ROL, NOMBRECOMPLETO, CARNET, DOMICILIO, TELEFONO, CELULAR, USUARIO, CONTRASEÑA) VALUES (?, ?, ?, ?, ?, ?,?, ?)";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, ROL);
            pst.setString(2, NOMBRECOMPLETO);
            pst.setString(3, CARNET);
            pst.setString(4, DOMICILIO);
            pst.setString(5, TELEFONO);
            pst.setString(6, CELULAR);
            pst.setString(7, USUARIO);
            pst.setString(8, CONTRASEÑA);

            int resultado = pst.executeUpdate();
            return resultado > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar secretaria/o: " + e.getMessage());
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
        String sql = "DELETE FROM administrativos WHERE ID_ADMINISTRATIVOS = ?";

        try (Connection conn = Mysql.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, idAdministrativo);

            int resultado = pst.executeUpdate();
            return resultado > 0;
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
public boolean modificarSecretaria(int ID_ADMINISTRATIVOS, String ROL, String NOMBRECOMPLETO, String CARNET, String DOMICILIO, String TELEFONO, String CELULAR, String USUARIO, String CONTRASEÑA) {
    String sql = "UPDATE administrativos SET ROL = ?, NOMBRECOMPLETO = ?, CARNET = ?, DOMICILIO = ?, TELEFONO = ?, CELULAR = ?, USUARIO = ?, CONTRASEÑA = ? WHERE ID_ADMINISTRATIVOS = ?";

    try (Connection conn = Mysql.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, ROL);
        pst.setString(2, NOMBRECOMPLETO);
        pst.setString(3, CARNET);
        pst.setString(4, DOMICILIO);
        pst.setString(5, TELEFONO);
        pst.setString(6, CELULAR);
        pst.setString(7, USUARIO);
        pst.setString(8, CONTRASEÑA);
        pst.setInt(9, ID_ADMINISTRATIVOS); // Se utiliza el ID para identificar el registro a modificar

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
}
