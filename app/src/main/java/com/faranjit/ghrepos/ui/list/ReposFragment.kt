package com.faranjit.ghrepos.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.faranjit.ghrepos.R
import com.faranjit.ghrepos.databinding.FragmentReposBinding
import com.faranjit.ghrepos.domain.model.Repo
import com.faranjit.ghrepos.ui.ReposUiState
import com.faranjit.ghrepos.ui.list.adapter.RepoAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReposFragment : Fragment() {

    private var _binding: FragmentReposBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReposViewModel by viewModels()
    private val repoAdapter = RepoAdapter()

    private val idlingResource = ReposIdlingResource()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReposBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapterLoadStateListener()
        setupRecyclerView()
        setupRetryButton()
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.recyclerRepos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = repoAdapter
        }
    }

    private fun setupAdapterLoadStateListener() {
        repoAdapter.addLoadStateListener { loadStates ->
            viewModel.handleLoadStates(loadStates)
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.fetchRepos()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        ReposUiState.Loading -> showLoading()
                        is ReposUiState.Success -> showRepos(state.pagingData)
                        is ReposUiState.Error -> showError(state)
                        ReposUiState.ConnectionRestored -> showConnectionRestored()
                        else -> {}
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            recyclerRepos.isVisible = false
            errorGroup.isVisible = false
        }
    }

    private fun showRepos(pagingData: PagingData<Repo>) {
        binding.apply {
            progressBar.isVisible = false
            recyclerRepos.isVisible = true
            errorGroup.isVisible = false

            idlingResource.setIdle(false)
            repoAdapter.submitData(lifecycle, pagingData)
            idlingResource.setIdle(true)
        }
    }

    private fun showError(error: ReposUiState.Error) {
        binding.apply {
            progressBar.isVisible = false
            recyclerRepos.isVisible = false
            errorGroup.isVisible = true
            txtError.text = error.message
            btnRetry.isVisible = error.showRetry
        }
    }

    private fun showConnectionRestored() {
        Snackbar.make(
            binding.root,
            getString(R.string.connection_restored),
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(getString(R.string.btn_refresh)) {
                viewModel.fetchRepos()
            }
            setActionTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_blue_light
                )
            )
        }.show()
    }
}
