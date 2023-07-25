package com.example.searchbar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    private lateinit var wordApiService: WordApiService
    var jsonList : List<String>? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://betatest-avd.tunnels.onboardbase.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        wordApiService = retrofit.create(WordApiService::class.java)
        wordApiService.getWords().enqueue(object : Callback<List<WordItem>> {
            override fun onResponse(call: Call<List<WordItem>>, response: Response<List<WordItem>>) {
                if (response.isSuccessful) {
                    val jsonData = response.body().toString()
                    // Do something with the words list
                    Log.i(jsonData,"wordsjson")
                    object : TypeToken<List<WordItem>>() {}.type
                    val words: List<WordItem>? = response.body()
                    Log.i(words.toString(),"words")
                    jsonList = words?.map { item : WordItem -> item.word}
                    Toast.makeText(this@MainActivity,"api passed  ${jsonList?.size}",Toast.LENGTH_SHORT).show()
                    Log.i(jsonList.toString(),"jsonList")
                } else {
                    // Handle error
                    Toast.makeText(this@MainActivity,"api failed",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<WordItem>>, t: Throwable) {
                // Handle failure
                jsonList = listOf("apple","ball","hello");
            }
        })
        setContent {
            Column(modifier = Modifier.fillMaxSize()) {
                var inputValue by remember { mutableStateOf(TextFieldValue()) }
                TopAppBar(
                    title = { Text("Search App") },
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),

                )
                TextField(
                    value = inputValue,
                    onValueChange = { textFieldValue: TextFieldValue ->
                        // Assign the value from the lambda parameter to the inputValue
                        inputValue = textFieldValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Column {
                    val trie  = remember {
                        Trie()
                    }
                    val items = remember{jsonList}
                    if(items!=null) {
                        items.forEach { it: String -> trie.insert(it) }
                        if (!trie.isEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "trie is not null",
                                Toast.LENGTH_SHORT
                            ).show();
                            ShowList(input = inputValue, trie = trie, items)
                        }
                    }
                }

            }

        }
    }
}

@Composable
fun ShowList(input:TextFieldValue,trie:Trie,items:List<String>) {
    val context = LocalContext.current
    val searchResults by remember(input, items) {
        derivedStateOf { trie.search(input.text) }
    }
    if (!input.text.equals("")) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            for (item in searchResults) {
                Text(text = item, modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        Toast
                            .makeText(context, "$item is clicked", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .fillMaxWidth())
            }
        }

    }
}

