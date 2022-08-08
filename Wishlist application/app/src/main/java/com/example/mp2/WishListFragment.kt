package com.example.mp2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mp2.databinding.FragmentWishListBinding
/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class WishListFragment : Fragment(), IWishClickListener {

    private var _binding: FragmentWishListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: WishAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWishListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = WishAdapter(this).apply {
            replace()
        }
        binding.newWish.setOnClickListener { view ->
            findNavController().navigate(R.id.wishList_wishAdd)
        }
        binding.wishes.let{
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onWishClick(id: Int) {
        findNavController().navigate(R.id.wishList_wishAdd, bundleOf("wishID" to id, "isEdit" to true))
    }
}