package cat.itacademy.s04.t02.n02.services;

import cat.itacademy.s04.t02.n02.dto.ProviderDTO;
import cat.itacademy.s04.t02.n02.exception.ProviderDuplicateException;
import cat.itacademy.s04.t02.n02.exception.ProviderNotFoundException;
import cat.itacademy.s04.t02.n02.model.Provider;
import cat.itacademy.s04.t02.n02.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.repository.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final FruitRepository fruitRepository;

    public ProviderServiceImpl(ProviderRepository providerRepository, FruitRepository fruitRepository) {
        this.providerRepository = providerRepository;
        this.fruitRepository = fruitRepository;
    }

    @Override
    public ProviderDTO createProvider(ProviderDTO providerDTO) {
        if (providerRepository.existsByName(providerDTO.getName())) {
            throw new ProviderDuplicateException("Provider with name '" + providerDTO.getName() + "' already exists");
        }
        Provider provider = new Provider();
        provider.setName(providerDTO.getName());
        provider.setCountry(providerDTO.getCountry());
        Provider saved = providerRepository.save(provider);
        return toDTO(saved);
    }

    @Override
    public List<ProviderDTO> getAllProviders() {
        return providerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + id));
        return toDTO(provider);
    }

    @Override
    public ProviderDTO updateProvider(Long id, ProviderDTO providerDTO) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found with id: " + id));
        if (!provider.getName().equals(providerDTO.getName()) && providerRepository.existsByName(providerDTO.getName())) {
            throw new ProviderDuplicateException("Provider with name '" + providerDTO.getName() + "' already exists");
        }
        provider.setName(providerDTO.getName());
        provider.setCountry(providerDTO.getCountry());
        Provider updated = providerRepository.save(provider);
        return toDTO(updated);
    }

    @Override
    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ProviderNotFoundException("Provider not found with id: " + id);
        }
        if (fruitRepository.existsByProviderId(id)) {
            throw new ProviderDuplicateException("Cannot delete provider with id: " + id + " because it has associated fruits");
        }
        providerRepository.deleteById(id);
    }

    private ProviderDTO toDTO(Provider provider) {
        ProviderDTO dto = new ProviderDTO();
        dto.setId(provider.getId());
        dto.setName(provider.getName());
        dto.setCountry(provider.getCountry());
        return dto;
    }
}