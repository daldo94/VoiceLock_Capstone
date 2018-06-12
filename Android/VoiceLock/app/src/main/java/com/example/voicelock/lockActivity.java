package com.example.voicelock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
public class lockActivity extends AppCompatActivity {

    public RecyclerView.Adapter adapter;
    FloatingActionButton fab_fb;
    private ArrayList<RecyclerItem> mItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private Paint p = new Paint();
    private View view;
    DBAdapter db = new DBAdapter(this);
    private Intent intent;

    SwipeController swipeController = null;


    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 200;
    private static final int REQUEST_CONNECT_DEVICE = 300;
    private BluetoothAdapter mBTAdapter;
    private Button button, button2;
    static BTService _btService1, _btService2;

    private String address;
    private String address1 = "98:D3:61:F9:3E:75";
    private String address2 = "98:D3:31:F7:37:38";

    static boolean click, avail;
    static int open;
    static String addr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        ContextUtil.CONTEXT = getApplicationContext();

        _btService1 = new BTService(getApplicationContext());
//        _btService2 = new BTService(getApplicationContext());
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null)
        {
            // device does not support Bluetooth
//            Toast.makeText(getApplicationContext(), "device does not support Bluetooth", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (!mBTAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        checkIntent(getIntent());

        fab_fb = (FloatingActionButton) findViewById(R.id.fab_setting);

        fab_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomFloating customDialog = new CustomFloating(lockActivity.this);
                customDialog.CustomDialog(mItems);
            }
        });
       }


    private void setRecyclerView() {
        adapter = new RecyclerAdapter(mItems, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new CustomDividerItemDecoration(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final ImageButton lockBtn = (ImageButton) view.findViewById(R.id.lock);
                final ImageButton openBtn = (ImageButton) view.findViewById(R.id.unlock);
                RecyclerItem item = mItems.get(position);
                Intent intent =new Intent(lockActivity.this, OneLockActivity.class);
                intent.putExtra("NAME", item.getName());
                intent.putExtra("POSITION", item.getPosition());
                intent.putExtra("ISOPEN", item.getIsOpen());
                intent.putExtra("ADDRESS", item.getAddress());
                intent.putExtra("BLUETOOTH", address);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {
    //            Toast.makeText(getApplicationContext(), position + "번 째 아이템 롱 클릭", Toast.LENGTH_SHORT).show();
            }
        }));

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                RecyclerItem item = mItems.get(position);
                String name = item.getName();
                db.openDB();
                db.delete(name);
                db.closeDB();
                mItems.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
        retrieve();

    }

    public ArrayList<RecyclerItem> getItems() {
        return mItems;
    }

    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }


    public void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(lockActivity.this);
                    builder.setMessage("해당 기기를 삭제하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeItem(position);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                TextPaint textPaint = new TextPaint();
                Rect bounds = new Rect();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                        String showText = "Delete";

                        textPaint.getTextBounds(showText, 0, showText.length(), bounds);
                        c.drawText(showText, 0, showText.length(), itemView.getRight() + dX,
                                (itemView.getBottom()) - (itemView.getHeight() - bounds.height() >> 1), textPaint);

//
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_mic);
//                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
//                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.notifyDataSetChanged();

    }


    public void removeItem(int position) {
        mItems.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, mItems.size());
    }

    private void retrieve() {
        mItems.clear();
        db.openDB();


        //RETRIEVE
        Cursor c = db.getAllPlayers();
        int size = 0;
        //LOOP AND ADD TO ARRAYLIST
        while (c.moveToNext()) {

            int id = c.getInt(0);
            String name = c.getString(1);
            String pos = c.getString(2);
            String op = c.getString(3);
            int open = Integer.parseInt(op);
            RecyclerItem item;
            if (size == 0) {
                if (open == 0) {
                    item = new RecyclerItem(id, name, pos, R.id.lock, open, address1);
                } else {
                    item = new RecyclerItem(id, name, pos, R.id.unlock, open, address1);
                }
            } else {
                if (open == 0) {
                    item = new RecyclerItem(id, name, pos, R.id.lock, open, address2);
                } else {
                    item = new RecyclerItem(id, name, pos, R.id.unlock, open, address2);
                }
            }

            //ADD TO ARRAYLIS
            mItems.add(item);


            //CHECK IF ARRAYLIST ISNT EMPTY
            if (!(mItems.size() < 1)) {
                recyclerView.setAdapter(adapter);
            }

            db.closeDB();

        }
    }
    @Override
    public void onResume(){
        super.onResume();
        setRecyclerView();
        retrieve();
        if(click && avail) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //지연시키길 원하는 밀리초 뒤에 동작
                    if(addr.equals(address)) {
                        if (open == 1) {
                            _btService1.writeMessage(_btService1.getSocket());
                        } else {
                            _btService1.deleteMessage(_btService1.getSocket());
                        }
                    }

                }
            }, 500);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("MainActivity.java | onActivityResult", "|==" + requestCode + "|" + resultCode + "(ok = " + RESULT_OK + ")|" + data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_ENABLE_BT)
        {
            discovery();
        }
        else if (requestCode == REQUEST_CONNECT_DEVICE_INSECURE)
        {
            address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Log.i("MainActivity.java | onActivityResult", "|==" + address + "|");
            if (TextUtils.isEmpty(address))
                return;
            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
            _btService1.connect(device);
        }

    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(Intent $intent)
    {
        Log.i("MainActivity.java | checkIntent", "|==" + $intent.getAction() + "|");
        if ("kr.mint.bluetooth.receive".equals($intent.getAction()))
        {
            Log.i("MainActivity.java | checkIntent", "|==" + $intent.getStringExtra("msg") + "|");
            Toast.makeText(getApplicationContext(),"연결 완료", Toast.LENGTH_SHORT).show();
            avail=true;
        }
    }


    public void onBtnClick(View v)
    {
        discovery();

    }


    private void discovery()
    {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }
    public BTService get_btService1(){return _btService1;}
    public BTService get_btService2(){return _btService2;}

}