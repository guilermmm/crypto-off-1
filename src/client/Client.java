package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

import crypto.Crypto;
import dbg.Dbg;
import dbg.Dbg.Color;

public class Client implements Runnable {
  /*
   * DatagramSocket para o cliente
   */
  Boolean active = true;
  DatagramSocket clientSocket = null;
  Scanner sc = new Scanner(System.in);
  InetAddress address;
  byte[] sendBuffer;
  byte[] receiveBuffer;
  String token;
  Crypto crypto;
  Boolean logged = false;

  public Client() {
    crypto = new Crypto();
    start();
  }

  public Client(String hmacKey) {
    crypto = new Crypto(hmacKey);
    start();
  }

  private void start() {
    Thread t = new Thread(this);
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      Dbg.log();
      clientSocket = new DatagramSocket();
      address = InetAddress.getByName("localhost");
      Dbg.log(Color.CYAN, "Cliente rodando em: " +
          InetAddress.getLocalHost() + ":" +
          clientSocket.getLocalPort() + "\n");
      while (active) {

        Dbg.log(Color.BLUE, "|--- Sistema do banco ---|");
        Dbg.log(Color.BLUE, "1. Login");
        Dbg.log(Color.BLUE, "2. Cadastrar");
        String msg = sc.nextLine().trim();

        switch (msg) {

          case "1":
            if (!logged) {
              login();
            } else {
              Dbg.log(Color.RED, "Usuário já autenticado");
            }
            break;
          case "2":
            if (!logged) {
              signUp();
            } else {
              Dbg.log(Color.RED, "Usuário já autenticado");
            }
            break;

          case "exit":
            active = false;
            Dbg.log(Color.RED, "Cliente encerrado");
            break;
          default:
            Dbg.log(Color.RED, "Comando inválido");
            break;
        }

      }
    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (clientSocket != null)
        clientSocket.close();
      sc.close();
    }
  }

  public void login() throws Exception {
    Dbg.log();
    Dbg.log(Color.BLUE, "|--- Login ---|");
    Dbg.log(Color.YELLOW, "Digite o usuário:");
    String user = sc.nextLine();
    Dbg.log(Color.YELLOW, "Digite a senha:");
    String password = sc.nextLine();

    sendMessage("login/" + user + ":" + password);

    String response = receiveMessage();

    Dbg.log();

    try {
      response = crypto.decryptMessage(new String(receiveBuffer));
    } catch (Exception e) {
      Dbg.log(Color.RED, e.getMessage());
      return;
    }

    if (response.contains("true")) {
      Dbg.log(Color.GREEN, "Usuário autenticado com sucesso!");
      logged = true;
    } else {
      Dbg.log(Color.RED, "Número da conta e/ou senha incorretos.");
    }
  }

  public void signUp() throws Exception {
    Dbg.log();
    Dbg.log(Color.BLUE, "|--- Cadastro ---|");
    Dbg.log(Color.YELLOW, "Digite o nome:");
    String name = sc.nextLine();
    Dbg.log(Color.YELLOW, "Digite o CPF:");
    String cpf = sc.nextLine();
    Dbg.log(Color.YELLOW, "Digite a senha:");
    String password = sc.nextLine();
    Dbg.log(Color.YELLOW, "Digite o endereço:");
    String address = sc.nextLine();
    Dbg.log(Color.YELLOW, "Digite o telefone:");
    String phone = sc.nextLine();
    String msg = "signup/" + name + ":" + cpf + ":" + password + ":" + address + ":" + phone;

    sendMessage(msg);

    String response = receiveMessage();

    Dbg.log();

    try {
      response = crypto.decryptMessage(new String(receiveBuffer));
    } catch (Exception e) {
      Dbg.log(Color.RED, e.getMessage());
      return;
    }

    if (response.contains("true")) {
      String accountNumber = response.split(":")[1];
      Dbg.log(Color.GREEN, "Usuário cadastrado com sucesso!\nFaça login com o número da conta " + accountNumber
          + " e a senha cadastrada.");
    } else {
      Dbg.log(Color.RED, "Erro ao cadastrar usuário.");
    }
  }

  private String receiveMessage() throws Exception {
    receiveBuffer = new byte[1024];

    DatagramPacket receiveDatagram = new DatagramPacket(
        receiveBuffer,
        receiveBuffer.length);
    clientSocket.receive(receiveDatagram);
    receiveBuffer = receiveDatagram.getData();

    try {
      return crypto.decryptMessage(new String(receiveBuffer));
    } catch (Exception e) {
      Dbg.log(Color.RED, e.getMessage());
      return null;
    }
  }

  private void sendMessage(String message) throws Exception {
    sendBuffer = new byte[1024];
    String msg = crypto.encryptMessage(message);
    sendBuffer = msg.getBytes();
    DatagramPacket sendDatagram = new DatagramPacket(
        sendBuffer,
        sendBuffer.length,
        address,
        5050);

    clientSocket.send(sendDatagram);
  }
}