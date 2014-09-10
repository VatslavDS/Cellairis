package util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mx.cellairispos.ClientsListFragment;
import com.mx.cellairispos.R;

import java.util.List;

import database.models.Customer;

/**
 * Created by juanc.jimenez on 01/08/14.
 */
public class ClientsAdapter extends BaseAdapter implements View.OnClickListener{

    Context context;
    List<Customer> clients;
    OnClientDeletedListener onClientDeletedListener;

    //When call the constructor we need to pass context and list clients
    public ClientsAdapter(Context context, List<Customer> clients) {

        this.context = context;
        this.clients = clients;
    }

    @Override
    public int getCount() {
        return clients.size();
    }

    @Override
    public Object getItem(int i) {
        return clients.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        //An declaration of viewholder for performance of the list
        final ViewHolder viewHolder;


        //We check if the view is first time initialize
        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_clients, viewGroup, false);

            viewHolder = new ViewHolder();

            viewHolder.deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
            viewHolder.clientPhoto = (ImageView) view.findViewById(R.id.client_photo);
            viewHolder.clientName = (TextView) view.findViewById(R.id.client_name);
            viewHolder.clientEmail = (TextView) view.findViewById(R.id.client_email);

             //we need to set the listener to an object of the holder, important thing.
            viewHolder.deleteButton.setOnClickListener(this);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        //Retrieve the current customer of the position, as the customer are order by a list and of the other thins in the app
        Customer item = clients.get(position);

        viewHolder.clientName.setText(item.getInfo().getFirstName());
        viewHolder.clientEmail.setText(item.getInfo().getEmail());

           //This part we introduce the of the buttons
        if (ClientsListFragment.hideButtons)
            viewHolder.deleteButton.setVisibility(View.GONE);
        else
            viewHolder.deleteButton.setVisibility(View.VISIBLE);

        viewHolder.deleteButton.setTag(position);
        viewHolder.position = position;

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.delete_button){
            int position = (Integer)view.getTag();
            int data = Customer.delete(context, clients.get(position).getId());
            Log.v("ELiminate", " " + data + " customer " + clients.get(position).getId());
            clients.remove(position);
            notifyDataSetChanged();
            onClientDeletedListener.onClientDeleted(position);
        }
    }

    public void filterClients(String query) {

        query = query.toLowerCase();

        List<Customer> clientsCopy = Customer.getAll(context);
        clients.clear();

        if (query.isEmpty()) {
            clients.addAll(clientsCopy);
        } else {
            for (Customer item : clientsCopy)
            {
                if (item.getInfo().getFullName().toLowerCase().contains(query)
                        || item.getInfo().getEmail().toLowerCase().contains(query)
                        || item.getInfo().getPhone().contains(query)) {
                    if (!clients.contains(item))
                        clients.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    //Interfaces
    public interface OnClientDeletedListener{

        public void onClientDeleted(int position);
    }

    public interface OnClientAddListener{

        public void onClientAdd(Customer customer);
    }

    public void setOnClientDeletedListener(OnClientDeletedListener listener) {

        onClientDeletedListener = listener;
    }

    class ViewHolder{

        ImageView deleteButton;
        ImageView clientPhoto;
        TextView clientName;
        TextView clientEmail;

        int position;
    }
}
