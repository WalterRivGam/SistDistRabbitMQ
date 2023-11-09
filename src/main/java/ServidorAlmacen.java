
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServidorAlmacen {

    private final static String COLA_ALMACEN = "almacen";
    private final static String COLA_CLIENTE = "cliente";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(COLA_ALMACEN, false, false, false, null);
        channel.queueDeclare(COLA_CLIENTE, false, false, false, null);

        System.out.println(" [*] Esperando mensajes. Presione Ctrl + C para salir.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensajeRecibido = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Mensaje recibido:\n" + mensajeRecibido);
            if (mensajeRecibido.equals("getproductos")) {
                
                try {
                    // Conexión a la base de datos
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    java.sql.Connection conex_bd = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_almacen", "root", "12345");
                    Statement st = conex_bd.createStatement();
                    ResultSet rs = st.executeQuery("select * from productos");
                    
                    JSONArray productos = new JSONArray();
                    
                    while(rs.next()) {
                        int id_prod = rs.getInt(1);
                        String nombre_prod = rs.getString(2);
                        String desc_prod = rs.getString(3);
                        String unidad = rs.getString(4);
                        double precio = rs.getDouble(5);
                        int cantidad = rs.getInt(6);
                        String url_imagen = rs.getString(7);
                        
                        JSONObject producto = new JSONObject();
                        producto.put("id_prod", id_prod);
                        producto.put("name_prod", nombre_prod);
                        producto.put("description", desc_prod);
                        producto.put("unit", unidad);
                        producto.put("price", precio);
                        producto.put("quantity", cantidad);
                        producto.put("image", url_imagen);
                        
                        productos.put(producto);
                        
                    }
                    
                    String mensajeAEnviar = productos.toString();
                    
                    channel.basicPublish("", COLA_CLIENTE, null, mensajeAEnviar.getBytes(StandardCharsets.UTF_8));
                    
                    conex_bd.close();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


                
            }
        };
        channel.basicConsume(COLA_ALMACEN, true, deliverCallback, consumerTag -> {
        });
    }
}
