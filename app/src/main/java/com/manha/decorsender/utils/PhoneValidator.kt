package com.manha.decorsender.utils

object PhoneValidator {

    /**
     * Cleans a pasted/typed Indian mobile number and returns it in
     * "91XXXXXXXXXX" format (no plus sign, ready for WhatsApp jid use).
     * Returns null if the number is not a valid 10-digit Indian mobile number.
     */
    fun normalizeIndianNumber(raw: String): String? {
        var digits = raw.filter { it.isDigit() }

        // Strip a leading 0 (some people type 0-prefixed numbers)
        if (digits.length == 11 && digits.startsWith("0")) {
            digits = digits.substring(1)
        }

        // Strip country code if present (91 or 0091)
        if (digits.length == 12 && digits.startsWith("91")) {
            digits = digits.substring(2)
        } else if (digits.length == 13 && digits.startsWith("0091")) {
            digits = digits.substring(4)
        } else if (digits.length == 14 && digits.startsWith("0091")) {
            digits = digits.substring(4)
        }

        if (digits.length != 10) return null
        // Indian mobile numbers start with 6, 7, 8 or 9
        if (digits[0] !in charArrayOf('6', '7', '8', '9')) return null

        return "91$digits"
    }

    fun displayFormat(normalized91: String): String {
        // normalized91 e.g. "919824451750"
        val local = normalized91.removePrefix("91")
        return "+91 $local"
    }
}
