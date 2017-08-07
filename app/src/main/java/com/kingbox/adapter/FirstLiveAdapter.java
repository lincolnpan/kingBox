package com.kingbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.service.entity.LiveField;
import com.kingbox.utils.GlideCatchUtil;
import com.kingbox.view.RoundImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
public class FirstLiveAdapter extends RecyclerView.Adapter<FirstLiveAdapter.VHolder> {

    private Context context;

    private List<LiveField> list;
    private int screenWidth;//屏幕宽度
    public FirstLiveAdapter(Context context, List<LiveField> liveFieldList) {
        this.context = context;
        this.list = liveFieldList;

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);

        screenWidth = metric.widthPixels;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.first_field_list_item, parent,
                false);
        /*GridView.LayoutParams gl = (GridView.LayoutParams) view.getLayoutParams();
        gl.width = screenWidth / 2;*/
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(VHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    class VHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        ImageView userImg;
        TextView roomNameTv;
        RoundImageView roomImg;
        LinearLayout fieldItemLayout;

        public VHolder(final View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            userImg = (ImageView) itemView.findViewById(R.id.user_img);
            roomNameTv = (TextView) itemView.findViewById(R.id.room_name_tv);
            roomImg = (RoundImageView) itemView.findViewById(R.id.room_img);
            fieldItemLayout = (LinearLayout) itemView.findViewById(R.id.field_list_item_layout);

        }

        public void bindData(final int position) {


            LiveField liveField = list.get(position);

            String message = liveField.getMsg();
            String[] m = message.split("\\|\\|");
            String roomName = "";
            String livePic = "";
            String liveUrl = "";
            if (m.length == 2) {   // 广场
                nameTv.getBackground().setAlpha(200);//0~255透明度值
                roomImg.setVisibility(View.VISIBLE);
                nameTv.setVisibility(View.VISIBLE);

                String roomMsg;
                String liveMsg;
                try {
                    roomMsg = m[0];
                    liveMsg = m[1];
                } catch (Exception e){
                    roomMsg = "";
                    liveMsg = "";
                }

                if (!TextUtils.isEmpty(roomMsg) && !TextUtils.isEmpty(liveMsg)) {
                    int one = roomMsg.indexOf("@mc");
                    int two = roomMsg.indexOf("|@tp");
                    int three = roomMsg.indexOf("|@dz");

                    try {
                        roomName = roomMsg.substring(one + 3, two);
                    } catch (Exception e) {
                        roomName = "";
                    }

                    String roomPic;
                    try {
                        roomPic = roomMsg.substring(two + 4, three);
                    } catch (Exception e) {
                        roomPic = "";
                    }
                    // 圆形图片
                    /*RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                    roundingParams.setRoundAsCircle(true);
                    roomImg.getHierarchy().setRoundingParams(roundingParams);*/
                    GlideCatchUtil.getInstance().ImageLoading(context, "http://bee.donewe.com/" + roomPic, roomImg);
                    //roomImg.setImageURI("http://bee.donewe.com/" + roomPic);

                    int four = liveMsg.indexOf("@mc");
                    int five = liveMsg.indexOf("|@tp");
                    int six = liveMsg.indexOf("|@dz");

                    String liveName;
                    try {
                        liveName = liveMsg.substring(four + 3, five);
                    } catch (Exception e) {
                        liveName = "";
                    }
                    nameTv.setText(liveName);

                    try {
                        livePic = liveMsg.substring(five + 4, six);
                    } catch (Exception e) {
                        livePic = "";
                    }


                    try {
                        liveUrl = liveMsg.substring(six + 4, liveMsg.length() - 1);
                    } catch (Exception e) {
                        liveUrl = "";
                    }


                }
            } else {   // 国外
                roomImg.setVisibility(View.GONE);
                nameTv.setVisibility(View.GONE);

                int a = message.indexOf("@mc");
                int b = message.indexOf("|@rs");
                int c = message.indexOf("@tp");
                int d = message.indexOf("|@dz");
                try {
                    roomName = message.substring(a + 3, b);
                } catch (Exception e){
                    roomName = "";
                }
                try {
                    livePic = message.substring(c + 3, d);
                } catch (Exception e){
                    livePic = "";
                }
                try {
                    liveUrl = message.substring(d + 4, message.length() -1);
                } catch (Exception e){
                    liveUrl = "";
                }
                liveField.setWebPlay(true);
                liveField.setRoomName(roomName);
            }
            roomNameTv.setText(roomName);
            //userImg.setImageURI(livePic);
            GlideCatchUtil.getInstance().ImageLoading(context, livePic, userImg, R.drawable.df);
            liveField.setImg(livePic);
            liveField.setLiveUrl(liveUrl);

            fieldItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemView, position);
                }
            });
        }
    }

    /**
     * item点击监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * item点击监听
     */
    private OnItemClickListener onItemClickListener;

    /**
     * 设置item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
