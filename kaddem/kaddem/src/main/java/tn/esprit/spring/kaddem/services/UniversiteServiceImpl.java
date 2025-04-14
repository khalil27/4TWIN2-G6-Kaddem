package tn.esprit.spring.kaddem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class UniversiteServiceImpl implements IUniversiteService {

    private static final Logger logger = LoggerFactory.getLogger(UniversiteServiceImpl.class);

    @Autowired
    UniversiteRepository universiteRepository;
    @Autowired
    DepartementRepository departementRepository;

    public List<Universite> retrieveAllUniversites(){
        logger.info("Appel de retrieveAllUniversites()");
        List<Universite> result =(List<Universite>) universiteRepository.findAll();
        logger.debug("Universités récupérées : {}", result);
        return result;
    }

    public Universite addUniversite(Universite u){
        logger.info("Ajout d’une université : {}", u.getNomUniv());
        Universite saved = universiteRepository.save(u);
        logger.debug("Université enregistrée avec ID : {}", saved.getIdUniv());
        return saved;
    }

    public Universite updateUniversite(Universite u){
        logger.info("Mise à jour de l’université ID : {}", u.getIdUniv());
        Universite updated = universiteRepository.save(u);
        logger.debug("Université mise à jour : {}", updated);
        return updated;
    }

    public Universite retrieveUniversite(Integer idUniversite){
        logger.info("Récupération de l’université ID : {}", idUniversite);
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        if (u != null)
            logger.debug("Université trouvée : {}", u.getNomUniv());
        else
            logger.warn("Université ID {} non trouvée", idUniversite);
        return u;
    }

    public void deleteUniversite(Integer idUniversite){
        logger.warn("Suppression de l’université ID : {}", idUniversite);
        universiteRepository.delete(retrieveUniversite(idUniversite));
    }

    public void assignUniversiteToDepartement(Integer idUniversite, Integer idDepartement){
        logger.info("Assignation du département {} à l’université {}", idDepartement, idUniversite);
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        Departement d = departementRepository.findById(idDepartement).orElse(null);
        if (u != null && d != null) {
            u.getDepartements().add(d);
            universiteRepository.save(u);
            logger.debug("Département {} assigné à l’université {}", idDepartement, idUniversite);
        } else {
            logger.error("Erreur assignation : université ou département introuvable.");
        }
    }

    public Set<Departement> retrieveDepartementsByUniversite(Integer idUniversite){
        logger.info("Récupération des départements pour l’université ID : {}", idUniversite);
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        if (u == null) {
            logger.error("Université ID {} introuvable pour récupération de ses départements", idUniversite);
            return null;
        }
        Set<Departement> deps = u.getDepartements();
        logger.debug("Départements trouvés : {}", deps);
        return deps;
    }
}
