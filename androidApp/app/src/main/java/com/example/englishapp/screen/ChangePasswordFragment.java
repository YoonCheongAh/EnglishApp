package com.example.englishapp.screen;

import android.os.Bundle;
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
import com.example.englishapp.model.ChangePasswordRequest;
import com.example.englishapp.utils.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText currentPassEdt, newPassEdt, confirmPassEdt;
    private AuthService authService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPrefManager.init(requireContext());
        authService = RetrofitClient.getInstance().create(AuthService.class);

        currentPassEdt = view.findViewById(R.id.current_password_input);
        newPassEdt = view.findViewById(R.id.new_password_input);
        confirmPassEdt = view.findViewById(R.id.confirm_password_input);
        Button btnChange = view.findViewById(R.id.btn_change_password);

        btnChange.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPass = currentPassEdt.getText().toString().trim();
        String newPass = newPassEdt.getText().toString().trim();
        String confirmPass = confirmPassEdt.getText().toString().trim();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra token
        String token = SharedPrefManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập hoặc token hết hạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        ChangePasswordRequest req = new ChangePasswordRequest(currentPass, newPass, confirmPass);

        authService.changePassword(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    currentPassEdt.setText("");
                    newPassEdt.setText("");
                    confirmPassEdt.setText("");
                } else if (response.code() == 400) {
                    Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Toast.makeText(getContext(), "Token hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.clearAll();
                } else if (response.code() == 403) {
                    Toast.makeText(getContext(), "Bạn không có quyền thực hiện hành động này!", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.clearAll();
                } else {
                    Toast.makeText(getContext(), "Lỗi: " + response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
