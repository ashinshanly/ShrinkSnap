package com.example.shrinksnap.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Main class that handles photo compression operations for ShrinkSnap
 */
class ShrinkSnapCompressor(private val context: Context) {

    /**
     * Represents a photo with its metadata
     */
    data class Photo(
        val id: Long,
        val uri: Uri,
        val name: String,
        val path: String,
        val size: Long,
        val dateTaken: Long,
        val width: Int,
        val height: Int
    )

    /**
     * Represents the result of a compression operation
     */
    data class CompressionResult(
        val photo: Photo,
        val compressedSize: Long,
        val originalSize: Long,
        val savedBytes: Long,
        val compressedFilePath: String
    )

    /**
     * Find photos older than the specified time period
     * 
     * @param timeValue The value of time units to go back
     * @param timeUnit The unit (days, weeks, months, years)
     * @return A list of photos older than the specified time
     */
    suspend fun findPhotosOlderThan(timeValue: Int, timeUnit: String): List<Photo> = withContext(Dispatchers.IO) {
        val cutoffTime = calculateCutoffTime(timeValue, timeUnit)
        val photos = mutableListOf<Photo>()
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )
        
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} <= ?"
        val selectionArgs = arrayOf(cutoffTime.toString())
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(dataColumn)
                val size = cursor.getLong(sizeColumn)
                val date = cursor.getLong(dateColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                
                photos.add(
                    Photo(
                        id = id,
                        uri = contentUri,
                        name = name,
                        path = path,
                        size = size,
                        dateTaken = date,
                        width = width,
                        height = height
                    )
                )
            }
        }
        
        photos
    }

    /**
     * Calculate the cutoff time in milliseconds based on the time period
     */
    private fun calculateCutoffTime(timeValue: Int, timeUnit: String): Long {
        val currentTimeMillis = System.currentTimeMillis()
        
        val millisToSubtract = when (timeUnit.toLowerCase(Locale.ROOT)) {
            "days" -> TimeUnit.DAYS.toMillis(timeValue.toLong())
            "weeks" -> TimeUnit.DAYS.toMillis(timeValue.toLong() * 7)
            "months" -> TimeUnit.DAYS.toMillis(timeValue.toLong() * 30) // Approximation
            "years" -> TimeUnit.DAYS.toMillis(timeValue.toLong() * 365) // Approximation
            else -> TimeUnit.DAYS.toMillis(timeValue.toLong())
        }
        
        return currentTimeMillis - millisToSubtract
    }

    /**
     * Compress a list of photos and return results as a flow
     * 
     * @param photos List of photos to compress
     * @param quality Compression quality (0-100)
     * @param preserveMetadata Whether to preserve photo metadata
     * @return Flow of compression results
     */
    fun compressPhotos(
        photos: List<Photo>,
        quality: Int,
        preserveMetadata: Boolean
    ): Flow<CompressionResult> = flow {
        photos.forEachIndexed { index, photo ->
            val originalFile = File(photo.path)
            if (!originalFile.exists()) return@forEachIndexed
            
            // Get original file directory and name
            val originalDir = originalFile.parentFile
            val originalName = originalFile.nameWithoutExtension
            val fileExtension = originalFile.extension
            
            // Create compressed file in the same directory with "_compressed" suffix
            val outputFile = File(originalDir, "${originalName}_compressed.${fileExtension}")
            
            try {
                val compressedFile = Compressor.compress(context, originalFile) {
                    quality(quality)
                    format(Bitmap.CompressFormat.JPEG)
                    
                    // Optional: limit size if file is very large
                    if (photo.size > 5 * 1024 * 1024) { // > 5MB
                        size(2 * 1024 * 1024) // Target 2MB
                    }
                    
                    // Optional: reduce resolution for very high-res images while keeping aspect ratio
                    if (photo.width > 2000 || photo.height > 2000) {
                        val targetWidth = 2000
                        val targetHeight = if (photo.width > photo.height) {
                            (targetWidth.toFloat() / photo.width * photo.height).toInt()
                        } else {
                            (targetWidth.toFloat() / photo.height * photo.width).toInt()
                        }
                        resolution(targetWidth, targetHeight)
                    }
                }
                
                // Copy to destination in the original folder
                compressedFile.copyTo(outputFile, overwrite = true)
                
                // Delete the temporary compressed file
                compressedFile.delete()
                
                // Delete the original file after successful compression
                originalFile.delete()
                
                // Emit result
                val result = CompressionResult(
                    photo = photo,
                    compressedSize = outputFile.length(),
                    originalSize = photo.size,
                    savedBytes = photo.size - outputFile.length(),
                    compressedFilePath = outputFile.absolutePath
                )
                
                emit(result)
                
            } catch (e: Exception) {
                e.printStackTrace()
                // Continue with next photo
            }
        }
    }

    /**
     * Calculate the potential space savings for a list of photos
     * 
     * @param photos List of photos to analyze
     * @return Estimated space savings in bytes
     */
    fun calculatePotentialSavings(photos: List<Photo>): Long {
        var totalSize = 0L
        photos.forEach { photo ->
            totalSize += photo.size
        }
        
        // Estimate 70% reduction on average (very rough estimate)
        return (totalSize * 0.7).toLong()
    }

    /**
     * Format bytes to human-readable format
     */
    fun formatSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1.0 -> String.format("%.2f GB", gb)
            mb >= 1.0 -> String.format("%.2f MB", mb)
            kb >= 1.0 -> String.format("%.2f KB", kb)
            else -> "$bytes bytes"
        }
    }
} 