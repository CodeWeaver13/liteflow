package com.yomahub.liteflow.benchmark;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.FlowBus;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@EnableAutoConfiguration
@PropertySource(value = "classpath:application.properties")
@ComponentScan("com.yomahub.liteflow.benchmark.cmp")
public class ScriptJavaBenchmark {

    private ConfigurableApplicationContext applicationContext;

    private FlowExecutor flowExecutor;

    @Setup
    public void setup() {
        applicationContext = SpringApplication.run(ScriptJavaBenchmark.class);
        flowExecutor = applicationContext.getBean(FlowExecutor.class);
    }

    @TearDown
    public void tearDown() {
        applicationContext.close();
    }

    //普通执行
    @Benchmark
    public  void test1(){
        flowExecutor.execute2Resp("chain1");
    }

    //LF动态创建组件和规则，并执行
    @Benchmark
    public  void test2(){
        String scriptContent = ResourceUtil.readUtf8Str("classpath:javaScript.java");
        LiteFlowNodeBuilder.createScriptNode().setId("ds").setScript(scriptContent).build();

        if(!FlowBus.containChain("chain2")){
            LiteFlowChainELBuilder.createChain().setChainId("chain2").setEL("THEN(ds)").build();
        }
        flowExecutor.execute2Resp("chain2");
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ScriptJavaBenchmark.class.getSimpleName())
                .mode(Mode.Throughput)
                .warmupIterations(1)//预热次数
                .measurementIterations(3)//执行次数
                .measurementTime(new TimeValue(10, TimeUnit.SECONDS))//每次执行多少时间
                .threads(300)//多少个线程
                .forks(1)//多少个进程
                .timeUnit(TimeUnit.SECONDS)
                .build();
        new Runner(opt).run();
    }
}