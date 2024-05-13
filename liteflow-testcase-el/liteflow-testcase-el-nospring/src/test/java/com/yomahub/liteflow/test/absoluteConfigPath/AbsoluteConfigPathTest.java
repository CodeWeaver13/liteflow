package com.yomahub.liteflow.test.absoluteConfigPath;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.core.FlowExecutorHolder;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.property.LiteflowConfig;
import com.yomahub.liteflow.property.LiteflowConfigGetter;
import com.yomahub.liteflow.test.BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import java.util.Objects;

/**
 * 非spring环境下异步线程超时日志打印测试
 *
 * @author Bryan.Zhang
 * @since 2.6.11
 */
public class AbsoluteConfigPathTest extends BaseTest {

	private static String rootDir;

	private static FlowExecutor flowExecutor;


	@Test
	public void testAbsoluteConfig() throws Exception {
		Assertions.assertTrue(() -> {
			LiteflowConfig config = new LiteflowConfig();
			config.setRuleSource(StrUtil.format("{}/sub/a/flow1.xml",rootDir));
			flowExecutor = FlowExecutorHolder.loadInstance(config);
			return flowExecutor.execute2Resp("chain1", "arg").isSuccess();
		});
	}

	@Test
	public void testAbsolutePathMatch() throws Exception {
		Assertions.assertTrue(() -> {
			LiteflowConfig config = new LiteflowConfig();
			config.setRuleSource(StrUtil.format("{}/sub/**/*.xml",rootDir));
			flowExecutor = FlowExecutorHolder.loadInstance(config);
			return flowExecutor.execute2Resp("chain1", "arg").isSuccess();
		});
	}

	@Test
	@DisabledIf("isWindows")
	public void testAbsPath() throws Exception{
		Assertions.assertTrue(() -> {
			LiteflowConfig config = new LiteflowConfig();
			config.setRuleSource(StrUtil.format("{}\\sub\\**\\*.xml",rootDir));
			flowExecutor = FlowExecutorHolder.loadInstance(config);
			return flowExecutor.execute2Resp("chain1", "arg").isSuccess();
		});
	}

	public static boolean isWindows() {
		try {
			String osName = System.getProperty("os.name");
			if (osName.isEmpty()) return false;
			else {
				return osName.contains("windows");
			}
		} catch (Exception e) {
			return false;
		}
	}

	@BeforeAll
	public static void createFiles() {
		rootDir = FileUtil.getAbsolutePath(ResourceUtil.getResource("").getPath());

		String path1 = StrUtil.format("{}/sub/a", rootDir);
		String path2 = StrUtil.format("{}/sub/b", rootDir);

		FileUtil.mkdir(path1);
		FileUtil.mkdir(path2);

		String content1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><flow><nodes><node id=\"a\" class=\"com.yomahub.liteflow.test.absoluteConfigPath.cmp.ACmp\"/><node id=\"b\" class=\"com.yomahub.liteflow.test.absoluteConfigPath.cmp.BCmp\"/><node id=\"c\" class=\"com.yomahub.liteflow.test.absoluteConfigPath.cmp.CCmp\"/></nodes><chain name=\"chain1\">WHEN(a, b, c);</chain></flow>";
		String content2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><flow><nodes><node id=\"a\" class=\"com.yomahub.liteflow.test.absoluteConfigPath.cmp.ACmp\"/><node id=\"b\" class=\"com.yomahub.liteflow.test.absoluteConfigPath.cmp.BCmp\"/><node id=\"c\" class=\"com.yomahub.liteflow.test.absoluteConfigPath.cmp.CCmp\"/></nodes><chain name=\"chain2\">THEN(c, chain1);</chain></flow>";

		FileUtil.writeString(content1, path1 + "/flow1.xml", CharsetUtil.CHARSET_UTF_8);
		FileUtil.writeString(content2, path2 + "/flow2.xml", CharsetUtil.CHARSET_UTF_8);
	}

	@AfterAll
	public static void removeFiles() {
		FileUtil.del(StrUtil.format("{}/sub", rootDir));
	}
}
