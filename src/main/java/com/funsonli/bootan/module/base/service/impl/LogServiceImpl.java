package com.funsonli.bootan.module.base.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.funsonli.bootan.common.constant.CommonConstant;
import com.funsonli.bootan.common.vo.SearchVO;
import com.funsonli.bootan.module.base.dao.LogDao;
import com.funsonli.bootan.module.base.entity.Log;
import com.funsonli.bootan.module.base.mapper.LogMapper;
import com.funsonli.bootan.module.base.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日志接口
 * @author Funson
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogDao modelDao;


    @Autowired
    private LogMapper modelMapper;

    @Override
    public LogDao getDao() {
        return modelDao;
    }

    /**
    * 列表搜索分页
    * @param model
    * @param searchVO
    * @param pageable
    * @return
    */
    @Override
    public Page<Log> findByCondition(Log model, SearchVO searchVO, Pageable pageable) {
        return modelDao.findAll(new Specification<Log>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Log> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                // 默认搜索条件
                Path<String> nameField = root.get("name");
                Path<Integer> typeField = root.get("type");
                Path<Integer> statusField = root.get("status");
                Path<Date> createAtField = root.get("createdAt");
                // 添加您自定义条件

                List<Predicate> list = new ArrayList<Predicate>();

                if(StrUtil.isNotBlank(model.getName())){
                    list.add(cb.like(nameField, '%' + model.getName() + '%'));
                }

                if(model.getType() != null){
                    list.add(cb.equal(typeField, model.getType()));
                }

                if(model.getStatus() != null){
                    list.add(cb.equal(statusField, model.getStatus()));
                }

                if(StrUtil.isNotBlank(searchVO.getStartDate()) && StrUtil.isNotBlank(searchVO.getEndDate())){
                    Date start = DateUtil.parse(searchVO.getStartDate());
                    Date end = DateUtil.parse(searchVO.getEndDate());
                    list.add(cb.between(createAtField, start, DateUtil.endOfDay(end)));
                }

                // 添加您的自定义条件

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public Log beforeSave(Log entity) {
        if (entity.getType() == null) {
            entity.setType(CommonConstant.LOG_TYPE_ACCESS);
        }
        if (entity.getRequestUrl() == null) {
            entity.setRequestUrl("");
        }
        if (entity.getRequestType() == null) {
            entity.setRequestType("");
        }
        if (entity.getRequestParam() == null) {
            entity.setRequestParam("");
        }
        if (entity.getUsername() == null) {
            entity.setUsername("");
        }
        if (entity.getIp() == null) {
            entity.setIp("");
        }
        if (entity.getIpInfo() == null) {
            entity.setIpInfo("");
        }
        if (entity.getCostTime() == null) {
            entity.setCostTime(0);
        }
        return entity;
    }

}
