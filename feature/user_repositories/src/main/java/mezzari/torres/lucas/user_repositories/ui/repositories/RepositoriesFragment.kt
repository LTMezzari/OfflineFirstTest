package mezzari.torres.lucas.user_repositories.ui.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import mezzari.torres.lucas.android.generic.BaseFragment
import mezzari.torres.lucas.android.widgets.recycler.manager.LinearLayoutPaginatedManager
import mezzari.torres.lucas.core.model.ObservableList
import mezzari.torres.lucas.user_repositories.R
import mezzari.torres.lucas.user_repositories.adapter.RepositoriesAdapter
import mezzari.torres.lucas.user_repositories.databinding.FragmentRepositoriesBinding
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class RepositoriesFragment : BaseFragment() {
    private lateinit var binding: FragmentRepositoriesBinding
    private val viewModel: RepositoriesViewModel by inject()
    private val adapter: RepositoriesAdapter by lazy {
        RepositoriesAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRepositoriesBinding.inflate(inflater, container, false).let {
            binding = it
            return@let binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.srlRepositories.setOnRefreshListener {
            viewModel.isLoadingMore.value = false
            viewModel.getRepositories(0)
        }

        binding.rvRepositories.apply {
            layoutManager = LinearLayoutPaginatedManager(this, RecyclerView.VERTICAL, false).apply {
                onPaginationListener = object: LinearLayoutPaginatedManager.OnPaginationListener {
                    override fun onEndReached() {
                        val page = viewModel.page.value ?: 0
                        viewModel.isLoadingMore.value = true
                        viewModel.getRepositories(page + 1)
                    }

                    override fun shouldPaginate(): Boolean {
                        return viewModel.shouldLoadMore.value == true
                                && viewModel.isLoading.value == false
                    }
                }
            }
            adapter = this@RepositoriesFragment.adapter
        }

        viewModel.paginatedList.observe(viewLifecycleOwner) {
            adapter.observableList = it ?: ObservableList()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            adapter.isLoading = it ?: false
            if (it == false)
                binding.srlRepositories.isRefreshing = false
        }

        viewModel.error.observe(viewLifecycleOwner) {
            val error = it ?: return@observe
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }

        viewModel.isOutdated.observe(viewLifecycleOwner) {
            if (it == false)
                return@observe
            Toast.makeText(requireContext(), R.string.message_outdated, Toast.LENGTH_LONG).show()
        }

        viewModel.getRepositories(0)
    }
}