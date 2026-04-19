package cat.itacademy.s04.t02.n02.services;

import cat.itacademy.s04.t02.n02.dto.FruitDTO;
import cat.itacademy.s04.t02.n02.exception.FruitNotFoundException;
import cat.itacademy.s04.t02.n02.exception.ProviderNotFoundException;
import cat.itacademy.s04.t02.n02.model.Fruit;
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
public class FruitServiceTest {

    @Mock
    private FruitRepository fruitRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private FruitServiceImpl fruitService;

    private Provider createProvider() {
        return new Provider(1L, "FruitCo", "Spain");
    }

    @Test
    void createFruit_shouldReturnCreatedFruit() {
        Provider provider = createProvider();
        Fruit saved = new Fruit(1L, "Apple", 5, provider);
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(saved);

        FruitDTO input = new FruitDTO(null, "Apple", 5, 1L, null);
        FruitDTO result = fruitService.createFruit(input);

        assertEquals("Apple", result.getName());
        assertEquals(5, result.getWeightInKilos());
        assertEquals(1L, result.getProviderId());
        assertEquals("FruitCo", result.getProviderName());
    }

    @Test
    void createFruit_shouldThrowWhenProviderNotFound() {
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        FruitDTO input = new FruitDTO(null, "Apple", 5, 99L, null);
        assertThrows(ProviderNotFoundException.class, () -> fruitService.createFruit(input));
    }

    @Test
    void getFruitById_shouldReturnFruit() {
        Provider provider = createProvider();
        Fruit fruit = new Fruit(1L, "Apple", 5, provider);
        when(fruitRepository.findById(1L)).thenReturn(Optional.of(fruit));

        FruitDTO result = fruitService.getFruitById(1L);

        assertEquals("Apple", result.getName());
        assertEquals("FruitCo", result.getProviderName());
    }

    @Test
    void getFruitById_shouldThrowWhenNotFound() {
        when(fruitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FruitNotFoundException.class, () -> fruitService.getFruitById(99L));
    }

    @Test
    void getAllFruits_shouldReturnList() {
        Provider provider = createProvider();
        List<Fruit> fruits = Arrays.asList(
                new Fruit(1L, "Apple", 5, provider),
                new Fruit(2L, "Banana", 3, provider)
        );
        when(fruitRepository.findAll()).thenReturn(fruits);

        List<FruitDTO> result = fruitService.getAllFruits();
        assertEquals(2, result.size());
    }

    @Test
    void getFruitsByProviderId_shouldReturnList() {
        Provider provider = createProvider();
        List<Fruit> fruits = Arrays.asList(
                new Fruit(1L, "Apple", 5, provider),
                new Fruit(2L, "Banana", 3, provider)
        );
        when(providerRepository.existsById(1L)).thenReturn(true);
        when(fruitRepository.findByProviderId(1L)).thenReturn(fruits);

        List<FruitDTO> result = fruitService.getFruitsByProviderId(1L);
        assertEquals(2, result.size());
    }

    @Test
    void getFruitsByProviderId_shouldThrowWhenProviderNotFound() {
        when(providerRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProviderNotFoundException.class, () -> fruitService.getFruitsByProviderId(99L));
    }

    @Test
    void updateFruit_shouldReturnUpdatedFruit() {
        Provider provider = createProvider();
        Fruit existing = new Fruit(1L, "Apple", 5, provider);
        Fruit updated = new Fruit(1L, "Banana", 3, provider);
        when(fruitRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(updated);

        FruitDTO input = new FruitDTO(null, "Banana", 3, 1L, null);
        FruitDTO result = fruitService.updateFruit(1L, input);

        assertEquals("Banana", result.getName());
        assertEquals(3, result.getWeightInKilos());
    }

    @Test
    void deleteFruit_shouldDeleteWhenExists() {
        when(fruitRepository.existsById(1L)).thenReturn(true);

        fruitService.deleteFruit(1L);

        verify(fruitRepository).deleteById(1L);
    }

    @Test
    void deleteFruit_shouldThrowWhenNotFound() {
        when(fruitRepository.existsById(99L)).thenReturn(false);

        assertThrows(FruitNotFoundException.class, () -> fruitService.deleteFruit(99L));
    }
}