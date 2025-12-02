package com.example.equipotres.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.equipotres.model.Inventory
import com.example.equipotres.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class InventoryViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule() // Ejecuta LiveData de forma sincrónica en pruebas

    lateinit var inventoryViewModel: InventoryViewModel

    @Mock
    lateinit var inventoryRepository: InventoryRepository // Mock del repositorio real

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Inicializa los mocks
        inventoryViewModel = InventoryViewModel(inventoryRepository) // Se crea el ViewModel usando el mock
    }

    @Test
    fun `test totalProducto`() {
        // GIVEN → Datos de entrada para la prueba
        val price = 10
        val quantity = 5
        val expectedResult = (price * quantity).toDouble()

        // WHEN → Se ejecuta el metodo a probar
        val result = inventoryViewModel.totalProducto(price, quantity)

        // THEN → Se valida que el resultado sea el esperado
        assertEquals(expectedResult, result,0.0)
    }

    @Test
    fun testSaveInventory_success() = runBlocking {
        // GIVEN → Dispatcher y objeto a guardar
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = Inventory(id = "1", name = "Item1", price = 10, quantity = 5)

        // WHEN → Se llama el metodo del ViewModel
        inventoryViewModel.saveInventory(inventory)

        // THEN → Verificamos que el repositorio fue invocado correctamente
        verify(inventoryRepository).saveInventory(inventory)
    }

    @Test
    fun `test método getListInventory`() = runBlocking {
        // GIVEN → Datos que queremos que devuelva el mock
        val mockListInventory = mutableListOf(
            Inventory(id = "1", name = "Zapatos", price = 20, quantity = 3),
            Inventory(id = "2", name = "Camisa", price = 15, quantity = 2)
        )

        // WHEN → Mockeamos la respuesta del repositorio
        org.mockito.Mockito.`when`(inventoryRepository.getListInventory()).thenReturn(mockListInventory)

        // LLamamos al metodo del ViewModel
        inventoryViewModel.getListInventory()

        // THEN → Validamos que la LiveData se haya actualizado correctamente
        assertEquals(mockListInventory, inventoryViewModel.listInventory.value)
    }

    @Test
    fun `test método deleteInventory`() = runBlocking {
        // GIVEN
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = Inventory(id = 1, name = "Item1", price = 10, quantity = 5)

        // WHEN
        inventoryViewModel.deleteInventory(inventory)

        // THEN → Verificar que se llamó al repositorio
        verify(inventoryRepository).deleteInventory(inventory)
    }

    @Test
    fun `test método updateInventory`() = runBlocking {
        // GIVEN
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val inventory = Inventory(id = 1, name = "Item1", price = 12, quantity = 4)

        // WHEN
        inventoryViewModel.updateInventory(inventory)

        // THEN → Verificar que se llamó al repositorio
        verify(inventoryRepository).updateRepositoy(inventory)
    }





}