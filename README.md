# Instagram-Sdk
Third party Instagram Sdk which provides a simple login button like facebook or twitter, and Handles the oauth2 flow for users to login and authenticate your app.  The access token is stored in an Instagram session which persists upon closing the app.

### Adding the Sdk to your project
Add the library as a dependency to your project. When using Android Studio and Gradle it's as easy as adding this dependency:

        compile 'com.mrunia.instagram.sdk:app:1.0.0'

### Using the Sdk
Add the button to your xml layout file...

        <com.mrunia.instagram.sdk.InstagramLoginButton
          android:id="@+id/instagram_login_button"
          android:layout_width="wrap_content"
          android:layout_height="60dp"
          android:layout_gravity="center_horizontal"
          android:layout_marginTop="30dp"
        />
        
Add your ClientID, ClientSecret, and CallbackUrl Globals...

        String INSTA_CLIENT_ID = "your client id";
        String INSTA_CLIENT_SECRET = "your client secret";
        String INSTA_CALLBACK_URL = "your callback url - http://example.com";
        InstagramAuthConfig instaAuthConfig = new InstagramAuthConfig(INSTA_CLIENT_ID, INSTA_CLIENT_SECRET, INSTA_CALLBACK_URL);
        Instagram.getInstance().with(this, instaAuthConfig);
        
Grab the button from your layout and set the callback...

        instagramLogin = (InstagramLoginButton) findViewById(R.id.instagram_login_button);
        instagramLogin.setCallback(new com.mrunia.instagram.sdk.Callback<InstagramSession>() {
            @Override
            public void success(InstagramSession result) {
                // optional: put your own code here
                Log.i(TAG, "Instagram Logged in..."+result.toString());
            }

            @Override
            public void failure(Exception e) {
                // optional: put your own code here
                Log.i(TAG, "Instagram NOT Logged in...");
            }

            @Override
            public void logout() {
                // optional: put your own code here
                Log.i(TAG, "Instagram User was logged out...");
            }
        });
        
You can now use the Instagram AccessToken throughout your app to make Instagram Api calls...

        InstagramSession session = InstagramSession.getInstance(context);
        if(session.isSessionActive) {
            String accessToken = session.getAccessToken();
            String userName = session.getUsername();
            String userId = session.getId();
            ...
            
            // or get it all back using:
            HashMap sessionData = session.getSessionDetails();
            // then parse sessionData for your session data.
            
        }
        
### ScreenShots 

<img width="350px" height="600px" src="https://cloud.githubusercontent.com/assets/6709518/6282314/e633723e-b887-11e4-8e27-aede6aa00466.png"/>
<img width="350px" height="600px" src="https://cloud.githubusercontent.com/assets/6709518/6282312/e0d29d92-b887-11e4-8d9b-2db01f11ce1b.png"/>
<img width="350px" height="600px" src="https://cloud.githubusercontent.com/assets/6709518/6282313/e4263288-b887-11e4-898a-3d5c9e442f4b.png"/>
<img width="350px" height="600px" src="https://cloud.githubusercontent.com/assets/6709518/6282310/dc960714-b887-11e4-95d4-99562842a0dd.png"/>
