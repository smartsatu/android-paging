package com.smartsatu.android.paging.example.ui.util

import android.view.View
import androidx.databinding.BindingConversion

@BindingConversion
fun convertBooleanToVisibility(visible: Boolean): Int = if (visible) View.VISIBLE else View.GONE