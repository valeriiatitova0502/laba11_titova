package org.example;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Products")
public class Product {

    // Уникальный идентификатор продукта
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "product_id")
    private int productId;

    // Наименование продукта
    @Column(name = "product_name")
    private String productName;

    // Цена продукта
    @Column(name = "product_price")
    private double productPrice;

    // Связь с сущностью Customer (многие продукты могут принадлежать одному клиенту)
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Конструкторы

    // Пустой конструктор (необходим для Hibernate)
    public Product() {
    }
    // Новый метод для получения отформатированной информации о продукте
    public String getProductInfo() {
        return "   - " + getProductName() + " - " + getProductPrice();
    }

    // Конструктор с параметрами для установки наименования и цены продукта
    public Product(String productName, double productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
    }

    // Методы доступа (геттеры и сеттеры)

    // Получение уникального идентификатора продукта
    public int getProductId() {
        return productId;
    }

    // Установка уникального идентификатора продукта
    public void setProductId(int productId) {
        this.productId = productId;
    }

    // Получение наименования продукта
    public String getProductName() {
        return productName;
    }

    // Установка наименования продукта
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Получение цены продукта
    public double getProductPrice() {
        return productPrice;
    }

    // Установка цены продукта
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    // Получение клиента, которому принадлежит продукт
    public Customer getCustomer() {
        return customer;
    }

    // Установка клиента для продукта
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Методы equals и hashCode (опциональные, генерируются на основе ваших потребностей)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId &&
                Double.compare(product.productPrice, productPrice) == 0 &&
                Objects.equals(productName, product.productName);
    }
    public String toString() {
        return "Product [productId=" + productId + ", productName=" + productName + ", productPrice=" + productPrice + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, productPrice);
    }
}