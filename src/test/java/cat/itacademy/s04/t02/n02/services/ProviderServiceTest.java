package cat.itacademy.s04.t02.n02.services;

import cat.itacademy.s04.t02.n02.dto.ProviderDTO;
import cat.itacademy.s04.t02.n02.exception.ProviderDuplicateException;
import cat.itacademy.s04.t02.n02.exception.ProviderNotFoundException;
import cat.itacademy.s04.t02.n02.model.Provider;
import cat.itacademy.s04.t02.n02.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.repository.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private FruitRepository fruitRepository;

    @InjectMocks
    private ProviderServiceImpl providerService;

    @Test
    void createProvider_shouldReturnCreatedProvider() {
        Provider saved = new Provider(1L, "FruitCo", "Spain");
        when(providerRepository.existsByName("FruitCo")).thenReturn(false);
        when(providerRepository.save(any(Provider.class))).thenReturn(saved);

        ProviderDTO input = new ProviderDTO(null, "FruitCo", "Spain");
        ProviderDTO result = providerService.createProvider(input);

        assertEquals("FruitCo", result.getName());
        assertEquals("Spain", result.getCountry());
    }

    @Test
    void createProvider_shouldThrowWhenDuplicateName() {
        when(providerRepository.existsByName("FruitCo")).thenReturn(true);

        ProviderDTO input = new ProviderDTO(null, "FruitCo", "Spain");
        assertThrows(ProviderDuplicateException.class, () -> providerService.createProvider(input));
    }

    @Test
    void getAllProviders_shouldReturnList() {
        List<Provider> providers = Arrays.asList(
                new Provider(1L, "FruitCo", "Spain"),
                new Provider(2L, "TropicalFruits", "Colombia")
        );
        when(providerRepository.findAll()).thenReturn(providers);

        List<ProviderDTO> result = providerService.getAllProviders();
        assertEquals(2, result.size());
    }

    @Test
    void getProviderById_shouldReturnProvider() {
        Provider provider = new Provider(1L, "FruitCo", "Spain");
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));

        ProviderDTO result = providerService.getProviderById(1L);
        assertEquals("FruitCo", result.getName());
    }

    @Test
    void getProviderById_shouldThrowWhenNotFound() {
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProviderNotFoundException.class, () -> providerService.getProviderById(99L));
    }

    @Test
    void updateProvider_shouldReturnUpdatedProvider() {
        Provider existing = new Provider(1L, "FruitCo", "Spain");
        Provider updated = new Provider(1L, "FruitCo Updated", "Portugal");
        when(providerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(providerRepository.existsByName("FruitCo Updated")).thenReturn(false);
        when(providerRepository.save(any(Provider.class))).thenReturn(updated);

        ProviderDTO input = new ProviderDTO(null, "FruitCo Updated", "Portugal");
        ProviderDTO result = providerService.updateProvider(1L, input);

        assertEquals("FruitCo Updated", result.getName());
        assertEquals("Portugal", result.getCountry());
    }

    @Test
    void deleteProvider_shouldDeleteWhenNoFruits() {
        when(providerRepository.existsById(1L)).thenReturn(true);
        when(fruitRepository.existsByProviderId(1L)).thenReturn(false);

        providerService.deleteProvider(1L);

        verify(providerRepository).deleteById(1L);
    }

    @Test
    void deleteProvider_shouldThrowWhenHasFruits() {
        when(providerRepository.existsById(1L)).thenReturn(true);
        when(fruitRepository.existsByProviderId(1L)).thenReturn(true);

        assertThrows(ProviderDuplicateException.class, () -> providerService.deleteProvider(1L));
    }

    @Test
    void deleteProvider_shouldThrowWhenNotFound() {
        when(providerRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProviderNotFoundException.class, () -> providerService.deleteProvider(99L));
    }
}