package com.example.max.testapp

import android.content.Context
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * Created by max on 18.03.18.
 *
 */
class RecyclerAdapter: RecyclerView.Adapter<ViewHolder>() {

    var dataSet: List<ItemViewModel> = ArrayList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    private var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view: View? = LayoutInflater.from(parent?.context)?.inflate(R.layout.item, parent, false)
        ctx = parent?.context?.applicationContext
        return ViewHolder(view ?: throw IllegalStateException(""))
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(dataSet[position])
        } else {
            Toast.makeText(ctx, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewRecycled(holder: ViewHolder?) {
        super.onViewRecycled(holder)
        if (holder is ViewHolder) {
            holder.unbind()
        } else {
            Toast.makeText(ctx, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }
}