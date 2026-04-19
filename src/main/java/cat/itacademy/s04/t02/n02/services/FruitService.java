package cat.itacademy.s04.t02.n02.services;

import cat.itacademy.s04.t02.n02.dto.FruitDTO;
import java.util.List;

public interface FruitService {

    FruitDTO createFruit(FruitDTO fruitDTO);
    FruitDTO getFruitById(Long id);
    List<FruitDTO> getAllFruits();
    List<FruitDTO> getFruitsByProviderId(Long providerId);
    FruitDTO updateFruit(Long id, FruitDTO fruitDTO);
    void deleteFruit(Long id);
}