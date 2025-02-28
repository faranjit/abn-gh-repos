package com.faranjit.ghrepos.ui.list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.faranjit.ghrepos.databinding.ListItemRepoBinding
import com.faranjit.ghrepos.domain.model.Repo

class RepoAdapter : PagingDataAdapter<Repo, RepoAdapter.RepoViewHolder>(repoItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder(
            ListItemRepoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("ReposFragment", "onBindViewHolder: position=$position, item=$item")
        item?.let { holder.bind(it) }
    }

    class RepoViewHolder internal constructor(
        private val binding: ListItemRepoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) {
            binding.txtRepoName.text = repo.name
        }
    }

    companion object {
        private val repoItemCallback = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }
        }
    }
}