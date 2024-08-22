package mezzari.torres.lucas.feature.viacep.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mezzari.torres.lucas.android.generic.BaseFragment
import mezzari.torres.lucas.commons.archive.bindTo
import mezzari.torres.lucas.core.model.bo.Address
import mezzari.torres.lucas.feature.viacep.databinding.FragmentAddressDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author Lucas T. Mezzari
 * @since 31/05/2023
 */
class AddressDetailFragment: BaseFragment() {
    private lateinit var binding: FragmentAddressDetailBinding
    private val viewModel: AddressDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val address = findArgument<Address>("address") ?: return
        viewModel.address.postValue(address)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAddressDetailBinding.inflate(inflater, container, false).let {
            binding = it
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etZipCode.bindTo(viewModel.zipCode)
        binding.etStreet.bindTo(viewModel.street)
        binding.etNeighborhood.bindTo(viewModel.neighborhood)
        binding.etCity.bindTo(viewModel.city)
        binding.etState.bindTo(viewModel.state)
    }
}