package com.example.task24.repository

import com.example.task24.model.Contact

interface ContactRepository {
   suspend fun getAllContact():List<Contact>
   suspend fun deleteContact(contact: Contact):List<Contact>
   suspend fun addContact(contact: Contact):List<Contact>
   suspend fun clearContact():List<Contact>
}