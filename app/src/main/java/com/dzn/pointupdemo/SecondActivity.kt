package com.dzn.pointupdemo

import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dzn.pointupdemo.adapter.MyAdapter
import com.dzn.pointupdemo.entity.MyItem
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import java.util.*

class SecondActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mWrappedAdapter: RecyclerView.Adapter<*>? = null
//    private var mRecyclerViewSwipeManager: RecyclerViewSwipeManager? = null
//    private var mRecyclerViewTouchActionGuardManager: RecyclerViewTouchActionGuardManager? = null
    private var mRecyclerViewDragDropManager: RecyclerViewDragDropManager? = null

    private val dataList = ArrayList<MyItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)


        //获取RecycleView,创建LayoutManager
        mRecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        mLayoutManager = LinearLayoutManager(this)

        // touch guard manager 阻止当Swipe时列表滚动
//        mRecyclerViewTouchActionGuardManager = RecyclerViewTouchActionGuardManager()
//        mRecyclerViewTouchActionGuardManager!!.setInterceptVerticalScrollingWhileAnimationRunning(true)
//        mRecyclerViewTouchActionGuardManager!!.isEnabled = true

        // drag & drop manager
        mRecyclerViewDragDropManager = RecyclerViewDragDropManager()
        mRecyclerViewDragDropManager!!.setDraggingItemShadowDrawable(
                resources.getDrawable(R.drawable.material_shadow_z3_xxhdpi) as NinePatchDrawable)

        // swipe manager
//        mRecyclerViewSwipeManager = RecyclerViewSwipeManager()

        //adapter
        for (i in 0..99) {
            dataList.add(MyItem(i, "测试数据$i"))
        }
        mAdapter = MyAdapter(dataList)

        mWrappedAdapter = mRecyclerViewDragDropManager!!.createWrappedAdapter(mAdapter)   //处理一下mAdapter可以拖拽
//        mWrappedAdapter = mRecyclerViewSwipeManager!!.createWrappedAdapter(mWrappedAdapter)      //处理一下mAdapter可以滑动删除


        val animator = SwipeDismissItemAnimator()

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.supportsChangeAnimations = false

        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.adapter = mWrappedAdapter  // 注意设置的是mWrappedAdapter
        mRecyclerView!!.itemAnimator = animator

        //additional decorations

        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView!!.addItemDecoration(ItemShadowDecorator(resources.getDrawable(R.drawable.material_shadow_z3_xxhdpi) as NinePatchDrawable))
        }
        mRecyclerView!!.addItemDecoration(SimpleListDividerDecorator(resources.getDrawable(R.drawable.list_divider), true))

        // NOTE:
        // 初始化的顺序十分重要,这决定了处理Touch事件的优先级
        // 优先级: TouchActionGuard > Swipe > DragAndDrop
//        mRecyclerViewTouchActionGuardManager!!.attachRecyclerView(mRecyclerView!!)
//        mRecyclerViewSwipeManager!!.attachRecyclerView(mRecyclerView!!)
        mRecyclerViewDragDropManager!!.attachRecyclerView(mRecyclerView)

    }

    private fun supportsViewElevation(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
}
