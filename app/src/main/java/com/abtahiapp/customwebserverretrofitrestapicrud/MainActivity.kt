package com.abtahiapp.customwebserverretrofitrestapicrud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var etId: EditText
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var tvResult: TextView
    private lateinit var btnGet: Button
    private lateinit var btnPost: Button
    private lateinit var btnPut: Button
    private lateinit var btnDelete: Button

    private val apiService = RetrofitInstance.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etId = findViewById(R.id.et_id)
        etName = findViewById(R.id.et_name)
        etDescription = findViewById(R.id.et_description)
        tvResult = findViewById(R.id.tv_result)
        btnGet = findViewById(R.id.btn_get)
        btnPost = findViewById(R.id.btn_post)
        btnPut = findViewById(R.id.btn_put)
        btnDelete = findViewById(R.id.btn_delete)

        btnGet.setOnClickListener { getItem() }
        btnPost.setOnClickListener { createItem() }
        btnPut.setOnClickListener { updateItem() }
        btnDelete.setOnClickListener { deleteItem() }

    }

    private fun getItem() {
        val idText = etId.text.toString()
        if (idText.isEmpty()) {
            apiService.getItems().enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                    if (response.isSuccessful) {
                        val items = response.body() ?: emptyList()
                        tvResult.text = if (items.isNotEmpty()) {
                            "${items.joinToString(separator = "\n") { "${it.id}: ${it.name}, ${it.course}" }}"
                        } else {
                            "No items found."
                        }
                    } else {
                        tvResult.text = "GET ALL Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                    tvResult.text = "GET ALL Failure: ${t.message}"
                }
            })
        } else {
            val id = idText.toIntOrNull()
            if (id != null) {
                apiService.getItem(id).enqueue(object : Callback<Item> {
                    override fun onResponse(call: Call<Item>, response: Response<Item>) {
                        if (response.isSuccessful) {
                            tvResult.text = "${response.body()?.let { "${it.id}: ${it.name}, ${it.course}" }}"
                        } else {
                            tvResult.text = "GET Error: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<Item>, t: Throwable) {
                        tvResult.text = "GET Failure: ${t.message}"
                    }
                })
            } else {
                tvResult.text = "Invalid ID. Please enter a valid numeric ID."
            }
        }
    }

    private fun createItem() {
        val id = etId.text.toString().toIntOrNull() ?: 0
        val name = etName.text.toString()
        val course = etDescription.text.toString()
        val newItem = Item(id = id, name = name, course = course)

        apiService.createItem(newItem).enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (response.isSuccessful) {
                    tvResult.text = "POST Success: ${response.body()}"
                } else {
                    tvResult.text = "POST Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                tvResult.text = "POST Failure: ${t.message}"
            }
        })
    }

    private fun updateItem() {
        val id = etId.text.toString().toIntOrNull()
        if (id != null) {
            val name = etName.text.toString()
            val course = etDescription.text.toString()
            val updatedItem = Item(id = id, name = name, course = course)

            apiService.updateItem(id, updatedItem).enqueue(object : Callback<Item> {
                override fun onResponse(call: Call<Item>, response: Response<Item>) {
                    if (response.isSuccessful) {
                        tvResult.text = "PUT Success: ${response.body()}"
                    } else {
                        tvResult.text = "PUT Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Item>, t: Throwable) {
                    tvResult.text = "PUT Failure: ${t.message}"
                }
            })
        } else {
            tvResult.text = "Please enter a valid ID"
        }
    }

    private fun deleteItem() {
        val id = etId.text.toString().toIntOrNull()
        if (id != null) {
            apiService.deleteItem(id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        tvResult.text = "DELETE Success: ${response.code()}"
                    } else {
                        tvResult.text = "DELETE Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    tvResult.text = "DELETE Failure: ${t.message}"
                }
            })
        } else {
            tvResult.text = "Please enter a valid ID"
        }
    }
}