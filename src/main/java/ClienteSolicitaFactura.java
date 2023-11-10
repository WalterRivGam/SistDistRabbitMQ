import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class ClienteSolicitaFactura {

    private final static String COLA_FACTURA = "factura";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("ip_adress");
        factory.setUsername("user");
        factory.setPassword("pass");
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare(COLA_FACTURA, false, false, false, null);

            JSONObject factura = new JSONObject();

            JSONObject usuario = new JSONObject();
            usuario.put("nombres", "John");
            usuario.put("apellidos", "Smith");
            usuario.put("ruc", 10124516);

            JSONArray productos = new JSONArray();

            JSONObject producto1 = new JSONObject();
            producto1.put("id_prod", 123456);
            producto1.put("name_prod", "Televisor1");
            producto1.put("unit", "televisor");
            producto1.put("price", 1234.45);
            producto1.put("quantity", 1);
            
            JSONObject producto2 = new JSONObject();
            producto2.put("id_prod", 457858);
            producto2.put("name_prod", "Celular1");
            producto2.put("unit", "celular");
            producto2.put("price", 888.99);
            producto2.put("quantity", 5);
            
            JSONObject producto3 = new JSONObject();
            producto3.put("id_prod", 789456);
            producto3.put("name_prod", "Laptop1");
            producto3.put("unit", "laptop");
            producto3.put("price", 4578.47);
            producto3.put("quantity", 3);
            
            productos.put(producto1);
            productos.put(producto2);
            productos.put(producto3);
            
            String mensajeJSON = productos.toString();

            channel.basicPublish("", COLA_FACTURA, null, mensajeJSON.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Mensaje enviado: '" + mensajeJSON + "'");
        }
    }
}
