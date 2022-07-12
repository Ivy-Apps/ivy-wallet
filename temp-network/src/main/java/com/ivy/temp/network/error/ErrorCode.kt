package com.ivy.wallet.io.network.error

enum class ErrorCode(val code: Int) {
    SERVER_EXCEPTION(666),
    PARSE(-2),
    INPUT(-1),

    STATE_ERROR(7),

    SECURITY(13),
    PERMISSION_ERROR(14),

    CATEGORY_NOT_FOUND(4041),
    LABEL_NOT_FOUND(4042),
    TASK_NOT_FOUND(4043),
    NOTE_NOT_FOUND(4044),
    EVENT_NOT_FOUND(4045),
    CUSTOM_FIELD_NOT_FOUND(4046),

    NOT_IVY_ATTACHMENT(7404),

    UNKNOWN(-666)
}