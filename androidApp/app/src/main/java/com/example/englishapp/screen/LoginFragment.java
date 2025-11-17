package com.example.englishapp.screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.api.AuthService;
import com.example.englishapp.api.RetrofitClient;
import com.example.englishapp.model.AuthResponse;
import com.example.englishapp.model.LoginRequest;
import com.example.englishapp.utils.SharedPrefManager;
import com.example.englishapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private TextInputLayout passwordInputLayout;
    private TextInputEditText passwordEditText;
    private TextInputEditText usernameEditText;
    private AuthService authService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Init shared preferences và auth service
        SharedPrefManager.init(requireContext());
        authService = RetrofitClient.getInstance().create(AuthService.class);

        // Tham chiếu view
        usernameEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        Button loginButton = view.findViewById(R.id.loginButton);

        // Đặt password mặc định ẩn và icon mắt đóng ngay từ đầu
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordInputLayout.setEndIconDrawable(R.drawable.close);

        // Bấm vào icon để ẩn/hiện mật khẩu
        passwordInputLayout.setEndIconOnClickListener(v -> {
            if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                // Hiển thị mật khẩu
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordInputLayout.setEndIconDrawable(R.drawable.open);
            } else {
                // Ẩn mật khẩu
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordInputLayout.setEndIconDrawable(R.drawable.close);
            }
            // Giữ con trỏ cuối text
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Button đăng nhập
        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(username, password);
        authService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();

                    // Lưu token
                    SharedPrefManager.saveToken(auth.getAccessToken());
                    SessionManager sessionManager = new SessionManager(getContext());
                    sessionManager.saveLoginSession(auth.getUserId(), auth.getUsername(), auth.getAccessToken());
                    RetrofitClient.resetInstance();

                    android.util.Log.d("LoginFragment", "Login successful. Token saved: " + auth.getAccessToken());

                    Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
