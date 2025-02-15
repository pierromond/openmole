package org.openmole.gui.client.core

import org.openmole.gui.client.core.alert.{ AlertPanel, BannerAlert }
import org.openmole.gui.client.core.files.{ FileDisplayer, TreeNodeManager, TreeNodePanel, TreeNodeTabs }
import org.openmole.gui.ext.api.Api
import org.openmole.gui.ext.data.{ ErrorManager, GUIPluginAsJS, PluginServices, WizardPluginFactory }

/*
 * Copyright (C) 24/07/15 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

object panels {
  val pluginServices =
    PluginServices(
      errorManager = new ErrorManager {
        override def signal(message: String, stack: Option[String]): Unit = panels.bannerAlert.registerWithStack(message, stack)
      }
    )

  lazy val treeNodeManager = new TreeNodeManager()

  lazy val executionPanel =
    new ExecutionPanel(
      setEditorErrors = TreeNodeTabs.setErrors(treeNodeTabs, _, _),
      bannerAlert = bannerAlert)

  def openExecutionPanel = {
    bannerAlert.clear
    executionPanel.dialog.show
  }

  lazy val treeNodeTabs = new TreeNodeTabs()

  lazy val fileDisplayer =
    new FileDisplayer(
      treeNodeTabs = treeNodeTabs,
      showExecution = () ⇒ openExecutionPanel
    )

  lazy val treeNodePanel =
    new TreeNodePanel(
      treeNodeManager = treeNodeManager,
      fileDisplayer = fileDisplayer,
      showExecution = () ⇒ openExecutionPanel,
      treeNodeTabs = treeNodeTabs,
      services = pluginServices)

  def modelWizardPanel(wizards: Seq[WizardPluginFactory]) =
    new ModelWizardPanel(
      treeNodeManager = treeNodeManager,
      treeNodeTabs = treeNodeTabs,
      bannerAlert = bannerAlert,
      wizards = wizards)

  def urlImportPanel =
    new URLImportPanel(
      treeNodeManager,
      bannerAlert = bannerAlert)

  lazy val marketPanel = new MarketPanel(treeNodeManager)
  lazy val pluginPanel = new PluginPanel(bannerAlert = bannerAlert)

  lazy val stackPanel = new TextPanel("Error stack")
  lazy val settingsView = new SettingsView(fileDisplayer)
  lazy val connection = new Connection

  lazy val bannerAlert =
    new BannerAlert(
      resizeTabs = () ⇒ treeNodeTabs.tabs.now.foreach { t ⇒ t.resizeEditor }
    )

  lazy val alertPanel = new AlertPanel

}
