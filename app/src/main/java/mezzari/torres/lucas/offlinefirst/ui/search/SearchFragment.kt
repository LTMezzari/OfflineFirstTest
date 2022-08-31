package mezzari.torres.lucas.offlinefirst.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import mezzari.torres.lucas.offlinefirst.R
import mezzari.torres.lucas.offlinefirst.archive.bindTo
import mezzari.torres.lucas.offlinefirst.databinding.FragmentSearchBinding
import mezzari.torres.lucas.offlinefirst.generic.BaseFragment
import mezzari.torres.lucas.offlinefirst.model.User
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 30/08/2022
 */
class SearchFragment : BaseFragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSearchBinding.inflate(inflater, container, false).let {
            binding = it
            return@let it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etSearch.bindTo(viewModel.search, viewLifecycleOwner)
        binding.btnSearch.bindTo(viewModel.isValid, viewLifecycleOwner)
        binding.pbLoading.bindTo(viewModel.isLoading, viewLifecycleOwner) {
            binding.btnSearch.text = if (it == true) "" else getString(R.string.action_search)
        }
        binding.tvErrorMessage.bindTo(viewModel.error, viewLifecycleOwner) {
            binding.tvErrorMessage.isVisible = !it.isNullOrEmpty() && it.isNotBlank()
        }

        binding.btnSearch.setOnClickListener {
            viewModel.getUser callback@{
                if (it == null)
                    return@callback
                navController.navigate(R.id.action_searchFragment_to_repositoriesFragment)
            }
        }
    }
}