package com.example.myapplication

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectAppsActivity : AppCompatActivity(), AppsAdapter.OnAppSelectedListener {

    private lateinit var appsAdapter: AppsAdapter
    private var selectedApps = mutableListOf<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_apps)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewApps)
        val btnConfirmSelection: Button = findViewById(R.id.btnConfirmSelection)

        recyclerView.layoutManager = LinearLayoutManager(this)
        appsAdapter = AppsAdapter(this, mutableListOf(), this)
        recyclerView.adapter = appsAdapter

        btnConfirmSelection.setOnClickListener {
            confirmSelectedApps()
        }

        loadInstalledApps()  // Load apps after setting up the UI
    }

    // Function to load installed apps safely
    private fun loadInstalledApps() {
        try {
            val pm: PackageManager = packageManager
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val appsList = mutableListOf<AppInfo>()

            for (app in installedApps) {
                if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                    val appName = pm.getApplicationLabel(app).toString()
                    val appIcon: Drawable = pm.getApplicationIcon(app.packageName)
                    appsList.add(AppInfo(app.packageName, appName, appIcon))
                }
            }

            // Update the adapter with the installed apps
            appsAdapter.updateApps(appsList)

        } catch (e: Exception) {
            Log.e("SelectAppsActivity", "Error loading installed apps", e)
        }
    }

    override fun onAppSelected(appInfo: AppInfo) {
        if (appInfo.isSelected) {
            selectedApps.add(appInfo)
        } else {
            selectedApps.remove(appInfo)
        }
    }

    private fun confirmSelectedApps() {
        val resultIntent = intent
        val selectedAppNames = selectedApps.joinToString(", ") { it.appName }
        resultIntent.putExtra("SELECTED_APPS", selectedAppNames)
        setResult(RESULT_OK, resultIntent)
        finish()  // Close activity after confirming
    }
}
