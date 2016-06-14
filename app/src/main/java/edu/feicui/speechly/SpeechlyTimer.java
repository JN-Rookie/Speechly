package edu.feicui.speechly;

import android.os.Handler;
import android.util.Log;

/**
 * Created by Administrator on 2016/6/14.
 */
public abstract class SpeechlyTimer implements Runnable{
    private static final String TAG = "MainActivity";
    private long timeRemaining;
    private Handler mHandler;
    private boolean isKilled=false;//判断是否执行

    public SpeechlyTimer(long timeRemaining, Handler handler) {
        this.timeRemaining = timeRemaining;
        mHandler = handler;
    }

    public SpeechlyTimer(Handler handler) {
        mHandler = handler;
        timeRemaining=0;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public static boolean isValidInput(String timeInput){
        if(timeInput==null||timeInput.isEmpty()){//判断是否为空
            return false;
        }
        String trimmedInput=timeInput.trim();
        if(trimmedInput.length()==5&&trimmedInput.indexOf(':')==2){//判断输入时间格式是否正确
            try {
                int totalDuration=extractTotalDuration(trimmedInput);//转换时间的格式
                return totalDuration>30? true:false;//设定输入时间至少为30S
            }catch (NumberFormatException e){
                return false;
            }
        }else{
            return false;
        }
    }

    public static long converToMilliseconds(String timeInput){
        try {
            long milliseconds=extractTotalDuration(timeInput)*1000;
            return milliseconds;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int extractMinutes(String timeInput)throws NumberFormatException{//把输入的前两位数字取出
        int minutes = Integer.parseInt(timeInput.substring(0, 2));
        return minutes;
    }

    public static int extractSeconds(String timeInput)throws NumberFormatException{//把输入的后两位数字取出
        int seconds = Integer.parseInt(timeInput.substring(3, timeInput.length()));
        return seconds;
    }

    public static int extractTotalDuration(String timeInput)throws NumberFormatException{
        int totalDuration = extractMinutes(timeInput)*60+extractSeconds(timeInput);
        return totalDuration;
    }

    public static String convertToString(long timeInput){//输出时间格式
        int totalSecond= (int) (timeInput/1000);
        int minutes=totalSecond / 60;
        int seconds=totalSecond % 60;
        String minutesString=(minutes<10)?"0"+minutes:minutes+"";
        String secondsString=(seconds<10)?"0"+seconds:seconds+"";
        return minutesString+":"+secondsString;
    }

    public void start(){
        isKilled=false;
        mHandler.postDelayed(this,1000);
    }

    public void stop(){
        isKilled=true;
        onTimerStopped();
    }

    @Override
    public void run() {
        if(!isKilled){
            Log.d(TAG, "run: "+timeRemaining/1000);
            upDateUI(timeRemaining);
            if(timeRemaining==30000){
                onPalyNotification();
            }
            timeRemaining=timeRemaining-1000;
            if(timeRemaining>=0){
                mHandler.postDelayed(this,1000);
            }else{
                onTimerFinished();
            }
        }

    }

    public abstract void onTimerStopped();

    public abstract void onPalyNotification();

    public abstract void onTimerFinished();

    public abstract void upDateUI(long timeRemaining);
}
