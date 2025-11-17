package com.example.englishapp.screen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.api.ProfileService;
import com.example.englishapp.api.RetrofitClient;
import com.example.englishapp.model.UserProfileResponse;
import com.example.englishapp.model.ProfileFragment;
import com.example.englishapp.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.example.englishapp.screen.ChangePasswordFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Khởi tạo SharedPrefManager ---
        SharedPrefManager.init(this);

        // --- Kiểm tra đăng nhập ---
        String token = SharedPrefManager.getToken();
        if (token == null || token.isEmpty()) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // --- Setup Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // --- Setup Drawer ---
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem otherItem = menu.findItem(R.id.nav_other);

        SpannableString spanString = new SpannableString(otherItem.getTitle());
        spanString.setSpan(new AbsoluteSizeSpan(18, true), 0, spanString.length(), 0); // 18sp
        spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);

        otherItem.setTitle(spanString);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- Gọi API để lấy thông tin user hiển thị ở nav_header ---
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);

        ProfileService profileService = RetrofitClient.getInstance().create(ProfileService.class);
        profileService.getProfile("Bearer " + token).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse user = response.body();

                    // Hiển thị họ tên
                    userNameTextView.setText(user.getFullname());

                    // Hiển thị avatar
                    String avatarUrl = user.getAvatarUrl();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(MainActivity.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.default_avatar)
                                .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.default_avatar);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                t.printStackTrace();
                profileImageView.setImageResource(R.drawable.default_avatar);
            }
        });

        // --- Xử lý chọn menu trong Navigation Drawer ---
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.nav_vocabulary) {
                // Load Vocabulary Fragment
                loadFragment(new VocabularyFragment());
            } else if (id == R.id.nav_change_password) {
                // Change Password
                loadFragment(new ChangePasswordFragment());
            } else if (id == R.id.nav_settings) {
                loadFragment(new SettingsFragment());
            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // --- Setup Bottom Navigation ---
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_bottom_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_bottom_search) {
                selectedFragment = new SearchFragment();
            } else if (id == R.id.nav_bottom_activities) {
                selectedFragment = new ActivitiesFragment();
            } else if (id == R.id.nav_bottom_profile) {
                selectedFragment = new ProfileFragment();
            }

            return loadFragment(selectedFragment);
        });

        // --- Load Fragment mặc định ---
        loadFragment(new HomeFragment());
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void logout() {
        // Xóa token
        SharedPrefManager.saveToken(null);
        RetrofitClient.resetInstance();

        // Chuyển về AuthActivity
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
