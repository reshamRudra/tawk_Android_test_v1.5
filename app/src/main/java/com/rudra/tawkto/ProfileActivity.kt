package com.rudra.tawkto

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.rudra.tawkto.retrofit.GithubDataService
import com.rudra.tawkto.retrofit.ServiceBuilder
import com.rudra.tawkto.room.AppDatabase
import com.rudra.tawkto.room.UserDao
import com.rudra.tawkto.room.UserEntity
import com.rudra.tawkto.utils.ConnectivityReceiver
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    //database
    private lateinit var userDao: UserDao
    private lateinit var appDatabase: AppDatabase

    private lateinit var userProfile: UserEntity
    private lateinit var login: String
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //register connectivity receiver
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        //get login username from intent
        login = intent.getStringExtra("LOGIN")!!

        // Set up database
        appDatabase = UserMasterApplication.database!!
        userDao = appDatabase.userDao()

        //fetch data from API
        loadProfile(login)

        //save note event listener
        save?.setOnClickListener {
            userProfile.notes = editTextTextMultiLine.text.toString()
            AsyncTask.execute({
                userDao.upsert(userProfile)
                runOnUiThread {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Note saved!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }

    private fun loadProfile(login: String) {

        val destinationService = ServiceBuilder.buildService(GithubDataService::class.java)
        val requestCall = destinationService.getUserProfile(login)

        if (isInternetAvailable(this@ProfileActivity)) requestCall.enqueue(object :
            Callback<UserEntity> {
            override fun onResponse(
                call: Call<UserEntity>,
                response: Response<UserEntity>
            ) {
                if (response.isSuccessful) {
                    AsyncTask.execute({

                        val user = userDao.getAllByLoginId(userProfile.login!!)
                        if (user.size > 0) {
                            response.body()!!.notes = user[0].notes
                        }
                        userProfile = response.body()!!
                        userDao.upsert(response.body()!!)
                        runOnUiThread {
                            updateUI()
                            Log.d("Profile", "Response" + response.body())
                        }
                    })

                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Something went wrong ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserEntity>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Something went wrong $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        AsyncTask.execute({
            val userProfile = userDao.getAllByLoginId(login)
            if (userProfile.size > 0) {
                this.userProfile = userProfile[0]
            }
            runOnUiThread { updateUI() }
        })
    }

    private fun updateUI() {
        Picasso.get()
            .load(userProfile.avatar_url)
            .into(userImage)
        userFollowers.text =
            "Followers: ${userProfile.followers.toString()}"
        userFollowings.text =
            "Followings: ${userProfile.following.toString()}"

        tvName.text = userProfile.name
        tvCompany.text = userProfile.company
        tvBlog.text = userProfile.blog
        tvLocation.text = userProfile.location
        tvRepos.text = userProfile.public_repos.toString()
        editTextTextMultiLine.setText(userProfile.notes)

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            //show sanck bar
            snackBar = Snackbar.make(
                save,
                "No Internet Connection Available!",
                Snackbar.LENGTH_LONG
            )
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            //dismiss snack bar and fetch data from API.
            snackBar?.dismiss()
            loadProfile(login)
        }
    }
}