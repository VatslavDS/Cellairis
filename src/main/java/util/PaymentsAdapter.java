package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mx.cellairispos.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by juanc.jimenez on 14/08/14.
 */
public class PaymentsAdapter extends BaseAdapter {

    Context context;
    List<Payments> paymentsList;

    public PaymentsAdapter(Context context, List<Payments> paymentsList) {
        this.context = context;
        this.paymentsList = paymentsList;
    }

    @Override
    public int getCount() {
        return paymentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return paymentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_payments, parent, false);

            holder = new ViewHolder();

            holder.paymentTimestamp = (TextView) convertView.findViewById(R.id.payment_date_time);
            holder.paymentItemsTotal = (TextView) convertView.findViewById(R.id.payment_items_total);
            holder.paymentsTotal = (TextView) convertView.findViewById(R.id.payments_total);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Payments item = paymentsList.get(position);

        holder.paymentTimestamp.setText(item.getTimestamp());
        holder.paymentItemsTotal.setText(item.getItemsTotal() + " " + context.getString(R.string.receipt_items_total));

        DecimalFormat form = new DecimalFormat("0.00");
        holder.paymentsTotal.setText("$" + form.format(item.getTotal()));

        return convertView;
    }

    //We need to modify this, and handle correct data
    public void filterPayments(String timestampFrom, String timestampTo) {

        List<Payments> paymentsCopy = Payments.getAll(context);
        paymentsList.clear();

        if (timestampFrom.isEmpty() || timestampTo.isEmpty()) {
            paymentsList.addAll(paymentsCopy);
        } else {

            for (Payments item : paymentsCopy)
            {
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                    long date = formatter.parse(item.getTimestamp()).getTime();
                    long from = formatter.parse(timestampFrom).getTime();
                    long to = formatter.parse(timestampTo).getTime();

                    if (date >= from && date <= to) {
                        if (!paymentsList.contains(item))
                            paymentsList.add(item);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder{

        TextView paymentTimestamp;
        TextView paymentItemsTotal;
        TextView paymentsTotal;
    }
}
