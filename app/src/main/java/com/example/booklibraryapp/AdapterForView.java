package com.example.booklibraryapp;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class AdapterForView
//used for RecyclerView in order to be able to click on items in list to redirect to item page
{
    public static class stringStencil
    {
        private String clickedItem;
        public stringStencil(String title)
        {
            this.clickedItem = title;
        }

        public String getClickedItem()
        {
            return clickedItem;
        }
    }
    public interface OnItemClickListener
    {
        void onItemClick(stringStencil item, int index);
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.VHolder>
    {
        private List<stringStencil> list;
        private OnItemClickListener listener;
        public Adapter(List<stringStencil> list, OnItemClickListener listener)
        {
            this.list = list;
            this.listener = listener;
        }

        @Override
        public VHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new VHolder(view);
        }

        @Override
        public void onBindViewHolder(VHolder holder, int index)
        {
            stringStencil item = list.get(index);
            holder.bind(item, listener);
        }

        @Override
        public int getItemCount()
        {
            return list.size();
        }

        static class VHolder extends RecyclerView.ViewHolder
        {
            TextView textView;

            public VHolder(View itemView)
            {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }

            public void bind(stringStencil item, OnItemClickListener listener)
            {
                textView.setText(item.getClickedItem());

                itemView.setOnClickListener(v -> listener.onItemClick(item, getBindingAdapterPosition()));
            }
        }
    }
}

