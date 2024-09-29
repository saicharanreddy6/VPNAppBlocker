package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppsAdapter(
    private val context: Context,
    private var appsList: List<AppInfo>,
    private val listener: OnAppSelectedListener
) : RecyclerView.Adapter<AppsAdapter.AppViewHolder>() {

    interface OnAppSelectedListener {
        fun onAppSelected(appInfo: AppInfo)
    }

    // Method to update the list of apps
    fun updateApps(newAppsList: List<AppInfo>) {
        appsList = newAppsList
        notifyDataSetChanged()  // Notify the adapter to refresh the UI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = appsList[position]
        holder.bind(appInfo)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            appInfo.isSelected = isChecked
            listener.onAppSelected(appInfo)
        }
    }

    override fun getItemCount(): Int {
        return appsList.size
    }

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val appName: TextView = view.findViewById(R.id.appName)
        private val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        fun bind(appInfo: AppInfo) {
            appName.text = appInfo.appName
            appIcon.setImageDrawable(appInfo.icon)
            checkBox.isChecked = appInfo.isSelected
        }
    }
}
