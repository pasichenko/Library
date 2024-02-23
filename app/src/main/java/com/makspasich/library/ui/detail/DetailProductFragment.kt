package com.makspasich.library.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makspasich.library.R
import com.makspasich.library.databinding.DetailProductFragmentBinding
import com.makspasich.library.screens.detail.DetailProductScreenContent
import com.makspasich.library.theme.LibraryTheme

class DetailProductFragment : Fragment() {

    private lateinit var binding: DetailProductFragmentBinding
    private val args: DetailProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailProductFragmentBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            LibraryTheme {
                val viewModel: DetailProductViewModel = viewModel()
                val viewState by viewModel.state.collectAsStateWithLifecycle()
                Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    DetailProductScreenContent(
                        modifier = Modifier.padding(16.dp),
                        product = viewState.product,
                        onStateChange = viewModel::onStateChanged
                    )
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editFab.setOnClickListener {
            val action = DetailProductFragmentDirections
                .actionDetailProductFragmentToAddEditProductFragment(
                    args.productId,
                    false,
                    getString(R.string.edit_product)
                )
            findNavController().navigate(action)
        }
    }
}
