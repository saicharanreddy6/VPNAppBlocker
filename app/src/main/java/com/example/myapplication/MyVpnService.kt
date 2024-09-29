package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat

class MyVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyVpnService", "VPN service starting")
        startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        // Start the service in the foreground
        startVpn()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyVpnService", "VPN service stopping")
        stopVpn()
    }

    private fun startVpn() {
        Log.d("MyVpnService", "Starting VPN")

        val builder = Builder()
        builder.addAddress("10.0.0.2", 32)  // Address for the VPN interface
        builder.addRoute("0.0.0.0", 0)  // Route all traffic through the VPN

        vpnInterface = builder.setSession("MyVPN")
            .setMtu(1500)
            .establish()

        Log.d("MyVpnService", "VPN started successfully")
    }

    private fun stopVpn() {
        Log.d("MyVpnService", "Stopping VPN")

        try {
            vpnInterface?.close()
            Log.d("MyVpnService", "VPN interface closed")
        } catch (e: Exception) {
            Log.e("MyVpnService", "Error closing VPN interface", e)
        }
        vpnInterface = null

        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val channelId = "vpn_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "VPN Service", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("VPN Running")
            .setContentText("VPN is running and routing selected apps")
            .setSmallIcon(R.drawable.ic_vpn_key)
            .setContentIntent(pendingIntent)
            .build()
    }
}
