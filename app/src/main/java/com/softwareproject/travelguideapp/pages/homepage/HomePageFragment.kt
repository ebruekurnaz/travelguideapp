package com.softwareproject.travelguideapp.pages.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.pages.home.HomeActivity


class HomePageFragment: Fragment(), HomePageContract.View {

    private var presenter : HomePageContract.Presenter? = null

    private lateinit var mainCardView: CardView
    private lateinit var firstCardView: CardView
    private lateinit var secondCardView: CardView
    private lateinit var thirdCardView: CardView
    private lateinit var fourthCardView: CardView

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private lateinit var thirdTextView: TextView
    private lateinit var fourthTextView: TextView

    companion object{
        const val TAG = "HomePageFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPresenter(HomePagePresenter(this))
        init(view)
        setAttributes()
    }

    override fun setPresenter(presenter: HomePageContract.Presenter) {
        this.presenter = presenter
    }

    private fun init(view: View){
        mainCardView = view.findViewById(R.id.frag_homePage_cv_main)
        firstCardView = view.findViewById(R.id.frag_homePage_cv_first)
        secondCardView = view.findViewById(R.id.frag_homePage_cv_second)
        thirdCardView = view.findViewById(R.id.frag_homePage_cv_third)
        fourthCardView = view.findViewById(R.id.frag_homePage_cv_fourth)

        firstTextView = view.findViewById(R.id.frag_homePage_tv_first)
        secondTextView = view.findViewById(R.id.frag_homePage_tv_second)
        thirdTextView = view.findViewById(R.id.frag_homePage_tv_third)
        fourthTextView = view.findViewById(R.id.frag_homePage_tv_fourth)

    }

    private fun setAttributes(){
        firstCardView.setOnClickListener { openSearchViewWitCityName(firstTextView.text.toString()) }
        secondCardView.setOnClickListener { openSearchViewWitCityName(secondTextView.text.toString()) }
        thirdCardView.setOnClickListener { openSearchViewWitCityName(thirdTextView.text.toString()) }
        fourthCardView.setOnClickListener { openSearchViewWitCityName(fourthTextView.text.toString()) }

        mainCardView.setOnClickListener { openCameraView() }
    }

    private fun openSearchViewWitCityName(cityName: String){
        (activity as HomeActivity).openSearchFragmentWithCityName(cityName)
    }
    private fun openCameraView(){
        (activity as HomeActivity).openCameraFragment()
    }
}