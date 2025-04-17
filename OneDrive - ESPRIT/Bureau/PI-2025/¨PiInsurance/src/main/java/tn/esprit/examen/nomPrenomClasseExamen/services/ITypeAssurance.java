package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.DemandeAssurance;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TypeAssurance;
import java.util.List;
import java.util.Optional;

public interface ITypeAssurance {
    List<TypeAssurance> getAllTypeAssurances();
    Optional<TypeAssurance> getTypeAssuranceById(Long id);
    TypeAssurance createTypeAssurance(TypeAssurance typeAssurance);
    TypeAssurance updateTypeAssurance(Long id, TypeAssurance typeAssurance);
    boolean deleteTypeAssurance(Long id);

}