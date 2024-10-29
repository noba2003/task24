package com.example.task24.repository

import com.example.task24.model.Contact

class ContactRepositoryImp : ContactRepository {
     private var list = mutableListOf<Contact>()
    override suspend fun getAllContact(): List<Contact> {
        return list
    }

    override suspend fun deleteContact(contact: Contact): List<Contact> {
        list.remove(contact)
        return list
    }

    override suspend fun addContact(contact: Contact): List<Contact> {
        list.add(contact)
        return list
    }

    override suspend fun clearContact(): List<Contact> {
        list.clear()
        return list
    }
}