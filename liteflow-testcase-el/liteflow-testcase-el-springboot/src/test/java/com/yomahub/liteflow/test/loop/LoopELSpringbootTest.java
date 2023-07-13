package com.yomahub.liteflow.test.loop;

import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;
import com.yomahub.liteflow.test.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import javax.annotation.Resource;
import java.util.List;

/**
 * springboot环境EL循环的例子测试
 *
 * @author Bryan.Zhang
 */
@TestPropertySource(value = "classpath:/loop/application.properties")
@SpringBootTest(classes = LoopELSpringbootTest.class)
@EnableAutoConfiguration
@ComponentScan({ "com.yomahub.liteflow.test.loop.cmp" })
public class LoopELSpringbootTest extends BaseTest {

	@Resource
	private FlowExecutor flowExecutor;

	// FOR循环数字直接在el中定义
	@Test
	public void testLoop1() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain1", "arg");
		Assertions.assertTrue(response.isSuccess());
		Assertions.assertEquals("LOOP_2==>a==>b==>c==>a==>b==>c", response.getExecuteStepStr());
	}

	// FPR循环由For组件定义
	@Test
	public void testLoop2() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain2", "arg");
		Assertions.assertTrue(response.isSuccess());
		Assertions.assertEquals("x==>a==>b==>c==>a==>b==>c==>a==>b==>c", response.getExecuteStepStr());
	}

	// FOR循环中加入BREAK组件
	@Test
	public void testLoop3() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain3", "arg");
		Assertions.assertTrue(response.isSuccess());
	}

	// WHILE循环
	@Test
	public void testLoop4() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain4", "arg");
		Assertions.assertTrue(response.isSuccess());
		Assertions.assertEquals("z==>a==>d==>z==>a==>d==>z==>a==>d==>z==>a==>d==>z==>a==>d==>z",
				response.getExecuteStepStr());
	}

	// WHILE循环加入BREAK
	@Test
	public void testLoop5() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain5", "arg");
		Assertions.assertTrue(response.isSuccess());
		Assertions.assertEquals("z==>a==>d==>y==>z==>a==>d==>y==>z==>a==>d==>y==>z==>a==>d==>y",
				response.getExecuteStepStr());
	}

	// 测试FOR循环中的index
	@Test
	public void testLoop6() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain6", "arg");
		DefaultContext context = response.getFirstContextBean();
		Assertions.assertTrue(response.isSuccess());
		Assertions.assertEquals("01234", context.getData("loop_e1"));
		Assertions.assertEquals("01234", context.getData("loop_e2"));
		Assertions.assertEquals("01234", context.getData("loop_e3"));
	}

	// 测试WHILE循环中的index
	@Test
	public void testLoop7() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain7", "arg");
		DefaultContext context = response.getFirstContextBean();
		Assertions.assertTrue(response.isSuccess());
		Assertions.assertEquals("01234", context.getData("loop_e1"));
		Assertions.assertEquals("01234", context.getData("loop_e2"));
		Assertions.assertEquals("01234", context.getData("loop_e3"));
	}

	// 测试嵌套循环
	@Test
	public void testLoop8() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain8", "arg");
		DefaultContext context = response.getFirstContextBean();
		List<Integer> list = context.getData("test");
		String str = StrUtil.join(StrUtil.EMPTY, list);
		Assertions.assertTrue(response.isSuccess());
		Assertions.assertEquals("001101201", str);
	}

	//FOR循环同一个组件，下标获取不到问题的测试
	@Test
	public void testLoop9() throws Exception {
		LiteflowResponse response = flowExecutor.execute2Resp("chain9", "arg");
		Assertions.assertTrue(response.isSuccess());
	}
}
