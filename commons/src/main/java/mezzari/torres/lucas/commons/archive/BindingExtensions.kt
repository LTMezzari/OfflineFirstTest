package mezzari.torres.lucas.commons.archive

import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
fun EditText.addAfterTextChangedListener(listener: (Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            listener(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.bindTo(
    property: MutableLiveData<String>,
    owner: LifecycleOwner = context as LifecycleOwner,
    observer: ((String?) -> Unit)? = null
) {
    addAfterTextChangedListener { editable ->
        if (property.value != editable.toString()) {
            property.value = editable.toString()
            observer?.invoke(property.value)
        }
    }
    property.observe(owner) { value ->
        if (property.value != this.text.toString()) {
            setText(value)
            observer?.invoke(value)
        }
    }
}

fun SwitchCompat.bindTo(
    property: MutableLiveData<Boolean>,
    owner: LifecycleOwner = context as LifecycleOwner,
    observer: ((Boolean?) -> Unit)? = null
) {
    setOnCheckedChangeListener { _, isChecked ->
        if (property.value != isChecked) {
            property.value = isChecked
            observer?.invoke(property.value)
        }
    }
    property.observe(owner) {
        if (it != isChecked) {
            isChecked = it ?: false
            observer?.invoke(it)
        }
    }
}

fun CheckBox.bindTo(
    property: MutableLiveData<Boolean>,
    owner: LifecycleOwner = context as LifecycleOwner,
    observer: ((Boolean?) -> Unit)? = null
) {
    setOnCheckedChangeListener { _, isChecked ->
        if (property.value != isChecked) {
            property.value = isChecked
            observer?.invoke(property.value)
        }
    }
    property.observe(owner) {
        if (it != isChecked) {
            isChecked = it ?: false
            observer?.invoke(it)
        }
    }
}

fun TextView.bindTo(
    property: LiveData<String>,
    owner: LifecycleOwner = context as LifecycleOwner,
    observer: ((String?) -> Unit)? = null
) {
    property.observe(owner) {
        this@bindTo.text = it
        observer?.invoke(it)
    }
}

fun ProgressBar.bindTo(
    property: LiveData<Boolean>,
    owner: LifecycleOwner = context as LifecycleOwner,
    observer: ((Boolean?) -> Unit)? = null
) {
    property.observe(owner) {
        this@bindTo.isVisible = it == true
        observer?.invoke(it)
    }
}

fun Button.bindTo(
    property: LiveData<Boolean>,
    owner: LifecycleOwner = context as LifecycleOwner,
    observer: ((Boolean?) -> Unit)? = null
) {
    property.observe(owner) {
        this@bindTo.isEnabled = it
        observer?.invoke(it)
    }
}