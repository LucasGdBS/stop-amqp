package stop.clientplayer;

import org.springframework.stereotype.Component;

@Component
public class Receiver {

    public void receiveMessage(byte[] message) {
        String messageString = new String(message);  // Converte o array de bytes para String
        System.out.println("Received <" + messageString + ">");
    }

}
