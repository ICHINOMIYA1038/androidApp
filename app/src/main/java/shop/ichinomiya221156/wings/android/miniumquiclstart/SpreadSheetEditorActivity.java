package shop.ichinomiya221156.wings.android.miniumquiclstart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpreadSheetEditorActivity extends AppCompatActivity {
    EditText et1,et2,et3;
    TextView tv1,tv2,tv3,tvDate;
    Button bt1,bt2,bt3,btSubmit,btSignOut;
    String id ,fileName;
    GoogleAccountCredential credential;
    NetHttpTransport HTTP_TRANSPORT;
    CalendarView calenderview;
    List<List<Object>> returnValue;
    int year,month,day;
    int maxIndex=100;
    Calendar calender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spread_sheet_editor);

        /**
         * 遷移もとからのデータを受け取る。
         */

        id = getIntent().getStringExtra("id");
        fileName = getIntent().getStringExtra("name");


        /**
         * レイアウト画面からviewを紐付ける
         */
        tvDate=findViewById(R.id.iddate);
        calenderview = findViewById(R.id.idcalender);
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        btSubmit = findViewById(R.id.btSubmit);
        btSignOut = findViewById(R.id.btSignout);

        /**
         * カレンダーのviewから日付を取得してカレンダーのオブジェクトに日付を入れる。
         */
        calender = Calendar.getInstance();
        calender.setTimeInMillis(calenderview.getDate());

        /**
         * カレンダーの日付を取得し、フィールドに情報を保存
         */
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH)+1;
        day = calender.get(Calendar.DATE);

        /**
         * HTTP通信を確立
         */
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        /**
         * サインイン中のアカウントを取得。
         * もし、サインインしている状態なら、クレデンシャルに情報を格納
         */
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            List<String> scopes = new ArrayList<String>(Arrays.asList("https://www.googleapis.com/auth/spreadsheets", "https://www.googleapis.com/auth/drive"));

            credential = GoogleAccountCredential.usingOAuth2(this, scopes);
            credential.setSelectedAccount(acct.getAccount());
        }

        /**
         * ボタンにクリックした時の処理を追加。
         */

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et1.getEditableText().clear();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et2.getEditableText().clear();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et3.getEditableText().clear();
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                String input1 = et1.getText().toString();
                String input2 = et2.getText().toString();
                String input3 = et3.getText().toString();
                maxIndex = maxIndex + 1;
                if(!input1.equals("")||!input2.equals("")||!input3.equals("")) {
                    writeSpreadAPI("A"+maxIndex,"" + year + "/" + month + "/" + day);
                }
                if(!input1.equals("")) {
                    writeSpreadAPI("B"+maxIndex, input1);
                }
                if(!input2.equals("")){
                    writeSpreadAPI ("C"+maxIndex,input2);
                }
                if(!input3.equals("")){
                    writeSpreadAPI ("D"+maxIndex,input3);
                }
                //writeSpreadAPI("A2:A100","");
            }

        });

        btSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final int RESULT_ACTIVITY = 1001;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("signout","signout");
                startActivityForResult(intent, RESULT_ACTIVITY);
                finish();
            }

        });


        /**
         * スプレッドシートの情報を読み取り。
         * returnValueのフィールドに値を格納
         */
        readSpreadAPI();

        /**
         * カレンダーを操作した時の処理。
         */
        calenderview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int y, int m, int dayOfMonth) {
                String date = y+"/"+(m+1)+"/"+dayOfMonth;
                tvDate.setText(date);
                calender.setTimeInMillis(calenderview.getDate());
                year = y;
                month = m+1;
                day = dayOfMonth;
            }
        });
    }


    /**
     * セーブデータをローカルに保存する処理
     * 今回は未実装でかつ未使用
     */
    protected void saveText(){
        String message = "msg";
        String fileName = "test2.csv";
        String inputText = "あいうえお";

        try {
            FileOutputStream outStream = openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(outStream,"UTF-8");
            writer.write(inputText);
            writer.write(",");
            writer.write("かきくけこ");
            writer.write(",");
            writer.write("さしすせそ");
            writer.write("\n");
            writer.write("たちつてと");
            writer.write(",");
            writer.write("なにぬねの");
            writer.write(",");
            writer.write("はひふへほ");
            writer.close();

            message = "File saved.";
        } catch (FileNotFoundException e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            message = e.getMessage();
            e.printStackTrace();
        }
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * スプレッドシートを読み取る非同期処理を行うメソッド
     */
    void readSpreadAPI() {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);
        ReadSpread readSpread = new ReadSpread(handler,id);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(readSpread);
    }


    /**
     * スプレッドシートに書き込む非同期処理を行うメソッド
     */
    void writeSpreadAPI(String range,String value) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);
        WriteSpread writeSpread = new WriteSpread(handler,id,range,value);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(writeSpread);
    }


    public class ReadSpread implements Runnable {
         String spreadsheetId = "";
        private final Handler _handler;

        public ReadSpread(Handler _handler, String spreadsheetId) {
            this._handler = _handler;
            this.spreadsheetId = spreadsheetId;
        }

        @WorkerThread
        @Override
        public void run() {
            Sheets target = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential)
                    // アプリケーション名を指定するのだが、適当でいいっぽい
                    .setApplicationName("Test Project2")
                    .build();
            //スプレッドシートへの読み込み
            ValueRange response = null;
            try {
                response = target.spreadsheets().values().get(spreadsheetId, "A1:D"+maxIndex).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<List<Object>> value = response.getValues();
            ReadSpreadPostExecutor postExecutor = new ReadSpreadPostExecutor(value);
            _handler.post(postExecutor);
        }

    }

    public class ReadSpreadPostExecutor implements Runnable {

        List<List<Object>> value;
        public ReadSpreadPostExecutor(List<List<Object>> value) {
            this.value=value;
        }

        @Override
        public void run() {
            returnValue = value;
            maxIndex=returnValue.size();
            tv1.setText(String.valueOf(value.get(0).get(1)));
            tv2.setText(String.valueOf(value.get(0).get(2)));
            tv3.setText(String.valueOf(value.get(0).get(3)));
            et1.setText(String.valueOf(value.get(1).get(1)));
            et2.setText(String.valueOf(value.get(1).get(2)));
            et3.setText(String.valueOf(value.get(1).get(3)));
        }
    }


    public class WriteSpread implements Runnable {
        String spreadsheetId = "";
        private final Handler _handler;
        String range;
        String value;

        public WriteSpread(Handler _handler, String spreadsheetId,String range , String value) {
            this._handler = _handler;
            this.spreadsheetId = spreadsheetId;
            this.range=range;
            this.value = value;
        }

        @WorkerThread
        @Override
        public void run() {

            Sheets target = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credential)
                    // アプリケーション名を指定するのだが、適当でいいっぽい
                    .setApplicationName("TestProject2")
                    .build();

            //スプレッドシートへの書き込み
            ValueRange mydata = new ValueRange();
            List<List<Object>> example1=Arrays.asList(Arrays.asList(value));
            List<List<Object>>dateList = new ArrayList<List<Object>>();
            /*
            for(int i=0;i<60;i++) {
                calender.add(Calendar.DATE,1);
                year = calender.get(Calendar.YEAR);
                month = calender.get(Calendar.MONTH)+1;
                day = calender.get(Calendar.DATE);
                dateList.add(Arrays.asList("" + year + "/" + month + "/" + day));
            }


            List<List<Object>> list = Arrays.asList(
                    Arrays.asList("","no"),
                    Arrays.asList("","yes")
            );
            */
            mydata.setValues(example1);
            try {
                Sheets.Spreadsheets.Values.Update sheetChangeInstance= target.spreadsheets().values().update(spreadsheetId,range,mydata);
                sheetChangeInstance.setValueInputOption("USER_ENTERED");
                sheetChangeInstance.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            WriteSpreadPostExecutor writepostExecutor = new WriteSpreadPostExecutor();
            _handler.post(writepostExecutor);
        }

    }

    public class WriteSpreadPostExecutor implements Runnable {

        public WriteSpreadPostExecutor() {

        }

        @Override
        public void run() {

        }
    }
}
