package co.droidchef.oldschool.feature.search.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.droidchef.oldschool.R
import co.droidchef.oldschool.feature.search.viewmodel.SearchViewModel
import co.droidchef.oldschool.models.Photo

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel

    private lateinit var photosGridRecyclerView: RecyclerView

    private lateinit var searchResultsAdapter: SearchResultsAdapter

    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var progressBar: ProgressBar

    private lateinit var etSearchQuery: EditText

    private lateinit var btSearch: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.main_fragment, container, false)
        progressBar = rootView.findViewById(R.id.progressBar)
        etSearchQuery = rootView.findViewById(R.id.et_searchQuery)
        btSearch = rootView.findViewById(R.id.bt_search)
        photosGridRecyclerView = rootView.findViewById(R.id.rv_searchResults)
        gridLayoutManager = GridLayoutManager(this.activity, 3)
        photosGridRecyclerView.layoutManager = gridLayoutManager

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        //setupRecyclerView()

        viewModel.isWorkInProgress.observe(this,
            Observer<Boolean> { t -> toggleProgressIndicator(t) })

        searchResultsAdapter = SearchResultsAdapter(arrayListOf())
        searchResultsAdapter.setHasStableIds(true)
        photosGridRecyclerView.adapter = searchResultsAdapter

        viewModel.photos.observe(
            this,
            Observer<ArrayList<Photo>> {
                searchResultsAdapter.addPhotos(it)
            })

        btSearch.setOnClickListener {

            viewModel.performSearchFor(etSearchQuery.editableText.toString())

            searchResultsAdapter.clear()

            photosGridRecyclerView.visibility = View.VISIBLE

            observeForErrors()

            hideKeyBoard()

        }

        photosGridRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0) {

                    val totalItemCount = gridLayoutManager.itemCount

                    val lastItem = gridLayoutManager.findLastCompletelyVisibleItemPosition()

                    val visibleThreshold = 6

                    if (totalItemCount <= lastItem + visibleThreshold) {

                        viewModel.loadMore()

                    }
                }


            }
        })

    }

    private fun observeForErrors() {
        viewModel.errorMessage.observeEvent(this, Observer {
            Toast.makeText(this.context, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(etSearchQuery.windowToken, 0)
    }

    private fun toggleProgressIndicator(isWorkInProgress: Boolean) {
        progressBar.visibility = if (isWorkInProgress) View.VISIBLE else View.GONE
        photosGridRecyclerView.visibility = if (isWorkInProgress) View.GONE else View.VISIBLE
    }
}
