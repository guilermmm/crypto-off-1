package server;

public class User {
  String cpf, password, name, address, phone, token;
  int currentBalance, savingsBalance, fixedBalance;

  public User(String cpf, String password, String name, String address, String phone) {
    this.cpf = cpf;
    this.password = password;
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.currentBalance = 0;
    this.savingsBalance = 0;
    this.fixedBalance = 0;
  }

  private float transformToFloat(int value) {
    return (float) value / 100;
  }

  private int transformToInt(float value) {
    return (int) (value * 100);
  }

  public Boolean withdraw(float value) {
    int intValue = transformToInt(value);

    if (this.currentBalance < intValue)
      return false;

    this.currentBalance -= intValue;
    return true;
  }

  public void deposit(float value) {
    int intValue = transformToInt(value);

    this.currentBalance += intValue;
  }

  public Boolean transfer(User user, int value) {
    if (this.currentBalance < value)
      return false;

    this.currentBalance -= value;
    user.currentBalance += value;
    return true;
  }

  public float getCurrent() {
    return transformToFloat(this.currentBalance);
  }

  public float getSavings() {
    return transformToFloat(this.savingsBalance);
  }

  public float getFixed() {
    return transformToFloat(this.fixedBalance);
  }

}
