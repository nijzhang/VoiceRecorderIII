package com.example.l.immersiondemo;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;

public class PlayingList extends AppCompatActivity implements  View.OnClickListener{

    File file = new File(Environment.getExternalStorageDirectory()+"/TestMethod");

    List<VoiceRecordInfoDb> list;

    MediaPlayer mediaPlayer;

    PopupMenuView popupMenu;

    EditText rename_txt;

    String dest_name,ori_name;

    VoiceRecordInfoDb get_voice_record_class;

    SwipeRefreshLayout swipeRefreshLayout;

    private  int record_num = 0;

    private boolean thread_state_isinterrupt = true;

    MyBaseAdapter myBaseAdapter;

    private int remPosition = 0;


    Timer timer ;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {


                playing_music_start_txt.setText(returnCurentTime());

                playing_seekbar.setProgress(record_num);

            super.handleMessage(msg);

        }
    };

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

            if(!thread_state_isinterrupt &&  record_num < mediaPlayer.getDuration() / 1000) {
                record_num++;
                handler.obtainMessage().sendToTarget();
            }
        }
    };


    ListView listView;

    View playing_view;

    SeekBar playing_seekbar;

    ImageButton playing_image_btn;

    TextView playing_music_name_txt,playing_music_start_txt,playing_music_end_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_list);

       list = DataSupport.findAll(VoiceRecordInfoDb.class);

        Init();

         myBaseAdapter = new MyBaseAdapter(this,list);

        listView.setAdapter(myBaseAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //refresh listview

                //  arrayAdapter.notifyDataSetChanged();

               refreshListView();

                swipeRefreshLayout.setRefreshing(false);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                popupMenu.show(view);

                remPosition = position;
                popupMenu.setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
                    @Override
                    public boolean onOptionMenuClick(int position, OptionMenu menu) {
                        switch (position)
                        {
                            case 0:
                                //playing
                                playing_recorded_file()
                                ;break;
                            case 1:
                                //delete file
                                dele_record_file();
                               break;
                            case 2:

                                //realize share method here
                                Toast.makeText(PlayingList.this,"realize share method",Toast.LENGTH_SHORT).show();


                                break;
                            case 3:

                                rename_record_file();
                                //rename
                                break;
                        }
                        return true;
                    }
                });

            }
        });


    }

    private void refreshListView() {

        list = DataSupport.findAll(VoiceRecordInfoDb.class);

        myBaseAdapter = new MyBaseAdapter(PlayingList.this,list);

        listView.setAdapter(myBaseAdapter);
    }

    private void dele_record_file() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayingList.this);

        builder.setMessage("Make sure to del?");
        builder.setPositiveButton("sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File delfile = new File( file.getAbsolutePath()+list.get(remPosition).getFileName());

                delfile.delete();

                //delete the data inside db

                DataSupport.deleteAll(VoiceRecordInfoDb.class,"fileName = ?",list.get(remPosition).getFileName());

                refreshListView();

            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });

        builder.create().show();
    }



    private void playing_recorded_file() {

        get_voice_record_class = list.get(remPosition);

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayingList.this);

        playing_view =  View.inflate(PlayingList.this,R.layout.playing,null);

        InitPlayingView();

        InitMediaPlayer();


        builder.setView(playing_view);

        builder.create().show();

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    record_num = 0;
                }
            }
        });


    }


    private void rename_record_file() {

        AlertDialog.Builder  builder = new AlertDialog.Builder(this);

        View view = View.inflate(PlayingList.this,R.layout.input_filename,null);

        builder.setView(view);

        builder.setTitle("input your file name");

        rename_txt = view.findViewById(R.id.fileNameEdit);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input

                dest_name = rename_txt.getText().toString()+".mp3";

                ori_name = list.get(remPosition).getFileName();

                Toast.makeText(PlayingList.this,ori_name,Toast.LENGTH_SHORT).show();

                new MyFileOperator(PlayingList.this).changeFileName(ori_name,dest_name);

                //refresh DB

                 VoiceRecordInfoDb voiceRecordInfoDb = new VoiceRecordInfoDb();

                voiceRecordInfoDb.setFileName(dest_name);

                voiceRecordInfoDb.updateAll("fileName = ? ",ori_name);

                refreshListView();

            }
        });

        builder.create().show();
    }
    private void InitMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getAbsolutePath()+File.separator+get_voice_record_class.getFileName());
            mediaPlayer.prepare();
            playing_seekbar.setMax(mediaPlayer.getDuration()/1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitPlayingView() {


        playing_seekbar = playing_view.findViewById(R.id.playing_seekbar);

        playing_music_name_txt = playing_view.findViewById(R.id.playing_recordFile_name_tv);

        playing_music_start_txt = playing_view.findViewById(R.id.playing_start_time_tv);

        playing_music_end_txt = playing_view.findViewById(R.id.playing_end_time_tv);

        playing_image_btn = playing_view.findViewById(R.id.playing_imageBtn);

        playing_music_name_txt.setText(get_voice_record_class.getFileName());

        playing_music_end_txt.setText(get_voice_record_class.getRecordingTime());

        playing_image_btn.setOnClickListener(this);



    }

    private void Init() {

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        listView = findViewById(R.id.listview);

        popupMenu = new PopupMenuView(PlayingList.this);

        popupMenu.setMenuItems(Arrays.asList(
                new OptionMenu("playing"), new OptionMenu("delete"),
                new OptionMenu("share"), new OptionMenu("rename")));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.playing_imageBtn:
                StartPlaying();
                break;
                default:
        }
    }

    private void StartPlaying() {

        if(!mediaPlayer.isPlaying())
        {
            if(timer == null)
                timer = new Timer();

            mediaPlayer.start();

            playing_image_btn.setImageResource(R.drawable.pause);

            thread_state_isinterrupt = false;
            if(record_num == 0)
             timer.schedule(timerTask,1000,1000);

        }

        else {

            thread_state_isinterrupt = true;
            mediaPlayer.pause();
            playing_image_btn.setImageResource(R.drawable.start);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.reset();

            thread_state_isinterrupt = false;

        }
    }


    private String returnCurentTime() {
            //
        //record_num

        String H = ""+record_num / 3600;

        String M = ""+(record_num - Integer.parseInt(H) * 3600)/ 60;

        String S = ""+record_num % 60;

        if (Integer.parseInt(M) < 10 ) M = "0"+M;
        if (Integer.parseInt(S) < 10)  S = "0"+S;

        return ""+H+":"+M+":"+S;
    }

}
