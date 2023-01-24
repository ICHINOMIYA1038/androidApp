package shop.ichinomiya221156.wings.android.miniumquiclstart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContentsActivity extends AppCompatActivity {
    GoogleAccountCredential credential;
    NetHttpTransport HTTP_TRANSPORT;
    Button startBtn,signoutbtn;
    private RecyclerView itemRV;
    ArrayList<FileModel> fileArrayList;
    FileRVAdapter fileRVAdapter;
    TextView tvname,tvemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);
        tvemail=findViewById(R.id.tvemail);
        tvname=findViewById(R.id.tvname);
        itemRV = findViewById(R.id.idRVfilelist);
        fileArrayList = new ArrayList<FileModel>();
        fileRVAdapter = new FileRVAdapter(this, fileArrayList);
        itemRV.setLayoutManager(new LinearLayoutManager(this));
        itemRV.setAdapter(fileRVAdapter);
        startBtn = findViewById(R.id.STARTBtn);
        signoutbtn = findViewById(R.id.signoutBtn);
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            List<String> scopes = new ArrayList<String>(Arrays.asList("https://www.googleapis.com/auth/spreadsheets", "https://www.googleapis.com/auth/drive"));
            credential = GoogleAccountCredential.usingOAuth2(this, scopes);
            credential.setSelectedAccount(acct.getAccount());
            tvname.setText(acct.getDisplayName());
            tvname.setText(acct.getEmail());

        }
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runAPI();
            }
        });
        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int RESULT_ACTIVITY = 1001;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("signout","signout");
                startActivityForResult(intent, RESULT_ACTIVITY);
                finish();
            }
        });

        fileRVAdapter.notifyDataSetChanged();
    }

    void runAPI() {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);
        PurseJson purseJson = new PurseJson(handler);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(purseJson);
    }

    public class PurseJson implements Runnable {
        private final Handler _handler;

        public PurseJson(Handler _handler) {
            this._handler = _handler;
        }

        @WorkerThread
        @Override
        public void run() {

            Sheets target = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential)
                    // アプリケーション名を指定するのだが、適当でいいっぽい
                    .setApplicationName("Test Project2")
                    .build();


            Drive drive = new Drive.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential).setApplicationName("Drive API").build();
            try {
                String pageToken = null;
                FileList filelist = drive.files().list().setSpaces("drive").setQ("mimeType = 'application/vnd.google-apps.spreadsheet'").setPageSize(1000).setFields("nextPageToken, files(id, name, modifiedTime)").setPageToken(pageToken).execute();
                List<File> fileArray = filelist.getFiles();
                for (int i = 0; i < fileArray.size(); i++) {
                    fileArrayList.add(new FileModel(fileArray.get(i).getName(), fileArray.get(i).getId(), false));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            PurseJsonPostExecutor postExecutor = new PurseJsonPostExecutor();
            _handler.post(postExecutor);
        }

        }

        public class PurseJsonPostExecutor implements Runnable {

            public PurseJsonPostExecutor() {
            }

            @Override
            public void run() {
                fileRVAdapter.notifyDataSetChanged();
            }
        }
    }

