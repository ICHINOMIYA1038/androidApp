package shop.ichinomiya221156.wings.android.miniumquiclstart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * メインアクティビティクラス
 */
public class MainActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleBtn;
    GoogleAccountCredential credential;
    NetHttpTransport HTTP_TRANSPORT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope("https://www.googleapis.com/auth/spreadsheets"), new Scope("https://www.googleapis.com/auth/drive")).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            //navigateToSecondActivity();
        }

        if (getIntent().getStringExtra("signout") != null) {
            gsc.signOut();
        }

        //ボタンの設定
        googleBtn = findViewById(R.id.google_btn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn();
            }
        });
    }

    /**
     * signInメソッド
     * インテントを作成して、サインインクライアントに遷移して、戻ってくる。
     *
     * @return　void
     */
    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }


    /**
     * onActivityResultメソッド
     * サインインインテントから戻ってきた時の処理
     *
     * @return　void
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount account = task.getResult();
                //クレデンシャルに現在のログインしているアカウントの情報を送る。
                credential.setSelectedAccount(account.getAccount());
                nextActivity(credential);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ;

    /**
     * nextActivityメソッド
     *
     * @param credential 　アカウントの情報が入った認証情報。これを用いてグーグルのAPIを用いる。
     *                   このメソッドを実行すると、コンテンツアクティビティへ画面遷移をする。
     */
    void nextActivity(GoogleAccountCredential credential) {
        finish();
        Intent i = new Intent(MainActivity.this, ContentsActivity.class);
        startActivity(i);
    }


}