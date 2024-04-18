package edu.utap.movie_app;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import edu.utap.movie_app.MainActivity
import edu.utap.movie_app.R
import edu.utap.movie_app.databinding.FragmentOnePostBinding
import edu.utap.movie_app.glide.Glide

class MovieDetailFragment: Fragment() {
//    // XXX initialize viewModel
//    private var _binding: FragmentOnePostBinding? = null
//    // This property is only valid between onCreateView and onDestroyView.
//    private val binding get() = _binding!!
//    private val viewModel: MainViewModel by activityViewModels()
//    private val args: OnePostFragmentArgs by navArgs()
//
//    override fun onCreateView(
//            inflater: LayoutInflater,
//            container: ViewGroup?,
//            savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentOnePostBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val post = args.post
//        //set the title bar name
//
////        viewModel.setTitle("One Post")
//
//        binding.apply {
//            // Set the post title
//            onePostTitle.text = post.title
//            onePostSelfText.text = post.selfText
//
//            // Load the post image using Glide
//            if (!post.imageURL.isNullOrEmpty()) {
//                onePostImage.visibility = View.VISIBLE
//                Glide.glideFetch(post.imageURL, post.thumbnailURL, onePostImage)
//            } else {
//                onePostImage.visibility = View.GONE
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}