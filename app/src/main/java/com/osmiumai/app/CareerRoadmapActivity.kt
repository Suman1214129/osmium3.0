package com.osmiumai.app

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class CareerRoadmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_career_roadmap)

        val careerTitle = intent.getStringExtra("CAREER_TITLE") ?: "Frontend Developer"
        findViewById<TextView>(R.id.tvCareerTitle).text = careerTitle
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        buildRoadmap(careerTitle)
    }

    private fun buildRoadmap(careerTitle: String) {
        val container = findViewById<LinearLayout>(R.id.roadmapContainer)
        val roadmap = when (careerTitle) {
            "Software Developer" -> getSoftwareDeveloperRoadmap()
            "Android Developer" -> getAndroidDeveloperRoadmap()
            else -> getFrontendDeveloperRoadmap()
        }

        roadmap.forEach { section ->
            addSection(container, section)
        }
    }

    private fun addSection(parent: LinearLayout, section: Section) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_career_section, parent, false)
        
        view.findViewById<TextView>(R.id.tvSectionTitle).text = section.title
        view.findViewById<TextView>(R.id.tvSectionSubtitle).text = section.subtitle
        
        val badge = view.findViewById<TextView>(R.id.tvBadge)
        if (section.badge.isNotEmpty()) {
            badge.text = section.badge
            badge.isVisible = true
        }

        val content = view.findViewById<LinearLayout>(R.id.sectionContent)
        val expandIcon = view.findViewById<ImageView>(R.id.ivExpand)
        val childContainer = view.findViewById<LinearLayout>(R.id.childContainer)

        if (section.children.isEmpty()) {
            expandIcon.isVisible = false
        } else {
            section.children.forEach { child ->
                addChild(childContainer, child)
            }

            view.findViewById<View>(R.id.sectionHeader).setOnClickListener {
                val isExpanded = content.isVisible
                content.isVisible = !isExpanded
                ObjectAnimator.ofFloat(expandIcon, "rotation", if (isExpanded) 180f else 0f, if (isExpanded) 0f else 180f).apply {
                    duration = 200
                    start()
                }
            }
        }

        parent.addView(view)
    }

    private fun addChild(parent: LinearLayout, child: Child) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_career_child, parent, false)
        
        view.findViewById<TextView>(R.id.tvChildTitle).text = child.title
        view.findViewById<TextView>(R.id.tvChildSubtitle).text = child.subtitle
        
        val badge = view.findViewById<TextView>(R.id.tvChildBadge)
        if (child.badge.isNotEmpty()) {
            badge.text = child.badge
            badge.isVisible = true
        }

        parent.addView(view)
    }

    private fun getFrontendDeveloperRoadmap() = listOf(
        Section("Internet", "How it works", "ESSENTIAL"),
        Section("HTML", "Structure", "ESSENTIAL"),
        Section("CSS", "Styling", "ESSENTIAL"),
        Section("JavaScript", "Interactivity", "ESSENTIAL"),
        Section("Frameworks", "App Architecture", "POPULAR", listOf(
            Child("React", "UI Library", "POPULAR"),
            Child("Vue", "Progressive Framework", "POPULAR"),
            Child("Angular", "Full Framework", "ESSENTIAL")
        )),
        Section("Testing", "Quality Assurance", "ESSENTIAL"),
        Section("Tools", "Workflow", "")
    )

    private fun getSoftwareDeveloperRoadmap() = listOf(
        Section("Programming Basics", "Core fundamentals", "ESSENTIAL"),
        Section("Data Structures", "Algorithms & patterns", "ESSENTIAL", listOf(
            Child("Arrays & Lists", "Linear structures", "ESSENTIAL"),
            Child("Trees & Graphs", "Hierarchical data", "ESSENTIAL"),
            Child("Hash Tables", "Fast lookups", "ESSENTIAL")
        )),
        Section("Databases", "Data management", "ESSENTIAL", listOf(
            Child("SQL", "Relational databases", "ESSENTIAL"),
            Child("NoSQL", "Document stores", "POPULAR")
        )),
        Section("APIs", "Integration", "ESSENTIAL"),
        Section("Version Control", "Git & GitHub", "ESSENTIAL")
    )

    private fun getAndroidDeveloperRoadmap() = listOf(
        Section("Kotlin", "Primary language", "ESSENTIAL"),
        Section("Android SDK", "Framework basics", "ESSENTIAL"),
        Section("UI Components", "Views & layouts", "ESSENTIAL", listOf(
            Child("XML Layouts", "Traditional UI", "ESSENTIAL"),
            Child("Jetpack Compose", "Modern UI", "POPULAR")
        )),
        Section("Architecture", "Design patterns", "ESSENTIAL", listOf(
            Child("MVVM", "Recommended pattern", "ESSENTIAL"),
            Child("Clean Architecture", "Scalable apps", "POPULAR")
        )),
        Section("Networking", "API integration", "ESSENTIAL"),
        Section("Storage", "Data persistence", "ESSENTIAL")
    )

    data class Section(val title: String, val subtitle: String, val badge: String, val children: List<Child> = emptyList())
    data class Child(val title: String, val subtitle: String, val badge: String)
}
