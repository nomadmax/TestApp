package com.example.max.testapp

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * Created by max on 18.03.18.
 *
 */
interface Mvp {
    interface View{
        val disposable:CompositeDisposable
        fun renderState(state: State)
    }

    interface Presenter {
        val disposable:CompositeDisposable
        val events: PublishRelay<Event>
        val state: PublishRelay<State>
        fun processEvent(event: Event)
    }
}

sealed class State{
    data class ShowProgressBar(val show: Boolean) : State()
    data class ListUpdate(val list: List<ItemViewModel>) : State()
    data class Error(val throwable: Throwable): State()
    data class ShowUnDeleteSnackBar(val item: ItemViewModel): State()
    data class ScrollTo(val scrollPosition: Int) : State()
}
sealed class Event {
    object FabClick : Event()
    object Init : Event()
    data class DeleteItemClick(val item: ItemViewModel) : Event()
    data class UnDeleteItemClick(val item: ItemViewModel) : Event()
    data class AddItem(val item: ItemViewModel): Event()
    data class RemoveItem(val item: ItemViewModel): Event()
    data class SaveScrollPosition(val pos: Int) : Event()
}

data class ItemViewModel(val id: Int,
                         val data: Int,
                         var isBtnAccessible: Boolean,
                         var showProgress: Boolean,
                         val eventsConsumer: Consumer<Event>)