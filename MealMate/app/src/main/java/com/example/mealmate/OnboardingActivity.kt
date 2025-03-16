package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mealmate.databinding.ActivityOnboardingBinding
import com.example.mealmate.databinding.ItemOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private val onboardingItems = listOf(
        OnboardingItem(
            R.drawable.ic_grocery,
            R.string.onboarding_grocery_title,
            R.string.onboarding_grocery_desc
        ),
        OnboardingItem(
            R.drawable.ic_recipe,
            R.string.onboarding_recipe_title,
            R.string.onboarding_recipe_desc
        ),
        OnboardingItem(
            R.drawable.ic_share,
            R.string.onboarding_share_title,
            R.string.onboarding_share_desc
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupTabLayout()
        setupButtons()
    }

    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = onboardingAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtons(position)
            }
        })
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }

    private fun setupButtons() {
        binding.btnSkip.setOnClickListener {
            navigateToDashboard()
        }

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < onboardingItems.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                navigateToDashboard()
            }
        }

        updateButtons(0)
    }

    private fun updateButtons(position: Int) {
        if (position == onboardingItems.size - 1) {
            binding.btnNext.text = getString(R.string.get_started)
            binding.btnSkip.visibility = View.GONE
        } else {
            binding.btnNext.text = getString(R.string.next)
            binding.btnSkip.visibility = View.VISIBLE
        }
    }

    private fun navigateToDashboard() {
        // Mark first launch as completed
        getSharedPreferences("MealMatePrefs", MODE_PRIVATE)
            .edit()
            .putBoolean("isFirstLaunch", false)
            .apply()

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}

data class OnboardingItem(
    val imageResId: Int,
    val titleResId: Int,
    val descriptionResId: Int
)

class OnboardingAdapter(
    private val items: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class OnboardingViewHolder(
        private val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OnboardingItem) {
            binding.apply {
                ivOnboardingImage.setImageResource(item.imageResId)
                tvTitle.setText(item.titleResId)
                tvDescription.setText(item.descriptionResId)
            }
        }
    }
}
