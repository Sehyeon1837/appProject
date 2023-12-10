package com.example.appproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.appproject.WebCrawling.OnTaskCompleted;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class ShowClothes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_clothes);

        // UI 요소 초기화하기
        TextView productNameTextView = findViewById(R.id.productNameTextView);
        TextView brandNameTextView = findViewById(R.id.brandNameTextView);
        TextView priceTextView = findViewById(R.id.priceTextView);
        ImageView productImageView = findViewById(R.id.productImageView);

        // WebCrawling.fetchData를 호출하고 OnTaskCompleted의 인스턴스를 전달하기
        com.example.appproject.WebCrawling.fetchData(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(List<ProcessData> dataList) {

                // 작업 완료 처리, UI 업데이트 등을 수행하기
                if (dataList != null && !dataList.isEmpty()) {

                    // 목록에서 첫 번째 항목을 표시하려고 가정하기
                    ProcessData processData = dataList.get(0);

                    // 데이터로 UI 엘리먼트 업데이트하기
                    productNameTextView.setText(processData.getProductName());
                    brandNameTextView.setText(processData.getBrandName());
                    priceTextView.setText(processData.getPrice());

                    // Glide를 사용하여 이미지 로드하기
                    Glide.with(ShowClothes.this)
                            .load(processData.getImageUrl()) //
                            .apply(RequestOptions.centerCropTransform())
                            .into(productImageView);
                }
            }
        });
    }
}
