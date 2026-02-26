package com.osmiumai.app.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osmiumai.app.databinding.ItemPlanCardBinding

data class PlanData(
    val name: String,
    val price: String,
    val bonus: String?,
    val feature1: String,
    val feature2: String,
    val feature3: String,
    val idealFor: String,
    val buttonText: String,
    val showBadge: Boolean = false
)

class PlansPagerAdapter(
    private val plans: List<PlanData>,
    private val onPlanSelected: (PlanData) -> Unit
) : RecyclerView.Adapter<PlansPagerAdapter.PlanViewHolder>() {

    inner class PlanViewHolder(private val binding: ItemPlanCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: PlanData) {
            binding.apply {
                tvPlanName.text = plan.name.uppercase()
                tvPrice.text = plan.price
                tvFeature1.text = plan.feature1
                tvFeature2.text = plan.feature2
                tvFeature3.text = plan.feature3
                tvIdealFor.text = plan.idealFor
                btnSelectPlan.text = plan.buttonText

                if (plan.bonus != null) {
                    pillBonus.visibility = View.VISIBLE
                    tvBonus.text = plan.bonus
                } else {
                    pillBonus.visibility = View.GONE
                }

                if (plan.showBadge) {
                    tvBadge.visibility = View.VISIBLE
                } else {
                    tvBadge.visibility = View.GONE
                }

                btnSelectPlan.setOnClickListener {
                    onPlanSelected(plan)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(plans[position])
    }

    override fun getItemCount() = plans.size
}
