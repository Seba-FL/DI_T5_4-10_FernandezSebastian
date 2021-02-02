package appinformes;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Sebastian Fernandez Lopez
 */
public class FXMLDocumentController implements Initializable {

    private Label label;
    @FXML
    private MenuItem MenuItemListadoFacturas;
    @FXML
    private MenuItem MenuItemVentas;
    @FXML
    private MenuItem MenuItemFacturasCliente;
    @FXML
    private MenuItem MenuItemSubinforme;
    public static Connection conexion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conectaBD();

        MenuItemListadoFacturas.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generaInformeListadoFactura();
                System.out.println("Generando informe");
            }
        });

        MenuItemVentas.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generaInformeVentasTotales();
                System.out.println("Generando informe");
            }
        });

        MenuItemFacturasCliente.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                start();
                System.out.println("Generando informe");
            }
        });

        MenuItemSubinforme.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generaSubinformeFacturas();
                System.out.println("Generando informe");
            }
        });
    }

    public void stop() throws Exception {
        try {
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost;shutdown=true");
        } catch (Exception ex) {
            System.out.println("No se pudo cerrar la conexion a la BD");
        }
    }

    public void conectaBD() {
//Establecemos conexión con la BD
        String baseDatos = "jdbc:hsqldb:hsql://localhost:9001/sample";
        String usuario = "sa";
        String clave = "";
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            conexion = DriverManager.getConnection(baseDatos, usuario, clave);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Fallo al cargar JDBC");
            System.exit(1);
        } catch (SQLException sqle) {
            System.err.println("No se pudo conectar a BD");
            System.exit(1);
        } catch (java.lang.InstantiationException sqlex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        }
    }

    public void generaInformeListadoFactura() {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("/Recursos/facturas.jasper"));
            //Map de parámetros
            Map parametros = new HashMap();

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaInformeVentasTotales() {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("/Recursos/Ventas Totales.jasper"));
            //Map de parámetros
            Map parametros = new HashMap();

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaInformeFacturasPorCliente(String tintro) {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("/Recursos/Facturas_Por_Cliente.jasper"));
            Map parametros = new HashMap();
            int nproducto = Integer.valueOf(tintro);
            parametros.put("ParamProducto", nproducto);
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr,
                    parametros, conexion);
            JasperViewer.viewReport(jp);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaSubinformeFacturas() {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("/Recursos/SubinformesFactura.jasper"));
            JasperReport jsr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("/Recursos/Subinforme1.jasper"));
            //Map de parámetros
            Map parametros = new HashMap();
            parametros.put("subReportParameter", jsr);
            //Ya tenemos los datos para instanciar un objeto JasperPrint que permite ver,
            //imprimir o exportar a otros formatos
            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros,
                    conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void start() {
        Stage primaryStage = new Stage();
        TextField tituloIntro = new TextField("no producto");

        Button btn = new Button();
        btn.setText("Informe");
        VBox root = new VBox();
        root.getChildren().addAll(tituloIntro, btn);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String text = tituloIntro.getText().toString();
                generaInformeFacturasPorCliente(text);
                System.out.println("Generando informe");
            }
        });
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Obtener informe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
