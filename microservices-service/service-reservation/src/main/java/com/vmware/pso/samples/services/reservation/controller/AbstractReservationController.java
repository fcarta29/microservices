package com.vmware.pso.samples.services.reservation.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.vmware.pso.samples.core.dto.AbstractDto;
import com.vmware.pso.samples.core.model.AbstractUUIDEntity;

@CrossOrigin(origins = "http://localhost:8080")
public abstract class AbstractReservationController<D extends AbstractDto, E extends AbstractUUIDEntity> {

    protected static final UUID DEFAULT_DATACENTER_ID = UUID.fromString("aefd7ed1-7393-41a5-9ccf-311fca0b62d4");
    protected static final UUID DEFAULT_GROUP_ID = UUID.fromString("82d515a8-2ab6-4b88-aceb-7d1aa70309fb");
    protected static final UUID DEFAULT_USER_ID = UUID.fromString("1beb73fb-b5d8-4cf8-b69a-d81b55e9967d");

    protected static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    protected List<D> toDtoList(final Collection<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.<D> emptyList();
        }

        final List<D> dtoList = new ArrayList<D>();
        for (final E entity : entities) {
            dtoList.add(toDto(entity));
        }
        return dtoList;
    }

    protected abstract D toDto(final E entity);

    protected abstract E toEntity(final D dto);
}
