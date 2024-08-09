## Retrofit Web Server (Kotlin & Node.js)
This documentation provides details for the RESTful API created using Node.js and Express. The API includes endpoints for creating, reading, updating, and deleting items.

Creating a web server with node and express. The in client side of android with Kotlin used retrofit for HTTP connection with server.

Postman: https://documenter.getpostman.com/view/36920253/2sA3s3GWia

### Base URL

`http://localhost:1113`

``` kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
}

 ```

Create a `ApiService` for RESTful API:

``` kotlin
import retrofit2.Call
import retrofit2.http.*
interface ApiService {
    @GET("get")
    fun getItems(): Call<List<Item>>
    @GET("get/{id}")
    fun getItem(@Path("id") id: Int): Call<Item>
    @POST("post")
    fun createItem(@Body item: Item): Call<Item>
    @PUT("update/{id}")
    fun updateItem(@Path("id") id: Int, @Body item: Item): Call<Item>
    @DELETE("delete/{id}")
    fun deleteItem(@Path("id") id: Int): Call<Void>
}

 ```

Create a data class `Item` :

``` kotlin
data class Item (
    val id: Int = 0,
    val name: String,
    val course: String
)

 ```

Initialize Retrofit in a singleton

``` kotlin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.13:1113/"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

 ```

`MainActivity.kt`

``` kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
class MainActivity : AppCompatActivity() {
    private val apiService = RetrofitInstance.apiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

 ```

`network-security-config.xml` in res/xml:

``` xml
<network-security-config>
<base-config cleartextTrafficPermitted="true">
    <trust-anchors>
        <certificates src="system" />
    </trust-anchors>
</base-config>
</network-security-config>

 ```

`AndroidManifest.xml`

``` xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET"></uses-permission>
            android:networkSecurityConfig="@xml/network_security_config"
        android:usesCleartextTraffic="true"
    </application>
</manifest>

 ```

Server Setup:

`mkdir node-web-server`

`cd node-web-server`

`npm init -y`

`npm install express`

`Server.js :`

``` javascript
const express = require('express')
const app = express()
const port = 1113
app.use(express.json())
let data = [
    { id: 1, name: 'Alex', course: 'Computer Science'},
    { id: 2, name: 'John', course: 'Electrical'},
    { id: 3, name: 'Charlie', course: 'Civil'},
    { id: 4, name: 'JohnWick', course: 'Mathematics' },
    { id: 5, name: 'Jane', course: 'Physics' },
    { id: 6, name: 'Smith', course: 'Chemistry' }
]
//Read all items
app.get('/get', (req, res) => {
    res.json(data)
})
//Read specific item
app.get('/get/:id', (req, res) => {
    const id = parseInt(req.params.id)
    const item = data.find( i => i.id === id)
    if(item){
        res.json(item)
    } else{
        res.status(404).send('Item Not Fund')
    }
})
//Create a new item
app.post('/post', (req, res) => {
    const newItem =  { id: data.length + 1, ...req.body}
    data.push(newItem)
    res.status(201).json(newItem)
})
//Update an item
app.put('/update/:id', (req, res) => {
    const id = parseInt(req.params.id);
    const index = data.findIndex(i => i.id === id);
    if (index !== -1) {
      data[index] = { id, ...req.body };
      res.json(data[index]);
    } else {
      res.status(404).send('Item not found');
    }
});
// Delete an item
app.delete('/delete/:id', (req, res) => {
    const id = parseInt(req.params.id);
    const index = data.findIndex(i => i.id === id);
    if (index !== -1) {
      const deletedItem = data.splice(index, 1);
      res.json(deletedItem[0]);
      res.send(`Item deleted`);
    } else {
      res.status(404).send('Item not found');
    }
});
app.get('/', (req, res) => {
  res.send(`Server running successfully`);
});
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}/`)
})

 ```

`package.json:`

``` json
{
  "name": "web-server",
  "version": "1.0.0",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
  "description": "",
  "dependencies": {
    "express": "^4.19.2"
  }
}

 ```

`node server.js`