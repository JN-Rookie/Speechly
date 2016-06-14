package edu.feicui.speechly;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener ,DialogInterface.OnClickListener{
    private static final String TAG = "MainActivity";
    private TextView mTextTime;//屏幕显示时间
    private ToggleButton mButton;
    private EditText mTextInput;//输入框
    private Handler mHandler;
    private SpeechlyTimer mTimer;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextTime = (TextView) findViewById(R.id.textview);
        mButton = (ToggleButton) findViewById(R.id.toggleButton);
        mButton.setOnCheckedChangeListener(this);
        AssetManager assetManager=getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "fronts/plantc.ttf");//获取字体格式
        mTextTime.setTypeface(typeface);//设置字体格式
        mHandler=new Handler();
        mTimer = new SpeechlyTimer(mHandler) {

            @Override
            public void onTimerStopped() {//点击关闭时执行
                mTextTime.setText("00:00");
            }

            @Override
            public void onPalyNotification() {//时间倒数到30时开始执行
                playSound();
            }

            @Override
            public void onTimerFinished() {//时间结束时执行
                mButton.setChecked(false);
            }

            @Override
            public void upDateUI(long timeRemaining) {//设置显示时间的格式
                mTextTime.setText(SpeechlyTimer.convertToString(timeRemaining));
            }
        };
    }

//  播放音乐的放法
    private void playSound() {
        try {
            AssetFileDescriptor openFd = getAssets().openFd("music/Numb.mp3");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(openFd.getFileDescriptor());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    togglebutton点击是执行的放发
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            LayoutInflater inflater=LayoutInflater.from(this);
            View view=inflater.inflate(R.layout.user_input,null);
            mTextInput = (EditText) view.findViewById(R.id.edt_input);
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("请输入时间");
            builder.setView(view);
            builder.setCancelable(false);
            builder.setPositiveButton("确定",this);
            builder.setNegativeButton("取消",this);
            builder.show();
        }else{
            mTimer.stop();
            mMediaPlayer.stop();
        }
    }

//    设置弹窗效果
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                String input = mTextInput.getText().toString();
                if(SpeechlyTimer.isValidInput(input)){
                    mTimer.setTimeRemaining(SpeechlyTimer.converToMilliseconds(input));
                    mTimer.start();
                }else{
                    mButton.setChecked(false);
                    Toast.makeText(this,"输入格式不正确",Toast.LENGTH_SHORT).show();
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mButton.setChecked(false);
                break;
        }
    }
}
