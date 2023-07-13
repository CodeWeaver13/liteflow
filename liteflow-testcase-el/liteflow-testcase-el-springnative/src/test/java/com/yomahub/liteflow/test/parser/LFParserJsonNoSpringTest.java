package com.yomahub.liteflow.test.parser;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.property.LiteflowConfig;
import com.yomahub.liteflow.test.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 无spring环境的json parser单元测试
 *
 * @author Bryan.Zhang
 * @since 2.5.0
 */
public class LFParserJsonNoSpringTest extends BaseTest {

	// 测试无spring场景的json parser
	@Test
	public void testNoSpring() {
		LiteflowConfig liteflowConfig = new LiteflowConfig();
		liteflowConfig.setRuleSource("parser/flow.el.json");
		FlowExecutor executor = new FlowExecutor(liteflowConfig);
		LiteflowResponse response = executor.execute2Resp("chain1", "arg");
		Assertions.assertTrue(response.isSuccess());
	}

}
