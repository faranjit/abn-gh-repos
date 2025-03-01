package com.faranjit.ghrepos.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.faranjit.ghrepos.databinding.FragmentRepoDetailBinding

class RepoDetailFragment : Fragment() {

    private var _binding: FragmentRepoDetailBinding? = null
    private val binding get() = _binding!!

    private val args: RepoDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = args.repo
        (requireActivity() as AppCompatActivity).supportActionBar?.title = repo.name

        binding.txtRepoName.text = repo.name
        binding.txtRepoFullName.text = repo.fullName
        binding.txtRepoDescription.text = repo.description
        binding.txtRepoVisibility.text = repo.visibility.visibility
        binding.imgRepoVisibility.setImageResource(repo.visibility.imageId)

        Glide.with(binding.imgOwner.context)
            .load(repo.owner.avatarUrl)
            .into(binding.imgOwner)

        binding.btnOpenInBrowser.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}