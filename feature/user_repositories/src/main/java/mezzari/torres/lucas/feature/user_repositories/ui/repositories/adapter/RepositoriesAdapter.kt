package mezzari.torres.lucas.feature.user_repositories.ui.repositories.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mezzari.torres.lucas.commons.recycler.adapter.PaginatedAdapter
import mezzari.torres.lucas.core.model.bo.Repository
import mezzari.torres.lucas.feature.user_repositories.R
import mezzari.torres.lucas.feature.user_repositories.databinding.RowRepositoryBinding

/**
 * @author Lucas T. Mezzari
 * @since 31/08/2022
 */
class RepositoriesAdapter(context: Context) : PaginatedAdapter<Repository, RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    val items: List<Repository>
        get() = observableList

    var isLoading: Boolean = true
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onRepositoryClick: ((Repository) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                RepositoriesViewHolder(RowRepositoryBinding.inflate(inflater, parent, false))
            }

            VIEW_TYPE_EMPTY -> {
                DefaultViewHolder(inflater.inflate(R.layout.row_empty, parent, false))
            }

            else -> {
                DefaultViewHolder(inflater.inflate(R.layout.row_loading, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        var size = items.size
        if (isLoading)
            size++
        return if (observableList.isEmpty()) {
            1
        } else {
            size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading && position >= items.size -> {
                VIEW_TYPE_LOADING
            }
            items.isEmpty() -> {
                VIEW_TYPE_EMPTY
            }
            else -> {
                VIEW_TYPE_ITEM
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ITEM -> {
                (holder as RepositoriesViewHolder).also {
                    val repository = items[position]
                    it.binding.tvRepositoryName.text = repository.name
                    it.binding.tvRepositoryDescription.text = repository.description
                    it.binding.tvRepositoryLanguage.text = repository.language
                    it.binding.tvRepositoryStars.text = repository.stars.toString()
                    it.itemView.setOnClickListener {
                        onRepositoryClick?.invoke(repository)
                    }
                }
            }

            VIEW_TYPE_EMPTY -> {
                val view = (holder as DefaultViewHolder).itemView
                val tvEmptyMessage = view.findViewById<TextView>(R.id.tvEmptyMessage)
                tvEmptyMessage.setText(R.string.message_empty_repositories)
            }
        }
    }

    class RepositoriesViewHolder(val binding: RowRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_EMPTY = 1
        private const val VIEW_TYPE_ITEM = 2
    }
}