/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.stores.modules.sys.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stores.common.service.BaseService;
import com.stores.modules.sys.dao.OfficeDao;
import com.stores.modules.sys.entity.Office;
import com.stores.modules.sys.utils.UserUtils;

/**
 * 部门Service
 * @author ThinkGem
 * @version 2013-01-15
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends BaseService {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(OfficeService.class);
	
	@Autowired
	private OfficeDao officeDao;
	
	public Office get(Long id) {
		return officeDao.findOne(id);
	}
	
	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		office.setParent(this.get(office.getParent().getId()));
		String oldParentIds = office.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		office.setParentIds(office.getParent().getParentIds()+office.getParent().getId()+",");
		officeDao.clear();
		officeDao.save(office);
		// 更新子节点 parentIds
		List<Office> list = officeDao.findByParentIdsLike("%,"+office.getId()+",%");
		for (Office e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, office.getParentIds()));
		}
		officeDao.save(list);
		UserUtils.removeCache("officeList");
	}
	
	@Transactional(readOnly = false)
	public void delete(Long id) {
		officeDao.deleteById(id, "%,"+id+",%");
		UserUtils.removeCache("officeList");
	}
	
}
