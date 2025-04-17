package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TypeAssurance;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TypeAssuranceRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeAssuranceServiceImpl implements ITypeAssurance {

    private final TypeAssuranceRepository typeAssuranceRepository;

    @Override
    public List<TypeAssurance> getAllTypeAssurances() {
        return typeAssuranceRepository.findAll();
    }

    @Override
    public Optional<TypeAssurance> getTypeAssuranceById(Long id) {
        return typeAssuranceRepository.findById(id);
    }

    @Override
    public TypeAssurance createTypeAssurance(TypeAssurance typeAssurance) {
        return typeAssuranceRepository.save(typeAssurance);
    }

    @Override
    public TypeAssurance updateTypeAssurance(Long id, TypeAssurance typeAssurance) {
        return typeAssuranceRepository.findById(id).map(existingType -> {
            existingType.setNom(typeAssurance.getNom());
            return typeAssuranceRepository.save(existingType);
        }).orElseThrow(() -> new RuntimeException("TypeAssurance non trouvé avec ID " + id));
    }

    @Override
    public boolean deleteTypeAssurance(Long id) {
        if (typeAssuranceRepository.existsById(id)) {
            typeAssuranceRepository.deleteById(id);
            return true;  // ✅ Successfully deleted
        }
        return false;  // ✅ TypeAssurance not found
    }

    public Map<String, Long> getTypeAssuranceStatistics() {
        List<TypeAssurance> typeAssurances = typeAssuranceRepository.findAll();
        return typeAssurances.stream()
                .collect(Collectors.groupingBy(TypeAssurance::getNom, Collectors.counting()));
    }
    public List<TypeAssurance> searchByKeyword(String keyword) {
        return typeAssuranceRepository.findByNomContainingIgnoreCase(keyword);
    }

}
