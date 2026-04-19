package cat.itacademy.s04.t02.n02.services;

import cat.itacademy.s04.t02.n02.dto.ProviderDTO;
import java.util.List;

public interface ProviderService {

    ProviderDTO createProvider(ProviderDTO providerDTO);
    List<ProviderDTO> getAllProviders();
    ProviderDTO getProviderById(Long id);
    ProviderDTO updateProvider(Long id, ProviderDTO providerDTO);
    void deleteProvider(Long id);
}