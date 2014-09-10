package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mx.cellairispos.R;

import java.text.DecimalFormat;
import java.util.List;

import database.models.Product;
import database.models.SaleItem;

/**
 * Created by juanc.jimenez on 03/07/14.
 */
public class ReceiptAdapter extends BaseAdapter {

    Context context;
    List products;

    public ReceiptAdapter(Context context, List products){

        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_receipt, viewGroup, false);

            holder = new ViewHolder();
            holder.productQuantity = (TextView) view.findViewById(R.id.product_quantity);
            holder.productSKU = (TextView) view.findViewById(R.id.product_sku);
            holder.productDescription = (TextView) view.findViewById(R.id.product_description);
            holder.productUnitPrice = (TextView) view.findViewById(R.id.product_unit_price);
            holder.productPrice = (TextView) view.findViewById(R.id.product_price);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        Product product;
        String description, sku;
        int quantity;
        double unitPrice, price;

        SaleItem saleItem = (SaleItem) getItem(position);
        product = Product.getProduct(context, saleItem.getProductId());
        description = product.getName() + "\n" + product.getDescription();
        quantity = saleItem.getQuantity();
        unitPrice = saleItem.getPriceUnit();
        price = quantity * unitPrice;
        sku = product.getCode();

        DecimalFormat form = new DecimalFormat("0.00");
        holder.productQuantity.setText(String.valueOf(quantity));
        holder.productSKU.setText(sku);
        holder.productDescription.setText(description);
        holder.productUnitPrice.setText(form.format(unitPrice));
        holder.productPrice.setText(form.format(price));

        return view;
    }

    static class ViewHolder {

        TextView productQuantity;
        TextView productSKU;
        TextView productDescription;
        TextView productUnitPrice;
        TextView productPrice;
    }
}
