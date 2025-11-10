package com.example.equipotres.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.equipotres.model.Inventory
import com.example.equipotres.model.Product
import androidx.lifecycle.viewModelScope
import com.example.equipotres.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel (application : Application) : AndroidViewModel(application){
    val context = getApplication<Application>()

    //Instancia del Repository
    private val inventoryRepository = InventoryRepository(context)

    ////Actualizacion de RecyclerView
    private val _listInventory = MutableLiveData<MutableList<Inventory>>()
    val listInventory: LiveData<MutableList<Inventory>> get() = _listInventory

    ////Control de ProgressBar
    private val _progreesState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progreesState

    //para almacenar una lista de productos
    private val _listProducts = MutableLiveData<MutableList<Product>>()
    val listProducts: LiveData<MutableList<Product>> = _listProducts

    ////Guardar un registro en la BD
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

    ////Obtiene toda la lista de inventario desde el repositorio y la carga en _listInventory
    fun getListInventory() {
        viewModelScope.launch {
            _progreesState.value = true
            try {
                _listInventory.value = inventoryRepository.getListInventory()
                _progreesState.value = false
            } catch (e: Exception) {
                _progreesState.value = false
            }

        }
    }

    //Elimina un registro específico del inventario en la base de datos.
    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progreesState.value = true
            try {
                inventoryRepository.deleteInventory(inventory)
                _progreesState.value = false
            } catch (e: Exception) {
                _progreesState.value = false
            }

        }
    }

    //Actualiza los datos de un inventario existente.
    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progreesState.value = true
            try {
                inventoryRepository.updateRepositoy(inventory)
                _progreesState.value = false
            } catch (e: Exception) {
                _progreesState.value = false
            }
        }   
    }

    //Calcula el total
    fun totalProducto(price: Int, quantity: Int): Double {
        val total = price * quantity
        return total.toDouble()
    }
}