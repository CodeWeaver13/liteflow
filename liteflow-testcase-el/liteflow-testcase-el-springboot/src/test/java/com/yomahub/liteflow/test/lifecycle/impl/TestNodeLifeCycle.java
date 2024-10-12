package com.yomahub.liteflow.test.lifecycle.impl;

import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.flow.element.Node;
import com.yomahub.liteflow.lifecycle.PostProcessAfterNodeBuildLifeCycle;
import org.springframework.stereotype.Component;

@Component
public class TestNodeLifeCycle implements PostProcessAfterNodeBuildLifeCycle {
    @Override
    public void postProcessAfterNodeBuild(Node node) {
        System.out.println(StrUtil.format("Node生命周期——[{}]已被加载",node.getId()));
    }
}