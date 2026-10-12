package com.animegen.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareManager @Inject constructor(
    private val context: Context
) {

    fun shareImageFile(file: File, prompt: String): Boolean {
        return try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "com.animegen.app.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Generated anime character: $prompt\n\nCreated with Anime Generator App")
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Image")
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun copyToClipboard(text: String): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Image Prompt", text)
            clipboard.setPrimaryClip(clip)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareToInstagram(imageUri: Uri, caption: String): Boolean {
        return try {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, caption)
                type = "image/*"
                setPackage("com.instagram.android")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareToTwitter(caption: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://twitter.com/intent/tweet?text=${Uri.encode(caption)}")
                setPackage("com.twitter.android")
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareToFacebook(imageUri: Uri): Boolean {
        return try {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/*"
                setPackage("com.facebook.katana")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shareToDiscord(imageUri: Uri, caption: String): Boolean {
        return try {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, caption)
                type = "image/*"
                setPackage("com.discord")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun saveImageToAppCache(inputFile: File): File? {
        return try {
            val cacheDir = context.cacheDir
            val fileName = "anime_gen_${System.currentTimeMillis()}.png"
            val outputFile = File(cacheDir, fileName)

            inputFile.inputStream().use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            outputFile
        } catch (e: Exception) {
            null
        }
    }

    fun getShareableUri(file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "com.animegen.app.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }

    fun deleteImageFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
}
