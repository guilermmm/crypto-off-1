package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

import dbg.Dbg;
import dbg.Dbg.Color;

public class Client {
  /*
   * DatagramSocket para o cliente
   */
  Boolean active = true;
  DatagramSocket clientSocket = null;
  Scanner sc = new Scanner(System.in);
  InetAddress address;
  byte[] sendBuffer;
  byte[] receiveBuffer;

  public Client() {
    run();
  }

  public void run() {
    try {
      clientSocket = new DatagramSocket();
      address = InetAddress.getByName("localhost");
      System.out.println("Cliente rodando em: " +
          InetAddress.getLocalHost() + ":" +
          clientSocket.getLocalPort());
      while (active) {

        Dbg.log(Color.BLUE, "|--- Sistema do banco ---|");
        Dbg.log(Color.BLUE, "1. Autenticar");
        String msg = sc.nextLine().trim();

        Integer.parseInt(msg);

        if (msg.equals("1")) {
          if (authenticate()) {
            Dbg.log(Color.GREEN, "Autenticado com sucesso");
          } else {
            Dbg.log(Color.RED, "Falha na autenticação");
          }
        }

        if (msg.trim().equals("exit")) {
          active = false;
          System.out.println("Cliente encerrado");
          break;
        }

        sendBuffer = msg.getBytes();
        DatagramPacket sendDatagram = new DatagramPacket(
            sendBuffer,
            sendBuffer.length,
            address,
            5050);
        System.out.println("Cliente " +
            address.getHostAddress() + " " +
            clientSocket.getLocalPort() +
            " enviando mensagem > " +
            msg);

        clientSocket.send(sendDatagram);

        receiveBuffer = new byte[1024];
        DatagramPacket receiveDatagram = new DatagramPacket(
            receiveBuffer,
            receiveBuffer.length);
        clientSocket.receive(receiveDatagram);
        receiveBuffer = receiveDatagram.getData();
        System.out.println("Cliente " +
            address.getHostAddress() +
            " recebeu mensagem < " +
            new String(receiveBuffer));
      }
    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    } finally {
      if (clientSocket != null)
        clientSocket.close();
      sc.close();
    }
  }

  public Boolean authenticate() throws IOException {
    Dbg.log(Color.YELLOW, "Digite o usuário:");
    String user = sc.nextLine();
    Dbg.log(Color.YELLOW, "Digite a senha:");
    String password = sc.nextLine();

    String msg = "auth/" + user + ":" + password;

    sendBuffer = msg.getBytes();
    DatagramPacket sendDatagram = new DatagramPacket(
        sendBuffer,
        sendBuffer.length,
        address,
        5050);

    clientSocket.send(sendDatagram);

    receiveBuffer = new byte[1024];

    DatagramPacket receiveDatagram = new DatagramPacket(
        receiveBuffer,
        receiveBuffer.length);
    clientSocket.receive(receiveDatagram);
    receiveBuffer = receiveDatagram.getData();

    String response = new String(receiveBuffer);

    return response.trim().equals("true");
  }
}