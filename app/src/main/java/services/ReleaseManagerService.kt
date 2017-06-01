package services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class ReleaseManagerService : IntentService("ReleaseManagerService") {

    override fun onCreate() {
        super.onCreate()
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json") //TODO: replace with URL
                        .setPriority(Priority.LOW)
                        .addHeaders("X-CSRF-Token", "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA")
                        .addBodyParameter("message", "get:roles") // TODO: replace as well
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                // write code that will do the networking
                                val sdcardAndroidDir = File(Environment.getExternalStorageDirectory().absoluteFile.absolutePath)
                              //  val f = File(sdcardAndroidDir.getAbsolutePath() + "/GNURoot/home")
                                val inputStream: FileInputStream? = FileInputStream("") // should come from network TODO: replace as well
                                val fileOutputStream: FileOutputStream? = FileOutputStream(sdcardAndroidDir.getAbsolutePath() + "/GNURoot/home/install_core_new.sh")
                                if(fileOutputStream != null && inputStream != null) {
                                    val buffer: ByteArray = ByteArray(1024)
                                    var r: Int = 0
//                                    r = stream.read(buffer)
                                    while (r != -1) {
                                        r = inputStream.read(buffer)
                                        fileOutputStream.write(buffer, 0, r)
                                    }
                                }

                            }

                            override fun onError(anError: ANError) { // error happend
                                Log.d(TAG, "onError: error" + anError.toString())
                            }
                        })

            }
        }, 0, 1000.toLong()) // interval
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        // TODO: Handle action Baz
        throw UnsupportedOperationException("Not yet implemented")
    }

    companion object {
        // TODO: Rename actions, choose action names that describe tasks that this
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        private val ACTION_FOO = "services.action.FOO"
        private val ACTION_BAZ = "services.action.BAZ"
        private val TAG = "ReleaseManagerService"


        // TODO: Rename parameters
        private val EXTRA_PARAM1 = "services.extra.PARAM1"
        private val EXTRA_PARAM2 = "services.extra.PARAM2"

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // TODO: Customize helper method
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, ReleaseManagerService::class.java)
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
            val intent = Intent(context, ReleaseManagerService::class.java)
            intent.action = ACTION_BAZ
            intent.putExtra(EXTRA_PARAM1, param1)
            intent.putExtra(EXTRA_PARAM2, param2)
            context.startService(intent)
        }
    }
}
