package com.example.equipotres.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver.PendingResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.equipotres.R
import com.example.equipotres.repository.InventoryRepository
import com.example.equipotres.ui.LoginActivity
import com.example.equipotres.ui.MainActivity
import com.example.equipotres.utils.SessionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun inventoryRepository(): InventoryRepository
}

class WidGetApp : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE_BALANCE = "com.example.equipotres.widget.TOGGLE_BALANCE"
        const val ACTION_LOGIN_SUCCESS = "com.example.equipotres.widget.LOGIN_SUCCESS"
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
        when (intent.action) {
            ACTION_TOGGLE_BALANCE -> {
                val sessionManager = SessionManager(context.applicationContext)
                if (sessionManager.isLoggedIn()) {
                    val newVisible = !isBalanceVisible(context)
                    setBalanceVisible(context, newVisible)
                    triggerUpdate(context) // Update UI
                } else {
                    launchLogin(context) // Not logged in
                }
            }
            ACTION_LOGIN_SUCCESS -> {
                setBalanceVisible(context, true)
                triggerUpdate(context) // Update UI
            }
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val pendingResult = goAsync()
                updateWidgets(context, AppWidgetManager.getInstance(context), pendingResult)
            }
            else -> super.onReceive(context, intent)
        }
    }

    private fun triggerUpdate(context: Context) {
        val intent = Intent(context, WidGetApp::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, WidGetApp::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }

    private fun launchLogin(context: Context) {
        val loginIntent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("LAUNCHED_FROM_WIDGET", true)
        }
        context.startActivity(loginIntent)
    }


    private fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        pendingResult: PendingResult
    ) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val inventoryRepository = hiltEntryPoint.inventoryRepository()

        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, WidGetApp::class.java))

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val inventoryList = withContext(Dispatchers.IO) {
                    inventoryRepository.getListInventory()
                }
                val total = inventoryList.sumOf { it.price.toDouble() * it.quantity }

                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, total)
                }
            } finally {
                pendingResult.finish()
            }
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
    appWidgetId: Int,
    total: Double
) {
    val views = RemoteViews(context.packageName, R.layout.wid_get_app)

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

private fun formatCurrency(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return formatter.format(value)
}
