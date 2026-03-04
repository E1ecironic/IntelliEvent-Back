package com.kevin.basecore.modules.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.basecore.modules.ai.entity.AiKnowledge;
import com.kevin.basecore.modules.ai.mapper.AiKnowledgeMapper;
import com.kevin.basecore.modules.ai.service.AiRagService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AiRagServiceImpl extends ServiceImpl<AiKnowledgeMapper, AiKnowledge> implements AiRagService {

    @Override
    public List<AiKnowledge> search(String query, int limit) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }
        LambdaQueryWrapper<AiKnowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledge::getStatus, 1)
                .and(inner -> inner.like(AiKnowledge::getTitle, query)
                        .or()
                        .like(AiKnowledge::getContent, query)
                        .or()
                        .like(AiKnowledge::getTags, query))
                .last("limit " + Math.max(limit, 1));
        return list(wrapper);
    }

    @Override
    public boolean saveKnowledge(AiKnowledge knowledge) {
        return saveOrUpdate(knowledge);
    }
}
