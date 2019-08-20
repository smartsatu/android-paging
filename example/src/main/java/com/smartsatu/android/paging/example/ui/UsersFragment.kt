package com.smartsatu.android.paging.example.ui

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
import java.util.concurrent.Executors

class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUsersBinding

    private val usersViewModel by lazy {
        ViewModelProviders.of(this).get(UsersViewModel::class.java)
    }

    private val usersAdapter by lazy {
        UsersAdapter()
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
        with(binding.usersRecycler) {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(view.context, RecyclerView.VERTICAL).apply {
                AppCompatResources.getDrawable(view.context, R.drawable.divider)?.let { setDrawable(it) }
            })
        }
        usersViewModel.users.observe(viewLifecycleOwner, Observer {
            usersAdapter.submitList(it)
        })
        usersViewModel.networkState.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.RUNNING -> snackBar.show()
                Status.SUCCESS -> snackBar.dismiss()
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