package mezzari.torres.lucas.android.widgets.recycler.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import mezzari.torres.lucas.core.model.ObservableList

/**
 * @author Lucas T. Mezzari
 * @since 18/05/2023
 */
abstract class PaginatedAdapter<T, VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {
    open val listener: ObservableList.Listener<T> by lazy {
        object: ObservableList.Listener<T> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onListCleared() {
                notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onListRemoved(list: ObservableList<T>, items: Collection<T>) {
                notifyDataSetChanged()
            }

            override fun onItemAdded(list: ObservableList<T>, index: Int, item: T) {
                notifyItemInserted(index)
            }

            override fun onItemChanged(
                list: ObservableList<T>,
                index: Int,
                currentItem: T,
                previousItem: T
            ) {
                notifyItemChanged(index)
            }

            override fun onItemRemoved(list: ObservableList<T>, index: Int, item: T) {
                notifyItemRemoved(index)
            }

            override fun onRangeAdded(
                list: ObservableList<T>,
                items: Collection<T>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onRangeRemoved(
                list: ObservableList<T>,
                items: Collection<T>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        }
    }

    var observableList: ObservableList<T> = ObservableList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field.removeListener(listener)
            field = value
            field.addListener(listener)
            notifyDataSetChanged()
        }

    init {
        observableList.removeAllListeners()
        observableList.addListener(listener)
    }
}