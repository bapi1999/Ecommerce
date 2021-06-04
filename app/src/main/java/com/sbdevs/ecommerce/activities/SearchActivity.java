package com.sbdevs.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sbdevs.ecommerce.R;
import com.sbdevs.ecommerce.adapters.MyWishlistAdapter;
import com.sbdevs.ecommerce.models.MyWishlistModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private TextView textView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view);
        textView = findViewById(R.id.text_view);
        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        final List<MyWishlistModel> list = new ArrayList<>();
        final  List<String> ids = new ArrayList<>();
        final Adapter adapter = new Adapter(list,false);
        adapter.setFromSearch(true);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                list.clear();
                ids.clear();
                final  String[] tags = query.toLowerCase().split("");
                for (final String tag : tags){
                    tag.trim();
                    FirebaseFirestore.getInstance().collection("PRODUCTS").whereArrayContains("tags",tag)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                    //todo- ===================================================================================
                                    //todo- ---- T-103 on hold at 30.37 min. method cant be implemented due to previous mistake
                                    //todo- ===================================================================================

                                    MyWishlistModel model =  new MyWishlistModel(
                                            documentSnapshot.getId(),
                                            documentSnapshot.get("product_img_1").toString(),
                                            documentSnapshot.get("product_title").toString(),
                                            documentSnapshot.get("rating_avg").toString(),
                                            (long)documentSnapshot.get("rating_total"),
                                            documentSnapshot.get("product_price").toString(),
                                            documentSnapshot.get("product_price").toString(),
                                            (boolean)documentSnapshot.get("in_stock"));

                                    model.setTags((ArrayList<String>) documentSnapshot.get("tags"));
                                    if (!ids.contains(model.getProductID())){
                                        list.add(model);
                                        ids.add(model.getProductID());
                                    }

                                }

                                textView.setVisibility(View.GONE);

                                if (tag.equals(tags[tags.length - 1])){
                                    if (list.size() == 0){
                                        textView.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else {
                                        textView.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adapter.getFilter().filter(query);
                                    }

                                }

                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    class Adapter extends MyWishlistAdapter implements Filterable{


        List<MyWishlistModel> originalList;

        public Adapter(List<MyWishlistModel> list, Boolean wishlist) {
            super(list, wishlist);
            originalList =list;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    List<MyWishlistModel> filteredList = new ArrayList<>();

                    final  String[] tags = charSequence.toString().toLowerCase().split("");

                    for (MyWishlistModel model : originalList){
                        ArrayList<String> presentTags = new ArrayList<>();
                        for (String tag : tags){
                            if (model.getTags().contains(tag)){
                                presentTags.add(tag);
                            }
                        }
                        model.setTags(presentTags);
                    }
                    for (int i = tags.length - 1; i>0 ;i--){
                        for (MyWishlistModel model : originalList){
                            if (model.getTags().size() == i){
                                filteredList.add(model);
                            }
                        }
                    }

                    results.values = filteredList;
                    results.count  = filteredList.size();

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults results) {
                    if (results.count > 0){
                        setList((List<MyWishlistModel>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}