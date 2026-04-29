package com.example.dynalar_frontend_v1.repository

import com.example.dynalar_frontend_v1.model.Material
import com.example.dynalar_frontend_v1.network.RetrofitClient

class MaterialRepository {
    private val materialApiService = RetrofitClient.materialApiService

    suspend fun getAllMaterials(): List<Material> {
        return materialApiService.getAllMaterials()
    }

    suspend fun getMaterialById(id: Long): Material {
        return materialApiService.getMaterialById(id)
    }

    suspend fun createMaterial(material: Material): Material {
        return materialApiService.createMaterial(material)
    }

    suspend fun updateMaterial(id: Long, material: Material): Material {
        return materialApiService.updateMaterial(id, material)
    }

    suspend fun deleteMaterial(id: Long) {
        materialApiService.deleteMaterial(id)
    }
}
