package com.finogeeks.finclip.business.home.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.blankj.utilcode.util.ConvertUtils
import com.drakeet.multitype.ItemViewBinder
import com.finogeeks.finclip.R
import com.finogeeks.finclip.http.model.AppletInfo
import com.finogeeks.finclip.utils.extensions.onClick
@Keep
class UIAppletBean(val iconURL: String, val appName: String, val info: AppletInfo)


class HomeAppletListBinder(private val onClick: (item: UIAppletBean) -> Unit) :
    ItemViewBinder<UIAppletBean, HomeAppletListBinder.ViewHolder>() {


    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.applet_item_layout, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, item: UIAppletBean) {

        val size = adapter.items.size
        val position = getPosition(holder)
        //The first four
        if(position <= 3){
            (holder.itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = ConvertUtils.dp2px(25F)
        }else{
             (holder.itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = ConvertUtils.dp2px(18F)
        }


        val remainder = size % 4
        if( (remainder == 0 && size - position <=4 ) || (remainder != 0 && size - position <= remainder) ){
            (holder.itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = ConvertUtils.dp2px(25F)
        }else{
            (holder.itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = ConvertUtils.dp2px(0F)
        }



        holder.icon.load(item.iconURL) {
            transformations(RoundedCornersTransformation(ConvertUtils.dp2px(8F).toFloat()))
            placeholder(R.drawable.login_logo)
            error(R.drawable.login_logo)
        }
        holder.appName.text = item.appName
        holder.itemView.onClick {
            onClick(item)
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon = itemView.findViewById<ImageView>(R.id.iv_icon)
        val appName = itemView.findViewById<TextView>(R.id.tv_name)
    }
}




