package com.smartsatu.android.paging.example.ui.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.smartsatu.android.paging.example.R
import com.smartsatu.android.paging.example.databinding.ItemUserBinding

@EpoxyModelClass(layout = R.layout.item_user)
abstract class UserEpoxyModel : EpoxyModelWithHolder<UserEpoxyModel.Holder>() {

    @EpoxyAttribute
    lateinit var firstName: String
    @EpoxyAttribute
    lateinit var lastName: String

    override fun bind(holder: Holder) {
        holder.userBinding?.text1?.text = firstName
        holder.userBinding?.text1?.text = lastName
    }

    class Holder : EpoxyHolder() {

        var userBinding: ItemUserBinding? = null

        override fun bindView(itemView: View) {
            userBinding = ItemUserBinding.bind(itemView)
        }
    }
}