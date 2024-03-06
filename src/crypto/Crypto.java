package crypto;

public class Crypto {
  AES aes = new AES();

  String HMACkey = "05d39add-cf30-4c9b-a922-808aa201ed38";
  String VernamKey = "93bc776d-3fd0-4190-8205-8ffabffe9b82";

  String splitter = "&&::&&";

  public Crypto() {
  }

  public Crypto(String HMACkey) {
    this.HMACkey = HMACkey;
  }

  public String encryptMessage(String message) throws Exception {
    String hmacHash = HMAC.encrypt(message, HMACkey);
    String aesEncrypted = aes.encrypt(hmacHash);
    String base64String = B64.encode(aesEncrypted);

    String encryptedMessage = Vernam.encrypt(message, VernamKey);

    String finalMessage = encryptedMessage + splitter + base64String;

    return finalMessage;
  }

  public String decryptMessage(String message) throws Exception {
    String[] parts = message.split(splitter);

    String encryptedMessage = parts[0];
    String base64String = parts[1];

    String aesEncrypted = B64.decode(base64String);
    String hmacHash = aes.decrypt(aesEncrypted);

    String decryptedMessage = Vernam.decrypt(encryptedMessage, VernamKey);

    String generatedHmac = HMAC.encrypt(decryptedMessage, HMACkey);

    if (!hmacHash.equals(generatedHmac)) {
      throw new Exception("HMACs não conferem");
    }

    return decryptedMessage;
  }
}
