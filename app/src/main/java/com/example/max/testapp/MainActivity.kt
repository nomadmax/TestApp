package com.example.max.testapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), Mvp.View {
    override val disposable: CompositeDisposable = CompositeDisposable()

    private lateinit var presenter: Mvp.Presenter
    private lateinit var layoutManager: LinearLayoutManager
    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        configurePresenter()
        configureRecycler()
    }

    private fun configurePresenter() {
        presenter = if (lastCustomNonConfigurationInstance != null) {
            lastCustomNonConfigurationInstance as Mvp.Presenter
        } else {
            PresenterImpl()
        }
    }

    private fun configureRecycler() {
        layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        presenter.state
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    renderState(it)
                }, {
                    renderError(it)
                })
                .addTo(disposable)
        fab.clicks()
                .map { view -> Event.FabClick }
                .subscribe(presenter.events)
                .addTo(disposable)
        presenter.events.accept(Event.Init)
    }

    override fun renderState(state: State) {
        when (state) {
            is State.ShowProgressBar -> {
                progressBar.visibility = state.show.toVisible()
            }
            is State.ShowUnDeleteSnackBar -> {
                Snackbar.make(recycler, getString(R.string.item_deleted, state.item.data.toString()), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo) { presenter.events.accept(Event.UnDeleteItemClick(state.item)) }
                        .show()
            }
            is State.ListUpdate -> {
                adapter.dataSet = state.list
            }
            is State.ScrollTo -> {
                recycler.scrollToPosition(state.scrollPosition)
            }
            is State.Error -> {
                renderError(state.throwable)
            }
        }
    }

    private fun renderError(throwable: Throwable) {
        Toast.makeText(this, throwable.message
                ?: getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        super.onRetainCustomNonConfigurationInstance()
        presenter.events.accept(Event.SaveScrollPosition(layoutManager.findFirstVisibleItemPosition()))
        return presenter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

