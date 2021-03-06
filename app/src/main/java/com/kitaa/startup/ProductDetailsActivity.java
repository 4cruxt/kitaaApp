package com.kitaa.startup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kitaa.R;
import com.kitaa.startup.adapters.ProductDetailsAdapter;
import com.kitaa.startup.adapters.ProductImageAdapter;
import com.kitaa.startup.auth.RegisterActivity;
import com.kitaa.startup.models.ProductSpecificationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kitaa.startup.MainActivity.SHOW_WISHLIST;
import static com.kitaa.startup.auth.RegisterActivity._setSignUpFragment;

public class ProductDetailsActivity extends AppCompatActivity
{

    public static boolean ALREADY_IN_WISHLIST = false;
    private Toolbar _toolbar;
    private ViewPager _productImagesViewPager;
    private TabLayout _viewPagerIndicator;
    private FloatingActionButton _addWishlistButton;

    private ViewPager _productDetailsViewPager;
    private TabLayout _productDetailsTabLayout;

    /////ProductImages
    private Dialog _signInDialog;
    private List<String> _productImages;
    private ImageView _productOwnerCall;
    private ImageView _productOwnerChat;

    /////Firebase firestore
    private FirebaseFirestore _firestore;
    private FirebaseUser _currentUser;
    private TextView _productTitle;
    private TextView _productLocation;
    private TextView _productUploadTime;
    private TextView _productPriceDisplay;
    private TextView _product_owner_name;
    private String _dialProductOwner;

    private String _productDescription;
    private List<ProductSpecificationModel> _productSpecificationModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        _toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        _productImagesViewPager = findViewById(R.id.products_images_viewpager);
        _viewPagerIndicator = findViewById(R.id.viewpager_indicator);

        _productDetailsTabLayout = findViewById(R.id.product_details_tablayout);
        _productDetailsViewPager = findViewById(R.id.product_details_viewpager);

        _addWishlistButton = findViewById(R.id.add_to_wishlist_btn);
        _productOwnerCall = findViewById(R.id.product_owner_call);
        _productOwnerChat = findViewById(R.id.product_owner_chat);
        _productTitle = findViewById(R.id.product_title);
        _productLocation = findViewById(R.id.product_map);
        _productUploadTime = findViewById(R.id.product_upload_time);
        _productPriceDisplay = findViewById(R.id.product_price_display);
        _product_owner_name = findViewById(R.id.product_owner_name);

        _firestore = FirebaseFirestore.getInstance();

        prepareProductImageData();
        toggleWishlist();
        detailsTabController();
        authDialog();
        contactProductOwner();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        _currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void contactProductOwner()
    {
        _productOwnerCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent _intentCall = new Intent(Intent.ACTION_DIAL);
                _intentCall.setData(Uri.parse("tel:" + _dialProductOwner));
                startActivity(_intentCall);
            }
        });

        _productOwnerChat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent _intentChat = new Intent(Intent.ACTION_VIEW);
                _intentChat.setType("vnd.android-dir/mms-sms");
                _intentChat.putExtra("address", _dialProductOwner);
                startActivity(_intentChat);
            }
        });
    }

    private void detailsTabController()
    {

        _productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(_productDetailsTabLayout));
        _productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                _productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }

    private void authDialog()
    {
        _signInDialog = new Dialog(ProductDetailsActivity.this);
        _signInDialog.setContentView(R.layout.sign_in_dialog);
        _signInDialog.setCancelable(true);
        Objects.requireNonNull(_signInDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInDialogBtn = _signInDialog.findViewById(R.id.sign_in_dialog_btn);
        Button dialogSignUpDialogBtn = _signInDialog.findViewById(R.id.sign_up_dialog_btn);
        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInDialogBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _signInDialog.dismiss();
                _setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        dialogSignUpDialogBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _signInDialog.dismiss();
                _setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
    }

    private void prepareProductImageData()
    {
        productImageDataList();
        _viewPagerIndicator.setupWithViewPager(_productImagesViewPager, true);
    }

    private void productImageDataList()
    {
        _productImages = new ArrayList<>();
        //todo: need to input product Id
        _firestore.collection("PRODUCTS").document(Objects.requireNonNull(getIntent().getStringExtra("PRODUCT_ID"))).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @SuppressLint("SetTextI18n")

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot _documentSnapshot = task.getResult();
                    for(long x = 1; x < (long) Objects.requireNonNull(_documentSnapshot).get("number_of_product_images") + 1; x++)
                    {
                        _productImages.add(Objects.requireNonNull(_documentSnapshot.get("product_image_" + x)).toString());
                    }
                    ProductImageAdapter _productImageAdapter = new ProductImageAdapter(_productImages);
                    _productImagesViewPager.setAdapter(_productImageAdapter);

                    _productTitle.setText(Objects.requireNonNull(Objects.requireNonNull(_documentSnapshot).get("product_title")).toString());
                    _productLocation.setText(Objects.requireNonNull(Objects.requireNonNull(_documentSnapshot).get("product_location")).toString());
                    _productPriceDisplay.setText("Tshs." + Objects.requireNonNull(Objects.requireNonNull(_documentSnapshot).get("product_price")).toString());
                    _productUploadTime.setText(Objects.requireNonNull(Objects.requireNonNull(_documentSnapshot).get("product_uploaded_time")).toString());
                    _product_owner_name.setText(Objects.requireNonNull(Objects.requireNonNull(_documentSnapshot).get("product_owner")).toString());
                    _dialProductOwner = Objects.requireNonNull(Objects.requireNonNull(_documentSnapshot).get("product_contact")).toString();

                    _productDescription = Objects.requireNonNull(_documentSnapshot.get("product_description")).toString();

                    for(long x = 1; x < (long) _documentSnapshot.get("number_of_field_names") + 1; x++)
                    {
                        _productSpecificationModelList.add(new ProductSpecificationModel(Objects.requireNonNull(_documentSnapshot.get("field_" + x + "_name")).toString(), Objects.requireNonNull(_documentSnapshot.get("field_" + x + "_value")).toString()));
                    }
                    _productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), _productDetailsTabLayout.getTabCount(), _productDescription, _productSpecificationModelList));
                }
                else
                {
                    String error = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void toggleWishlist()
    {
        _addWishlistButton.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v)
            {
                if(_currentUser != null)
                {
                    if(ALREADY_IN_WISHLIST)
                    {
                        ALREADY_IN_WISHLIST = false;
                        _addWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#E6CE97")));
                    }
                    else
                    {
                        ALREADY_IN_WISHLIST = true;
                        _addWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#FFB000")));
                    }
                }
                else
                {
                    _signInDialog.show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_wishlist_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            finish();
            return true;
        }
        else if(id == R.id.main_search_icon)
        {
            //todo : search
            return true;
        }
        else if(id == R.id.main_wishlist_icon)
        {
            if(_currentUser != null)
            {
                Intent wishlistIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                SHOW_WISHLIST = true;
                startActivity(wishlistIntent);
                return true;
            }
            else
            {
                _signInDialog.show();
                return false;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
