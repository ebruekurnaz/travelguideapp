package com.softwareproject.travelguideapp.pages.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.`interface`.SearchItemClicked
import com.softwareproject.travelguideapp.adapter.SearchRecyclerAdapter
import com.softwareproject.travelguideapp.model.SearchItem
import com.softwareproject.travelguideapp.pages.landmark.LandmarkFragment
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.softwareproject.travelguideapp.util.JsonParser
import com.softwareproject.travelguideapp.util.StringUtil
import com.tuyenmonkey.mkloader.MKLoader

class SearchFragment(private var initialSearchText:String): Fragment(), SearchContract.View, SearchView.OnQueryTextListener, SearchItemClicked {

    private var presenter : SearchContract.Presenter? = null
    private lateinit var searchItemsRecycler: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerAdapter: SearchRecyclerAdapter
    private lateinit var loaderView: MKLoader

    companion object{
        const val TAG = "SearchFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        presenter = SearchPresenter(this)
    }

    private fun init(view: View){
        searchView = view.findViewById(R.id.frag_search_sv_search)
        searchItemsRecycler = view.findViewById(R.id.frag_search_rv_searchRecycler)
        loaderView = view.findViewById(R.id.frag_search_loader)
    }

    private fun setAttributes(){
        searchView.setOnQueryTextListener(this)
        searchView.setQuery(StringUtil().toLower(initialSearchText), false)
    }

    override fun setPresenter(presenter: SearchContract.Presenter) {
        this.presenter = presenter
        openProgress()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        search(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        search(newText)
        return true
    }

    private fun search(s: String?){
        s?.let {
            val loweredQuery = StringUtil().toLower(s)
            searchRecyclerAdapter.search(loweredQuery){
                Toast.makeText(context, "Nothing Found :(", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSearchItemsFetched(searchItems: MutableList<SearchItem>) {
        closeProgress()
        val mContext = context
        mContext?.let {
            searchRecyclerAdapter = SearchRecyclerAdapter(searchItems, this, mContext)
            searchItemsRecycler.layoutManager = LinearLayoutManager(context)
            searchItemsRecycler.adapter = searchRecyclerAdapter
            setAttributes()
        }
    }

    override fun onSearchItemClicked(landmarkId: Int) {
        val bundle = Bundle()
        bundle.putInt("landmarkId", landmarkId)
        bundle.let {
            val landmarkFragment = LandmarkFragment()
            landmarkFragment.arguments = it
            val mSupportManager = activity?.supportFragmentManager
            mSupportManager?.let {
                ActivityUtil().changeFragmentWithStack(landmarkFragment,
                    LandmarkFragment.TAG,
                    mSupportManager,
                    R.id.act_home_fl_container)
            }
        }
    }

    private fun openProgress() {
        loaderView.visibility = View.VISIBLE
    }

    private fun closeProgress(){
        loaderView.visibility = View.GONE
    }

    override fun onRequestError(errorMsg: String) {
        closeProgress()
        val mContext = context
        mContext?.let {
            val title = mContext.getString(R.string.header_error)
            ActivityUtil().openAlertDialog(mContext, title, errorMsg, false ){}
        }
    }

}