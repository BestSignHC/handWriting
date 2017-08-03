package hecheng.com.handwriting.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengH on 2017/8/2.
 */

public class PaintView extends View{
    // 画笔，定义绘制属性
    private Paint myPaint;
    private Paint mBitmapPaint;

    // 绘制路径
    private Path myPath;

    // 画布及其底层位图
    private Bitmap myBitmap;
    private Canvas myCanvas;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    // 记录宽度和高度
    private int mWidth;
    private int mHeight;

    private boolean isTakeSample = false;

    List<String> handWritingList = null;
    StringBuilder builder = null;

    Thread timerThread = null;

    public List<String> getHandWritingList() {
        return handWritingList;
    }

    public PaintView(Context context)
    {
        super(context);
        initialize();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    public PaintView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    /**
     * 初始化工作
     */
    private void initialize()
    {
        // 绘制自由曲线用的画笔
        myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(12);

        myPath = new Path();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        builder = new StringBuilder();
        handWritingList = new ArrayList<>();
        timerThread = new Thread(new TimerThead());
        timerThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // 背景颜色
        // canvas.drawColor(getResources().getColor(R.color.blue_dark));

        // 如果不调用这个方法，绘制结束后画布将清空
        canvas.drawBitmap(myBitmap, 0, 0, mBitmapPaint);

        // 绘制路径
        canvas.drawPath(myPath, myPaint);

    }

    private void touch_start(float x, float y)
    {
        myPath.reset();
        myPath.moveTo(x, y);
        mX = x;
        mY = y;
        isTakeSample = true;
    }

    private void touch_move(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            myPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up()
    {
        myPath.lineTo(mX, mY);
        // commit the path to our offscreen
        // 如果少了这一句，笔触抬起时myPath重置，那么绘制的线将消失
        myCanvas.drawPath(myPath, myPaint);
        // kill this so we don't double draw
        myPath.reset();
        isTakeSample = false;
        String handWritingPosition = builder.toString().substring(0, builder.toString().length() -2);
        handWritingList.add(handWritingPosition);
        Log.d("hand", handWritingPosition);
        builder = new StringBuilder();
    }

    /**
     * 清除整个图像
     */
    public void clear()
    {
        isTakeSample = false;
        // 清除方法1：重新生成位图
         myBitmap = Bitmap
         .createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
         myCanvas = new Canvas(myBitmap);

        myPath.reset();
        // 刷新绘制
        invalidate();
        handWritingList.clear();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int x = (int) mX;
                int y = (int) mY;
                if (isTakeSample) {
                    builder.append(x + ", " + y + " ,");
                }
            }
        }
    };

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
