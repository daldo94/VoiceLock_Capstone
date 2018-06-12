package com.example.voicelock;

/**
 * Created by Sujin Jeong on 2018-05-03.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopeer.itemtouchhelperextension.Extension;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
    ArrayList<RecyclerItem> mItems;
    private int focusedItem=0;
    Context context;
    View view;
    public RecyclerAdapter(ArrayList<RecyclerItem> items , Context context){
        mItems=items;
        this.context=context;
    }

    public void setFocusedItem(int position){
        notifyItemChanged(focusedItem);
        focusedItem=position;
        notifyItemChanged(focusedItem);
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerview){
        super.onAttachedToRecyclerView(recyclerview);
        recyclerview.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                RecyclerView.LayoutManager lm=recyclerview.getLayoutManager();

                if(event.getAction()==KeyEvent.ACTION_DOWN){
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return tryMoveSelection(lm,1);
                    }else if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
                        return tryMoveSelection(lm, -1);
                    }
                }
                return false;
            }
        });
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction){
        int tryFocusItem=focusedItem+direction;

        if(tryFocusItem>=0 && tryFocusItem < getItemCount()){
            notifyItemChanged(focusedItem);
            focusedItem=tryFocusItem;
            notifyItemChanged(focusedItem);
            lm.scrollToPosition(focusedItem);
            return true;
        }
        return false;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position){
        holder.mNameTv.setText(mItems.get(position).getName());
        holder.mSettingTv.setText(mItems.get(position).getPosition());
        holder.itemView.setSelected(focusedItem==position);
        if(mItems.get(position).getIsOpen()==0 ) {
            holder.lockimg.setVisibility(view.VISIBLE);
            holder.unlockimg.setVisibility(view.GONE);
        //    holder.mLayout.setBackgroundColor(context.getResources().getColor(R.color.itemLayoutColor));
            holder.mLinear.setBackground(context.getDrawable(R.drawable.longgraycircle));

        }
        else{
            holder.lockimg.setVisibility(view.GONE);
            holder.unlockimg.setVisibility(view.VISIBLE);
           // holder.mLayout.setBackgroundColor(context.getResources().getColor(R.color.backgroundSky));
            holder.mLinear.setBackground(context.getDrawable(R.drawable.longcircle));
        }
    }

    @Override
    public int getItemCount(){
        return mItems.size();
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView mNameTv, mSettingTv, mConnect;
        private RelativeLayout mLayout;
        private LinearLayout mLinear,mUnconnect;
        private ImageView lockimg, unlockimg;

        public ItemViewHolder(View itemView){
            super(itemView);
            view=itemView;
            mLinear=(LinearLayout)itemView.findViewById(R.id.linear);
            mLayout=(RelativeLayout) itemView.findViewById(R.id.itemLayout);
            lockimg= (ImageView) itemView.findViewById(R.id.lock);
            unlockimg=(ImageView)itemView.findViewById(R.id.unlock);
            mNameTv=(TextView)itemView.findViewById(R.id.itemName);
            mSettingTv=(TextView)itemView.findViewById(R.id.itemSetting);
           }
    }
}

