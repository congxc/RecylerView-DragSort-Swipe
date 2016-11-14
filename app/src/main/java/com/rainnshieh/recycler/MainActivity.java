package com.rainnshieh.recycler;

import android.app.Service;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rainnshieh.recycler.veiws.RecyclerItemLongClickListener;
import com.rainnshieh.recycler.veiws.SpacesItemDecoration;
import com.rainnshieh.recycler.veiws.SwipeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.rainnshieh.recycler.R.id.swipe;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch mSwitch;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter<Message> mBaseQuickAdapter;
    private List<Message> mDatas = new ArrayList<>();
    private ItemTouchHelper mHelper;
    private List<SwipeLayout> mOpendLayout = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwitch = (Switch) findViewById(R.id.swit);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        initData();
        mBaseQuickAdapter = new BaseQuickAdapter<Message>(this,R.layout.item_layout,mDatas) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, Message message) {
                baseViewHolder.setText(R.id.tv_username,message.username)
                        .setText(R.id.tv_message,message.message)
                        .setImageResource(R.id.iv_icon,message.img_id)
                        .setText(R.id.tv_time,message.time);
                SwipeLayout swipelayout = baseViewHolder.getView(swipe);
                swipelayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        closeAllOpenLayout();
                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {
                        mOpendLayout.add(layout);
                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {

                    }

                    @Override
                    public void onClose(SwipeLayout layout) {
                        mOpendLayout.remove(layout);
                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                    }
                });
            }
        };
        mRecyclerView.setAdapter(mBaseQuickAdapter);
        bindItemTouchHerlper();
        mSwitch.setOnCheckedChangeListener(this);
        updateRecyclerView(false);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemLongClickListener(this,mRecyclerView) {
            @Override
            public void onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
                if(position != mDatas.size() - 1){//如果不是最后一个 可以拖动
                    mHelper.startDrag(viewHolder);
                }
            }
        });
    }

    private void closeAllOpenLayout() {
        for (SwipeLayout swipeLayout : mOpendLayout) {
            if(swipeLayout.getOpenStatus() == SwipeLayout.Status.Open){
                swipeLayout.close();
            }
        }
        mOpendLayout.clear();
    }

    private void bindItemTouchHerlper() {
        //有上下左右
        //数据集交换位置
        //数据集交换位置
        //这个是UI展示滑动
        //长按的时候改变item背景色
        //震动70ms
        //松手的时候恢复背景色
        //这里可以自己定义是否可以拖拽
        mHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags;
                int swipeFlags = 0;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {//有上下左右
                    dragFlags = ItemTouchHelper.UP|
                                    ItemTouchHelper.DOWN|
                            ItemTouchHelper.LEFT|
                            ItemTouchHelper.RIGHT;
                }else{
                    dragFlags = ItemTouchHelper.UP|
                            ItemTouchHelper.DOWN;
//                    swipeFlags = ItemTouchHelper.START|
//                            ItemTouchHelper.END;
                }

                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if(toPosition == mDatas.size() -1){//如果拖到最后一个 只让其拖到倒数第一个这样就不会与最后一个交换了
                    toPosition --;
                }

                if(fromPosition < toPosition){
                    for(int i = fromPosition ; i < toPosition; i++){//数据集交换位置
                        Collections.swap(mDatas,i,i+1);
                    }
                }else{
                    for(int i = fromPosition ; i > toPosition; i--){//数据集交换位置
                        Collections.swap(mDatas,fromPosition,i-1);
                    }
                }
                mBaseQuickAdapter.notifyItemMoved(fromPosition,toPosition);//这个是UI展示滑动
                return false;
            }

            @Override//长按的时候改变item背景色
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    closeAllOpenLayout();
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                    Vibrator vib = (Vibrator)MainActivity.this.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(70);//震动70ms
                }

                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override//松手的时候恢复背景色
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                mBaseQuickAdapter.notifyDataSetChanged();//这里是因为托完以后有些布局滑出来一点点，更新一下就好了
            }

            @Override//这里可以自己定义是否可以拖拽 设置未false 并且为Recyclerview设置长按监听事件调用mHelper.startDrag(mViewHolder);
            public boolean isLongPressDragEnabled() {
//                return super.isLongPressDragEnabled();//默认为true
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int positon  = viewHolder.getAdapterPosition();
//                mBaseQuickAdapter.notifyItemRemoved(positon);
//                mDatas.remove(positon);
            }
        });
        mHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateRecyclerView(isChecked);
    }

    private void updateRecyclerView(boolean isChecked) {
        if(!isChecked){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.addItemDecoration(new SpacesItemDecoration(10));
            mBaseQuickAdapter.notifyDataSetChanged();
        }else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
            mRecyclerView.addItemDecoration(new SpacesItemDecoration(10));
            mBaseQuickAdapter.notifyDataSetChanged();
        }
    }
    private void initData() {
        mDatas.add(new Message("Hensen", "下午1:22", "老板：哈哈哈", R.mipmap.ic_launcher));
        mDatas.add(new Message("流年不利", "早上10:31", "美女：呵呵哒", R.mipmap.ic_launcher));
        mDatas.add(new Message("1402", "下午1:55", "嘻嘻哈哈", R.mipmap.ic_launcher));
        mDatas.add(new Message("Unstoppable", "下午4:32", "美美哒", R.mipmap.ic_launcher));
        mDatas.add(new Message("流年不利", "晚上7:22", "萌萌哒", R.mipmap.ic_launcher));
        mDatas.add(new Message("Hensen", "下午1:22", "哈哈哈", R.mipmap.ic_launcher));
        mDatas.add(new Message("Hensen", "下午1:22", "哈哈哈", R.mipmap.ic_launcher));
        mDatas.add(new Message("Hensen", "下午1:22", "哈哈哈", R.mipmap.ic_launcher));
        mDatas.add(new Message("更多", "下午1:22", "更多", R.mipmap.ic_launcher));
    }

    public class Message {
        public String username;
        public String time;
        public String message;
        public int img_id;
        public Message(String username, String time, String message, int img_id) {
                this.username = username;
                this.time = time;
                this.message = message;
                this.img_id = img_id;
            }
    }

}
