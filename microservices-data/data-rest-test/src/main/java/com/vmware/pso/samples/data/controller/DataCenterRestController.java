package com.vmware.pso.samples.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.Dao;
import com.vmware.pso.samples.core.dao.DataCenterDao;
import com.vmware.pso.samples.core.model.DataCenter;

@RestController
@RequestMapping("/dataCenters")
public class DataCenterRestController extends AbstractRestController<DataCenter> {

    @Autowired
    private DataCenterDao dataCenterDao;

    @Override
    protected Dao<DataCenter> getDao() {
        return dataCenterDao;
    }

    @Override
    protected void merge(final DataCenter persistedDataCenter, final DataCenter dataCenter) {
        persistedDataCenter.setName(dataCenter.getName());
        persistedDataCenter.setActive(dataCenter.isActive());
    }
}