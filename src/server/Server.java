package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
  DatagramSocket serverSocket = null;
  InetAddress host;
  int port;
  Boolean active = true;
  byte[] receiveBuffer;
  byte[] sendBuffer;

  public Server(int port) {
    this.port = port;
    run(this.port);
  }

  public void run(int port) {
    try {
      serverSocket = new DatagramSocket(port);
      host = InetAddress.getLocalHost();
      System.out.println("Server online at: " +
          host +
          ":" +
          port);
      receiveBuffer = new byte[1024];
      DatagramPacket receiveDatagram = new DatagramPacket(
          receiveBuffer,
          receiveBuffer.length);

      while (active) {
        serverSocket.receive(receiveDatagram);
        receiveBuffer = receiveDatagram.getData();
        String message = new String(receiveBuffer);
        System.out.println("Servidor recebeu a mensagem < " + message);

        System.out.println("Preparando uma resposta");
        String response = "resposta:" + message;
        System.out.println(response);
        sendBuffer = response.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
            sendBuffer,
            sendBuffer.length,
            receiveDatagram.getAddress(),
            receiveDatagram.getPort());
        serverSocket.send(sendPacket);
        System.out.println("Servidor enviou resposta > " + response);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      // Fechando o servidor.
      if (serverSocket != null)
        serverSocket.close();
    }
  }

  public static String inverteString(String s) {
    String retorna = "";
    s = s.trim();
    int len = s.length();
    for (int i = len; i > 0; i--) {
      if (i - 1 >= 0)
        retorna = retorna + s.charAt(i - 1);
    }
    return retorna;
  }
}
