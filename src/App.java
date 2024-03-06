import java.text.DecimalFormat;

import crypto.Crypto;

public class App {
    public static void main(String[] args) throws Exception {
        Crypto crypto = new Crypto();
        String message = "Hello, World!";

        String encryptedMessage = crypto.encryptMessage(message);
        System.out.println("Encrypted message: " + encryptedMessage);

        String decryptedMessage = crypto.decryptMessage(encryptedMessage);
        System.out.println("Decrypted message: " + decryptedMessage);

    }
}
