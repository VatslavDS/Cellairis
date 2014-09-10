package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.cellairispos.R;

import java.text.DecimalFormat;
import java.util.List;

import database.models.Product;
import database.models.ProductPic;

/**
 * Created by juanc.jimenez on 13/08/14.
 */
public class ProductsAdapter extends BaseAdapter {

    private Context context;
    private boolean isGrid;

    List<Product> products;
    List<ProductPic> pics;

    //In all adapter we need to pass the context and the list of items
    public ProductsAdapter(Context context, boolean isGrid, List<Product> products, List<ProductPic> pics) {
        this.context = context;
        this.isGrid = isGrid;
        this.products = products;
        this.pics = pics;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //We initialize a holder
        ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //We choose between the the grid or list view, in this case we'll choose gridview
            if (isGrid)
                convertView = inflater.inflate(R.layout.item_products_grid, parent, false);
            else
                convertView = inflater.inflate(R.layout.item_products_list, parent, false);

            //We initialize the viewholder and start to attach the references to ui elements
            holder = new ViewHolder();
            holder.productName = (TextView) convertView.findViewById(R.id.product_name);
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            if (!isGrid)
                holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);
        ProductPic current = ProductPic.getProductByIdProduct(context, products.get(position).getId());
        try{
            holder.productName.setText(product.getName());
            holder.productImage.setImageBitmap(readImageFromExternal(current.getSource()));
        }catch(Exception e){
            holder.productImage.setImageResource(R.drawable.icon_no_product_big);
            holder.productName.setText(product.getName());
        }

        if (!isGrid) {
            DecimalFormat form = new DecimalFormat("0.00");
            holder.productPrice.setText("$" + form.format(product.getPrice()));
        }

        return convertView;
    }

    //Method attached to instance of adapter for change data in the searchField
    public void filterProducts(String query) {

        query = query.toLowerCase();

        List<Product> productsCopy = Product.getAll(context);
        products.clear();

        if (query.isEmpty()) {
            products.addAll(productsCopy);
        } else {
            for (Product item : productsCopy)
            {
                if (item.getName().toLowerCase().contains(query)
                        || item.getBrand().toLowerCase().contains(query)
                        || item.getCode().contains(query)) {
                    if (!products.contains(item))
                        products.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {

        TextView productName, productPrice;
        ImageView productImage;
    }

    public Bitmap readImageFromExternal(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + path, options);
        Bitmap return_bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        return return_bitmap;
    }
}
