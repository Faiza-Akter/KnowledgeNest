package com.knowledgenest;

import static com.knowledgenest.R.layout.activity_main;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.knowledgenest.Fragment.HomeFragment;
import com.knowledgenest.Fragment.ProfileFragment;
import com.knowledgenest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
   ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),(v, insets) ->  {
           Insets systemBars =insets.getInsets(WindowInsetsCompat.Type.systemBars());
           v.setPadding(systemBars.left,systemBars.top,systemBars.right,systemBars.bottom);
           return insets;
       });

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.background));


        binding.chipNavigationBar2.setItemSelected(R.id.home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

        binding.chipNavigationBar2.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if(i==R.id.home)
                {
                    binding.toolbarTitle.setText("KnowledgeNest");
                    loadFragment(new HomeFragment());

                }
                else if(i==R.id.profile)
                {
                    binding.toolbarTitle.setText("Profile");
                    loadFragment(new ProfileFragment());
                }
            }
        });



    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        fragmentTransaction.commit();

    }
}