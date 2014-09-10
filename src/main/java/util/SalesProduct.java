package util;

import database.models.Product;

/**
 * Created by dmolinero on 14/07/14.
 */
public class SalesProduct extends Product{
    private int quantity;

    public SalesProduct(int id, String name, String brand, Double price, Double cost, float tax, int stock, int stock_central,
                        String code, String description, String category) {
        super(id, name, brand, price, cost, tax, stock, stock_central, code, description, category);

        this.quantity = 0;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {

        return quantity;
    }

    public static SalesProduct parseToSalesProduct(Product product) {

        int id = product.getId() ;
        String name = product.getName();
        String brand = product.getBrand();
        String code = product.getCode();
        String description = product.getDescription();
        Double price = product.getPrice();
        Double cost = product.getCost();
        String category = product.getCategory();
        int stock = product.getStock();
        int stock_central = product.getStock_central();
        float tax = product.getTax();

        return new SalesProduct(id,name,brand,price,cost,tax,stock,stock_central,code,description,category);
    }
}
