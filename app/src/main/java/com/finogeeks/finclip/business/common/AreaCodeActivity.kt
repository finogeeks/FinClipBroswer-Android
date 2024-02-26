package com.finogeeks.finclip.business.common


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.model.AreaCodeListBinder
import com.finogeeks.finclip.business.common.model.UIAreaCodeBean
import com.finogeeks.finclip.utils.JsonUtils
import com.finogeeks.finclip.utils.extensions.onClick


const val DefaultAreaCode = "+86"
const val AreaCodeKey = "AreaCodeKey"

class AreaCodeActivity : BaseActivity() {
    private lateinit var rv_area_code: RecyclerView
    private lateinit var iv_back: View
    override fun getLayoutId() = R.layout.activity_area_code
    private val mdAdapter = MultiTypeAdapter()
    private val uiAreaCodes = mutableListOf<Any>()
    override fun configView(savedInstanceState: Bundle?) {
        rv_area_code = findViewById(R.id.rv_area_code)
        iv_back = findViewById(R.id.iv_back)
    }


    override fun configData() {
        super.configData()

        ResourceUtils.readAssets2String("areaCode.json")?.let {
            val areaCodes = JsonUtils.fromJson(it, Array<UIAreaCodeBean>::class.java)
            if (areaCodes != null) {
                uiAreaCodes.clear()
                uiAreaCodes.addAll(areaCodes)
            }
        }

    }

    override fun configEvent() {
        super.configEvent()

        iv_back.onClick {
            finish()
        }

        rv_area_code.layoutManager = LinearLayoutManager(this)
        mdAdapter.register(UIAreaCodeBean::class.java, AreaCodeListBinder {
            val bundle = Bundle()
            bundle.putString(AreaCodeKey, it.prefix)
            setResult(RESULT_OK, intent.putExtras(bundle))
            finish()
        })

        mdAdapter.items = uiAreaCodes
        rv_area_code.adapter = mdAdapter
    }
}