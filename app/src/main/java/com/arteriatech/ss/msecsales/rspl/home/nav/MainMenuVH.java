package com.arteriatech.ss.msecsales.rspl.home.nav;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10769 on 27-04-2017.
 */

public class MainMenuVH extends RecyclerView.ViewHolder {
    public final ImageView ivMenuImage;
    public final TextView tvMenuText, tvGroupTitle;
    public final View viewLine;

    public MainMenuVH(View itemView) {
        super(itemView);
        ivMenuImage = (ImageView)itemView.findViewById(R.id.iv_menu);
        tvMenuText = (TextView)itemView.findViewById(R.id.icon_text);
        tvGroupTitle = (TextView)itemView.findViewById(R.id.tvGroupTitle);
        viewLine = itemView.findViewById(R.id.viewLine);
    }
}
