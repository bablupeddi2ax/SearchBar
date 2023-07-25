package com.example.searchbar

import android.util.Log

class Trie {
    private val root = TrieNode()
    fun isEmpty(): Boolean{
        return root.children.isEmpty();
    }
    fun insert(word: String) {
        var node = root
        for (char in word) {
            node = node.children.getOrPut(char) { TrieNode() }
        }
        node.isEndOfWord = true
    }

    fun search(prefix: String): List<String> {
        val result = mutableListOf<String>()
        var node = root
        for (char in prefix) {
            node = node.children[char] ?: return emptyList()
        }
        collectWords(node, prefix, result)
        Log.i("$result","result")
        return result
    }

    private fun collectWords(node: TrieNode, prefix: String, result: MutableList<String>) {
        if (node.isEndOfWord) {
            result.add(prefix)
        }
        for ((char, childNode) in node.children) {
            collectWords(childNode, prefix + char, result)
        }
    }
}

class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var isEndOfWord = false
}