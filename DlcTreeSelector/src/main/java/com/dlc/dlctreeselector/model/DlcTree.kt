package com.dlc.dlctreeselector.model

/**
 * title：
 * description：树状结构需继承此方法
 * author：dinglicheng on 2022/11/16 11:36
 */
abstract class DlcTree {
    abstract override fun toString(): String
    abstract fun toChildDlcTree(): Any?
    var isChick: Boolean = false
    var live = 0
    var dlc_index = 0
}