package com.dam2d.garagedoor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActionsFragment extends Fragment {

    private static final String TAG = "qqq";

    ActionsFragment(FirebaseUser mFirebaseUser) {
        this.firebaseUser = mFirebaseUser;
    }

    private final FirebaseUser firebaseUser;
    static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_actions, container, false);

        //NO ES SEGURO, solo para desarrollo
        handleSSLHandshake();

        new DescargarImagen().execute(firebaseUser.getPhotoUrl().toString());
        TextView textView = view.findViewById(R.id.actions_textView_bienvenida);
        textView.setText(getString(R.string.actions_textView_bienvenida) + " " + firebaseUser.getDisplayName());

        Button actionButton = view.findViewById(R.id.actions_button_abrir);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    //String url = "https://192.168.1.3/open";//Solo durante el desarrollo
                    String url = "https://2.152.242.16/open";//Solo durante el desarrollo

                    Log.d(TAG, "Llego");

                    HashMap<String, String> params = new HashMap<>();
                    params.put("token", firebaseUser.getUid());
                    Log.d(TAG, firebaseUser.getUid());

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "recibida respuesta volley");
                            parsearJSON(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    });
                    MySingleton.getInstance(getContext()).addToRequestQueue(request);
                }
            }
        });

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo redActiva = cm.getActiveNetworkInfo();
        return redActiva != null && redActiva.isAvailable() && redActiva.isConnected();
    }

    private void parsearJSON(JSONObject devuelto) {
        Log.d(TAG, "parseando");
        try {
            String mensaje = devuelto.getString("message");
            Log.d(TAG, mensaje);
            Log.d(TAG, devuelto.toString());
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("TrulyRandom")
    private static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}

class DescargarImagen extends AsyncTask<String, Void, Bitmap> {
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            InputStream is = (InputStream) new URL(params[0]).getContent();
            Bitmap d = BitmapFactory.decodeStream(is);
            is.close();
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        ImageView imageView = ActionsFragment.view.findViewById(R.id.actions_imageview_fotoDePerfil);
        imageView.setImageBitmap(result);
    }
}