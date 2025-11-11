package com.example.equipotres.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.room.Room
import com.example.equipotres.R
import com.example.equipotres.data.InventoryDB
import com.example.equipotres.ui.MainActivity
import com.example.equipotres.utils.Constants
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

class WidGetApp : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE_BALANCE = "com.example.equipotres.widget.TOGGLE_BALANCE"
        private const val PREFS_NAME = "inventory_widget_prefs"
        private const val KEY_IS_BALANCE_VISIBLE = "is_balance_visible"

        fun isBalanceVisible(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_IS_BALANCE_VISIBLE, false)
        }

        fun setBalanceVisible(context: Context, visible: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_IS_BALANCE_VISIBLE, visible).apply()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TOGGLE_BALANCE -> {
                // Cambia el estado visible/oculto del saldo
                val newVisible = !isBalanceVisible(context)
                setBalanceVisible(context, newVisible)

                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, WidGetApp::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

                for (id in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, id)
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}


fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.wid_get_app)

    val total = calculateInventoryTotal(context)
    val formattedTotal = formatCurrency(total)

    val visible = WidGetApp.isBalanceVisible(context)
    if (visible) {
        views.setTextViewText(R.id.widgetDescription, "$ $formattedTotal")
    } else {
        views.setTextViewText(R.id.widgetDescription, "$ ****")
    }

    val toggleIntent = Intent(context, WidGetApp::class.java).apply {
        action = WidGetApp.ACTION_TOGGLE_BALANCE
    }

    val togglePendingIntent = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        toggleIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.btnRefreshWidget, togglePendingIntent)

    val manageIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("open_login_from_widget", true)
    }

    val managePendingIntent = PendingIntent.getActivity(
        context,
        1000 + appWidgetId,
        manageIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    views.setOnClickPendingIntent(R.id.widgetProductName, managePendingIntent)
    views.setOnClickPendingIntent(R.id.ivManageInventory, managePendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}


private fun calculateInventoryTotal(context: Context): Double {
    val db = Room.databaseBuilder(
        context.applicationContext,
        InventoryDB::class.java,
        Constants.NAME_BD
    )
        .fallbackToDestructiveMigration()
        .build()

    return runBlocking {
        val list = db.inventoryDao().getListInventory()
        list.sumOf { it.price.toDouble() * it.quantity.toDouble() }
    }
}


private fun formatCurrency(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return formatter.format(value)
}
