package MQTTTest;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Sample
{

  public static void main( String[] args ) {

  String action = "publish";
  String password = null;
  String userName = null;

  String topic        = "MQTT Examples";
  String content      = "Message from MqttPublishSample";
  int qos             = 2;
  String broker       = "iot.eclipse.org";
  String port         = "1883";
  String clientId     = "JavaSample";

  for ( int i = 0; i < args.length; i++ ) {
    // Check this is a valid argument
    if ( args[i].length() == 2 && args[ i ].startsWith( "-" ) ) {
      char arg = args[i].charAt( 1 );

      // Now handle the arguments that take a value and
      // ensure one is specified
      if ( i == args.length -1 || args[ i + 1 ].charAt( 0 ) == '-' ) {
        System.out.println( "Missing value for argument: " + args[ i ] );
        return;
      }
      switch( arg ) {
        case 'a': action = args[ ++i ];                   break;
        case 't': topic = args[ ++i ];                    break;
        case 'm': content = args[ ++i ];                  break;
        case 's': qos = Integer.parseInt( args[ ++i ] );  break;
        case 'b': broker = args[ ++i ];                   break;
        case 'p': port = args[ ++i ];                     break;
        case 'i': clientId = args[ ++i ];				  break;
        case 'u': userName = args[ ++i ];                 break;
        case 'z': password = args[ ++i ];                 break;
        default:
          System.out.println( "Unrecognised argument: " + args[ i ] );
          return;
        }
      } else {
        System.out.println( "Unrecognised argument: " + args[ i ] );
        return;
      }
    }

    MemoryPersistence persistence = new MemoryPersistence();

    try {

      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession( true );
      System.out.println( "Connecting to broker: " + broker );

      if ( "publish".equals( action ) ) {
        clientId = "producer";

        MqttClient sampleClient = new MqttClient( "tcp://" + broker + ":" + port, clientId, persistence );
        sampleClient.connect( connOpts );
        System.out.println( "Connected" );

        System.out.println( "Topic: " + topic );
        System.out.println( "Publishing message: " + content );
        MqttMessage message = new MqttMessage( content.getBytes() );
        message.setQos( qos );
        sampleClient.publish( topic, message );
        System.out.println( "Message published" );
        sampleClient.disconnect();
        System.out.println( "Disconnected" );
      } else if ( "subscribe".equals( action ) ) {
        clientId = "consumer";

        MqttClient sampleClient = new MqttClient( "tcp://" + broker + ":" + port, clientId, persistence );
        sampleClient.connect( connOpts );
        System.out.println( "Connected" );

        sampleClient.setCallback(new MqttCallback() {

          @Override
          public void messageArrived(String topic, MqttMessage msg) throws Exception {
            String messageBody = new String( msg.getPayload() );
            System.out.println( "Message arrived: " + messageBody );
          }

          @Override
          public void deliveryComplete( IMqttDeliveryToken token ) {
            // TODO Auto-generated method stub
          }

          @Override
          public void connectionLost( Throwable exception ) {
            // TODO Auto-generated method stub
          }
        });
        sampleClient.subscribe( topic, qos );
        System.out.println( "Topic: " + topic );
        System.out.println( "Broker: " + broker );
        // Continue waiting for messages until the Enter is pressed
        System.out.println( "Press <Enter> to exit" );
        try {
          System.in.read();
        } catch ( IOException e ) {
          // If we can't read we'll just exit
        }

        // Disconnect the client from the server
        sampleClient.disconnect();
        System.out.println( "Disconnected" );
      }

      System.exit(0);
    } catch( MqttException me ) {
      System.out.println( "reason " + me.getReasonCode() );
      System.out.println( "msg " + me.getMessage() );
      System.out.println( "loc " + me.getLocalizedMessage() );
      System.out.println( "cause " + me.getCause() );
      System.out.println( "excep " + me );
      me.printStackTrace();
    }
  }
}
