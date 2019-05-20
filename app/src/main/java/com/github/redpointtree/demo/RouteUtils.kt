package com.github.redpointtree.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by loganpluo on 2019/5/20.
 */
object RouteUtils {

    interface RouteListener {
        fun dispatch(intent:String)
    }

    var routeListener:RouteListener? = null

    fun dispatch(activity:Activity,intent:String){
        if(canDispatch(activity,intent)){
            activity.launchActivity(intent)
            routeListener?.dispatch(intent)
        }
    }

    private fun canDispatch(context: Context, intent: String): Boolean {
        val uri = Uri.parse(intent)
        return uri != null && (uri.scheme == context.resources.getString(R.string.app_page_scheme))
    }

}

inline fun Activity.launchActivity(uri: String): Boolean {
    val intent = Intent().apply { data = Uri.parse(uri) }
    if (canResolveIntent(this, intent)) {
        startActivity(intent)
        return true
    }

    return false
}

inline fun canResolveIntent(context: Context, intent: Intent) : Boolean {
    try {
        return intent.resolveActivity(context.packageManager) != null
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}