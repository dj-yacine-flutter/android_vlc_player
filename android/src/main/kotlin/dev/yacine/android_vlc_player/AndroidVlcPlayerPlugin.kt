package dev.yacine.android_vlc_player

import android.content.Intent
import android.net.Uri
import android.content.Context

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** AndroidVlcPlayerPlugin */
class AndroidVlcPlayerPlugin : FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var applicationContext: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    applicationContext = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "android_vlc_player")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "startVLCPlayer" -> {
        val filePath = call.argument<String>("filePath") ?: return result.error("InvalidArguments", "File path is null", null)
        val extension = call.argument<String>("extension") ?: return result.error("InvalidArguments", "Extension is null", null)
        val title = call.argument<String>("title") ?: return result.error("InvalidArguments", "Title is null", null)
        val success = playVideo(filePath, extension, title)
        result.success(success)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun playVideo(filePath: String, extension: String, title: String): Boolean {
    val uri = Uri.parse(filePath)
    val vlcIntent = Intent(Intent.ACTION_VIEW)
    vlcIntent.setPackage("org.videolan.vlc")
    vlcIntent.setDataAndTypeAndNormalize(uri, extension)
    vlcIntent.putExtra("title", title)

    try {
      vlcIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      applicationContext.startActivity(vlcIntent)
      return true
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}