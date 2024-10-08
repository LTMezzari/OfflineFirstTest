package mezzari.torres.lucas.commons.recycler.manager

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Lucas T. Mezzari
 * @since 18/05/2023
 */
class LinearLayoutPaginatedManager(
    private val recyclerView: RecyclerView,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(recyclerView.context, orientation, reverseLayout) {

    private val adapter: RecyclerView.Adapter<*>?
        get() = recyclerView.adapter

    var onPaginationListener: OnPaginationListener? = null
    var onEndReachedListener: (() -> Unit)? = null
        set(value) {
            field = value
            onPaginationListener = object: OnPaginationListener {
                override fun onEndReached() {
                    onEndReachedListener?.invoke()
                }
            }
        }

    var paginationOffset: Int = 1

    private val paginationScrollListener: RecyclerView.OnScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (reverseLayout) return
            checkDefaultLayoutPagination()
        }
    }

    init {
        recyclerView.addOnScrollListener(paginationScrollListener)
    }

    fun checkDefaultLayoutPagination() {
        val itemCount = adapter?.itemCount ?: return
        val isEmpty = itemCount <= 0
        val lastVisibleIndex = findLastCompletelyVisibleItemPosition()
        val shouldPaginate = onPaginationListener?.shouldPaginate() ?: true
        if (shouldPaginate && !isEmpty && lastVisibleIndex >= itemCount - paginationOffset) {
            onPaginationListener?.onEndReached()
        }
    }

    interface OnPaginationListener {
        fun onEndReached()

        fun shouldPaginate(): Boolean {
            return true
        }
    }
}