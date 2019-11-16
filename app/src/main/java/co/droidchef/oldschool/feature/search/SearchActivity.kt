package co.droidchef.oldschool.feature.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.droidchef.oldschool.network.NetworkRequestManager
import co.droidchef.oldschool.R
import co.droidchef.oldschool.feature.search.view.SearchFragment

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SearchFragment.newInstance())
                .commitNow()
        }
    }

}
