/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.example.movies.repository.terndingmovies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import app.example.movies.model.TrendingMovies
import app.example.movies.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class DataSourceFactory(
    private val apiService: ApiService
) : DataSource.Factory<Int, TrendingMovies.Movie>() {

    private val _sourceLiveData = MutableLiveData<MoviesDataSource>()
    val sourceLiveData: LiveData<MoviesDataSource> get() = _sourceLiveData

    override fun create(): DataSource<Int, TrendingMovies.Movie> {
//        todo inject
        val source = MoviesDataSource(
            apiService,
            CoroutineScope(Job() + Dispatchers.IO)
        )
        _sourceLiveData.postValue(source)
        return source
    }
}
