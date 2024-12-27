package webshop.models


import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String)

class NotFoundException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)