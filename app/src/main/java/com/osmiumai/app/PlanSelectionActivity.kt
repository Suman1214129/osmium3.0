package com.osmiumai.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.osmiumai.app.databinding.ActivityPlanSelectionBinding
import com.osmiumai.app.ui.settings.PlanData
import com.osmiumai.app.ui.settings.PlansPagerAdapter
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PlanSelectionActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPlanSelectionBinding
    private lateinit var adapter: PlansPagerAdapter
    private var selectedPlan: PlanData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewPager()
        setupClickListeners()
    }

    private fun setupViewPager() {
        val plans = listOf(
            PlanData(
                name = "Basic",
                price = "Free",
                bonus = null,
                feature1 = "Access to free courses",
                feature2 = "Limited downloads",
                feature3 = "Basic support",
                idealFor = "Beginners",
                buttonText = "Start Free"
            ),
            PlanData(
                name = "Premium",
                price = "$9.99/month",
                bonus = "Unlimited downloads",
                feature1 = "All courses included",
                feature2 = "Unlimited downloads",
                feature3 = "Priority support",
                idealFor = "Regular learners",
                buttonText = "Get Premium",
                showBadge = true
            ),
            PlanData(
                name = "Pro",
                price = "$99.99/year",
                bonus = "Personal mentor included",
                feature1 = "All Premium features",
                feature2 = "Offline access",
                feature3 = "Certification",
                idealFor = "Professional development",
                buttonText = "Get Pro"
            )
        )

        adapter = PlansPagerAdapter(plans) { plan ->
            selectedPlan = plan
            handlePlanSelection(plan)
        }

        binding.viewPagerPlans.adapter = adapter
        binding.viewPagerPlans.setCurrentItem(1, false)

        binding.viewPagerPlans.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })

        updateDots(1)
    }

    private fun updateDots(position: Int) {
        binding.dot1.setBackgroundResource(
            if (position == 0) R.drawable.dot_active_blue else R.drawable.dot_gray
        )
        binding.dot2.setBackgroundResource(
            if (position == 1) R.drawable.dot_active_blue else R.drawable.dot_gray
        )
        binding.dot3.setBackgroundResource(
            if (position == 2) R.drawable.dot_active_blue else R.drawable.dot_gray
        )

        val params1 = binding.dot1.layoutParams
        params1.width = if (position == 0) dpToPx(16) else dpToPx(6)
        binding.dot1.layoutParams = params1

        val params2 = binding.dot2.layoutParams
        params2.width = if (position == 1) dpToPx(16) else dpToPx(6)
        binding.dot2.layoutParams = params2

        val params3 = binding.dot3.layoutParams
        params3.width = if (position == 2) dpToPx(16) else dpToPx(6)
        binding.dot3.layoutParams = params3
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun handlePlanSelection(plan: PlanData) {
        when (plan.name) {
            "Basic" -> {
                // Free plan - navigate directly
                navigateToHome()
            }
            "Premium" -> {
                // Premium plan - initiate payment
                startRazorpayPayment(999, "Premium Plan")
            }
            "Pro" -> {
                // Pro plan - initiate payment
                startRazorpayPayment(9999, "Pro Plan")
            }
        }
    }

    private fun startRazorpayPayment(amount: Int, planName: String) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_SKT1GVrIMucXHH")

        try {
            val options = JSONObject()
            options.put("name", "Osmium AI")
            options.put("description", planName)
            options.put("currency", "INR")
            options.put("amount", amount * 100)
            options.put("theme.color", "#3399cc")
            
            val prefill = JSONObject()
            prefill.put("email", "user@example.com")
            prefill.put("contact", "9999999999")
            options.put("prefill", prefill)

            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("RazorpayError", e.message ?: "Unknown error")
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentId", Toast.LENGTH_LONG).show()
        navigateToHome()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}
