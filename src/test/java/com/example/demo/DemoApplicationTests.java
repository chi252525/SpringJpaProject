package com.example.demo;

import com.example.controller.CurrencyController;
import com.example.entity.Currency;
import com.example.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DemoApplicationTests {
    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyController currencyController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCurrencyByCode() {
        Currency currency = new Currency("USD", "US Dollar");
        when(currencyService.findByCode("USD")).thenReturn(currency);
        Currency result = currencyController.getCurrencyByCode("USD");
        assertEquals(currency, result);
        verify(currencyService, times(1)).findByCode("USD");
    }


    @Test
    public void testAddCurrency() {
        Currency currency = new Currency("USD", "US Dollar");
        when(currencyService.save(currency)).thenReturn(currency);
        Currency result = currencyController.addCurrency(currency);
        assertEquals(currency, result);
    }


    @Test
    public void testUpdateCurrency() {
        Currency existingCurrency = new Currency("USD", "US Dollar");
        existingCurrency.setId(1L);
        when(currencyService.findById(1L)).thenReturn(Optional.of(existingCurrency));
        Currency updatedCurrency = new Currency("EUR", "Euro");
        updatedCurrency.setId(1L);
        when(currencyService.save(existingCurrency)).thenReturn(updatedCurrency);
        Currency result = currencyController.updateCurrency(1L, updatedCurrency);
        assertEquals(updatedCurrency, result);
        verify(currencyService, times(1)).findById(1L);
        verify(currencyService, times(1)).save(existingCurrency);
    }

    @Test
    public void testDeleteCurrency() {
        doNothing().when(currencyService).deleteById(1L);
        currencyController.deleteCurrency(1L);
        verify(currencyService, times(1)).deleteById(1L);
    }


}
