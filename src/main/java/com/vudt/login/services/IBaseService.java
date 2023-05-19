package com.vudt.login.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface IBaseService<T, D, M, K> {
    List<D> findAll();

    Page<D> findAll(Pageable page);

    List<D> findAll(Specification<T> specs);

    Page<D> filter(Pageable page, Specification<T> specs);

    D findById(K id);
    T getById(K id);

    D add(M model);

    List<D> add(List<M> model);

    D update(M model);

    boolean deleteById(K id);

    boolean deleteByIds(List<K> ids);
}
