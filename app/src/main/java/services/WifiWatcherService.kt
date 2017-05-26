package services

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import java.util.*


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class WifiWatcherService : IntentService("WifiWatcherService") {

    companion object {
        // TODO: Rename actions, choose action names that describe tasks that this
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        private val ACTION_FOO = "services.action.FOO"
        private val ACTION_BAZ = "services.action.BAZ"

        // TODO: Rename parameters
        private val EXTRA_PARAM1 = "services.extra.PARAM1"
        private val EXTRA_PARAM2 = "services.extra.PARAM2"
        private var userId: Int = 0;
        private var netWorkSSID = ""
        private var networkPass = ""

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // TODO: Customize helper method
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, WifiWatcherService::class.java)
            intent.action = ACTION_FOO
            intent.putExtra(EXTRA_PARAM1, param1)
            intent.putExtra(EXTRA_PARAM2, param2)
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // TODO: Customize helper method
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, WifiWatcherService::class.java)
            intent.action = ACTION_BAZ
            intent.putExtra(EXTRA_PARAM1, param1)
            intent.putExtra(EXTRA_PARAM2, param2)
            context.startService(intent)
        }

        fun getSharedPrefs(context: Context) = context.getSharedPreferences("school_pref", Context.MODE_PRIVATE)
    }


    override fun onCreate() {
        super.onCreate()
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                    val wifiManager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val scanResults = wifiManager.scanResults
                    netWorkSSID = "Teacher_${userId}"
                    for (c in scanResults) {
                        if (c.SSID != null) {
                            if (netWorkSSID == c.SSID) {
                                connectToWifi(netWorkSSID)
                            }
                        }
                    }
            }
        }, 0, (1000*60*45).toLong()) // this makes 45 minutes

    }

    override fun onHandleIntent(intent: Intent?) {

        if (intent != null) {
            val action = intent.action
            if (ACTION_FOO == action) {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionFoo(param1, param2)
            } else if (ACTION_BAZ == action) {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionBaz(param1, param2)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {
        // TODO: Handle action Foo
        throw UnsupportedOperationException("Not yet implemented")
    }


    private fun connectToWifi(netWorkSSID: String) {
        userId = getSharedPrefs(this).getInt("userId", 0)
        val conf = WifiConfiguration()
        conf.SSID = "\"" + netWorkSSID + "\""   // Please note the quotes. String should contain ssid in quotes
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        val wifiManager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.addNetwork(conf)
        val list = wifiManager.getConfiguredNetworks()
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + netWorkSSID + "\"") {
                wifiManager.disconnect()
                wifiManager.enableNetwork(i.networkId, true)
                wifiManager.reconnect()
                break
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        // TODO: Handle action Baz
        throw UnsupportedOperationException("Not yet implemented")
    }


}
