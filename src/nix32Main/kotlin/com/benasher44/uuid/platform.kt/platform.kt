package com.benasher44.uuid

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.O_RDONLY
import platform.posix.close
import platform.posix.open
import platform.posix.read

internal actual fun getRandomUUIDBytes(): ByteArray {
    val bytes = ByteArray(UUID_BYTES)
    val fd = open("/dev/urandom", O_RDONLY)
    bytes.usePinned {
        read(fd, it.addressOf(0), UUID_BYTES.toUInt())
    }
    close(fd)
    return bytes
}