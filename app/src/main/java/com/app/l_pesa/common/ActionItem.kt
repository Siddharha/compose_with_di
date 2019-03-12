package com.app.l_pesa.common

import android.graphics.drawable.Drawable
import android.graphics.Bitmap

class ActionItem

@JvmOverloads constructor(actionId: Int = -1,

    var title: String? = null,
    var icon: Drawable? = null) {
    var thumb: Bitmap? = null
    var actionId = -1
    var isSelected: Boolean = false

    var isSticky: Boolean = false

    init {
        this.actionId = actionId
    }

    constructor(icon: Drawable) : this(-1, null, icon) {}

    constructor(actionId: Int, icon: Drawable) : this(actionId, null, icon) {}
}
