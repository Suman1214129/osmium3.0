package com.osmiumai.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.osmiumai.app.databinding.ActivityCareerDetailBinding

class CareerDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCareerDetailBinding
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCareerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        
        val careerType = intent.getStringExtra("CAREER_TYPE") ?: "software"
        
        setupWebView(careerType)
        
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(careerType: String) {
        binding.webViewRoadmap.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = false
            webViewClient = WebViewClient()
            
            val htmlContent = getRoadmapHtml(careerType)
            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    }
    
    private fun getRoadmapHtml(careerType: String): String {
        val (title, data) = when (careerType) {
            "software" -> "Software Developer" to getSoftwareDevData()
            "android" -> "Android Developer" to getAndroidDevData()
            else -> "Software Developer" to getSoftwareDevData()
        }
        
        binding.tvCareerTitle.text = title
        
        return """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/7.8.5/d3.min.js"></script>
    <style>
        body { margin: 0; padding: 0; background: #fff; overflow: hidden; }
        #mindmap { width: 100vw; height: 100vh; }
        .node rect { stroke: none; rx: 24px; filter: drop-shadow(0 1px 2px rgba(0,0,0,0.05)); }
        .node.depth-0 rect { fill: #c7d2fe; }
        .node.depth-1 rect { fill: #bae6fd; }
        .node.depth-2 rect { fill: #bbf7d0; }
        .node.depth-3 rect { fill: #fde68a; }
        .node.depth-4 rect { fill: #fed7aa; }
        .node text { font-family: sans-serif; pointer-events: none; }
        .node-title { font-size: 14px; font-weight: 600; fill: #334155; }
        .node-desc { font-size: 11px; fill: #64748b; }
        .indicator circle { fill: #fff; stroke: #e2e8f0; cursor: pointer; }
        .indicator text { font-size: 10px; fill: #64748b; font-weight: 700; pointer-events: none; }
        .link { fill: none; stroke: #cbd5e1; stroke-width: 1.5px; }
        .badge { font-size: 9px; fill: #334155; font-weight: 600; }
    </style>
</head>
<body>
    <div id="mindmap"></div>
    <script>
        const data = $data;
        const width = window.innerWidth;
        const height = window.innerHeight;
        const cardWidth = 200, cardHeight = 56;
        
        const svg = d3.select("#mindmap").append("svg")
            .attr("width", width).attr("height", height)
            .call(d3.zoom().on("zoom", e => g.attr("transform", e.transform)));
        
        const g = svg.append("g").attr("transform", "translate(100,"  + (height/2) + ")");
        
        const tree = d3.tree().nodeSize([cardHeight + 24, cardWidth + 80]);
        const root = d3.hierarchy(data);
        root.x0 = height / 2;
        root.y0 = 0;
        
        // Collapse all children initially
        if (root.children) {
            root.children.forEach(collapseAll);
        }
        
        function collapseAll(d) {
            if (d.children) {
                d._children = d.children;
                d._children.forEach(collapseAll);
                d.children = null;
            }
        }
        
        let i = 0;
        update(root);
        
        const zoomBehavior = d3.zoom().on("zoom", e => g.attr("transform", e.transform));
        svg.call(zoomBehavior);
        svg.call(zoomBehavior.transform, d3.zoomIdentity.translate(100, height/2).scale(0.85));
        
        function update(source) {
            const treeData = tree(root);
            const nodes = treeData.descendants();
            const links = treeData.links();
            
            const node = g.selectAll('g.node').data(nodes, d => d.id || (d.id = ++i));
            
            const nodeEnter = node.enter().append('g')
                .attr('class', d => 'node depth-' + Math.min(d.depth, 4))
                .attr("transform", "translate(" + source.y0 + "," + source.x0 + ")")
                .on('click', (e, d) => {
                    if (d.children) { 
                        d._children = d.children; 
                        d.children = null; 
                    } else { 
                        d.children = d._children; 
                        d._children = null;
                        setTimeout(() => {
                            const scale = 0.7;
                            const x = -d.y * scale + width / 6;
                            const y = -d.x * scale + height / 2;
                            svg.transition().duration(750)
                                .call(d3.zoom().transform, d3.zoomIdentity.translate(x, y).scale(scale));
                        }, 450);
                    }
                    update(d);
                });
            
            nodeEnter.append('rect')
                .attr('width', cardWidth).attr('height', cardHeight)
                .attr('y', -cardHeight/2);
            
            nodeEnter.append('text').attr('class', 'node-title')
                .attr("x", 20).attr("y", -4)
                .text(d => d.data.name);
            
            nodeEnter.append('text').attr('class', 'node-desc')
                .attr("x", 20).attr("y", 12)
                .text(d => d.data.description || "");
            
            nodeEnter.filter(d => d.data.tags).append('text')
                .attr('class', 'badge')
                .attr('x', cardWidth - 10).attr('y', cardHeight/2 - 6)
                .attr('text-anchor', 'end')
                .text(d => d.data.tags[0]);
            
            const indicator = nodeEnter.append('g').attr('class', 'indicator')
                .attr('transform', 'translate(' + cardWidth + ', 0)')
                .style("opacity", d => d._children || d.children ? 1 : 0);
            
            indicator.append('circle').attr('r', 10);
            indicator.append('text').attr('y', 3).attr('text-anchor', 'middle')
                .text(d => d._children ? "+" : "-");
            
            const nodeUpdate = nodeEnter.merge(node);
            nodeUpdate.transition().duration(400)
                .attr("transform", d => "translate(" + d.y + "," + d.x + ")");
            
            nodeUpdate.select('.indicator text')
                .text(d => d._children ? "+" : (d.children ? "-" : ""));
            
            node.exit().transition().duration(400)
                .attr("transform", "translate(" + source.y + "," + source.x + ")").remove();
            
            const link = g.selectAll('path.link').data(links, d => d.target.id);
            
            link.enter().insert('path', "g").attr("class", "link")
                .attr('d', d => {
                    const o = {x: source.x0, y: source.y0};
                    return 'M ' + (o.y + cardWidth) + ' ' + o.x + ' C ' + ((o.y + cardWidth + o.y)/2) + ' ' + o.x + ', ' + ((o.y + cardWidth + o.y)/2) + ' ' + o.x + ', ' + o.y + ' ' + o.x;
                })
                .merge(link).transition().duration(400)
                .attr('d', d => 'M ' + (d.source.y + cardWidth) + ' ' + d.source.x + ' C ' + ((d.source.y + cardWidth + d.target.y)/2) + ' ' + d.source.x + ', ' + ((d.source.y + cardWidth + d.target.y)/2) + ' ' + d.target.x + ', ' + d.target.y + ' ' + d.target.x);
            
            link.exit().transition().duration(400)
                .attr('d', d => {
                    const o = {x: source.x, y: source.y};
                    return 'M ' + (o.y + cardWidth) + ' ' + o.x + ' C ' + ((o.y + cardWidth + o.y)/2) + ' ' + o.x + ', ' + ((o.y + cardWidth + o.y)/2) + ' ' + o.x + ', ' + o.y + ' ' + o.x;
                }).remove();
            
            nodes.forEach(d => { d.x0 = d.x; d.y0 = d.y; });
        }
    </script>
</body>
</html>
        """.trimIndent()
    }
    
    private fun getSoftwareDevData() = """
    {
        "name": "Software Developer",
        "description": "Build applications",
        "tags": ["Career"],
        "children": [
            {
                "name": "Programming",
                "description": "Core languages",
                "tags": ["Essential"],
                "children": [
                    {"name": "Java", "description": "Enterprise apps"},
                    {"name": "Python", "description": "Versatile"},
                    {"name": "JavaScript", "description": "Web dev"}
                ]
            },
            {
                "name": "Data Structures",
                "description": "Algorithms",
                "tags": ["Core"],
                "children": [
                    {"name": "Arrays & Lists", "description": "Linear DS"},
                    {"name": "Trees & Graphs", "description": "Non-linear"},
                    {"name": "Sorting", "description": "Algorithms"}
                ]
            },
            {
                "name": "Frameworks",
                "description": "Build faster",
                "children": [
                    {"name": "Spring Boot", "description": "Java backend"},
                    {"name": "React", "description": "Frontend"},
                    {"name": "Node.js", "description": "JS backend"}
                ]
            },
            {
                "name": "Databases",
                "description": "Data storage",
                "children": [
                    {"name": "SQL", "description": "Relational"},
                    {"name": "MongoDB", "description": "NoSQL"}
                ]
            }
        ]
    }
    """
    
    private fun getAndroidDevData() = """
    {
        "name": "Android Developer",
        "description": "Mobile apps",
        "tags": ["Career"],
        "children": [
            {
                "name": "Kotlin",
                "description": "Primary language",
                "tags": ["Essential"],
                "children": [
                    {"name": "Basics", "description": "Syntax & OOP"},
                    {"name": "Coroutines", "description": "Async"},
                    {"name": "Collections", "description": "Data handling"}
                ]
            },
            {
                "name": "UI Development",
                "description": "User interface",
                "tags": ["Core"],
                "children": [
                    {"name": "XML Layouts", "description": "Traditional"},
                    {"name": "Jetpack Compose", "description": "Modern"},
                    {"name": "Material Design", "description": "Guidelines"}
                ]
            },
            {
                "name": "Architecture",
                "description": "App structure",
                "tags": ["Important"],
                "children": [
                    {"name": "MVVM", "description": "Pattern"},
                    {"name": "Repository", "description": "Data layer"},
                    {"name": "ViewModel", "description": "State"}
                ]
            },
            {
                "name": "Libraries",
                "description": "Essential tools",
                "children": [
                    {"name": "Retrofit", "description": "Networking"},
                    {"name": "Room", "description": "Database"},
                    {"name": "Glide", "description": "Images"}
                ]
            }
        ]
    }
    """
}
