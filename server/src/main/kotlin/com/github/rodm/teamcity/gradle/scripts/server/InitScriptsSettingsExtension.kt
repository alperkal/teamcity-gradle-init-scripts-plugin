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

import jetbrains.buildServer.serverSide.ProjectManager
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.web.openapi.buildType.EditBuildRunnerSettingsExtension
import jetbrains.buildServer.web.openapi.buildType.ViewBuildRunnerSettingsExtension

class InitScriptsSettingsExtension(projectManager: ProjectManager,
                                   pagePlaces: PagePlaces,
                                   descriptor: PluginDescriptor)
{
    private val supportedRunTypes = listOf("gradle-runner")

    init {
        val editRunnerExtension = EditBuildRunnerSettingsExtension(pagePlaces, supportedRunTypes)
        editRunnerExtension.pluginName = descriptor.pluginName
        editRunnerExtension.includeUrl = descriptor.getPluginResourcesPath("/editRunner.jsp")
        editRunnerExtension.addCssFile("/css/admin/buildTypeForm.css")
        editRunnerExtension.register()

        val viewRunnerExtension = ViewBuildRunnerSettingsExtension(projectManager, pagePlaces, supportedRunTypes)
        viewRunnerExtension.pluginName = descriptor.pluginName
        viewRunnerExtension.includeUrl = descriptor.getPluginResourcesPath("/viewRunner.jsp")
        viewRunnerExtension.register()
    }
}
