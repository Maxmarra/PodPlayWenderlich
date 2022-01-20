package com.example.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.podplay.R
import com.example.podplay.adapter.PodcastListAdapter
import com.example.podplay.databinding.ActivityPodcastBinding
import com.example.podplay.repository.ItunesRepo
import com.example.podplay.service.ItunesService
import com.example.podplay.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PodcastActivity : AppCompatActivity(),
    PodcastListAdapter.PodcastListAdapterListener{


    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var podcastListAdapter: PodcastListAdapter
    private lateinit var databinding: ActivityPodcastBinding

    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(databinding.root)
        setupToolbar()
        setupViewModels()
        updateControls()
        handleIntent(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
// 1
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
//2
        val searchMenuItem = menu?.findItem(R.id.search_item)
        val searchView = searchMenuItem?.actionView as SearchView
// 3
        val searchManager = getSystemService(Context.SEARCH_SERVICE)
                as SearchManager
// 4
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(
                componentName
            )
        )
        return true
    }

    private fun performSearch(term: String) {
        showProgressBar()
        GlobalScope.launch {
            val results = searchViewModel.searchPodcasts(term)
            withContext(Dispatchers.Main) {
                hideProgressBar()
                databinding.toolbar.title = term
                podcastListAdapter.setSearchData(results)
            }
        }
    }
//This method takes in an Intent and checks to see if it’s an ACTION_SEARCH.
// If so, it
//extracts the search query string and passes it to performSearch().
    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
                ?: return
            performSearch(query)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(databinding.toolbar)
    }

    private fun setupViewModels() {
        val service = ItunesService.instance
        searchViewModel.iTunesRepo = ItunesRepo(service)
    }

    private fun updateControls() {
        databinding.podcastRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        databinding.podcastRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            databinding.podcastRecyclerView.context,
            layoutManager.orientation)
        databinding.podcastRecyclerView.addItemDecoration(dividerItemDecoration)
        podcastListAdapter = PodcastListAdapter(null, this, this)
        databinding.podcastRecyclerView.adapter = podcastListAdapter
    }

    override fun onShowDetails(
        podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData
    ) {
// Not implemented yet
    }
    private fun showProgressBar() {
        databinding.progressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar() {
        databinding.progressBar.visibility = View.INVISIBLE
    }
}