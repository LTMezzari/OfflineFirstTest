package mezzari.torres.lucas.android.generic

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
abstract class BaseDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return onDialogOverride(dialog)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        onDialogOverride(dialog)
    }

    open fun onDialogOverride(dialog: Dialog): Dialog {
        return dialog
    }

    open fun show(activity: FragmentActivity): BaseDialog {
        val fragmentManager = activity.supportFragmentManager
        show(fragmentManager, this::class.java.name)
        return this
    }
}