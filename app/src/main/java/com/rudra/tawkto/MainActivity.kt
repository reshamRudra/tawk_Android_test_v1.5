package com.rudra.tawkto

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.rudra.tawkto.retrofit.GithubDataService
import com.rudra.tawkto.retrofit.ServiceBuilder
import com.rudra.tawkto.room.AppDatabase
import com.rudra.tawkto.room.UserDao
import com.rudra.tawkto.room.UserEntity
import com.rudra.tawkto.utils.ConnectivityReceiver
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    //recycler view and adapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter
    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    //database
    private lateinit var appDatabase: AppDatabase
    private lateinit var userDao: UserDao

    private var isLoading: Boolean = false
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //register connectivity receiver
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        // Set up database
        appDatabase = UserMasterApplication.database!!
        userDao = appDatabase.userDao()

        // Set up recycler adapter and listener
        setupRecyclerAdapter()
        setRecyclerViewScrollListener()

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("search", newText)
                adapter.filter.filter(newText)
                return false
            }

        })

    }

    private fun setupRecyclerAdapter() {
        linearLayoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.layoutManager = linearLayoutManager
        adapter = RecyclerAdapter(arrayListOf())
        recyclerView.adapter = adapter
    }


    private fun loadUsers(since: String) {

        val destinationService = ServiceBuilder.buildService(GithubDataService::class.java)
        val requestCall = destinationService.getUsersList(since)

        if (isInternetAvailable(this@MainActivity)) requestCall.enqueue(object :
            Callback<ArrayList<UserEntity>> {
            override fun onResponse(
                call: Call<ArrayList<UserEntity>>,
                response: Response<ArrayList<UserEntity>>
            ) {
                if (response.isSuccessful) {
                    AsyncTask.execute({
                        userDao.upsert(response.body()!!)
                        runOnUiThread {
                            isLoading = false
                            progressBar.visibility = View.GONE
                        }
                    })

                } else {
                    Log.d("!isSuccessful", "Something went wrong ${response.message()}")
                    isLoading = false
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        "Something went wrong.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<UserEntity>>, t: Throwable) {
                Log.d("onFailure", "Something went wrong $t")
                isLoading = false
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Something went wrong.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (!isLoading && totalItemCount == lastVisibleItemPosition + 1) {
                    loadUsers((adapter.users[lastVisibleItemPosition].id).toString())
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this

        // fetch data from API
        loadUsers("0")

        userDao.getAll().observe(this@MainActivity, object : Observer<List<UserEntity>> {
            override fun onChanged(images: List<UserEntity>?) {
                // update the UI
                adapter.users.clear()
                adapter.users.addAll(images!!)
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            //show sanck bar
            snackBar = Snackbar.make(
                recyclerView,
                "No Internet Connection Available!",
                Snackbar.LENGTH_LONG
            )
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            //dismiss snack bar and fetch data from API.
            snackBar?.dismiss()
            loadUsers("0")
        }
    }

}