package com.finogeeks.finclip.business.login


import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.finogeeks.finclip.R
import com.finogeeks.finclip.base.BaseActivity
import com.finogeeks.finclip.business.common.LocalData
import com.finogeeks.finclip.business.enter.ConfigServiceActivity
import com.finogeeks.finclip.utils.ActivitySkip
import com.finogeeks.finclip.utils.extensions.onClick

class PrivateLoginTypeActivity : BaseActivity() {

    private lateinit var tv_use_ps_login: View
    private lateinit var tv_con_ser: View
    private lateinit var tv_switch_type: TextView
    private lateinit var tv_ee_login_tl: TextView
    private var type = LocalData.UserType.EE_PR //Default Enterprise user

    override fun getLayoutId(): Int = R.layout.activity_private_login


    override fun configView(savedInstanceState: Bundle?) {
        LocalData.setUserType(type)
        tv_use_ps_login = findViewById(R.id.tv_use_ps_login)
        tv_con_ser = findViewById(R.id.tv_con_ser)
        tv_switch_type = findViewById(R.id.tv_switch_type)
        tv_ee_login_tl = findViewById(R.id.tv_ee_login_tl)

    }

    override fun configEvent() {
        super.configEvent()

        tv_switch_type.setOnClickListener {
            if (type == LocalData.UserType.EE_PR) {
                type = LocalData.UserType.OP_PR
                tv_ee_login_tl.text = getString(R.string.fpt_op_login)
                tv_switch_type.text = getString(R.string.fpt_switch_ee_login)
            } else {
                type = LocalData.UserType.EE_PR
                tv_ee_login_tl.text = getString(R.string.fpt_enterprise_login)
                tv_switch_type.text = getString(R.string.fpt_switch_op_login)
            }
            LocalData.setUserType(type)
        }

        tv_con_ser.onClick {
            ActivitySkip.startActivity(this, ConfigServiceActivity::class.java)
        }

        tv_use_ps_login.onClick {
            ActivitySkip.startActivity(this, UserNameLoginActivity::class.java)
        }

    }
}