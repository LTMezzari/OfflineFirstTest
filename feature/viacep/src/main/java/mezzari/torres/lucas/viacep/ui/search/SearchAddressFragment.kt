package mezzari.torres.lucas.viacep.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import mezzari.torres.lucas.android.generic.BaseFragment
import mezzari.torres.lucas.commons.archive.bindTo
import mezzari.torres.lucas.viacep.R
import mezzari.torres.lucas.viacep.databinding.FragmentSearchAddressBinding
import org.koin.android.ext.android.inject

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
class SearchAddressFragment: BaseFragment() {

    private lateinit var binding: FragmentSearchAddressBinding
    private val viewModel: SearchAddressViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSearchAddressBinding.inflate(inflater, container, false).let {
            binding = it
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pbLoader.bindTo(viewModel.isLoading) {
            binding.btnSearch.text = if (it == true) "" else getString(R.string.action_search)
        }
        binding.etZipCode.bindTo(viewModel.cep)
        binding.btnSearch.setOnClickListener {
            viewModel.searchAddress search@{
                val address = it ?: return@search
                val bundle = Bundle()
                bundle.putSerializable("address", address)
                navigate(R.id.action_searchAddressFragment_to_addressDetailFragment, bundle)
            }
        }
        binding.tvError.bindTo(viewModel.error) {
            binding.tvError.isVisible = it?.trim()?.isEmpty() == false
        }
    }
}