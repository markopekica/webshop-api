package webshop.utils

import webshop.models.CreateProductRequest
import webshop.models.UpdateProductRequest

object ValidationUtils {
    fun validateCreateRequest(request: CreateProductRequest): List<String> {
        val errors = mutableListOf<String>()
        if (request.name.isBlank()) errors.add("Name cannot be blank")
        if (request.price <= 0) errors.add("Price must be greater than 0")
        return errors
    }

    fun validateUpdateRequest(request: UpdateProductRequest): List<String> {
        val errors = mutableListOf<String>()
        if (request.name.isBlank()) errors.add("Name cannot be blank")
        if (request.price <= 0) errors.add("Price must be greater than 0")
        return errors
    }
}