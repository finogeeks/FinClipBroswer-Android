package com.finogeeks.finclip.business.common.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.StringUtils
import com.drakeet.multitype.ItemViewBinder
import com.finogeeks.finclip.R
import com.finogeeks.finclip.utils.extensions.onClick

@Keep
class UIAreaCodeBean {
    var prefix:String = ""
    var en:String = ""
    var cn:String = ""
}

class AreaCodeListBinder(private val onClick: (item: UIAreaCodeBean) -> Unit) :
    ItemViewBinder<UIAreaCodeBean, AreaCodeListBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_area_code, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: UIAreaCodeBean) {
        if ("EN".equals(StringUtils.getString(R.string.fpt_language_type), true)) {
            holder.tv_country.text = item.en.trim()
        } else {
            holder.tv_country.text = item.cn.trim()
        }
        holder.tv_area_code.text = item.prefix.trim()
        holder.itemView.onClick {
            onClick(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_area_code = itemView.findViewById<TextView>(R.id.tv_area_code)
        val tv_country = itemView.findViewById<TextView>(R.id.tv_country)
    }
}