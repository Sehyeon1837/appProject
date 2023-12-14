package com.example.appproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebCrawling {

    public interface OnTaskCompleted {
        void onTaskCompleted(List<ProcessData> dataList);
    }

    public static void fetchData(OnTaskCompleted listener) {
        String url = "https://www.musinsa.com/ranking/best?period=now&age=ALL&mainCategory=001&subCategory=&leafCategory=&price=&golf=false&kids=false&newProduct=false&exclusive=false&discount=false&soldOut=false&page=1&viewType=small&priceMin=&priceMax=";

        new FetchDataTask(listener).execute(url);
    }

    private static class FetchDataTask extends AsyncTask<String, Void, List<ProcessData>> {
        private OnTaskCompleted listener;

        public FetchDataTask(OnTaskCompleted listener) {
            this.listener = listener;
        }

        @Override
        protected List<ProcessData> doInBackground(String... urls) {
            List<ProcessData> dataList = new ArrayList<>();

            try {
                Document document = Jsoup.connect(urls[0]).get();
                Elements items = document.select(".list-box .li_box");

                for (Element item : items) {
                    // Product Name
                    String productName = item.select(".article_info .list_info").text();

                    // Brand Name
                    String brandName = item.select(".article_info .item_title").text();

                    // Price
                    String price = item.select(".article_info .price").text();

                    // Link
                    String link = item.select(".article_info .list_info a").attr("href");

                    // Image
                    String imageUrl = item.select(".list_img img").attr("data-original");
                    Bitmap image = downloadImage(imageUrl);

                    ProcessData data = new ProcessData(productName, brandName, price, link, image);
                    dataList.add(data);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return dataList;
        }

        @Override
        protected void onPostExecute(List<ProcessData> dataList) {
            if (listener != null) {
                listener.onTaskCompleted(dataList);
            }
        }
    }

    private static Bitmap downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}



