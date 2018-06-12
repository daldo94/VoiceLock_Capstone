package com.example.voicelock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomFloating  {

    private Context context;
    public CustomFloating(Context context){
        this.context=context;
    }
    private ArrayList<RecyclerItem> items = new ArrayList<>();

    public void CustomDialog(ArrayList<RecyclerItem> mItems) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_custom_floating);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        items=mItems;
        //Toast.makeText(context, p_id, Toast.LENGTH_SHORT).show();

        final FloatingActionButton closeButton=(FloatingActionButton) dialog.findViewById(R.id.fab_close);
        closeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.dismiss();
            }
        });

        final FloatingActionButton addButton=(FloatingActionButton)dialog.findViewById(R.id.fab_add);
        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(context, AddActivity.class);
                context.startActivity(intent);
            }
        });

        final FloatingActionButton micButton=(FloatingActionButton)dialog.findViewById(R.id.fab_mic);
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aintent = new Intent(context, VoiceActivity.class);
                aintent.putExtra("SIZE", items.size());
                context.startActivity(aintent);
            }
        });
    }



}
