package com.example.room

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.room.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactDao: ContactDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "contacts.db")
            .build()
        contactDao = db.contactDao()
        binding.saveButton.setOnClickListener {
            saveContact()
        }
        loadContacts()
    }

    private fun saveContact() {
        val lastName = binding.lastNameInput.text.toString()
        val phoneNumber = binding.phoneNumberInput.text.toString()
        if (lastName.isNotEmpty() && phoneNumber.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                contactDao.insert(Contact(0, lastName, phoneNumber))
                loadContacts()
            }
        }
    }

    private fun loadContacts() {
        lifecycleScope.launch(Dispatchers.IO) {
            val contacts = contactDao.getAllContacts()
            withContext(Dispatchers.Main) {
                binding.contactsView.text =
                    contacts.joinToString("\n") { "${it.lastName}: ${it.phoneNumber}" }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_exit -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}