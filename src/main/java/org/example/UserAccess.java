package org.example;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;

public class UserAccess {

    private final SessionFactory sessionFactory;

    public UserAccess(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addCustomer(Customer customer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(customer);
            session.getTransaction().commit();
        }
    }

    public void addProduct(Product product, Customer customer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            product.setCustomer(customer);

            session.persist(product);
            session.update(customer);

            session.getTransaction().commit();
        }
    }

    public List<Customer> getAllCustomers() {
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(Customer.class);
            return criteria.list();
        }
    }

    public List<Product> getAllProducts() {
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(Product.class);
            return criteria.list();
        }
    }

    public Customer getCustomerByName(Session session, String customerName) {
        Criteria criteria = session.createCriteria(Customer.class);
        criteria.add(Restrictions.eq("customerName", customerName));
        return (Customer) criteria.uniqueResult();
    }

    public Customer getCustomerById(int customerId) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Customer.class, customerId);
        }
    }

    public void show(int customerId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Customer customer = getCustomerById(customerId);

            if (customer != null) {
                Criteria criteria = session.createCriteria(Product.class);
                criteria.add(Restrictions.eq("customer", customer));
                List<Product> products = criteria.list();

                System.out.println("Продукты для клиента с ID " + customerId + ": " + customer.getCustomerName());

                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    System.out.println(product.toString());
                }
            } else {
                System.out.println("Клиент с ID " + customerId + " не найден.");
            }

            session.getTransaction().commit();
        }
    }

    public List<Customer> getCustomersByProductTitle(Session session, String productName) {
        try {
            Criteria criteria = session.createCriteria(Product.class);
            criteria.add(Restrictions.eq("productName", productName));

            // Получение списка продуктов с указанным наименованием
            List<Product> products = criteria.list();

            // Создание списка клиентов, купивших продукт
            List<Customer> customers = products.stream()
                    .map(Product::getCustomer)
                    .collect(Collectors.toList());

            return customers;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    public List<Product> getProductsByCustomer(Customer customer) {
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(Product.class);
            criteria.add(Restrictions.eq("customer", customer));
            return criteria.list();
        }
    }

    public void deleteCustomerByName(String customerName) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Customer customer = getCustomerByName(session, customerName);
            if (customer != null) {
                session.delete(customer);
            }
            session.getTransaction().commit();
        }
    }

    public void deleteProductByName(String productName) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Product product = getProductByName(session, productName);
            if (product != null) {
                session.delete(product);
            }
            session.getTransaction().commit();
        }
    }

    public Product getProductByName(Session session, String productName) {
        try {
            Criteria criteria = session.createCriteria(Product.class);
            criteria.add(Restrictions.eq("productName", productName));
            return (Product) criteria.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}