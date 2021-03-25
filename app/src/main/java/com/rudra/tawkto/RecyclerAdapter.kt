package com.rudra.tawkto

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.rudra.tawkto.room.UserEntity
import com.rudra.tawkto.utils.CircleTransform
import com.rudra.tawkto.utils.InvertTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter(var users: ArrayList<UserEntity>) :
    RecyclerView.Adapter<RecyclerAdapter.UserHolder>(), Filterable {
    var usersFilter = ArrayList<UserEntity>()

    init {
        usersFilter = users
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.UserHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return UserHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.UserHolder, position: Int) {
        val itemUser = usersFilter[position]
        holder.bindUser(itemUser, position)
    }

    override fun getItemCount() = usersFilter.size

    class UserHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v
        private var user: UserEntity? = null

        //3
        init {
            v.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
            val context = itemView.context
            val showUserIntent = Intent(context, ProfileActivity::class.java)
            showUserIntent.putExtra("LOGIN", user?.login)
            context.startActivity(showUserIntent)
        }

        fun bindUser(user: UserEntity, index: Int) {
            this.user = user
            Picasso.get()
                .load(user.avatar_url)
                .resize(100, 100)
                .centerCrop()
                .transform(CircleTransform())
                .transform(InvertTransform(index))
                .into(view.userImage)
            view.username.text = user.login
            view.userType.text = user.type
            view.userNotes.visibility = if (user.notes == null) View.GONE else View.VISIBLE
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    usersFilter = users
                } else {
                    val resultList = ArrayList<UserEntity>()
                    var login=""
                    var notes=""
                    for (row in users) {
                        login = row.login!!
                        notes = if(row.notes==null) "" else row.notes!!
                        if (login.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT)) ||
                            notes.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    usersFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = usersFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                usersFilter = results?.values as ArrayList<UserEntity>
                notifyDataSetChanged()
            }

        }
    }
}


