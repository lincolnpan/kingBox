package com.kingbox.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/7/8.
 */
public class ToastUtils {

    private static Toast toast = null;

    public static void ToastMessage(Context context, String msg) {
        if(TextUtils.isEmpty(msg)){
            return;
        }

        if(context == null){
            return ;
        }

        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            if (msg != null && !msg.trim().equals("")) {
                toast.setText(msg);
            }else{
                return;
            }
        }
        toast.show();
    }
}
