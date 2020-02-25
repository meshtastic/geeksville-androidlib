package com.geeksville.android

import android.content.Context
import android.widget.Toast

/// show a toast
fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()