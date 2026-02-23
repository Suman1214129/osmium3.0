package com.osmiumai.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.osmiumai.app.databinding.FragmentWelcomePageBinding

class WelcomePageFragment : Fragment() {

    private var _binding: FragmentWelcomePageBinding? = null
    private val binding get() = _binding!!
    private var pagePosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagePosition = arguments?.getInt(ARG_POSITION) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWelcomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        when (pagePosition) {
            0 -> {
                binding.tvTitle.text = "Learn Anything. Learn Intelligently."
                binding.tvSubtitle.text = "From code to chemistry - Osmium finds what works best for you, and helps you master it."
                binding.ivImage.setImageResource(R.drawable.welcom1)
                binding.btnContinue.visibility = View.VISIBLE
                binding.buttonContainer.visibility = View.GONE
                binding.tvTerms.visibility = View.GONE
            }
            1 -> {
                binding.tvTitle.text = "Mock Tests That Actually Matters"
                binding.tvSubtitle.text = "Experience AI-powered mocks built to mirror real exam trends."
                binding.ivImage.setImageResource(R.drawable.welcom2)
                binding.btnContinue.visibility = View.VISIBLE
                binding.buttonContainer.visibility = View.GONE
                binding.tvTerms.visibility = View.GONE
            }
            2 -> {
                binding.tvTitle.text = "Real-Time Analytics, Real Progress"
                binding.tvSubtitle.text = "Osmium tracks your performance and turns weaknesses into strengths with smart insights"
                binding.ivImage.setImageResource(R.drawable.welcom3)
                binding.btnContinue.visibility = View.VISIBLE
                binding.buttonContainer.visibility = View.GONE
                binding.tvTerms.visibility = View.GONE
            }
            3 -> {
                binding.tvTitle.text = "Designed Around How You Learn."
                binding.tvSubtitle.text = "Your journey to mastery starts now. Let Osmium be your guide"
                binding.ivImage.setImageResource(R.drawable.welcom4)
                binding.btnContinue.visibility = View.GONE
                binding.buttonContainer.visibility = View.VISIBLE
                binding.tvTerms.visibility = View.VISIBLE
            }
        }
        
        binding.btnContinue.setOnClickListener {
            (activity as? WelcomeActivity)?.nextPage()
        }
        
        binding.btnGoogle.setOnClickListener {
            startActivity(Intent(requireContext(), SignupActivity::class.java))
        }
        
        binding.btnPhone.setOnClickListener {
            startActivity(Intent(requireContext(), SignupActivity::class.java))
        }
        
        binding.btnEmail.setOnClickListener {
            startActivity(Intent(requireContext(), SignupEmailActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_POSITION = "position"
        
        fun newInstance(position: Int) = WelcomePageFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_POSITION, position)
            }
        }
    }
}
