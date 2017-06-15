/*
 * Copyright 2017 Rod MacKenzie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rodm.teamcity.gradle.scripts.server

import com.github.rodm.teamcity.gradle.scripts.GradleInitScriptsPlugin.FEATURE_TYPE
import com.github.rodm.teamcity.gradle.scripts.GradleInitScriptsPlugin.INIT_SCRIPT_NAME
import jetbrains.buildServer.serverSide.SProject
import java.util.ArrayList
import java.util.LinkedHashMap

class InitScriptsUsageAnalyzer(private val scriptsManager: GradleScriptsManager) {

    fun getProjectScriptsUsage(project: SProject): Map<String, ScriptUsage> {
        val scripts = scriptsManager.getScriptNames(project)
        val scriptsForCurrentProject = mutableMapOf<SProject, List<String>>()
        scriptsForCurrentProject.put(project, scripts.get(project).orEmpty())
        return getScriptsUsage(scriptsForCurrentProject)
    }

    fun getScriptsUsage(scripts: Map<SProject, List<String>>): Map<String, ScriptUsage> {
        val usage = LinkedHashMap<String, ScriptUsage>()
        for (entry in scripts.entries) {
            val project = entry.key
            for (script in entry.value) {
                usage.put(script, ScriptUsage())
                addScriptUsageForSubProjects(script, project, usage)
            }
        }
        return usage
    }

    private fun addScriptUsageForSubProjects(name: String, parent: SProject, usage: Map<String, ScriptUsage>) {
        for (project in parent.ownProjects) {
            val scripts = getProjectScripts(project)
            if (!scripts.contains(name)) {
                addScriptUsageForSubProjects(name, project, usage)
            }
        }
        addScriptUsageForProject(name, parent, usage)
    }

    private fun addScriptUsageForProject(name: String, project: SProject, usage: Map<String, ScriptUsage>) {
        for (buildType in project.ownBuildTypes) {
            for (feature in buildType.getBuildFeaturesOfType(FEATURE_TYPE)) {
                val parameters = feature.parameters
                val scriptName = parameters[INIT_SCRIPT_NAME]
                if (scriptName!! == name) {
                    usage[scriptName]!!.addBuildType(buildType)
                }
            }
        }
        for (buildTemplate in project.getOwnBuildTypeTemplates()) {
            for (feature in buildTemplate.getBuildFeaturesOfType(FEATURE_TYPE)) {
                val parameters = feature.parameters
                val scriptName = parameters[INIT_SCRIPT_NAME]
                if (scriptName!! == name) {
                    usage[scriptName]!!.addBuildTemplate(buildTemplate)
                }
            }
        }
    }

    private fun getProjectScripts(project: SProject): List<String> {
        return scriptsManager.getScriptNames(project).getOrDefault(project, ArrayList<String>())
    }
}