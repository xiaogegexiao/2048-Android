package com.xiao2048.app.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiao2048.app.R;
import com.xiao2048.app.listeners.On2048GestureListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * TODO: document your custom view class.
 */
public class View2048 extends FrameLayout implements On2048GestureListener {

    public enum DIRECTION {LEFT, UP, RIGHT, DOWN, NONE};
    public static final int MINUMUN_STROKE_MOVE = 100;
    public static final int ROW_COUNT = 4;
    public static final int COLUMN_COUNT = 4;

    private Context mContext;
    private On2048GestureListener mGestureListener;
    private float mOldX, mOldY;
    private ViewData[][] arrays2048 = new ViewData[ROW_COUNT][COLUMN_COUNT];
    private boolean hasChanged2048 = false;

    public View2048(Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public View2048(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public View2048(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs, defStyle);
    }

    public void setmGestureListener(On2048GestureListener mGestureListener) {
        this.mGestureListener = mGestureListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int width = r - l;
        int height = b - t;

        int sidesize = Math.min(width, height) / 4;
        int index = 0;
        for (int i = 0; i < COLUMN_COUNT; i++) {
            for (int j =0; j< ROW_COUNT;j++) {
                ViewData vd = arrays2048[j][i];
                if (vd == null) continue;
                vd.view.setGravity(Gravity.CENTER);
                LayoutParams params = (LayoutParams)vd.view.getLayoutParams();
                params.width = params.height = sidesize;
                vd.view.layout(i * sidesize, j * sidesize, (i + 1) * sidesize, (j + 1) * sidesize);
                if (index % 2 == 0) {
                    vd.view.setBackgroundResource(R.color.yellow);
                } else {
                    vd.view.setBackgroundResource(R.color.lime);
                }
                index ++;
            }
        }
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.View2048, defStyle, 0);

//        LayoutInflater.from(getContext()).inflate(R.layout.view2048, this, true);
        initViewData();
        ButterKnife.inject(this, this);
        setmGestureListener(this);
    }

    private void initViewData() {
        removeAllViews();
        for (int j = 0; j < ROW_COUNT; j++) {
            for (int i = 0; i < COLUMN_COUNT; i++) {
                TextView tv = new TextView(mContext);
                this.addView(tv);
                arrays2048[j][i] = new ViewData(tv, 0);
            }
        }
        int count = 0;
        while (count < 2) {
            int rowId = Math.round((float)(Math.random()) * (ROW_COUNT - 1));
            int columnId = Math.round((float)(Math.random()) * (COLUMN_COUNT - 1));
            if (arrays2048[rowId][columnId].num != 0) {
                continue;
            } else {
                count ++;
                arrays2048[rowId][columnId].setNum(2);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mOldX = event.getX();
                mOldY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();
                float currentY = event.getY();
                switch (getDirection(currentX - mOldX, currentY - mOldY)) {
                    case LEFT:
                        if (mGestureListener != null) {
                            mGestureListener.onLeftStroke();
                        }
                        break;
                    case UP:
                        if (mGestureListener != null) {
                            mGestureListener.onUpStroke();
                        }
                        break;
                    case RIGHT:
                        if (mGestureListener != null) {
                            mGestureListener.onRightStroke();
                        }
                        break;
                    case DOWN:
                        if (mGestureListener != null) {
                            mGestureListener.onDownStroke();
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private DIRECTION getDirection(float xDistance, float yDistance) {
        if (Math.pow(xDistance, 2) + Math.pow(yDistance, 2) < Math.pow(MINUMUN_STROKE_MOVE, 2)) {
            return DIRECTION.NONE;
        }
        if (xDistance > 0) {
            if (yDistance > 0) {
                if (Math.abs(xDistance) > Math.abs(yDistance)) {
                    return DIRECTION.RIGHT;
                } else {
                    return DIRECTION.DOWN;
                }
            } else {
                if (Math.abs(xDistance) > Math.abs(yDistance)) {
                    return DIRECTION.RIGHT;
                } else {
                    return DIRECTION.UP;
                }
            }
        } else {
            if (yDistance > 0) {
                if (Math.abs(xDistance) > Math.abs(yDistance)) {
                    return DIRECTION.LEFT;
                } else {
                    return DIRECTION.DOWN;
                }
            } else {
                if (Math.abs(xDistance) > Math.abs(yDistance)) {
                    return DIRECTION.LEFT;
                } else {
                    return DIRECTION.UP;
                }
            }
        }
    }

    @Override
    public void onRightStroke() {
        hasChanged2048 = false;
        moveRight();
        if (hasChanged2048)
            generateExtra();
    }

    @Override
    public void onDownStroke() {
        hasChanged2048 = false;
        moveDown();
        if (hasChanged2048)
            generateExtra();
    }

    @Override
    public void onLeftStroke() {
        hasChanged2048 = false;
        moveLeft();
        if (hasChanged2048)
            generateExtra();
    }

    @Override
    public void onUpStroke() {
        hasChanged2048 = false;
        moveUp();
        if (hasChanged2048)
            generateExtra();
    }

    private void generateExtra(){
        int count = 0;
        while (count < 1) {
            int rowId = Math.round((float)(Math.random()) * (ROW_COUNT - 1));
            int columnId = Math.round((float)(Math.random()) * (COLUMN_COUNT - 1));
            if (arrays2048[rowId][columnId].num != 0) {
                continue;
            } else {
                count ++;
                arrays2048[rowId][columnId].setNum(Math.random() > 0.2 ? 2 : 4);
            }
        }
    }

    private void moveLeft() {
        List<Integer> list = new ArrayList<Integer>();
        for (ViewData[] row : arrays2048) {
            int former = 0;
            list.clear();
            for (int i = 0; i < row.length; i++) {
                if (row[i].num == 0) {
                } else {
                    if (former == 0) {
                        former = row[i].num;
                    } else {
                        if (row[i].num == former) {
                            list.add(former * 2);
                            former = 0;
                        } else {
                            list.add(former);
                            former = row[i].num;
                        }
                    }
                }
            }
            if (former != 0) {
                list.add(former);
            }
            for (int i = 0; i < row.length; i++) {
                if (i >= list.size()) {
                    row[i].setNum(0);
                } else {
                    row[i].setNum(list.get(i));
                }
            }
        }
    }

    private void moveRight() {
        List<Integer> list = new ArrayList<Integer>();
        for (ViewData[] row : arrays2048) {
            int former = 0;
            list.clear();
            for (int i = row.length - 1; i >= 0; i--) {
                if (row[i].num == 0) {
                } else {
                    if (former == 0) {
                        former = row[i].num;
                    } else {
                        if (row[i].num == former) {
                            list.add(former * 2);
                            former = 0;
                        } else {
                            list.add(former);
                            former = row[i].num;
                        }
                    }
                }
            }
            if (former != 0) {
                list.add(former);
            }
            for (int i = row.length - 1; i >= 0; i--) {
                if ((row.length - 1 - i) >= list.size()) {
                    row[i].setNum(0);
                } else {
                    row[i].setNum(list.get(row.length - 1 - i));
                }
            }
        }
    }

    private void moveUp() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < COLUMN_COUNT; i++) {
            int former = 0;
            list.clear();
            for (int j = 0; j < arrays2048.length; j++) {
                if (arrays2048[j][i].num == 0) {
                } else {
                    if (former == 0) {
                        former = arrays2048[j][i].num;
                    } else {
                        if (arrays2048[j][i].num == former) {
                            list.add(former * 2);
                            former = 0;
                        } else {
                            list.add(former);
                            former = arrays2048[j][i].num;
                        }
                    }
                }
            }
            if (former != 0) {
                list.add(former);
            }
            for (int j = 0; j < arrays2048.length; j++) {
                if (j >= list.size()) {
                    arrays2048[j][i].setNum(0);
                } else {
                    arrays2048[j][i].setNum(list.get(j));
                }
            }
        }
    }

    private void moveDown() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < COLUMN_COUNT; i++) {
            int former = 0;
            list.clear();
            for (int j = arrays2048.length - 1; j >= 0; j--) {
                if (arrays2048[j][i].num == 0) {
                } else {
                    if (former == 0) {
                        former = arrays2048[j][i].num;
                    } else {
                        if (arrays2048[j][i].num == former) {
                            list.add(former * 2);
                            former = 0;
                        } else {
                            list.add(former);
                            former = arrays2048[j][i].num;
                        }
                    }
                }
            }
            if (former != 0) {
                list.add(former);
            }
            for (int j = arrays2048.length - 1; j >= 0; j--) {
                if ((arrays2048.length - 1 - j) >= list.size()) {
                    arrays2048[j][i].setNum(0);
                } else {
                    arrays2048[j][i].setNum(list.get(arrays2048.length - 1 - j));
                }
            }
        }
    }

    public class ViewData {
        TextView view;
        Integer num;

        public void setView(TextView view) {
            this.view = view;
        }

        public void setNum(Integer num) {
            if (this.num == num)
                return;
            hasChanged2048 = true;
            this.num = num;
            if (num == 0) {
                this.view.setText("");
            } else {
                this.view.setText(String.valueOf(num));
            }
        }

        public ViewData(TextView view, Integer num) {
            this.view = view;
            this.num = num;
        }
    }
}
