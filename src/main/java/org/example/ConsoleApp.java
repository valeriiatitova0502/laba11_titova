package org.example;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private static final UserAccess userAccess;

    static {
        userAccess = new UserAccess(Main.getSessionFactory());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Session session = Main.getSessionFactory().openSession()) {
            while (true) {
                System.out.println("Введите команду:");
                System.out.println("/showProductsByPerson <customer_id>");
                System.out.println("/findPersonsByProductTitle <product_name>");
                System.out.println("/removePerson <customer_id>");
                System.out.println("/removeProduct <product_name>");
                System.out.println("/buy <customer_id> <product_name>");
                System.out.println("/exit");

                String command = scanner.nextLine();
                String[] tokens = command.split("\\s+");

                switch (tokens[0]) {
                    case "/showProductsByPerson":
                        showProductsByPerson(session, tokens);
                        break;
                    case "/findPersonsByProductTitle":
                        findPersonsByProductTitle(session, tokens);
                        break;
                    case "/removePerson":
                        removePerson(session, tokens);
                        break;
                    case "/removeProduct":
                        removeProduct(session, tokens);
                        break;
                    case "/buy":
                        buyProduct(session, tokens);
                        break;
                    case "/exit":
                        return;
                    default:
                        System.out.println("Неверная команда. Попробуйте снова.");
                }

                // Добавьте ожидание завершения транзакции перед переходом к следующей итерации
                try {
                    Thread.sleep(1000); // Может потребоваться настроить под ваши нужды
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static void showProductsByPerson(Session session, String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Неверный формат команды. Использование: /showProductsByPerson <customer_id>");
            return;
        }

        try {
            int customerId = Integer.parseInt(tokens[1]);

            // Получение клиента без продуктов
            Customer customer = session.get(Customer.class, customerId);

            if (customer != null) {
                // Явная инициализация коллекции products
                Hibernate.initialize(customer.getProducts());

                System.out.println("Продукты, купленные " + customer  + ":");
                for (Product product : customer.getProducts()) {
                    System.out.println(product);
                }
            } else {
                System.out.println("Клиент не найден.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат идентификатора клиента. Введите корректное целое число.");
        }
    }

    private static void buyProduct(Session session, String[] tokens) {
        if (tokens.length != 3) {
            System.out.println("Неверный формат команды. Использование: /buy <customer_id> <product_name>");
            return;
        }

        try {
            int customerId = Integer.parseInt(tokens[1]);

            // Извлечение объекта клиента
            Customer customer = session.get(Customer.class, customerId);

            if (customer != null) {
                String productName = tokens[2].trim();

                // Запрос цены у пользователя
                Scanner scanner = new Scanner(System.in);
                System.out.println("Введите цену продукта:");
                double productPrice = scanner.nextDouble();

                // Создание нового объекта продукта
                Product product = new Product();
                product.setProductName(productName);
                product.setProductPrice(productPrice);

                // Используем текущую сессию для сохранения продукта
                try {
                    Transaction transaction = session.beginTransaction();

                    // Обновляем связь с клиентом
                    product.setCustomer(customer);
                    // Сохраняем продукт
                    session.persist(product);

                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Продукт " + productName + " куплен клиентом " + customer.getCustomerName() + " по цене " + productPrice + ".");
            } else {
                System.out.println("Клиент не найден.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат идентификатора клиента. Введите корректное целое число.");
        }
    }

    private static void findPersonsByProductTitle(Session session, String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Неверный формат команды. Использование: /findPersonsByProductTitle <product_name>");
            return;
        }

        String productName = tokens[1].trim();

        // Используем текущую сессию для выполнения запроса
        try (Scanner scanner = new Scanner(System.in)) {
            // Создаем экземпляр UserAccess
            UserAccess userAccess = new UserAccess(Main.getSessionFactory());

            List<Customer> customers = userAccess.getCustomersByProductTitle(session, productName);

            if (!customers.isEmpty()) {
                System.out.println("Клиенты, купившие " + productName + ":");
                customers.forEach(customer ->
                        System.out.println(customer.getCustomerId() + ": " + customer.getCustomerName()));
            } else {
                System.out.println("Не найдены клиенты, купившие " + productName + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат наименования продукта.");
        }
    }

    private static void removePerson(Session session, String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Неверный формат команды. Использование: /removePerson <customer_id>");
            return;
        }

        try {
            int customerId = Integer.parseInt(tokens[1]);

            // Извлечение объекта клиента
            Customer customer = session.get(Customer.class, customerId);

            if (customer != null) {
                // Используем текущую сессию для удаления клиента
                Transaction transaction = session.beginTransaction();
                session.delete(customer);
                transaction.commit();

                System.out.println("Клиент с ID " + customerId + " удален.");
            } else {
                System.out.println("Клиент не найден.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат идентификатора клиента. Введите корректное целое число.");
        }
    }

    private static void removeProduct(Session session, String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Неверный формат команды. Использование: /removeProduct <product_name>");
            return;
        }

        String productName = tokens[1].trim();

        // Используем текущую сессию для удаления продукта
        try {
            Product product = userAccess.getProductByName(session, productName);

            if (product != null) {
                Transaction transaction = session.beginTransaction();
                session.delete(product);
                transaction.commit();

                System.out.println("Продукт " + productName + " удален.");
            } else {
                System.out.println("Продукт не найден.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}