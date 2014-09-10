package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.cellairispos.R;

/**
 * Created by juanc.jimenez on 01/09/14.
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    public static final int SALES = 0;
    public static final int CATALOG = 1;
    public static final int PAYMENTS_HISTORY = 2;
    public static final int CLIENTS = 3;
    public static final int SETTINGS = 4;
    public static final int LOG_OUT = 5;

    public static int[] images = {R.drawable.icon_boxcash,
            R.drawable.icon_catalog, R.drawable.icon_folder,
            R.drawable.icon_no_profile, R.drawable.icon_settings, R.drawable.icon_logout};

    private Context context;
    private String[] values;

    public DrawerAdapter(Context context, String[] values){
        super(context, R.layout.item_navigation_drawer, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_navigation_drawer, parent, false);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.item_icon);
        TextView textView = (TextView)rowView.findViewById(R.id.item_title);

        textView.setText(values[position]);
        imageView.setImageResource(images[position]);
        return rowView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return values.length;
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


}
