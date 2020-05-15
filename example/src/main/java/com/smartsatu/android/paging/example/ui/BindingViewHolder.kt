package com.smartsatu.android.paging.example.ui

import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

open class BindingViewHolder<T : ViewDataBinding> : RecyclerView.ViewHolder, View.OnClickListener, View.OnLongClickListener {

    val binding: T

    private var clickListener: OnClickListener<T>? = null
    private var longClickListener: OnClickListener<T>? = null

    constructor(viewDataBinding: T) : super(viewDataBinding.root) {
        binding = viewDataBinding
    }

    constructor(itemView: View) : super(itemView) {
        binding = DataBindingUtil.getBinding<T>(itemView)
                ?: DataBindingUtil.findBinding(itemView)
                        ?: throw IllegalStateException("View's xml does not have root <layout> tag")
    }

    protected inline fun <reified V : ViewDataBinding> requireBinding(): V {
        return binding as V
    }

    /**
     * Bind data object @{code }to XML layout
     *
     * @param variableId from xml layout variable tag
     * @param variable data passed to xml through the variable named as generated variableId
     */
    fun bind(variableId: Int, variable: Any?) {
        binding.setVariable(variableId, variable)
        binding.executePendingBindings()
    }

    fun setOnClickListener(listener: OnClickListener<T>, @IdRes vararg ids: Int) {
        this.clickListener = listener
        if (ids.isNotEmpty()) {
            for (id in ids) {
                itemView.findViewById<View?>(id)?.setOnClickListener(this)
                        ?: Timber.w("Children is null")
                itemView.findViewById<View?>(id)?.setOnLongClickListener(this)
                        ?: Timber.w("Children is null")
            }
        } else {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        val view = v ?: throw IllegalArgumentException("Target view must not be null")
        clickListener?.onClick(view, this)
    }

    override fun onLongClick(v: View?): Boolean {
        val view = v ?: throw IllegalArgumentException("Target view must not be null")
        return clickListener?.onLongClick(view, this) ?: false
    }

    interface OnClickListener<T : ViewDataBinding> {

        fun onClick(view: View, holder: BindingViewHolder<T>)

        fun onLongClick(view: View, holder: BindingViewHolder<T>): Boolean = false
    }
}
