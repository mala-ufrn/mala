package br.ufrn.mala.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.mala.R;
import br.ufrn.mala.connection.Preferences;
import br.ufrn.mala.exception.ConnectionException;
import br.ufrn.mala.exception.JsonStringInvalidaException;
import br.ufrn.mala.util.Constants;
import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthResponse;

/**
 * Created by Joel Felipe on 02/10/17.
 */

public class AuthorizationActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        webView = (WebView) findViewById(R.id.main_activity_web_view);
        webView.requestFocus(View.FOCUS_DOWN);


        SharedPreferences preferences = this.getSharedPreferences(Constants.KEY_USER_INFO, 0);
        if (preferences.getAll().size() == 0) {
            webView.clearCache(true);
            webView.clearHistory();
            clearCookies();
        }

        pd = ProgressDialog.show(this, "", "Efetuando login", true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                if (authorizationUrl.startsWith(Constants.REDIRECT_URI)) {
                    Uri uri = Uri.parse(authorizationUrl);

                    String authorizationToken = uri.getQueryParameter(Constants.RESPONSE_TYPE_AUTHORIZATION);
                    if (authorizationToken == null) {
                        return true;
                    }

                    new PostRequestAsyncTask().execute(authorizationToken);

                    // Fecha o teclado, caso esteja aberto.
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    try {
                        imm.hideSoftInputFromWindow(webView.getWindowToken(),0);
                    } catch (NullPointerException e) {
                        Log.e("HideKeyboard", "Exception" + e);
                    }
                } else {
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        String authUrl = getAuthorizationUrl();
        webView.loadUrl(authUrl);
    }

    private static String getAuthorizationUrl() {
        return Uri.parse(Constants.AUTHORIZATION_URL)
                .buildUpon()
                .appendQueryParameter(Constants.RESPONSE_TYPE_PARAM, Constants.RESPONSE_TYPE_AUTHORIZATION)
                .appendQueryParameter(Constants.CLIENT_ID_PARAM, Constants.CLIENT_ID_VALUE)
                .appendQueryParameter(Constants.REDIRECT_URI_PARAM, Constants.REDIRECT_URI)
                .build()
                .toString();
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(AuthorizationActivity.this, "", "loading", true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.i("doInBackground", "doInBackground");
            try {
                OAuth2Client client;
                Map<String, String> map = new HashMap<>();
                map.put(Constants.REDIRECT_URI_PARAM, Constants.REDIRECT_URI);
                map.put(Constants.RESPONSE_TYPE_AUTHORIZATION, params[0]);

                client = new OAuth2Client.Builder(Constants.CLIENT_ID_VALUE, Constants.SECRET_KEY, Constants.ACCESS_TOKEN_URL)
                        .grantType(Constants.GRANT_TYPE_AUTHORIZATION)
                        .parameters(map)
                        .build();

                OAuthResponse response = client.requestAccessToken();
                if (response.isSuccessful()) {
                    Preferences.savePreferences(AuthorizationActivity.this, response);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (status) {
                Intent startProfileActivity = new Intent(AuthorizationActivity.this, WelcomeActivity.class);
                startProfileActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                AuthorizationActivity.this.startActivity(startProfileActivity);
            }
        }

    }

    @SuppressWarnings("deprecation")
    public void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.i("removing cookie", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("removing cookie", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(AuthorizationActivity.this);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}