package com.example.l.immersiondemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ibbhub.mp3recorderlib.Mp3Recorder;
import com.ibbhub.mp3recorderlib.SpectrumView;
import com.jiahuan.timelyanimation.NumberSwitchView;
import com.skyfishjy.library.RippleBackground;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton record_image_btn,change_page_image_btn;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean isRecording = false;

    RippleBackground rippleBackground;

    Random random;

    EditText DestMp3_edittext;

    String Mp3fileName,timeLength,dateInfo,DestMp3FileName;

    NumberSwitchView switchViewH1,switchViewH0,switchViewM1,switchViewM0,switchViewS1,switchViewS0;

    int numH1,numH0,numM1,numM0,numS1,numS0;

    Timer timer;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            IniNumber();
        }
    };

    File file = new File(Environment.getExternalStorageDirectory()+"/TestMethod");
   // File file2 = new File(Environment.getExternalStorageDirectory()+"");

    Mp3Recorder mp3Recorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initial();


    }

    private void Initial() {

        random = new Random();

        record_image_btn = findViewById(R.id.start);

        change_page_image_btn = findViewById(R.id.folder_btn);

        change_page_image_btn.getBackground().setAlpha(50);

        rippleBackground = findViewById(R.id.content);

        record_image_btn.setOnClickListener(this);

        change_page_image_btn.setOnClickListener(this);


        switchViewH1 = findViewById(R.id.numberswitchH1);

        switchViewH0 = findViewById(R.id.numberswitchH0);

        switchViewM1 = findViewById(R.id.numberswitchM1);

        switchViewM0 = findViewById(R.id.numberswitchM0);

        switchViewS1 = findViewById(R.id.numberswitchS1);

        switchViewS0 = findViewById(R.id.numberswitchS0);

        numH1 = numH0 = numM1 = numM0 = numS1 = numS0 = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

            int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Window window = getWindow();

                WindowManager.LayoutParams attributes = window.getAttributes();

                attributes.flags |= flagTranslucentNavigation;

                window.setAttributes(attributes);

                getWindow().setStatusBarColor(Color.TRANSPARENT);

            } else {

                Window window = getWindow();

                WindowManager.LayoutParams attributes = window.getAttributes();

                attributes.flags |= flagTranslucentStatus | flagTranslucentNavigation;

                window.setAttributes(attributes);

            }
        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},2);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},3);
        }

        if(!file.exists())
            file.mkdirs();

        /*String[] strings = file2.list();
        for (int i = 0 ,flag = strings.length;i < flag ; i ++)
        {
            Log.i("TestLog",strings[i]+""+i);
        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1)
        {
            if(grantResults.length  > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0])

            {
                //get permission
            }
            else {
                //permission deny
                new AlertDialog.Builder(this)
                        .setMessage("Please To Award the pemission to Write Storege")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        }

       else  if(requestCode == 2)
        {
            if(grantResults.length  > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0])

            {
                //get permission
            }
            else {
                //permission deny
                new AlertDialog.Builder(this)
                        .setMessage("Please To Award the pemission to Record")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        }

        else  if(requestCode == 3)
        {
            if(grantResults.length  > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0])

            {
                //get permission
            }
            else {
                //permission deny
                new AlertDialog.Builder(this)
                        .setMessage("Please To Award the pemission to read storage")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},3);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case  R.id.start:
                start_record();
                break;
            case R.id.folder_btn:
                changePage();
                break;
            default:
        }
    }

    private void changePage() {

        Intent intent  = new Intent();

        intent.setClass(this,PlayingList.class);

        startActivity(intent);



       /* VoiceRecordInfoDb voiceRecordInfoDb = new VoiceRecordInfoDb("hello.mp3","12:12","2018-12-12");
        voiceRecordInfoDb.save();*/

       // DataSupport.deleteAll(VoiceRecordInfoDb.class,"recordingTime like  ?","12:12");


       /* List<VoiceRecordInfoDb> list = DataSupport.findAll(VoiceRecordInfoDb.class);

        for(VoiceRecordInfoDb voiceRecordInfoDb : list)
        {
            Log.d("Date",voiceRecordInfoDb.getDateInfo());
            Log.d("time",voiceRecordInfoDb.getRecordingTime());
            Log.d("File",voiceRecordInfoDb.getFileName());
        }*/

    }


    private void start_record() {
      if(mp3Recorder == null && !isRecording) {
          mp3Recorder = new Mp3Recorder();

          Mp3fileName = random.nextInt()+".mp3";

          mp3Recorder.start(file.getPath() + File.separator + Mp3fileName);
          dateInfo = simpleDateFormat.format(new java.util.Date());
          isRecording = true;
          record_image_btn.setImageResource(R.drawable.radar);
          rippleBackground.startRippleAnimation();
          IniNumber();

          TimerTask timerTask = new TimerTask() {
              @Override
              public void run() {
                    numS0 ++;
                    predictNum();
                    handler.obtainMessage().sendToTarget();
              }
          };

          timer = new Timer();
            // delay : 1000 the first time to excute Run(), period : 1000 recycle excute
          timer.schedule(timerTask,1000,1000);
      }

      else
      {

          mp3Recorder.stop();
          mp3Recorder = null;
          isRecording = false;

          rippleBackground.stopRippleAnimation();

          DestMp3FileName = Mp3fileName;

          timeLength = ""+numH1+""+numH0+":"+numM1+""+numM0+":"+numS1+""+numS0;

          record_image_btn.setImageResource(R.drawable.start00);

          numH1 = numH0 = numM1 = numM0 = numS1 = numS0 = 0;

          timer.cancel();

          NamedRecordedFile();

          Connector.getDatabase();

          VoiceRecordInfoDb voiceRecordInfoDb = new VoiceRecordInfoDb(DestMp3FileName,timeLength,dateInfo);

          voiceRecordInfoDb.save();

          IniNumber();

      }
    }

    private void NamedRecordedFile() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);

        View view = View.inflate(this,R.layout.input_filename,null);

        builder.setView(view);

        builder.setTitle("input your file name");

        DestMp3_edittext = view.findViewById(R.id.fileNameEdit);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input

                DestMp3FileName = DestMp3_edittext.getText().toString()+".mp3";

                new MyFileOperator(MainActivity.this).changeFileName(Mp3fileName,DestMp3FileName);

                //refresh DB
                VoiceRecordInfoDb voiceRecordInfoDb = new VoiceRecordInfoDb();

                voiceRecordInfoDb.setFileName(DestMp3FileName);

                voiceRecordInfoDb.updateAll("dateInfo = ? and recordingTime = ?",dateInfo,timeLength);

            }
        });

        builder.create().show();
    }

    private void predictNum() {

        if(numS0 > 9)
        {
            numS0 = 0;

            numS1 ++;

        }

        if(numS1 > 5)
        {
            numS1 = 0;

            numM0 ++;

        }

        if(numM0 > 9)
        {
            numM0 = 0;

            numM1  ++;

        }

        if(numM1 > 5)
        {
            numH0 ++;

            if(numH0 > 9)
            {
                numH0 = 0;

                numH1 ++;
            }
        }

    }

    private void IniNumber() {

        switchViewS0.animateTo(numS0);
        switchViewS1.animateTo(numS1);

        switchViewM0.animateTo(numM0);
        switchViewM1.animateTo(numM1);

        switchViewH0.animateTo(numH0);
        switchViewH1.animateTo(numH1);
    }
}
