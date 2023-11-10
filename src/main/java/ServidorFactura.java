import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class ServidorFactura {

    private final static String COLA_FACTURA = "factura";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(COLA_FACTURA, false, false, false, null);
        System.out.println(" [*] Esperando mensajes. Presione Ctrl + C para salir.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensajeRecibido = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Mensaje recibido:\n" + mensajeRecibido);
        };
        channel.basicConsume(COLA_FACTURA, true, deliverCallback, consumerTag -> { });
    }
}