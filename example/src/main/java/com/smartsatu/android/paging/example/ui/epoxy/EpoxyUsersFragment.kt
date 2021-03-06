package com.smartsatu.android.paging.example.ui.epoxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.smartsatu.android.live.Status
import com.smartsatu.android.paging.example.R
import com.smartsatu.android.paging.example.databinding.FragmentUsersBinding
import com.smartsatu.android.paging.example.ui.UsersViewModel
import java.util.concurrent.Executors

class EpoxyUsersFragment : Fragment() {

    private lateinit var binding: FragmentUsersBinding

    private val usersViewModel by lazy {
        ViewModelProviders.of(this).get(UsersViewModel::class.java)
    }

    private val snackBar by lazy {
        Snackbar.make(requireView(), "Подгружаются данные...", Snackbar.LENGTH_INDEFINITE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = usersViewModel
        lifecycle.addObserver(usersViewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usersEpoxyController = UsersEpoxyController()
        binding.usersRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.usersRecycler.adapter = usersEpoxyController.adapter
        with(binding.usersRecycler) {
            addItemDecoration(DividerItemDecoration(view.context, RecyclerView.VERTICAL).apply {
                AppCompatResources.getDrawable(view.context, R.drawable.divider)?.let { setDrawable(it) }
            })
        }
        usersViewModel.users.observe(viewLifecycleOwner, Observer {
            usersEpoxyController.submitList(it)
        })
        usersViewModel.networkState.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.RUNNING -> snackBar.show()
                Status.SUCCESS -> snackBar.dismiss()
                Status.FAILED -> {
                    snackBar.dismiss()
                    val errorSnackBar = Snackbar.make(requireView(), "Error occurred: ${it.message}", Snackbar.LENGTH_INDEFINITE)
                    errorSnackBar.setAction("Refresh") {
                        usersViewModel.refresh()
                        errorSnackBar.dismiss()
                    }
                }
                else -> {
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemClear -> {
                Executors.newSingleThreadExecutor().execute {
                    usersViewModel.cancel()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}