package com.github.gradlerouter.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.lang.reflect.Field

class RouterPlugin implements Plugin<Project> {

    //注入插件的逻辑
    @Override
    void apply(Project project) {

        //注册Transform
        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            Transform transform = new RouterMappingTransform()
            appExtension.registerTransform(transform)
        }

        //1.自动把路径参数传递到注解处理器中
        if (project.extensions.findByName("kapt") != null) {
            project.extensions.findByName("kapt").arguments {
                arg("root_project_dir", project.rootProject.projectDir.absolutePath)
            }
        }

        //2.实现废弃构建产物的自动清理
        project.clean.doFirst {
            //删除上次构建生成的router_mapping目录
            File routerMappingDir =
                    new File(project.rootProject.projectDir, "router_mapping")
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }
        //3.在javac任务后，汇总生成文档

        if (!project.plugins.hasPlugin(AppPlugin)) {
            return
        }

        println("i am from RouterPlugin, apply from ${project.name}")

        project.getExtensions().create("router", RouterExtension)

        project.afterEvaluate {
            RouterExtension extension = project["router"]
            println("用户设置的WIKI路径: ${extension.wikiDir}")

            //3.在javac任务(compileDebugJavaWithJavac)后，汇总生成文档
            project.tasks.findAll { task ->
                task.name.startsWith('compile') &&
                        task.name.endsWith('JavaWithJavac')
            }.each { task ->
                task.doLast {

                    File routerMappingDir =
                            new File(project.rootProject.rootDir,
                                    "router_mapping")
                    if (!routerMappingDir.exists()) {
                        return
                    }
                    File[] allChildFiles = routerMappingDir.listFiles()
                    if (allChildFiles.length < 1) {
                        return
                    }
                    StringBuilder markdoewBuilder = new StringBuilder()
                    markdoewBuilder.append("# 页面文档\n\n")
                    allChildFiles.each {child ->
                        if (child.name.endsWith(".json")) {

                            JsonSlurper jsonSlurper = new JsonSlurper()
                            def content = jsonSlurper.parse(child)

                            content.each { innerContent ->
                                def url = innerContent['url']
                                def description = innerContent['description']
                                def realPath = innerContent['realPath']
                                markdoewBuilder.append("## $description \n")
                                markdoewBuilder.append("-url $url \n")
                                markdoewBuilder.append("-realPath $realPath \n\n")

                            }
                        }
                    }
                    File wikiFileDir = new File(extension.wikiDir)
                    if (!wikiFileDir.exists()) {
                        wikiFileDir.mkdir()
                    }
                    File wikiFile = new File(wikiFileDir, "页面文档.md")
                    if (wikiFileDir.exists()) {
                        wikiFileDir.delete()
                    }
                    wikiFile.write(markdoewBuilder.toString())
                }
            }
        }
    }
}