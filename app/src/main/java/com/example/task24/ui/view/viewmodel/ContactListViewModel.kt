package com.example.task24.ui.view.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task24.model.Contact
import com.example.task24.repository.ContactRepositoryImp
import com.example.task24.ui.view.uistates.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ContactListViewModel : ViewModel() {
    //data class Contact(val int: Int,val name:String,val phone:String)
    var repo = ContactRepositoryImp()

    private var _id = MutableStateFlow(0)
    val id: StateFlow<Int> = _id

    private var _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private var _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private var _errorName = MutableStateFlow(false)
    val errorName: StateFlow<Boolean> = _errorName

    private var _errorPhone = MutableStateFlow(false)
    val errorPhone: StateFlow<Boolean> = _errorPhone

    private var _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var _contacts = MutableStateFlow<List<Contact>>((emptyList()))
    val contacts: StateFlow<List<Contact>> = _contacts
    private var recentlyDeletedUser: Contact? = null

    init {
        observeSearchQuery()
        fetchContact()

    }

    private fun fetchContact() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                var contacts = repo.getAllContact()
                _contacts.value = contacts
                _uiState.value = UiState.Success(
                    contacts,
                    if (contacts.isEmpty()) "No Contact available" else "Contact fetched successfully!"
                )

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message.toString())
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery.debounce(300).distinctUntilChanged().collect { query ->
                filtterContact(query)
            }
        }

    }

     fun filtterContact(query: String) {
        var contents = (uiState.value as? UiState.Success)?.data ?: emptyList()
        _contacts.value = if (query.isEmpty()) contents else contents.filter {
            it.name.contains(query, ignoreCase = true) || it.phone.contains(
                query,
                ignoreCase = true
            )
        }


    }

    fun updateName(name: String) {
        _name.value = name

    }

    fun updatePhone(phone: String) {
        _phone.value = phone

    }

    fun updateId(id: Int) {
        _id.value = id

    }

    fun upDateSearecQuary(quary: String) {
        _searchQuery.value = quary

    }

    fun addContact() {
        if (name.value.isNotBlank() && phone.value.isNotBlank()) {
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                try {
                    val contact =
                        Contact(
                            id = if (id.value == 0) (0..1000).random() else id.value,
                            name = name.value,
                            phone = phone.value
                        )

                    var contacts = repo.addContact(contact)
                    _contacts.value = contacts
                    _uiState.value = UiState.Success(contacts, "Contact added successfully!")
                } catch (e: Exception) {
                    _uiState.value = UiState.Error("Error adding Contact: ${e.message}")

                }
            }
        } else {
            _errorName.value = name.value.isBlank()
            _errorPhone.value = phone.value.isBlank()

        }

    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            recentlyDeletedUser = contact

            try {
                val itemDelete = repo.deleteContact(contact)
                _contacts.value = itemDelete
                _uiState.value = UiState.Success(itemDelete, "Contact deleted successfully!")


            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error deleting Contact: ${e.message}")

            }
        }
    }

    fun undoDelete() {
        recentlyDeletedUser?.let {
            updateId(it.id)
            updateName(it.name)
            updatePhone(it.phone)

            addContact()

        }
    }

    fun clear() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val clearContact = repo.clearContact()
                _uiState.value = UiState.Success(clearContact, "Contact clear successfully!")
                _contacts.value = clearContact

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error clear Contact: ${e.message}")
            }
        }
    }
    fun clearInputFields() {
        updateName("")
        updatePhone("")
    }

}