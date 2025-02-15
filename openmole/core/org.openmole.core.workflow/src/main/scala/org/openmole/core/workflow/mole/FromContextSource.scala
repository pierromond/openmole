package org.openmole.core.workflow.mole

import monocle.macros.Lenses
import org.openmole.core.context.{ Context, Val }
import org.openmole.core.expansion.{ FromContext, Validate }
import org.openmole.core.fileservice.FileService
import org.openmole.core.workflow.builder.{ DefinitionScope, InfoBuilder, InfoConfig, InputOutputBuilder, InputOutputConfig }
import org.openmole.core.workflow.validation.ValidateSource
import org.openmole.core.workspace.TmpDirectory
import org.openmole.tool.random.RandomProvider
import org.openmole.core.workflow.validation

object FromContextSource {

  implicit def isBuilder: InputOutputBuilder[FromContextSource] = InputOutputBuilder(config)
  implicit def isInfo = InfoBuilder(FromContextSource.info)

  case class Parameters(context: Context, executionContext: MoleExecutionContext, implicit val random: RandomProvider, implicit val newFile: TmpDirectory, implicit val fileService: FileService)

  def apply(f: Parameters ⇒ Context)(implicit name: sourcecode.Name, definitionScope: DefinitionScope): FromContextSource = FromContextSource(name.value)(f)
  def apply(className: String)(f: FromContextSource.Parameters ⇒ Context)(implicit name: sourcecode.Name, definitionScope: DefinitionScope) =
    new FromContextSource(
      className,
      f,
      Validate.success,
      config = InputOutputConfig(),
      info = InfoConfig()
    )
}

@Lenses case class FromContextSource(
  override val className: String,
  f:                      FromContextSource.Parameters ⇒ Context,
  v:                      Validate,
  config:                 InputOutputConfig,
  info:                   InfoConfig) extends Source with ValidateSource {

  override def validate = v

  override protected def process(executionContext: MoleExecutionContext) = FromContext { p ⇒
    val fcp = FromContextSource.Parameters(p.context, executionContext, p.random, p.newFile, p.fileService)
    f(fcp)
  }

  def withValidate(validate: Validate) = copy(v = v ++ validate)

}

