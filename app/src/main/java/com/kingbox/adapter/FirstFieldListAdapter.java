package com.kingbox.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.service.entity.LiveField;
import com.kingbox.utils.GlideCatchUtil;
import com.kingbox.utils.ViewHolder;

import java.util.List;

/**
 * Created by lincolnpan on 2017/7/16.
 */
public class FirstFieldListAdapter extends BaseAdapter {

    private Context context;
    private List<LiveField> list;

    public FirstFieldListAdapter(Context context, List<LiveField> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.first_field_list_item, parent, false);
        }

        TextView nameTv = ViewHolder.getViewById(convertView, R.id.name_tv);
        ImageView userImg = ViewHolder.getViewById(convertView, R.id.user_img);
        TextView roomNameTv = ViewHolder.getViewById(convertView, R.id.room_name_tv);
        ImageView roomImg = ViewHolder.getViewById(convertView, R.id.room_img);


        nameTv.getBackground().setAlpha(100);//0~255透明度值

        LiveField liveField = list.get(position);

        //roomNameTv.setText(liveField.getRoomName());

        String message = liveField.getMsg();
        String[] m = message.split("\\|\\|");
        if (m.length == 2) {

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

                String roomName = roomMsg.substring(one + 3, two);
                roomNameTv.setText(roomName);

                String roomPic = roomMsg.substring(two + 4, three);
                // 圆形图片
                /*RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setRoundAsCircle(true);
                roomImg.getHierarchy().setRoundingParams(roundingParams);*/
                GlideCatchUtil.getInstance().ImageLoading(context, "http://bee.donewe.com/" + roomPic, roomImg);
                //roomImg.setImageURI("http://bee.donewe.com/" + roomPic);

                int four = liveMsg.indexOf("@mc");
                int five = liveMsg.indexOf("|@tp");
                int six = liveMsg.indexOf("|@dz");

                String liveName = liveMsg.substring(four + 3, five);
                nameTv.setText(liveName);

                String livePic = liveMsg.substring(five + 4, six);
                //userImg.setImageURI(livePic);
                GlideCatchUtil.getInstance().ImageLoading(context, livePic, userImg);
                liveField.setImg(livePic);

                String liveUrl = liveMsg.substring(six + 4, liveMsg.length() - 1);
                liveField.setLiveUrl(liveUrl);

            }

        }



        /*// 圆形图片
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        roomImg.getHierarchy().setRoundingParams(roundingParams);

        roomImg.setImageURI(liveField.getImg());

        String msg = liveField.getMsg();
        int index = msg.indexOf("@tp");
        int end = msg.indexOf("@dz");
        int a = msg.indexOf("@mc");
        String name;
        try {
            name = msg.substring(a + 3, index - 1);
        } catch (Exception e) {
            name = "";
        }
        nameTv.setText(name);
        String url;
        try {
            url = msg.substring(index + 3, end - 1);
        } catch (Exception e) {
            url = "";
        }

        userImg.setImageURI(url);*/

        final View finalConvertView = convertView;
        LinearLayout fieldItemLayout = ViewHolder.getViewById(convertView, R.id.field_list_item_layout);

        fieldItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(finalConvertView, position);
            }
        });

        return convertView;
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
    private LiveFieldListAdapter.OnItemClickListener onItemClickListener;

    /**
     * 设置item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(LiveFieldListAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}