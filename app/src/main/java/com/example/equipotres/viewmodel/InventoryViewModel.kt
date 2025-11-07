package com.example.equipotres.viewmodel

import androidx.lifecycle.AndroidViewModel
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.equipotres.model.Inventory
import androidx.lifecycle.viewModelScope
import com.example.equipotres.repository.InventoryRepository
import kotlinx.coroutines.launch



class InventoryViewModel (application : Application) : AndroidViewModel(application){
    val context = getApplication<Application>()
    private val inventoryRepository = InventoryRepository(context)
    private val _progreesState = MutableLiveData(false)

    val progressState: LiveData<Boolean> = _progreesState

    fun saveInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progreesState.value = true
            try {
                inventoryRepository.saveInventory(inventory)
                _progreesState.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _progreesState.value = false
            }
        }

    }
}