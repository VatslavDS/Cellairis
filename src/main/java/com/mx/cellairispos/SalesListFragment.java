package com.mx.cellairispos;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import database.models.Customer;
import database.models.PaymentType;
import database.models.Product;
import database.models.ProductPic;
import database.models.Sale;
import database.models.SaleItem;
import database.models.Session;
import util.LogUtil;
import util.SalesAdapter;
import util.SalesProduct;

/**
 * Created by juanc.jimenez on 12/08/14.
 */
public class SalesListFragment extends Fragment implements View.OnClickListener, SalesAdapter.OnTotalChangedListener, SalesAdapter.OnProductDeletedListener{

    public static final String TAG = "sales_list";

    //UI elements
    private TextView saleSubtotal, saleTax, saleTotal;
    private Button finishSale;
    private ImageButton delete;
    private View emptyView;

    //ListView elements
    private List<SalesProduct> list;
    private SalesAdapter adapter;

    public static boolean hideButtons = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflates the view and initializes the UI elements
        View view = inflater.inflate(R.layout.fragment_sales_list, container, false);

        ListView salesList = (ListView) view.findViewById(R.id.sales_list);
        saleSubtotal = (TextView) view.findViewById(R.id.sale_subtotal);
        saleTax = (TextView) view.findViewById(R.id.sale_tax);
        saleTotal = (TextView) view.findViewById(R.id.sale_total);
        finishSale = (Button) view.findViewById(R.id.finish_sale_button);
        emptyView = view.findViewById(R.id.empty_view);

        list = new ArrayList<SalesProduct>();
        adapter = new SalesAdapter(getActivity(), list);
        //we need to implement the interface and set the listeners
        adapter.setOnTotalChangedListener(this);
        adapter.setOnProductDeletedListener(this);
        salesList.setAdapter(adapter);

        delete = (ImageButton) view.findViewById(R.id.delete_button);
        delete.setOnClickListener(this);
        finishSale.setOnClickListener(this);

        return view;
    }

    //This method is guilty of add new products to list from the productDetial Dialog
    public void addProductToList(Product product) {

        //Convert the product to a SaleProduct item
        SalesProduct salesProduct = SalesProduct.parseToSalesProduct(product);

        //Add the product to the list and refresh the adapter
        list.add(salesProduct);
        adapter.notifyDataSetChanged();

        //Enable the buttons when the list has one or mor items
        if (!finishSale.isEnabled())
            finishSale.setEnabled(true);

        if (!delete.isEnabled())
            delete.setEnabled(true);

        //Hide the empty view
        emptyView.setVisibility(View.GONE);
    }

    public void clearList() {

        //Clear the list, refresh the adapter and show the empty view
        list.clear();
        adapter.notifyDataSetChanged();

        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.delete_button:
                //Toggle the hideButtons flag and refresh the list view to show the buttons
                hideButtons = !hideButtons;
                adapter.notifyDataSetChanged();
                break;

            case R.id.finish_sale_button:
                //Save the sale and show the receipt

                //int idSession = Handler.getCurrentSession(getActivity());
                int idSession = 1;  //Provisional
                long idPaymentType = PaymentType.insert(getActivity(), "CREDIT CARD");
                long idCustomer = Customer.insert(getActivity(), 1, 0, 1);
                long idSale = Sale.insert(getActivity(), (int)idPaymentType, idSession, (int)idCustomer, 1);

                for(SalesProduct itemProduct : list) {

                    int productId = itemProduct.getId();
                    double productCost = itemProduct.getPrice();
                    int quantity = itemProduct.getQuantity();

                    long idItemSale = SaleItem.insert(getActivity(), quantity, productCost, 0, (int) idSale, productId);

                    LogUtil.addCheckpoint("Nuevo SaleItem: " + "Item ID: " + idItemSale + ", Cantidad: " + quantity + ", Precio: " + productCost + ", Product ID: " + productId);
                }

                ReceiptFragment receiptPreview = ReceiptFragment.newInstance((int) idSale);
                receiptPreview.setTargetFragment(this, 1);
                receiptPreview.show(getActivity().getSupportFragmentManager(), ReceiptFragment.TAG);
                break;
        }
    }

    @Override
    public void OnTotalChanged(Double orderSubtotals) {
        //Implementation of the interface from SalesAdapter:OnTotalChangedListener
        //Set the value of the subtotal, total and taxes for the sale
        DecimalFormat form = new DecimalFormat("0.00");
        saleSubtotal.setText("$" + form.format(orderSubtotals));
        saleTax.setText("0.00%");
        saleTotal.setText("$" + form.format(orderSubtotals));
    }

    @Override
    public void onProductDeleted(SalesProduct product) {
        //Implementation of the interface from SalesAdapter:OnProductDeletedListener

        if (list.isEmpty()) {
            hideButtons = true;
            finishSale.setEnabled(false);
            delete.setEnabled(false);
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}
