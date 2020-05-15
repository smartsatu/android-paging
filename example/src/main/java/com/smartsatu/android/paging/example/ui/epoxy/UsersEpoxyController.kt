package com.smartsatu.android.paging.example.ui.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.smartsatu.android.paging.example.ItemUserBindingModel_
import com.smartsatu.android.paging.example.data.local.model.User

class UsersEpoxyController : PagedListEpoxyController<User>() {

    override fun buildItemModel(currentPosition: Int, item: User?): EpoxyModel<*> {
        return item?.let {
            ItemUserBindingModel_()
                    .id("user$currentPosition")
                    .user(item)
        } ?: throw IllegalStateException("User cannot be null in epoxy controller")
    }
}