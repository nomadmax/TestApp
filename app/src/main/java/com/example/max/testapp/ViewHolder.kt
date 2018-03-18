package com.example.max.testapp

import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.item.view.*
import kotlin.properties.Delegates

/**
 * Created by max on 18.03.18.
 *
 */
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), IBindable {
    override val disposable = CompositeDisposable()
    private val btnDeleteItem = itemView.delete_item
    private val tvNumber = itemView.number
    private val progressBar = itemView.item_progress_bar
    private var viewModel: ItemViewModel by Delegates.notNull()

    override fun bind(model: ItemViewModel) {
        viewModel = model
        progressBar.visibility = viewModel.showProgress.toVisible()
        tvNumber.visibility = (viewModel.showProgress == false).toVisible()
        btnDeleteItem.isClickable = viewModel.isBtnAccessible
        btnDeleteItem.isEnabled = viewModel.isBtnAccessible
        tvNumber.text = "${viewModel.data}"
        btnDeleteItem.clicks()
                .map {
                    Event.DeleteItemClick(viewModel)
                }
                .subscribe(viewModel.eventsConsumer)
                .addTo(disposable)

    }

}

interface IBindable {
    val disposable: CompositeDisposable
    fun bind(model: ItemViewModel)
    fun unbind(){
        disposable.clear()
    }
}

