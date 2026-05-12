package com.rentacar.notifications

import android.util.Log
import com.rentacar.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Sends booking-confirmation emails via EmailJS (free tier: 200 emails/month).
 * No backend required — works with a plain HTTPS POST from the app.
 *
 * ── One-time EmailJS setup ────────────────────────────────────────────────────
 * 1. Sign up at https://www.emailjs.com
 * 2. Email Services → Add Service → connect your Gmail account.
 *    Copy the Service ID (e.g. "service_abc123") → replace SERVICE_ID below.
 * 3. Email Templates → Create Template.
 *    Set "To email" to {{to_email}} (or hard-code najdoskistefan1@gmail.com).
 *    Use these variables anywhere in the Subject / Body:
 *      {{customer_name}}   {{customer_email}}
 *      {{car_name}}        {{start_date}}   {{end_date}}
 *      {{total_price}}     {{booking_id}}   {{timestamp}}
 *    Copy the Template ID → replace TEMPLATE_ID below.
 * 4. Account → API Keys → copy your Public Key → replace PUBLIC_KEY below.
 *
 * ── Upgrade path (Firebase Cloud Functions / Blaze plan) ─────────────────────
 * Replace sendBookingConfirmation() body with a call to a Cloud Function that
 * uses Nodemailer or SendGrid. The caller in PaymentViewModel stays identical.
 * ─────────────────────────────────────────────────────────────────────────────
 */
object EmailNotificationService {

    private const val EMAILJS_URL = "https://api.emailjs.com/api/v1.0/email/send"
    private const val FROM_NAME   = "RentaCar MK"

    private val SERVICE_ID   get() = BuildConfig.EMAILJS_SERVICE_ID
    private val TEMPLATE_ID  get() = BuildConfig.EMAILJS_TEMPLATE_ID
    private val PUBLIC_KEY   get() = BuildConfig.EMAILJS_PUBLIC_KEY
    private val ADMIN_EMAIL  get() = BuildConfig.EMAILJS_ADMIN_EMAIL

    private val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val tsFmt   = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    private const val TAG = "EmailNotificationService"

    /**
     * Sends a booking-confirmation email to [ADMIN_EMAIL].
     * Throws on network or API error — callers should wrap in try/catch
     * and treat failure as non-blocking.
     */
    suspend fun sendBookingConfirmation(
        customerName : String,
        customerEmail: String,
        carName      : String,
        startDate    : Long,
        endDate      : Long,
        totalPrice   : Double,
        bookingId    : String,
        timestamp    : Long
    ) = withContext(Dispatchers.IO) {

        val templateParams = JSONObject().apply {
            put("to_email",       ADMIN_EMAIL)
            put("from_name",      FROM_NAME)
            put("customer_name",  customerName.ifBlank { "Unknown customer" })
            put("customer_email", customerEmail.ifBlank { "—" })
            put("car_name",       carName)
            put("start_date",     dateFmt.format(Date(startDate)))
            put("end_date",       dateFmt.format(Date(endDate)))
            put("total_price",    String.format(Locale.US, "$%.2f", totalPrice))
            put("booking_id",     bookingId)
            put("timestamp",      tsFmt.format(Date(timestamp)))
        }

        val payload = JSONObject().apply {
            put("service_id",      SERVICE_ID)
            put("template_id",     TEMPLATE_ID)
            put("user_id",         PUBLIC_KEY)
            put("template_params", templateParams)
        }

        Log.d(TAG, "sendBookingConfirmation: posting to EmailJS — bookingId=$bookingId customer=$customerEmail")

        val conn = (URL(EMAILJS_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("origin", "http://localhost")
            doOutput        = true
            connectTimeout  = 15_000
            readTimeout     = 15_000
        }
        try {
            conn.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(payload.toString()) }

            val code = conn.responseCode
            if (code == 200) {
                Log.d(TAG, "sendBookingConfirmation: success")
            } else {
                val body = runCatching { conn.errorStream?.bufferedReader()?.readText() }.getOrNull() ?: ""
                Log.w(TAG, "sendBookingConfirmation: HTTP $code — $body")
                throw Exception("EmailJS HTTP $code: $body")
            }
        } finally {
            conn.disconnect()
        }
    }
}
