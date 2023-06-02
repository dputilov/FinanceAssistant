package com.example.financeassistant.manager

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore.Images
import android.provider.MediaStore.Video.Media
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import com.example.financeassistant.app.FinanceAssistantApp
import com.example.financeassistant.classes.BROADCAST_ACTION
import com.example.financeassistant.classes.BROADCAST_ACTION_TYPE
import com.example.financeassistant.classes.BROADCAST_SEND_FROM
import com.example.financeassistant.classes.BroadcastActionType
import com.example.financeassistant.classes.Credit
import com.example.financeassistant.classes.Flat
import com.example.financeassistant.classes.FlatPayment
import com.example.financeassistant.classes.Payment
import com.example.financeassistant.classes.Task
import com.example.financeassistant.database.DB
import com.example.financeassistant.utils.formatDate
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class FileManager {

    var context: Context? = null

    private object Holder { val INSTANCE = FileManager() }

    companion object {
        val instance: FileManager by lazy { Holder.INSTANCE }

        const val FILES_SUBDIRECTORY = "/sharedResources/"
    }

    fun initManager(context: Context) {
        this.context = context.applicationContext
    }

    fun sendGroupBroadcastMessage(action: BroadcastActionType) {
        val intent = Intent(BROADCAST_ACTION)
        intent.putExtra(BROADCAST_SEND_FROM, "DatabaseManager")
        intent.putExtra(BROADCAST_ACTION_TYPE, action)
//        intent.putExtra(BROADCAST_ENTITY, Gson().toJson(group))
        context?.sendBroadcast(intent)
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        return context?.let { context ->
            val projection = arrayOf(Media.DATA)
            val cursor = context.contentResolver.query(contentUri, projection, null, null, null)
            if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(Images.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(columnIndex)
            } else {
                null
            }
        }
    }

    fun copyFile(sourceUri: Uri) : File? {
        return getRealPathFromURI(sourceUri)?.let { realPath ->
            copyFile(File(realPath))
        }
    }

    fun copyFile(sourceFile: File) : File? {

        if (!sourceFile.exists()) {
            return null
        }

        val createDir = File(getNewImageFilePath())

        if(!createDir.exists()) {
            createDir.mkdir()
        }

        val imagePath = createDir.absolutePath + File.separator + sourceFile.name

        val destFile = File(imagePath)
        if (!destFile.exists()) {
            try {
                destFile.createNewFile()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }

        context?.let { context ->
            val source = FileInputStream(sourceFile).channel
            val destination = FileOutputStream(destFile).channel
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size())
            }
            source?.close()
            destination?.close()
        }

        return destFile
    }

    private fun getNewImageFilePath(): String {
        return Environment.getExternalStorageDirectory().absolutePath + FILES_SUBDIRECTORY
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight : Int) : Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight : Int = height / 2
            val halfWidth : Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun getByteArrayFromFile(file: File): ByteArray? {
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file.path)
            val bos = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var readNum: Int
            while (fis.read(b).also { readNum = it } != -1) {
                bos.write(b, 0, readNum)
            }
            return bos.toByteArray()
        } catch (e: java.lang.Exception) {
            Log.d("mylog", e.toString())
        }
        return null
    }

    fun decodeBitmapFromUri(imageUri: Uri, reqWidth: Int, reqHeight: Int) : Bitmap? {
        var bitmap : Bitmap? = null
        context?.also { context ->
            try {
                // Get input stream of the image
                val options = BitmapFactory.Options()
                var iStream = context.contentResolver.openInputStream(imageUri)

                // First decode with inJustDecodeBounds=true to check dimensions
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(iStream, null, options)
                if (iStream != null) {
                    iStream.close()
                }
                iStream = context.contentResolver.openInputStream(imageUri)

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeStream(iStream, null, options)
                if (iStream != null) {
                    iStream.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bitmap
    }
}