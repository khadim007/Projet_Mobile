package com.example.projet_mobile.Modele;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projet_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Accueil {

    public String[][] donnes;
    int nbrAttributs = 13; // table AnnonceS

    private String recherche;
    private String specialite;
    private String lieu;

    public Accueil(String recherche){
        this.recherche = recherche;
    }

    public Accueil(String specialite, String lieu){
        this.specialite = specialite;
        this.lieu = lieu;
    }

    public void recupDonnes(Context context, int part, VolleyCallback callback) {
        String url = context.getString(R.string.url);
        JSONObject postData = new JSONObject();
        if(part == 1){
            try {
                postData.put("choix", "select");
                postData.put("specialite", specialite);
                postData.put("lieu", lieu);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to create JSON object", e);
                return;
            }
        }else{
            try {
                postData.put("choix", "select recherche");
                postData.put("recherche", recherche);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to create JSON object", e);
                return;
            }
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
            response -> {
                try {
                    if(response.getString("donnees").equals("false")){
                        callback.onEmpty();
                    }else {
                        JSONArray jsonArray = response.getJSONArray("donnees");
                        donnes = new String[jsonArray.length()][nbrAttributs];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject dataElement = jsonArray.getJSONObject(i);
                            donnes[i][0] = dataElement.getString("id_");
                            donnes[i][1] = dataElement.getString("nom");
                            donnes[i][2] = dataElement.getString("description");
                            donnes[i][3] = dataElement.getString("employeur");
                            donnes[i][4] = dataElement.getString("remuneration");
                            donnes[i][5] = dataElement.getString("date_debut");
                            donnes[i][6] = dataElement.getString("date_fin");
                            donnes[i][7] = dataElement.getString("metier");
                            donnes[i][8] = dataElement.getString("ville");
                            donnes[i][9] = dataElement.getString("duree");
                            donnes[i][10] = dataElement.getString("mot_cles");
                            donnes[i][11] = dataElement.getString("categorie");
                            donnes[i][12] = dataElement.getString("descriptionEn");
                        }
                        callback.onSuccess();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                if (error instanceof NetworkError) {
                    Toast.makeText(context, "Pas de connexion Internet !", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context, "Probleme de json !", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Erreur lors de l'enregistrement. Veuillez reesayez !", Toast.LENGTH_SHORT).show();
                }
                callback.onError();
            });
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public interface VolleyCallback {
        void onSuccess();
        void onError();
        void onEmpty();
    }
}