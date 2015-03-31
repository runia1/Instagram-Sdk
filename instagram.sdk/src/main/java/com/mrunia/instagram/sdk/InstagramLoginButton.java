package com.mrunia.instagram.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mar on 2/15/15.
 */
public class InstagramLoginButton extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "InstagramLoginButton";

    private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
    private String callbackUrl, clientId, clientSecret;
    private String[] scopes;

    private String authUrlString;
    private String response_code;

    private Context c;
    private Dialog authDialog;
    private RelativeLayout th;
    private Callback<InstagramSession> callback;
    private Exception exception;

    public InstagramLoginButton(Context context) {
        super(context);
        c = context;
        onCreate(context);
    }

    public InstagramLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
        onCreate(context);
    }

    public InstagramLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        c = context;
        onCreate(context);
    }

    private void onCreate(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.insta_login_button_layout, this, true);
        th = (RelativeLayout)findViewById(R.id.insta_login_button);
        th.setOnClickListener(this);
        //if the session is active display "Log out of Instagram"
        if(InstagramSession.getInstance(context).isSessionActive()) {
            TextView t = (TextView)th.findViewById(R.id.insta_login_button_text);
            t.setText("Log out of Instagram");
        }

        InstagramAuthConfig authConfig = Instagram.getInstance().getAuthConfig();
        clientId = authConfig.getClientId();
        clientSecret = authConfig.getClientSecret();
        callbackUrl = authConfig.getCallbackUrl();
        scopes = authConfig.getScopes();

        authUrlString = AUTHURL +
                "?client_id=" + clientId +
                "&redirect_uri=" + callbackUrl +
                "&response_type=code" +
                "&display=touch" +
                "&scope=";
        // add the scopes
        for(int i=0; i<scopes.length; i++) {
            if(i+1 == scopes.length) authUrlString += scopes[i];
            else                     authUrlString += scopes[i] + "+"; // add a plus in between unless it's the last one.
        }
        Log.i(TAG, "AuthUrlString: "+authUrlString);
    }

    @Override
    public void onClick(View v) {
        if(InstagramSession.getInstance(c).isSessionActive()) {
            //logout and change button text
            InstagramSession.getInstance(c).closeSession();
            TextView t = (TextView)th.findViewById(R.id.insta_login_button_text);
            t.setText("Log in to Instagram");
            callback.logout();
        } else {
            //if there is an internet connection go ahead.
            if(isConnected()) {
                //attempt to login
                //disable the button till at least the authDialog is displayed.
                setButtonEnabled(false);
                authDialog = new Dialog(c);
                authDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                authDialog.setContentView(R.layout.insta_auth_dialog);
                authDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //if the user hits the back button or clicks outside the dialog, closing it...
                        //simply re-enable the button.
                        setButtonEnabled(true);
                    }
                });
                //make sure the webview clears any saved cookies before it displays so that login screen will appear.
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();

                WebView login = (WebView) authDialog.findViewById(R.id.insta_login_webview);
                login.setVerticalScrollBarEnabled(false);
                login.setHorizontalScrollBarEnabled(false);
                login.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        //if this page is from your callback url, grab the code=CODE and close the webview.
                        if (url.startsWith(callbackUrl)) {
                            Log.i(TAG, "RESPOSE FROM AUTH SERVER: " + url);
                            if (url.contains("code=")) {
                                Uri uri = Uri.parse(url);
                                response_code = uri.getQueryParameter("code");
                                new FetchToken().execute();
                            } else if (url.contains("error=")) {
                                Uri uri = Uri.parse(url);
                                String error = uri.getQueryParameter("error_description");
                                Toast.makeText(c, "Cannot get AccessToken: " + error, Toast.LENGTH_LONG).show();
                            }
                            authDialog.dismiss();
                            return true;
                        }
                        //Allow this page to load if it's not from your callback url.
                        //Which means it should be one of the instagram auth pages.
                        return false;
                    }
                });
                login.getSettings().setJavaScriptEnabled(true);
                login.loadUrl(authUrlString);
                authDialog.show();
            }
        }
    }

    private void setButtonEnabled(Boolean enabled) {
        if(enabled) {
            th.setEnabled(true);
            th.findViewById(R.id.insta_login_button_text).setEnabled(true);
            th.findViewById(R.id.insta_login_button_image).setEnabled(true);
        } else {
            th.setEnabled(false);
            th.findViewById(R.id.insta_login_button_text).setEnabled(false);
            th.findViewById(R.id.insta_login_button_image).setEnabled(false);
        }
    }

    public void setCallback(Callback<InstagramSession> callback) {
        this.callback = callback;
    }

    private boolean isConnected() {
        NetworkInfo networkInfo = ((ConnectivityManager) c.getSystemService(c.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        Toast.makeText(c, "No internet connection at this time.", Toast.LENGTH_LONG).show();
        return false;
    }

    public class FetchToken extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(TOKENURL);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=" + callbackUrl +
                        "&code=" + response_code);
                outputStreamWriter.flush();
                InputStream responseStream = httpsURLConnection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(responseStream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr = "";
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                //parse json response
                String response = responseStrBuilder.toString();
                Log.i("FetchToken", "AccessToken Response: "+response);
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                String accessTokenString = jsonObject.getString("access_token"); //Here is your ACCESS TOKEN
                JSONObject user = jsonObject.getJSONObject("user");
                String username = user.getString("username");
                String bio = user.getString("bio");
                String website = user.getString("website");
                String fullname = user.getString("full_name");
                String id = user.getString("id");

                //save access token to instagram session
                InstagramSession session = InstagramSession.getInstance(c);
                session.initSession(new String[]{accessTokenString, username, bio, website, fullname, id});

            } catch (Exception e) {
                Log.i("FetchToken", "Error: "+e.toString());
                exception = e;
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                setButtonEnabled(true);
                TextView t = (TextView)th.findViewById(R.id.insta_login_button_text);
                t.setText("Log out of Instagram");
                //fire the success callback
                callback.success(InstagramSession.getInstance(c));
            } else {
                //fire the failure callback
                callback.failure(exception);
            }
        }
    }
}