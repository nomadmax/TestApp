package com.example.max.testapp

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by max on 18.03.18.
 *
 */
class PresenterImpl: Mvp.Presenter {
    private val list  = ArrayList<ItemViewModel>()

    override val disposable: CompositeDisposable = CompositeDisposable()
    override val events: PublishRelay<Event> = PublishRelay.create()
    override val state: PublishRelay<State> = PublishRelay.create()

    private val idGenerator = AtomicInteger(0)
    private val nextId: Int get() = idGenerator.incrementAndGet()
    private val opCount = AtomicInteger(0)

    init {
        events.subscribe({
            processEvent(it)
        }, {
            state.accept(State.Error(it))
        }).addTo(disposable)
    }

    override fun processEvent(event: Event) {
        when (event) {
            Event.Init -> {
        //        if (list.size == 0) {
        //            generateList()
        //        }
                state.accept(State.ListUpdate(list))
            }
            Event.FabClick -> {
                opCount.getAndIncrement()
                state.accept(State.ShowProgressBar(opCount.get() != 0))
                Observable.timer(random.intInRangeInclusive(1, MAX_DELAY).toLong(), TimeUnit.SECONDS)
                        .map { random.intInRangeInclusive(RANGE_MIN, RANGE_MAX) }
                        .map { it.toItemViewModel() }
                        .map { Event.AddItem(it) }
                        .doOnNext{
                            opCount.decrementAndGet()
                            state.accept(State.ShowProgressBar(opCount.get() != 0))
                        }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(events)
                        .addTo(disposable)
            }
            is Event.DeleteItemClick -> {
                if (event.item.showProgress) { return }
                event.item.showProgress = true
                state.accept(State.ListUpdate(list))
                Observable.timer(random.intInRangeInclusive(1,MAX_DELAY).toLong(), TimeUnit.SECONDS)
                        .map { Event.RemoveItem(event.item) }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(events)
                        .addTo(disposable)
            }
            is Event.UnDeleteItemClick -> {
                opCount.getAndIncrement()
                state.accept(State.ShowProgressBar(opCount.get() != 0))
                Observable.timer(random.intInRangeInclusive(1,MAX_DELAY).toLong(), TimeUnit.SECONDS)
                        .map { Event.AddItem(event.item.copy(showProgress = false)) }
                        .doOnNext{
                            opCount.decrementAndGet()
                            state.accept(State.ShowProgressBar(opCount.get() != 0))
                        }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(events)
                        .addTo(disposable)
            }
            is Event.AddItem -> {
                if (!list.contains(event.item)) {
                    list.add(event.item)
                    Collections.sort(list, { left, right -> right.data.compareTo(left.data) })
                    state.accept(State.ListUpdate(list))
                }
            }
            is Event.RemoveItem -> {
                list.remove(event.item)
                state.accept(State.ListUpdate(list))
                state.accept(State.ShowUnDeleteSnackBar(event.item))
            }
        }
    }

    private val random: Random = Random()

    private fun generateList() {
        list.addAll(
        (0 until LIST_SIZE)
                .map { random.intInRangeInclusive(RANGE_MIN, RANGE_MAX) }
                .map { it.toItemViewModel() }
                .sortedByDescending { it.data }
        )
    }

    companion object {
        const val RANGE_MAX = 100
        const val RANGE_MIN = 0
        const val LIST_SIZE = 4
        const val MAX_DELAY = 2
    }

    private fun Int.toItemViewModel(): ItemViewModel =
            ItemViewModel(id = nextId,
            data = this,
            isBtnAccessible = true,
            showProgress = false,
            eventsConsumer = events)

}
