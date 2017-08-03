package hecheng.com.handwriting;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hecheng.com.handwriting.View.PaintView;

public class MainActivity extends AppCompatActivity {

    final PaintView paintView = (PaintView) findViewById(R.id.view_paint);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClear = (Button) findViewById(R.id.btnClear);
        new Thread(new TimerThead()).start();

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
                System.out.println(x + ", " + y + " ,");
            }
        }
    };

    class TimerThead  implements Runnable{

        @Override
        public void run() {
            while (paintView.isTakeSample()) {
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
