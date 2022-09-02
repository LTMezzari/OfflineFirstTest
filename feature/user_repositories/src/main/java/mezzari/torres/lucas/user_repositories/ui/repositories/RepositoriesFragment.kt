package mezzari.torres.lucas.user_repositories.ui.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mezzari.torres.lucas.commons.generic.BaseFragment
import mezzari.torres.lucas.user_repositories.databinding.FragmentRepositoriesBinding
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class RepositoriesFragment: BaseFragment() {
    private lateinit var binding: FragmentRepositoriesBinding
    private val viewModel: RepositoriesViewModel by inject()
    private val adapter: mezzari.torres.lucas.user_repositories.adapter.RepositoriesAdapter by lazy {
        mezzari.torres.lucas.user_repositories.adapter.RepositoriesAdapter(requireContext())
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
        binding.rvRepositories.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@RepositoriesFragment.adapter
        }

        viewModel.repositories.observe(viewLifecycleOwner) {
            adapter.items = it ?: arrayListOf()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            adapter.isLoading = it ?: false
        }

        viewModel.error.observe(viewLifecycleOwner) {
            val error = it ?: return@observe
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }

        viewModel.getRepositories()
    }
}