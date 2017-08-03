package hecheng.com.handwriting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

import hecheng.com.handwriting.Logic.HANWANG;
import hecheng.com.handwriting.View.PaintView;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView = null;
    private List<String> handWritingList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClear = (Button) findViewById(R.id.btnClear);
        Button btnRec = (Button) findViewById(R.id.btnOk);
        paintView = (PaintView) findViewById(R.id.view_paint);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.clear();
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

    class RecongnizeThread implements Runnable {
        @Override
        public void run() {
            String jsonString = JSON.toJSONString(handWritingList);
            Log.d("hand", jsonString);
            JSONArray handWritingPositionsJ = JSONArray.parseArray(jsonString);
            HANWANG hanwang = new HANWANG();
            List<String> recResult = hanwang.handWritingRecognize(handWritingPositionsJ);
            Log.d("hand", JSON.toJSONString(recResult));
        }
    }
}
