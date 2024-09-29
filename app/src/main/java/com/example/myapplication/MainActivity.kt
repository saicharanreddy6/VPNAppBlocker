package com.example.myapplication

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var selectedAppsLabel: TextView
    private lateinit var selectedAppsList: TextView

    // Define the VPN permission launcher using the Activity Result API
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // VPN permission granted, start the VPN service
            startVpnService()
        }
    }

    // Define the launcher for selecting apps
    private val selectAppsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            // Get the list of selected apps from the result
            val selectedApps = result.data?.getStringExtra("SELECTED_APPS")
            if (!selectedApps.isNullOrEmpty()) {
                // Show the selected apps in the TextView
                selectedAppsLabel.visibility = TextView.VISIBLE
                selectedAppsList.visibility = TextView.VISIBLE
                selectedAppsList.text = selectedApps
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStartVpn: Button = findViewById(R.id.btnStartVpn)
        val btnSelectApps: Button = findViewById(R.id.btnSelectApps)
        val btnQuit: Button = findViewById(R.id.btnQuit)
        selectedAppsLabel = findViewById(R.id.selectedAppsLabel)
        selectedAppsList = findViewById(R.id.selectedAppsList)

        // Start VPN button handler
        btnStartVpn.setOnClickListener {
            val intent = VpnService.prepare(this)
            if (intent != null) {
                // Request VPN permissions using the Activity Result API
                vpnPermissionLauncher.launch(intent)
            } else {
                // VPN permission already granted
                startVpnService()
            }
        }

        // Select Apps button handler
        btnSelectApps.setOnClickListener {
            val intent = Intent(this, SelectAppsActivity::class.java)
            selectAppsLauncher.launch(intent)
        }

        // Quit button handler
        btnQuit.setOnClickListener {
            stopVpnService()  // Stop the VPN service
            quitApp()         // Quit the app
        }
    }

    // Start VPN service
    private fun startVpnService() {
        val intent = Intent(this, MyVpnService::class.java)
        startService(intent)
    }

    // Stop VPN service
    private fun stopVpnService() {
        val intent = Intent(this, MyVpnService::class.java)
        stopService(intent)
    }

    // Quit the app completely
    private fun quitApp() {
        finishAffinity()  // Close all activities
        System.exit(0)    // Exit the app and stop the process
    }
}
