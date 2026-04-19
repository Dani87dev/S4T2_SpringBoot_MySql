package cat.itacademy.s04.t02.n02.services;

import cat.itacademy.s04.t02.n02.dto.FruitDTO;
import cat.itacademy.s04.t02.n02.exception.FruitNotFoundException;
import cat.itacademy.s04.t02.n02.exception.ProviderNotFoundException;
import cat.itacademy.s04.t02.n02.model.Fruit;
import cat.itacademy.s04.t02.n02.model.Provider;
import cat.itacademy.s04.t02.n02.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.repository.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FruitServiceImpl implements FruitService {

    private final FruitRepository fruitRepository;
    private final ProviderRepository providerRepository;

    public FruitServiceImpl(FruitRepository fruitRepository, ProviderRepository providerRepository) {
        this.fruitRepository = fruitRepository;
        this.providerRepository = providerRepository;
    }

    @Override
    public FruitDTO createFruit(FruitDTO fruitDTO) {
        Provider provider = providerRepository.findById(fruitDTO.getProviderId())
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + fruitDTO.getProviderId()));
        Fruit fruit = new Fruit();
        fruit.setName(fruitDTO.getName());
        fruit.setWeightInKilos(fruitDTO.getWeightInKilos());
        fruit.setProvider(provider);
        Fruit saved = fruitRepository.save(fruit);
        return toDTO(saved);
    }

    @Override
    public FruitDTO getFruitById(Long id) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new FruitNotFoundException("Fruit not found with id: " + id));
        return toDTO(fruit);
    }

    @Override
    public List<FruitDTO> getAllFruits() {
        return fruitRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FruitDTO> getFruitsByProviderId(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new ProviderNotFoundException("Provider not found with id: " + providerId);
        }
        return fruitRepository.findByProviderId(providerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FruitDTO updateFruit(Long id, FruitDTO fruitDTO) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new FruitNotFoundException("Fruit not found with id: " + id));
        Provider provider = providerRepository.findById(fruitDTO.getProviderId())
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + fruitDTO.getProviderId()));
        fruit.setName(fruitDTO.getName());
        fruit.setWeightInKilos(fruitDTO.getWeightInKilos());
        fruit.setProvider(provider);
        Fruit updated = fruitRepository.save(fruit);
        return toDTO(updated);
    }

    @Override
    public void deleteFruit(Long id) {
        if (!fruitRepository.existsById(id)) {
            throw new FruitNotFoundException("Fruit not found with id: " + id);
        }
        fruitRepository.deleteById(id);
    }

    private FruitDTO toDTO(Fruit fruit) {
        FruitDTO dto = new FruitDTO();
        dto.setId(fruit.getId());
        dto.setName(fruit.getName());
        dto.setWeightInKilos(fruit.getWeightInKilos());
        dto.setProviderId(fruit.getProvider().getId());
        dto.setProviderName(fruit.getProvider().getName());
        return dto;
    }
}