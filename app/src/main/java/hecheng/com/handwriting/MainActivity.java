package hecheng.com.handwriting;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import hecheng.com.handwriting.View.PaintView;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView = null;
    private List<String> handWritingList = null;
    private StringBuilder builder = null;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClear = (Button) findViewById(R.id.btnClear);
        paintView = (PaintView) findViewById(R.id.view_paint);
        handWritingList = new ArrayList<>();
        builder = new StringBuilder();

        new Thread(new TimerThead()).start();
        new Thread(new TakeThread()).start();

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clear();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int x = (int) paintView.getmX();
                int y = (int) paintView.getmY();
                flag = paintView.isTakeSample();
                if (paintView.isTakeSample()) {
                    builder.append(x + ", " + y + " ,");
                }
            }
        }
    };

    class TakeThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (!flag && builder.toString() != null) {
                    handWritingList.add(builder.toString());
                    Log.d("hand", builder.toString());
                    builder = new StringBuilder();
                }
            }
        }
    }

    class TimerThead  implements Runnable{

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
