package com.dzn.pointupdemo.entity

class MyItem(i: Int, var content: String?) {
    var isPinedToSwipeLeft: Boolean = false
    var id: Long = 0
    var viewType: Int = 0

    init {
        id = i.toLong()
        viewType = 0
    }
}
