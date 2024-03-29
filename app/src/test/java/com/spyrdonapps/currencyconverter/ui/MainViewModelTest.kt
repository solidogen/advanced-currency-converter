package com.spyrdonapps.currencyconverter.ui

import com.nhaarman.mockitokotlin2.mock
import com.spyrdonapps.currencyconverter.data.repository.CurrencyRepository
import com.spyrdonapps.currencyconverter.test.data.CurrenciesTestData
import com.spyrdonapps.currencyconverter.test.data.TestExceptions.illegalArgumentException
import com.spyrdonapps.currencyconverter.test.data.TestExceptions.ioException
import com.spyrdonapps.currencyconverter.test.util.InstantTaskExecutorRule
import com.spyrdonapps.currencyconverter.test.util.captureValues
import com.spyrdonapps.currencyconverter.util.state.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    // region constants

    // endregion constants

    // region helper fields

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val mockCurrencyRepository: CurrencyRepository = mock()

    // endregion helper fields

    private lateinit var classUnderTest: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        classUnderTest = MainViewModel(mockCurrencyRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `mainViewModel, remote data available, currenciesLiveData had loading and success state with correct data`() {
        runBlocking {
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { CurrenciesTestData.currencies }
            classUnderTest.currenciesLiveData.captureValues {
                classUnderTest.initialize()
                assertSendsValues(100, Result.Loading, Result.Success(CurrenciesTestData.currencies))
            }
        }
    }

    @Test
    fun `mainViewModel, remote data error and cached data available, currenciesLiveData had loading and error state with data from cache`() {
        runBlocking {
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { throw ioException }
            `when`(mockCurrencyRepository.getCurrenciesFromCache()).thenAnswer { CurrenciesTestData.currencies }
            classUnderTest.currenciesLiveData.captureValues {
                classUnderTest.initialize()
                assertSendsValues(100, Result.Loading, Result.Error(ioException, CurrenciesTestData.currencies))
            }
        }
    }

    @Test
    fun `mainViewModel, remote data error and cached data error, currenciesLiveData had loading and error state with no data`() {
        runBlocking {
            `when`(mockCurrencyRepository.getCurrenciesFromRemote()).thenAnswer { throw ioException }
            `when`(mockCurrencyRepository.getCurrenciesFromCache()).thenAnswer { throw illegalArgumentException }
            classUnderTest.currenciesLiveData.captureValues {
                classUnderTest.initialize()
                assertSendsValues(100, Result.Loading, Result.Error(illegalArgumentException, null))
            }
        }
    }

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}