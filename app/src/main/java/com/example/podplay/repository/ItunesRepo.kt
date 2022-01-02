package com.example.podplay.repository

import com.example.podplay.service.ItunesService


class ItunesRepo(private val itunesService: ItunesService) {

    suspend fun searchByTerm(term: String) =
        itunesService.searchPodcastByTerm(term)
}