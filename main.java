import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


class Transaction {
    private String type;
    private double amount;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return type + ": " + amount;
    }
}


class User {
    private String userId;
    private String pin;
    private double balance;
    private ArrayList<Transaction> transactionHistory;
    private Bank bank;

    public User(String userId, String pin, double initialBalance, Bank bank) {
        this.userId = userId;
        this.pin = pin;
        this.balance = initialBalance;
        this.bank = bank;
        this.transactionHistory = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public boolean validatePin(String inputPin) {
        return pin.equals(inputPin);
    }

    public void printTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions available.");
        } else {
            for (Transaction transaction : transactionHistory) {
                System.out.println(transaction);
            }
        }
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            System.out.println("Insufficient funds.");
        } else {
            balance -= amount;
            transactionHistory.add(new Transaction("Withdraw", amount));
            System.out.println("Withdrawal successful. New balance: " + balance);
        }
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add(new Transaction("Deposit", amount));
        System.out.println("Deposit successful. New balance: " + balance);
    }

    public void transfer(double amount, String recipientId) {
        User recipient = bank.findUserById(recipientId);
        if (recipient == null) {
            System.out.println("Recipient not found.");
        } else if (amount > balance) {
            System.out.println("Insufficient funds.");
        } else {
            balance -= amount;
            recipient.receiveTransfer(amount);
            transactionHistory.add(new Transaction("Transfer to " + recipientId, amount));
            recipient.addTransaction(new Transaction("Transfer from " + userId, amount));
            System.out.println("Transfer successful. New balance: " + balance);
        }
    }

    public void receiveTransfer(double amount) {
        balance += amount;
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
}


class Bank {
    private HashMap<String, User> users;

    public Bank() {
        users = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User authenticateUser(String userId, String pin) {
        User user = users.get(userId);
        if (user != null && user.validatePin(pin)) {
            return user;
        }
        return null;
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }
}


class ATM {
    private User currentUser;
    private Bank bank;

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the ATM");

        while (currentUser == null) {
            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();
            currentUser = bank.authenticateUser(userId, pin);

            if (currentUser == null) {
                System.out.println("Invalid User ID or PIN. Please try again.");
            }
        }

        while (true) {
            System.out.println("\n1. Transactions History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    currentUser.printTransactionHistory();
                    break;
                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    currentUser.withdraw(withdrawAmount);
                    break;
                case 3:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    currentUser.deposit(depositAmount);
                    break;
                case 4:
                    System.out.print("Enter recipient User ID: ");
                    String recipientId = scanner.next();
                    System.out.print("Enter amount to transfer: ");
                    double transferAmount = scanner.nextDouble();
                    currentUser.transfer(transferAmount, recipientId);
                    break;
                case 5:
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}


public class main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.addUser(new User("user1", "1234", 1000.0, bank));
        bank.addUser(new User("user2", "5678", 500.0, bank));

        ATM atm = new ATM(bank);
        atm.start();
    }
}
