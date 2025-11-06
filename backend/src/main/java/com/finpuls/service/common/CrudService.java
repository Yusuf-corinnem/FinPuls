package com.finpuls.service.common;

import java.util.List;
import java.util.Optional;

public interface CrudService<E, ID> {
    Optional<E> findById(ID id);
    List<E> findAll();
    E save(E entity);
    void deleteById(ID id);
}


