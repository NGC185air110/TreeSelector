package com.dlc.treeselector.model

import com.dlc.dlctreeselector.model.DlcTree

/**
 * title：
 * description：
 * author：dinglicheng on 2022/11/17 13:48
 */
class AddressModel : DlcTree() {

    var id: Int? = null
    var name: String? = null
    var data: ArrayList<AddressModel>? = null

    override fun toString(): String {
        return name ?: ""
    }

    override fun toChildDlcTree(): Any? {
        return data
    }

}