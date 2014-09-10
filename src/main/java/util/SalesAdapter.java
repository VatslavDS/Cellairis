package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import com.mx.cellairispos.R;
import com.mx.cellairispos.SalesListFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import database.models.Product;
import database.models.ProductPic;

/**
 * Created by juanc.jimenez on 13/08/14.
 */
public class SalesAdapter extends BaseAdapter implements View.OnClickListener{

    List<SalesProduct> sales;
    public List<Double> subtotals;
    Context context;
    //For the back button
    boolean userClicked;
    SalesProduct item;


    OnTotalChangedListener totalChangedListener;
    OnProductDeletedListener productDeletedListener;

    //We need always pass a context when initialize the adapter
    public SalesAdapter(Context context, List<SalesProduct> sales) {
        this.sales = sales;
        this.context = context;
        subtotals = new ArrayList<Double>();
    }
    @Override
    public int getCount() {
        return sales.size();
    }

    @Override
    public Object getItem(int position) {
        return sales.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_sales, parent, false);

            ProductPic current_pic = ProductPic.getProductByIdProduct(context, sales.get(sales.size() - 1).getId());

            holder = new ViewHolder();

            holder.productName = (TextView) convertView.findViewById(R.id.product_name);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.plusButton = (ImageButton) convertView.findViewById(R.id.plus_button);
            holder.minusButton = (ImageButton) convertView.findViewById(R.id.minus_button);
            holder.quantity = (EditText) convertView.findViewById(R.id.product_quantity);
            holder.productSubtotal = (TextView) convertView.findViewById(R.id.product_subtotal);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.delete_button);

            holder.plusButton.setOnClickListener(this);
            holder.minusButton.setOnClickListener(this);
            holder.deleteButton.setOnClickListener(this);

            try{
                holder.productImage.setImageBitmap(readImageFromExternal(current_pic.getSource()));
            }catch(Exception e){
                holder.productImage.setImageResource(R.drawable.icon_small_product);
            }


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Here is the current row in the list
         item = sales.get(position);

        double subTotal = item.getPrice() * item.getQuantity();

        DecimalFormat form = new DecimalFormat("0.00");

        if (subtotals.size() < sales.size()) {
            subtotals.add(0.0);
        }
        userClicked = false;
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.productName.setText(item.getName());
        holder.productSubtotal.setText("$" + form.format(subTotal));
        holder.position = position;
        holder.deleteButton.setTag(holder);
        holder.plusButton.setTag(holder);
        holder.minusButton.setTag(holder);

        //THE LISTENER FOR THE TEXTCHANGEDL in the quantity!!!
        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (userClicked) {
                    if (!s.toString().isEmpty()) {
                        int currentQuantity = Integer.parseInt(s.toString());
                        SalesProduct product = sales.get(holder.position);
                        product.setQuantity(currentQuantity);
                    }
                    updateSubtotal(item, holder.position, holder.productSubtotal);
                    userClicked = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (SalesListFragment.hideButtons)
            holder.deleteButton.setVisibility(View.GONE);
        else
            holder.deleteButton.setVisibility(View.VISIBLE);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        //We are retrieving the info of the current element
        ViewHolder holder = (ViewHolder) view.getTag();
        int id = view.getId();
        if (id == R.id.delete_button) {
            //Here we ned to retrieve the SalesProduct
            SalesProduct toDelete = sales.get(holder.position);
            //then take the current poisition and to delete, the deleteProduct will call the listener and the callback
            deleteProduct(toDelete, holder.position);
        } else {
            userClicked = true;
            String text = holder.quantity.getText().toString();
            //We need to retrieve the current quantity of each element
            int currentQuantity = Integer.parseInt(text);
            if (text.isEmpty())
                currentQuantity = 0;
            switch (view.getId()) {

                case R.id.plus_button:
                    currentQuantity = currentQuantity + 1;
                    text = String.valueOf(currentQuantity);
                    holder.quantity.setText(text);
                    break;

                case R.id.minus_button:
                    if (currentQuantity > 0)
                        currentQuantity = currentQuantity - 1;
                    else break;

                    text = String.valueOf(currentQuantity);
                    holder.quantity.setText(text);


                    break;
            }
        }
    }

    public void updateSubtotal(SalesProduct product, int position, TextView label) {
        //We retrieve the currentQuality of the current product in the list
        int currentQuantity = product.getQuantity();

        //Here the subtotal of the product is times currentQuantity
        double subTotal = product.getPrice() * currentQuantity;
        DecimalFormat form = new DecimalFormat("0.00");
        //After of create a DecimalFOrmat object we can give format to our subtotal
        //Then the current label (in this case we expect subtotal label and change the it's value)
        label.setText("$" + form.format(subTotal));

        //Then we update the current subtotal in the list subtotal
        subtotals.set(position, subTotal);
        //we iterate over subtotals and add each item to total variable
        double total = 0;
        for (Double item : subtotals) {
            total += item;
        }
        //When and object type interface is implemented we use the method and pass the double total
        //Behind the scene we can change the value
        //The best way to implement a listener
        totalChangedListener.OnTotalChanged(total);
    }

    public void updateSubtotal() {

        //we can use this method only when we want to update the subtotal value without alter any other part
        double total = 0;
        for (Double item : subtotals) {
            total += item;
        }
        //The listener when we finish to update something we call the listener
        totalChangedListener.OnTotalChanged(total);
    }

    public void deleteProduct(SalesProduct product, int position) {

        subtotals.remove(position);
        sales.remove(position);
        notifyDataSetChanged();

        productDeletedListener.onProductDeleted(product);
        updateSubtotal();
    }


    //INTERFACES
    public interface OnTotalChangedListener{

        public void OnTotalChanged(Double orderSubtotals);
    }

    public interface OnProductDeletedListener {
        public void onProductDeleted(SalesProduct product);
    }

    public void setOnTotalChangedListener(OnTotalChangedListener listener) {

        totalChangedListener = listener;
    }

    public void setOnProductDeletedListener(OnProductDeletedListener listener) {

        productDeletedListener = listener;
    }

    static class ViewHolder {

        TextView productName, productSubtotal;
        ImageView productImage;
        ImageButton plusButton, minusButton, deleteButton;
        EditText quantity;

        int position;
    }

    public Bitmap readImageFromExternal(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + path, options);
        Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, true);
        return bitmap;
    }
}
