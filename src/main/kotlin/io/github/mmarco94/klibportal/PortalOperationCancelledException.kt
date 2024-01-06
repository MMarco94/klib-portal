package io.github.mmarco94.klibportal

class PortalOperationCancelledException(val cancelledByUser: Boolean) : RuntimeException(
    if (cancelledByUser) {
        "Portal operation cancelled by user"
    } else {
        "Portal operation cancelled"
    }
)