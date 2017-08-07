package com.kingbox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.kingbox.holder.BaseViewHolder;
import com.kingbox.service.entity.Visitable;
import com.kingbox.utils.TypeFactory;
import com.kingbox.utils.TypeFactoryForList;

import java.util.List;

/**
 *  多类型item适配器
 */
public class MultiTypeAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private TypeFactory typeFactory;
    private List<Visitable> models;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MultiTypeAdapter(List<Visitable> models) {
        this.models = models;
        this.typeFactory = new TypeFactoryForList();
    }

    public void setList(List<Visitable> list){
        this.models = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View itemView = View.inflate(context,viewType,null);
        return typeFactory.createViewHolder(context, viewType,itemView);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        holder.setUpView(models.get(position),position,this);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(null == models){
            return  0;
        }
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        return models.get(position).type(typeFactory);
    }

}