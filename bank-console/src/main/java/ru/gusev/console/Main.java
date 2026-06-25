package ru.gusev.console;

import ru.gusev.account.Account;
import ru.gusev.account.AccountService;
import ru.gusev.account.AccountServiceImpl;
import ru.gusev.user.UserService;
import ru.gusev.user.UserServiceImpl;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import ru.gusev.account.JpaAccountRepository;
import ru.gusev.operation.JpaOperationHistoryRepository;
import ru.gusev.user.JpaUserRepository;
import ru.gusev.user.User;
import ru.gusev.user.info.HairColor;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final String PERSISTENCE_UNIT_NAME = "bank-persistence-unit";

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

        try {
            UserService userService = new UserServiceImpl(new JpaUserRepository(entityManagerFactory));
            AccountService accountService = new AccountServiceImpl(
                    new JpaAccountRepository(entityManagerFactory),
                    new JpaOperationHistoryRepository(entityManagerFactory)
            );

            try (Scanner scanner = new Scanner(System.in)) {
                runMenu(scanner, userService, accountService);
            }
        } finally {
            entityManagerFactory.close();
        }
    }

    private static void runMenu(
            Scanner scanner,
            UserService userService,
            AccountService accountService
    ) {
        while (true) {
            printMenu();
            String command = scanner.nextLine().trim();

            try {
                switch (command) {
                    case "1" -> createUser(scanner, userService);
                    case "2" -> createAccount(scanner, userService, accountService);
                    case "3" -> addFriend(scanner, userService);
                    case "4" -> deleteFriend(scanner, userService);
                    case "5" -> findUserById(scanner, userService);
                    case "6" -> findUserByLogin(scanner, userService);
                    case "7" -> printAccountBalance(scanner, accountService);
                    case "8" -> deposit(scanner, accountService);
                    case "9" -> withdraw(scanner, accountService);
                    case "10" -> transfer(scanner, accountService);
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Unknown command");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("""

                1. Create user
                2. Create account
                3. Add friend
                4. Delete friend
                5. Find user by id
                6. Find user by login
                7. Get account balance
                8. Deposit
                9. Withdraw
                10. Transfer
                0. Exit
                """);
        System.out.print("Choose command: ");
    }

    private static void createUser(Scanner scanner, UserService userService) {
        System.out.print("Login: ");
        String login = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        int age = readInt(scanner, "Age: ");
        boolean isMale = readBoolean(scanner, "Is male (true/false): ");
        HairColor hairColor = readHairColor(scanner);

        User user = userService.createUser(login, name, age, isMale, hairColor);
        System.out.println("Created user id: " + user.getId());
    }

    private static void createAccount(Scanner scanner, UserService userService, AccountService accountService) {
        UUID userId = readUuid(scanner, "Owner user id: ");
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserId not found"));

        Account account = accountService.create(user);
        System.out.println("Created account id: " + account.getId());
    }

    private static void addFriend(Scanner scanner, UserService userService) {
        UUID userId = readUuid(scanner, "User id: ");
        UUID friendId = readUuid(scanner, "Friend id: ");

        userService.addUserInFriends(userId, friendId);
        System.out.println("Friend added");
    }

    private static void deleteFriend(Scanner scanner, UserService userService) {
        UUID userId = readUuid(scanner, "User id: ");
        UUID friendId = readUuid(scanner, "Friend id: ");

        userService.deleteUserFromFriends(userId, friendId);
        System.out.println("Friend deleted");
    }

    private static void findUserById(Scanner scanner, UserService userService) {
        UUID userId = readUuid(scanner, "User id: ");

        userService.findUserById(userId)
                .ifPresentOrElse(
                        Main::printUser,
                        () -> System.out.println("User not found")
                );
    }

    private static void findUserByLogin(Scanner scanner, UserService userService) {
        System.out.print("Login: ");
        String login = scanner.nextLine().trim();

        userService.findByLogin(login)
                .ifPresentOrElse(
                        Main::printUser,
                        () -> System.out.println("User not found")
                );
    }

    private static void printAccountBalance(Scanner scanner, AccountService accountService) {
        UUID accountId = readUuid(scanner, "Account id: ");
        BigDecimal balance = accountService.getBalanceById(accountId);

        System.out.println("Balance: " + balance);
    }

    private static void deposit(Scanner scanner, AccountService accountService) {
        UUID accountId = readUuid(scanner, "Account id: ");
        BigDecimal amount = readAmount(scanner, "Amount: ");

        accountService.deposit(accountId, amount);
        System.out.println("Deposit completed");
    }

    private static void withdraw(Scanner scanner, AccountService accountService) {
        UUID accountId = readUuid(scanner, "Account id: ");
        BigDecimal amount = readAmount(scanner, "Amount: ");

        accountService.withdraw(accountId, amount);
        System.out.println("Withdraw completed");
    }

    private static void transfer(Scanner scanner, AccountService accountService) {
        UUID sourceAccountId = readUuid(scanner, "Source account id: ");
        UUID targetAccountId = readUuid(scanner, "Target account id: ");
        BigDecimal amount = readAmount(scanner, "Amount: ");

        accountService.transfer(sourceAccountId, targetAccountId, amount);
        System.out.println("Transfer completed");
    }

    private static UUID readUuid(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid UUID: " + value);
        }
    }

    private static BigDecimal readAmount(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(value);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            return amount;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid amount: " + value);
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer: " + value);
        }
    }

    private static boolean readBoolean(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        throw new IllegalArgumentException("Invalid boolean: " + value);
    }

    private static HairColor readHairColor(Scanner scanner) {
        System.out.print("Hair color (Black/White/Ginger): ");
        String value = scanner.nextLine().trim();

        try {
            return HairColor.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid hair color: " + value);
        }
    }

    private static void printUser(User user) {
        int friendsCount = user.getFriends() == null ? 0 : user.getFriends().size();

        System.out.println("Id: " + user.getId());
        System.out.println("Login: " + user.getLogin());
        System.out.println("Name: " + user.getName());
        System.out.println("Age: " + user.getAge());
        System.out.println("Male: " + user.isMale());
        System.out.println("Hair color: " + user.getHairColor());
        System.out.println("Friends count: " + friendsCount);
    }
}
