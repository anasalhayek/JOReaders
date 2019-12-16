package com.example.prototype2_0

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prototype2_0.mainActivity.*
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.post_look.*

@Suppress("NAME_SHADOWING")
class HomeActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_nav)
        }
        actionbar!!.title = ""

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.screen_area, PostsPage())
            .addToBackStack("tag").commit()

        val sharedPreference = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val user_name = sharedPreference.getString("user_name", "")
        val user_email = sharedPreference.getString("user_email", "")
        val user_image = sharedPreference.getString("user_image", "")
        val user_un = sharedPreference.getString("user_un", "")
        val user_role = sharedPreference.getString("user_role", "")

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val userName: TextView = headerView.findViewById(R.id.user_name_header)
        val userEmail: TextView = headerView.findViewById(R.id.user_email_header)
        val userUn: TextView = headerView.findViewById(R.id.user_un_header)
        val userImage: CircleImageView = headerView.findViewById(R.id.profile_photo_nav)
//        Toast.makeText(this, user_image, Toast.LENGTH_LONG).show()

        userName.text = user_name
        userEmail.text = user_email
        userUn.text = user_un

        if (user_image!="null")
            Picasso.get().load("https://library123456.000webhostapp.com/images/$user_image").into(userImage)
        headerView.setOnClickListener {
            fragmentManager.beginTransaction().replace(R.id.screen_area, ProfilePage())
                .addToBackStack("tag")
                .commit()
        }

        if (user_role == "1") {
            nav_view.menu.findItem(R.id.librarian).isVisible = true
        }
        if (user_role == "2") {
            nav_view.menu.findItem(R.id.nav_users_feedback).isVisible = true
            nav_view.menu.findItem(R.id.librarian).isVisible = true
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

//        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                R.id.nav_catalogue_aabu -> {
                    val bundel = Bundle()
                    bundel.putString("libraryId", "1")

                    val nextFragment = BooksCatalogue()
                    nextFragment.arguments = bundel

                    val fragmentManager = (this as AppCompatActivity).supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                        .addToBackStack("tag").commit()
                }
                R.id.nav_catalogue_ju -> {
                    val bundel = Bundle()
                    bundel.putString("libraryId", "2")

                    val nextFragment = BooksCatalogue()
                    nextFragment.arguments = bundel

                    val fragmentManager = (this as AppCompatActivity).supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                        .addToBackStack("tag").commit()
                }
                R.id.nav_catalogue_yu -> {
                    val bundel = Bundle()
                    bundel.putString("libraryId", "3")

                    val nextFragment = BooksCatalogue()
                    nextFragment.arguments = bundel

                    val fragmentManager = (this as AppCompatActivity).supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.screen_area, nextFragment)
                        .addToBackStack("tag").commit()
                }
                R.id.nav_posts -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, PostsPage())
                        .addToBackStack("tag")
                        .commit()
                }

                R.id.nav_profile -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, ProfilePage())
                        .addToBackStack("tag")
                        .commit()
                }
                R.id.nav_add_book -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, AddBook())
                        .addToBackStack("tag")
                        .commit()
                }
                R.id.nav_borrow -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, BorrowRequests())
                        .addToBackStack("tag")
                        .commit()
                }
                R.id.nav_borrowed -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, BorrowedBooks())
                        .addToBackStack("tag")
                        .commit()
                }
                R.id.nav_users_feedback -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, UsersFeedback())
                        .addToBackStack("tag")
                        .commit()
                }

                R.id.nav_about -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, About())
                        .addToBackStack("tag")
                        .commit()
                }

                R.id.nav_add_feedback -> {
                    fragmentManager.beginTransaction().replace(R.id.screen_area, AddFeedback())
                        .addToBackStack("tag")
                        .commit()
                }

                R.id.nav_share -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "This is a test from Jo readers to share.")
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }

                R.id.nav_logout -> {
                    LoginManager.getInstance().logOut()
                    val sharedPreference = getSharedPreferences(
                        "myPrefs",
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreference.edit()
                    editor.putString("user_id", null)
                    editor.putString("user_name", null)
                    editor.apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

    }


    //appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }
}

//onBoarding