package com.sbdevs.ecommerce.adapters;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.activities.DBqueriesClass;
import com.sbdevs.ecommerce.activities.DeliveryActivity;
import com.sbdevs.ecommerce.activities.MainActivity;
import com.sbdevs.ecommerce.activities.ProductDetailActivity;
import com.sbdevs.ecommerce.models.deliveryItemModel;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter {
    List<deliveryItemModel> list;
    int lastPosition = -1;
    private TextView cartTotalPriceUnit;
    private boolean showDeleteBtn;

    public CartItemAdapter(List<deliveryItemModel> list, TextView cartTotalPriceUnit, boolean showDeleteBtn) {
        this.list = list;
        this.cartTotalPriceUnit = cartTotalPriceUnit;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).getType()) {
            case 0:
                return deliveryItemModel.CART_ITEM;
            case 1:
                return deliveryItemModel.TOTAL_AMOUNT_LAY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case deliveryItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ast_cart_item_lay, parent, false);
                return new CartItemViewHolder(cartItemView);
            case deliveryItemModel.TOTAL_AMOUNT_LAY:
                View totalAmountView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ast_cart_total_amount_lay, parent, false);
                return new TotalAmountViewHolder(totalAmountView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (list.get(position).getType()) {
            case deliveryItemModel.CART_ITEM:
                String productID = list.get(position).getProductID();
                String resource = list.get(position).getProductImage();
                String name = list.get(position).getProductName();
                String price = list.get(position).getProductPrice();
                boolean inStock = list.get(position).isInStock();
                long quantity = list.get(position).getProductQty();
                long maxQty = list.get(position).getMaxQuantity();
                ((CartItemViewHolder) holder).setProductDetails(productID, resource, name, price, position, String.valueOf(quantity), inStock, maxQty);
                break;
            case deliveryItemModel.TOTAL_AMOUNT_LAY:
                int totalItem = 0;
                int totalItemPrice = 0;
                String deliveryCharge;
                int totalAmpount;

                for (int x = 0; x < list.size(); x++) {
                    if (list.get(x).getType() == deliveryItemModel.CART_ITEM && list.get(x).isInStock()) {
                        totalItem++;
                        totalItemPrice = totalItemPrice + Integer.parseInt(list.get(x).getProductPrice());
                    }
                }
                if (totalItemPrice > 500) {
                    deliveryCharge = "FREE";
                    totalAmpount = totalItemPrice;
                } else {
                    deliveryCharge = "60";
                    totalAmpount = totalItemPrice + 60;
                }
                list.get(position).setTotalItem(totalItem);
                list.get(position).setTotalItemPrice(totalItemPrice);
                list.get(position).setTotalAmpount(totalAmpount);
                list.get(position).setDeliveryCharge(deliveryCharge);
                ((TotalAmountViewHolder) holder).setTotalAmount(totalItem, totalItemPrice, deliveryCharge, totalAmpount);
                break;
            default:
                return;
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slidein_left);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productQty;
        private LinearLayout removeFromCart;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_ProductImg);
            productName = itemView.findViewById(R.id.cart_ProductName);
            productPrice = itemView.findViewById(R.id.cartProductPrice);
            productQty = itemView.findViewById(R.id.cartProductQty);
            removeFromCart = itemView.findViewById(R.id.cartRemoveBtn);
        }

        private void setProductDetails(String productID, String resource, String name, String price, final int position, final String quantity, boolean inStock, final long maxQty) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.i_oppo_a5)).into(productImage);
            productName.setText(name);

            if (inStock) {//todo - IN STOCK #####################################################################
                productPrice.setText("Rs." + price + "/-");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));

                productQty.setText("Qty: " + quantity);
                productQty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.ast_quentity_layout);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantityText = quantityDialog.findViewById(R.id.quantityText);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancelBtn);
                        Button okBtn = quantityDialog.findViewById(R.id.okBtn);
                        quantityText.setHint("max " + String.valueOf(maxQty));

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!TextUtils.isEmpty(quantityText.getText())) {
                                    if (Long.valueOf(quantityText.getText().toString()) <= maxQty && Long.valueOf(quantityText.getText().toString()) != 0 ) {
                                        if (itemView.getContext() instanceof MainActivity){
                                            DBqueriesClass.cartItemModelsList.get(position).setProductQty(Long.valueOf(quantityText.getText().toString()));
                                        }else {
                                            if (DeliveryActivity.fromcart) {
                                                DBqueriesClass.cartItemModelsList.get(position).setProductQty(Long.valueOf(quantityText.getText().toString()));
                                            }else {
                                                DeliveryActivity.cartItemModelsList.get(position).setProductQty(Long.valueOf(quantityText.getText().toString()));
                                            }
                                        }

                                        productQty.setText("Qty: " + quantityText.getText());
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max quantity is " + String.valueOf(maxQty), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });
            } else {//todo - OUT OF STOCK #####################################################################
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.brikeRed));
                productQty.setText("Qty: " + 0);
                productQty.setTextColor(itemView.getContext().getResources().getColor(R.color.brikeRed));
                productQty.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
            }

            if (showDeleteBtn) {
                removeFromCart.setVisibility(View.VISIBLE);
            } else {
                removeFromCart.setVisibility(View.GONE);
            }
            removeFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!ProductDetailActivity.running_Cart_query) {
                        ProductDetailActivity.running_Cart_query = true;
                        DBqueriesClass.removeFromCart(position, itemView.getContext(), cartTotalPriceUnit);
                    }
                }
            });
        }
    }

    public class TotalAmountViewHolder extends RecyclerView.ViewHolder {
        TextView totalItem;
        TextView totalItemPrice;
        TextView deliveryStatus;
        TextView totalAmount;

        public TotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItem = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_item_price);
            deliveryStatus = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_calculeted_price);

        }

        private void setTotalAmount(int itemNum, int totalItemPriceText, String deliveryCharge, int calculatedPrice) {
            totalItem.setText("Price(" + itemNum + ") items");
            totalItemPrice.setText("Rs." + totalItemPriceText + "/-");
            if (deliveryCharge.equals("FREE")) {
                deliveryStatus.setText(deliveryCharge);
            } else {
                deliveryStatus.setText("Rs." + deliveryCharge + "/-");
            }

            totalAmount.setText("Rs." + calculatedPrice + "/-");
            cartTotalPriceUnit.setText("Rs." + calculatedPrice + "/-");

            LinearLayout parent = (LinearLayout) cartTotalPriceUnit.getParent().getParent();
            if (totalItemPriceText == 0) {
                DBqueriesClass.cartItemModelsList.remove(DBqueriesClass.cartItemModelsList.size() - 1);
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
