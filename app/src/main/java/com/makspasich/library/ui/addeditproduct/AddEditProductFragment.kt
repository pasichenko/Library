package com.makspasich.library.ui.addeditproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makspasich.library.databinding.AddEditProductFragmentBinding
import com.makspasich.library.screens.edit.EditProductScreenContent
import com.makspasich.library.screens.edit.tag_dialog.CreateTagDialog
import com.makspasich.library.theme.LibraryTheme


class AddEditProductFragment : Fragment() {

    private lateinit var binding: AddEditProductFragmentBinding
    private val viewModel: AddEditProductViewModel by viewModels()
    private val args: AddEditProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEditProductFragmentBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            LibraryTheme {
                Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    val viewModel: AddEditProductViewModel = viewModel()
                    val viewState by viewModel.state.collectAsStateWithLifecycle()
                    var showDialog by remember { mutableStateOf(false) }

                    if (showDialog) {
                        CreateTagDialog(
                            onDismissRequest = { showDialog = false },
                            onConfirmation = { showDialog = false }
                        )
                    }
                    if (viewState.loading) {
                        CircularProgressIndicator()
                    } else {
                        EditProductScreenContent(
                            modifier = Modifier.padding(16.dp),
                            product = viewState.product,
                            tags = viewState.tags,
                            onNameChange = viewModel::onNameChange,
                            onTimestampChange = viewModel::onTimestampChange,
                            onExpiredTimestampChange = viewModel::onExpiredTimestampChange,
                            onSizeChange = viewModel::onSizeChange,
                            onTagChange = viewModel::addOrRemoveTag,
                            onClickAddTag = {
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveFab.setOnClickListener {
            viewModel.saveProduct {
                val action = AddEditProductFragmentDirections
                    .actionAddEditProductFragmentToDetailProductFragment(args.productId)
                findNavController().navigate(action)
            }
        }
    }
}