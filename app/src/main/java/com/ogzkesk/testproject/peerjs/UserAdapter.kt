package com.ogzkesk.testproject.peerjs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ogzkesk.testproject.R
import com.ogzkesk.testproject.databinding.UserItemBinding
import com.ogzkesk.testproject.peerjs.user.Call
import com.ogzkesk.testproject.peerjs.user.User

class UserAdapter() : ListAdapter<User,UserAdapter.Holder>(Differ()){

    private var onClick: ((User) -> Unit)? = null

    inner class Holder(private val binding: UserItemBinding) : ViewHolder(binding.root){
        fun bind(user: User){
            println("Holder.bind() :: $user")
            binding.run {
                btnStartCall.setOnClickListener { onClick?.invoke(user) }
                tvName.text = user.displayName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserItemBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnClickListener(onClick: (User) -> Unit) {
        this.onClick = onClick
    }

    class Differ : ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return true
        }
    }
}