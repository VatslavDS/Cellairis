package util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import database.models.Sale;
import database.models.SaleItem;

/**
 * Created by juanc.jimenez on 14/08/14.
 */
public class Payments {

    private int id;
    private String timestamp;
    private int itemsTotal;
    private double total;

    public Payments(int id, String timestamp, int itemsTotal, double total) {

        this.id = id;
        this.timestamp = timestamp;
        this.itemsTotal = itemsTotal;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getItemsTotal() {
        return itemsTotal;
    }

    public double getTotal() {
        return total;
    }

    public static List<Payments> getAll(Context context) {

        List<Payments> payments = new ArrayList<Payments>();
        List<Sale> sales = Sale.getAll(context);

        for (Sale sale : sales) {
            List<SaleItem> saleItems = SaleItem.getBySaleId(context, sale.getId());
            double total = 0;
            for (SaleItem saleItem : saleItems) {
                double price = saleItem.getPriceUnit() * saleItem.getQuantity();
                total += price;
            }
            Payments item = new Payments(sale.getId(), sale.getTimestamp(), saleItems.size(), total);
            payments.add(item);
        }
        return payments;
    }
}

