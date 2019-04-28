package Citrus.enviramend;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;

import org.json.JSONException;




public class IngredientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        Intent intent = getIntent();
        //Bundle bundle = intent.getBundleExtra(getResources().getString(R.string.Bundle_Start_Ingredient));
        //String query = bundle.getString(getResources().getString(R.string.Ingredient_Intent_Call));
        String query = intent.getStringExtra(getResources().getString(R.string.Bundle_Start_Ingredient));
        APICall(query);
    }

    public void APICall(String query){
        final Intent returnIntent = new Intent();
        final RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.query_url) + query + "&" + getResources().getString(R.string.api_key);
        if(url == ""){
            Log.e("MyActivity", "Empty url!");
        }
        else {
            Log.e("MyActivity", url);
        }
        JsonObjectRequest apiReturn = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject list = response.getJSONObject("list");
                            JSONArray item = list.getJSONArray("item");
                            JSONObject firstItem = item.getJSONObject(0);
                            String ndbno = firstItem.getString("ndbno");
                            String url = getResources().getString(R.string.item_url) + ndbno + "&" + getResources().getString(R.string.api_key);
                            JsonObjectRequest apiReturn = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONArray foods = response.getJSONArray("foods");
                                                JSONObject firstFood = foods.getJSONObject(0);
                                                JSONObject food = firstFood.getJSONObject("food");
                                                JSONObject ing = food.getJSONObject("ing");
                                                String desc = ing.getString("desc");

                                                returnIntent.putExtra(getResources().getString(R.string.Ingredient_Intent_Return), desc);

                                                setResult(Activity.RESULT_OK, returnIntent);
                                                finish();
                                            }catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            setResult(Activity.RESULT_CANCELED, returnIntent);
                                            finish();
                                        }
                                    });
                            queue.add(apiReturn);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }
                });
        queue.add(apiReturn);
    }
}
