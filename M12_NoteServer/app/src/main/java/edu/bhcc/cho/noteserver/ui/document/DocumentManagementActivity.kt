package edu.bhcc.cho.noteserver.ui.document

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import edu.bhcc.cho.noteserver.R
import kotlinx.android.synthetic.main.activity_document_management.*

class DocumentManagementActivity : AppCompatActivity() {
    private val tabTitles = listOf("My Files", "Files Shared With Me")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_management)

        viewPager.adapter = DocumentPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}