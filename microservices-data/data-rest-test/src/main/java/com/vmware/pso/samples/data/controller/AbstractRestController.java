package com.vmware.pso.samples.data.controller;

import java.util.Collection;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.Dao;
import com.vmware.pso.samples.core.model.AbstractUUIDEntity;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public abstract class AbstractRestController<E extends AbstractUUIDEntity> {

    protected abstract Dao<E> getDao();

    protected abstract void merge(final E persitedEntity, final E newEntity);

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<E> getList() {
        return getDao().list();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody E get(@PathVariable("id") final UUID id) {
        return getDao().get(id);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    final public void create(@RequestBody final E entity) {
        createEntity(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    final public void save(@PathVariable("id") final UUID id, final @RequestBody E entity) {
        final E persistedEntity = getDao().get(id);
        updateEntity(persistedEntity, entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    final public void delete(@PathVariable("id") final UUID id) {
        final E persistedEntity = getDao().get(id);
        if (persistedEntity != null) {
            getDao().remove(persistedEntity);
        }
    }

    protected void createEntity(final E entity) {
        getDao().save(entity);
    }

    protected void updateEntity(final E persistedEntity, final E entity) {
        merge(persistedEntity, entity);
        getDao().save(persistedEntity);
    }
}
