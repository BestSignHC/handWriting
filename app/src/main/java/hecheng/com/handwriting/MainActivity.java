package hecheng.com.handwriting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

import hecheng.com.handwriting.Logic.HANWANG;
import hecheng.com.handwriting.View.PaintView;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView = null;
    private List<String> handWritingList = null;
    private List<String> recResult = null;
    private TextView tvRes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClear = (Button) findViewById(R.id.btnClear);
        Button btnRec = (Button) findViewById(R.id.btnOk);
        paintView = (PaintView) findViewById(R.id.view_paint);
        tvRes = (TextView) findViewById(R.id.tv_res);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clear();
                tvRes.setText("");
            }
        });

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handWritingList = paintView.getHandWritingList();
                new Thread(new RecongnizeThread()).start();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                String result = "";
                if (null == recResult) {
                    result = "识别失败";
                }
                else {
                    result = JSON.toJSONString(recResult);
                }
                tvRes.setText(result.substring(1, result.length() - 1));
            }
        }
    };

    class RecongnizeThread implements Runnable {
        @Override
        public void run() {
            String jsonString = JSON.toJSONString(handWritingList);
            Log.d("hand", "jsonString:" + jsonString);
            JSONArray handWritingPositionsJ = JSONArray.parseArray(jsonString);
            HANWANG hanwang = new HANWANG();
            recResult = hanwang.handWritingRecognize(handWritingPositionsJ);
            Log.d("hand", JSON.toJSONString(recResult));

            Message msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);
        }
    }
}
