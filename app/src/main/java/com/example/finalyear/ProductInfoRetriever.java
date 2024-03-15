package com.example.finalyear;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductInfoRetriever extends AsyncTask<String, Void, String> {
    private static final String API_BASE_URL = "https://world.openfoodfacts.org/api/v2/product/";

    private Context context; // Added member variable to hold the context

    public ProductInfoRetriever(Context context) { // Added constructor to receive context
        this.context = context;
    }

    private OnProductInfoReceivedListener listener;

    public interface OnProductInfoReceivedListener {
        void onProductInfoReceived(String productName, String ingredients);
    }

    public void setOnProductInfoReceivedListener(OnProductInfoReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... barcodes) {
        String barcode = barcodes[0];
        String apiUrl = API_BASE_URL + barcode;
        String result = null;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            result = stringBuilder.toString();

            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();
        } catch (IOException e) {
            Log.e("ProductInfoRetriever", "Error fetching data", e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject product = jsonObject.getJSONObject("product");

                String productName = product.optString("product_name", "");
                String ingredients = product.optString("ingredients_text", "");

                if (listener != null) {
                    listener.onProductInfoReceived(productName, ingredients);
                    startProductInfoActivity(productName, ingredients); // Changed this line
                }
            } catch (JSONException e) {
                Log.e("ProductInfoRetriever", "Error parsing JSON", e);
            }
        }
    }

    private void startProductInfoActivity(String productName, String ingredients) {
        Intent intent = new Intent(context, productInfoActivity.class);
        intent.putExtra("PRODUCT_NAME", productName);
        intent.putExtra("INGREDIENTS", ingredients);
        context.startActivity(intent);
    }
}


