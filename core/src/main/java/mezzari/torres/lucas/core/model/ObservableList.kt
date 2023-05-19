package mezzari.torres.lucas.core.model

import java.lang.IllegalArgumentException

/**
 * @author Lucas T. Mezzari
 * @since 18/05/2023
 */
open class ObservableList<T>(collection: MutableList<T> = arrayListOf()) : MutableList<T> {

    private val mListeners: ArrayList<Listener<T>> = arrayListOf()
    private val mList: MutableList<T> = collection

    constructor(collection: Collection<T>): this(ArrayList(collection))

    override val size: Int
        get() = mList.size

    override fun clear() {
        mList.clear()
        notifyListeners(type = ChangeType.CLEARED)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val index = mList.size
        val result = mList.addAll(elements)
        if (!result)
            return false
        notifyListeners(
            type = ChangeType.RANGE_ADDED,
            index = index,
            list = elements
        )
        return true
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val result = mList.addAll(index, elements)
        if (!result)
            return false
        notifyListeners(
            type = ChangeType.RANGE_ADDED,
            index = index,
            list = elements
        )
        return true
    }

    override fun add(index: Int, element: T) {
        mList.add(index, element)
        notifyListeners(
            type = ChangeType.ITEM_ADDED,
            index = index,
            item = element
        )
    }

    override fun add(element: T): Boolean {
        val index = mList.size
        val result = mList.add(element)
        if (!result)
            return false
        notifyListeners(
            type = ChangeType.ITEM_ADDED,
            index = index,
            item = element
        )
        return true
    }

    override fun removeAt(index: Int): T {
        val result = mList.removeAt(index)
        notifyListeners(
            type = ChangeType.ITEM_REMOVED,
            index = index,
            item = result
        )
        return result
    }

    override fun set(index: Int, element: T): T {
        val result = mList.set(index, element)
        notifyListeners(
            type = ChangeType.ITEM_CHANGED,
            index = index,
            item = element,
            previous = result
        )
        return result
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val removedElements = mList.subtract(elements.toSet())
        val result = mList.retainAll(elements.toSet())
        if (!result)
            return false
        notifyListeners(type = ChangeType.LIST_REMOVED, list = removedElements)
        return true
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val result = mList.removeAll(elements.toSet())
        if (!result)
            return false
        notifyListeners(type = ChangeType.LIST_REMOVED, list = elements)
        return true
    }

    override fun remove(element: T): Boolean {
        val index = mList.indexOf(element)
        val result = mList.remove(element)
        if (!result)
            return false
        notifyListeners(
            type = ChangeType.ITEM_REMOVED,
            index = index,
            item = element
        )
        return true
    }

    override fun lastIndexOf(element: T): Int = mList.lastIndexOf(element)

    override fun indexOf(element: T): Int = mList.indexOf(element)

    override fun containsAll(elements: Collection<T>): Boolean = mList.containsAll(elements)

    override fun contains(element: T): Boolean = mList.contains(element)

    override fun get(index: Int): T = mList[index]

    override fun isEmpty(): Boolean = mList.isEmpty()

    override fun iterator(): MutableIterator<T> = mList.iterator()

    override fun listIterator(): MutableListIterator<T> = mList.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> = mList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        mList.subList(fromIndex, toIndex)

    fun addListener(listener: Listener<T>) {
        if (mListeners.contains(listener))
            return
        mListeners.add(listener)
    }

    fun removeListener(listener: Listener<T>) {
        mListeners.remove(listener)
    }

    fun removeAllListeners() {
        mListeners.clear()
    }

    private fun notifyListeners(
        type: ChangeType,
        index: Int? = null,
        item: T? = null,
        previous: T? = null,
        list: Collection<T>? = null
    ) {
        val block: (Listener<T>) -> Unit =
            getExecutionBlockByType(type, index, item, previous, list)
        mListeners.forEach { listener ->
            block(listener)
            listener.onListChanged(this)
        }
    }

    private fun getExecutionBlockByType(
        type: ChangeType,
        index: Int? = null,
        item: T? = null,
        previous: T? = null,
        list: Collection<T>? = null
    ): (Listener<T>) -> Unit {
        return when (type) {
            ChangeType.ITEM_ADDED -> { listener ->
                if (index == null || item == null)
                    throw IllegalArgumentException("This event should contain a index and an item")
                listener.onItemAdded(this, index, item)
            }

            ChangeType.ITEM_REMOVED -> { listener ->
                if (index == null || item == null)
                    throw IllegalArgumentException("This event should contain a index and an item")
                listener.onItemRemoved(this, index, item)
            }

            ChangeType.ITEM_CHANGED -> { listener ->
                if (index == null || item == null || previous == null)
                    throw IllegalArgumentException("This event should contain a index, an item and a previous value")
                listener.onItemChanged(this, index, item, previous)
            }

            ChangeType.RANGE_ADDED -> { listener ->
                if (index == null || list == null)
                    throw IllegalArgumentException("This event should contain a index and a list")
                listener.onRangeAdded(this, list, index, list.size)
            }

            ChangeType.RANGE_REMOVED -> { listener ->
                if (index == null || list == null)
                    throw IllegalArgumentException("This event should contain a index and a list")
                listener.onRangeRemoved(this, list, index, list.size)
            }

            ChangeType.LIST_REMOVED -> { listener ->
                if (index == null || list == null)
                    throw IllegalArgumentException("This event should contain a list")
                listener.onListRemoved(this, list)
            }

            ChangeType.CLEARED -> {
                { listener ->
                    listener.onListCleared()
                }
            }

            else -> { _ -> }
        }
    }

    interface Listener<T> {
        /**
         * Will be called for any event on the list
         *
         * @param list The updated list
         */
        fun onListChanged(list: ObservableList<T>) {}

        /**
         * Will be called only when a item is added
         *
         * @param list The updated list
         * @param index The index in witch the item was added
         * @param item The added item
         */
        fun onItemAdded(list: ObservableList<T>, index: Int, item: T) {}

        /**
         * Will be called only when a item is removed
         *
         * @param list The updated list
         * @param index The index in witch the item was removed
         * @param item The removed item
         */
        fun onItemRemoved(list: ObservableList<T>, index: Int, item: T) {}

        /**
         * Will be called only when a item is changed
         *
         * @param list The updated list
         * @param index The index in witch the item was changed
         * @param currentItem The new value
         * @param previousItem The previous value
         */
        fun onItemChanged(list: ObservableList<T>, index: Int, currentItem: T, previousItem: T) {}

        /**
         * Will be called only when a list of items is added
         *
         * @param list The updated list
         * @param items The list of items added
         * @param positionStart The position in witch the items were added
         * @param itemCount The size of the list of items added
         */
        fun onRangeAdded(
            list: ObservableList<T>,
            items: Collection<T>,
            positionStart: Int,
            itemCount: Int
        ) {
        }

        /**
         * Will be called only when a list of items is removed
         *
         * @param list The updated list
         * @param items The list of items removed
         * @param positionStart The position in witch the items were removed
         * @param itemCount The size of the list of items removed
         */
        fun onRangeRemoved(
            list: ObservableList<T>,
            items: Collection<T>,
            positionStart: Int,
            itemCount: Int
        ) {
        }

        /**
         * Will be called only when a list of items is removed
         *
         * @param list The updated list
         * @param items The list of items removed
         */
        fun onListRemoved(
            list: ObservableList<T>,
            items: Collection<T>
        ) {
        }

        /**
         * Will be called only when the list is cleared
         */
        fun onListCleared() {}
    }

    enum class ChangeType {
        OTHER,
        ITEM_ADDED,
        ITEM_REMOVED,
        ITEM_CHANGED,
        RANGE_ADDED,
        RANGE_REMOVED,
        LIST_REMOVED,
        CLEARED
    }
}