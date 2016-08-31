package com.vmware.pso.samples.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.vmware.pso.samples.core.model.AbstractUUIDEntity;

public interface Dao<E extends AbstractUUIDEntity> {

    public E get(final UUID id);

    public List<E> getList(final Collection<UUID> ids);

    public List<E> list();

    public void save(final E entity);

    public void remove(final E entity);
}
